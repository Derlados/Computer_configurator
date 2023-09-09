import { ForbiddenException, Injectable, NotFoundException, UnauthorizedException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import * as bcrypt from 'bcrypt';
import { SignInUser } from 'src/users/dto/sign-in-user.dto';
import { User } from '../users/models/user.model';
import { UsersService } from '../users/users.service';
import { getAuth, UserRecord } from 'firebase-admin/auth';
import { CreateUserDto } from 'src/users/dto/create-user.dto';
import { CheckAuthStatusDto } from './dto/check-auth-status.dto';

@Injectable()
export class AuthService {
    static HASH_SALT = 5;

    constructor(private usersService: UsersService, private jwtService: JwtService) { }

    async checkAuthStatus(dto: CheckAuthStatusDto) {
        try {
            const userInfo = await this.decodeUserInfo(dto.idToken);
            const user = await this.usersService.findUserById(userInfo.id);
            if (!user) {
                throw new NotFoundException();
            }

            return true;
        } catch (error) {
            throw new UnauthorizedException();
        }
    }

    async signIn(dto: SignInUser) {
        try {
            const userInfo = await this.decodeUserInfo(dto.idToken);
            userInfo.username = dto.username ?? userInfo.username;

            const user = await this.usersService.findUserById(userInfo.id);
            if (user) {
                return user;
            }

            const newUser = await this.usersService.createUser(userInfo);
            return newUser;
        } catch (error) {
            throw new UnauthorizedException();
        }
    }

    private async decodeUserInfo(idToken: string): Promise<CreateUserDto> {
        const decodedToken = await getAuth().verifyIdToken(idToken, true)
        return {
            id: decodedToken.uid,
            email: decodedToken.email,
            providerId: decodedToken.firebase.sign_in_provider,
            username: decodedToken.name,
            photo: decodedToken.picture
        };
    }
}
