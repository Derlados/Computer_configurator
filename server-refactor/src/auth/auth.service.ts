import { ConflictException, ForbiddenException, Injectable, InternalServerErrorException, NotFoundException, UnauthorizedException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import * as bcrypt from 'bcrypt';
import { AccountDataDto } from '../users/dto/accoun-data.dto';
import { CreateUserDto } from '../users/dto/create-user.dto';
import { GoogleSignInDto } from '../users/dto/google-sign-in-dto';
import { LoginUserDto } from '../users/dto/login-user.dto';
import { RestorePassDto } from '../users/dto/restore-pass.dto';
import { User } from '../users/models/user.model';
import { UsersService } from '../users/users.service';

@Injectable()
export class AuthService {
    static HASH_SALT = 5;

    constructor(private usersService: UsersService, private jwtService: JwtService) { }

    async register(dto: CreateUserDto | GoogleSignInDto) {
        if (dto instanceof CreateUserDto) {
            const hashPassword = bcrypt.hashSync(dto.password, AuthService.HASH_SALT);
            const hashSecret = bcrypt.hashSync(dto.secret, AuthService.HASH_SALT);
            dto = { ...dto, password: hashPassword, secret: hashSecret };
        }

        try {
            const user = await this.usersService.createUser(dto);
            return this.getAccountData(user, this.generateToken(user));
        } catch (err) {
            if (err.code == 'ER_DUP_ENTRY') {
                throw new ConflictException("User with this username, already exist");
            } else {
                throw new InternalServerErrorException();
            }
        }
    }

    async googleSignIn(dto: GoogleSignInDto) {
        const user = await this.usersService.findUserByGoogleId(dto.googleId);
        if (!user) {
            return this.register(dto);
        } else {
            return this.getAccountData(user, this.generateToken(user));
        }
    }

    async login(dto: LoginUserDto) {
        const user = await this.usersService.findUserByUsername(dto.username);
        if (!user) {
            throw new NotFoundException("User not found");
        }

        const isAvailablePass = bcrypt.compareSync(dto.password, user.password);
        if (!isAvailablePass) {
            throw new NotFoundException("User not found");
        }

        return this.getAccountData(user, this.generateToken(user));
    }

    async restorePass(dto: RestorePassDto) {
        const user = await this.usersService.findUserByUsername(dto.username);
        if (!user) {
            throw new NotFoundException("User not found");
        }

        const isAvailableSecret = bcrypt.compareSync(dto.secret, user.secret);
        if (!isAvailableSecret) {
            throw new ForbiddenException();
        }

        const hashPassword = bcrypt.hashSync(dto.newPassword, AuthService.HASH_SALT);
        return await this.usersService.updatePassword(user.id, hashPassword);
    }

    private getAccountData(user: User, token: string) {
        const dto: AccountDataDto = {
            id: user.id,
            username: user.username,
            email: user.email,
            photoUrl: user.photoUrl,
            token: token
        }
        return dto;
    }

    private generateToken(user: User) {
        const payload = { id: user.id, username: user.username };
        return this.jwtService.sign(payload);
    }
}
