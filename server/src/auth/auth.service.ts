import { ForbiddenException, Injectable, NotFoundException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import * as bcrypt from 'bcrypt';
import { SignInUser } from 'src/users/dto/sign-in-user.dto';
import { User } from '../users/models/user.model';
import { UsersService } from '../users/users.service';
import { getAuth, UserRecord } from 'firebase-admin/auth';
import { CreateUserDto } from 'src/users/dto/create-user.dto';

@Injectable()
export class AuthService {
    static HASH_SALT = 5;

    constructor(private usersService: UsersService, private jwtService: JwtService) { }

    async signIn(dto: SignInUser) {
        const userInfo = await this.decodeUserInfo(dto.idToken);
        userInfo.username = dto.username ?? userInfo.username;

        const user = await this.usersService.findUserById(userInfo.id);
        if (user) {
            return user;
        }

        const newUser = await this.usersService.createUser(userInfo);
        return newUser;
    }

    private async decodeUserInfo(idToken: string): Promise<CreateUserDto> {
        const decodedToken = await getAuth().verifyIdToken(idToken)
        return {
            id: decodedToken.uid,
            email: decodedToken.email,
            providerId: decodedToken.firebase.sign_in_provider,
            username: decodedToken.name,
            photo: decodedToken.picture
        };
    }

}
