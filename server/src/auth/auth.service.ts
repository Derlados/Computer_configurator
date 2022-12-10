import { ConflictException, ForbiddenException, Injectable, InternalServerErrorException, NotFoundException, UnauthorizedException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import * as bcrypt from 'bcrypt';
import { Errors } from 'src/constants/Errors';
import { ChangePasswordDto } from 'src/users/dto/change-password.dto';
import { CreateUserDto } from '../users/dto/create-user.dto';
import { GoogleSignInDto } from '../users/dto/google-sign-in-dto';
import { LoginUserDto } from '../users/dto/login-user.dto';
import { User } from '../users/models/user.model';
import { UsersService } from '../users/users.service';
import * as uuid from 'uuid';

@Injectable()
export class AuthService {
    static HASH_SALT = 5;

    constructor(private usersService: UsersService, private jwtService: JwtService) { }

    async register(dto: CreateUserDto | GoogleSignInDto) {
        if (dto instanceof CreateUserDto) {
            const hashPassword = bcrypt.hashSync(dto.password, AuthService.HASH_SALT);
            const hashSecret = bcrypt.hashSync(dto.secret, AuthService.HASH_SALT);
            dto = { ...dto, password: hashPassword, secret: hashSecret };
        } else {
            dto.username = `${process.env.GOOGLE_ADD_NICKNAME}-${uuid.v4().replace(/(-)+/g, '_')}-${dto.username}`
        }


        const candidate = await this.usersService.findByUserame(dto.username)
        if (candidate) {
            throw new ConflictException(Errors.NICKNAME_TAKEN);
        }

        const user = await this.usersService.createUser(dto);
        return this.generateToken(user)
    }

    async googleSignIn(dto: GoogleSignInDto) {
        const user = await this.usersService.findUserByGoogleId(dto.googleId);
        if (!user) {
            return this.register(dto);
        } else {
            return this.generateToken(user)
        }
    }

    async login(dto: LoginUserDto) {
        const user = await this.usersService.findByUserame(dto.username);
        if (!user) {
            throw new NotFoundException(Errors.LOGIN_USER_NOT_FOUND);
        }

        const isAvailablePass = bcrypt.compareSync(dto.password, user.password);
        if (!isAvailablePass) {
            throw new NotFoundException(Errors.LOGIN_USER_NOT_FOUND);
        }

        return this.generateToken(user)
    }

    async changePassword(dto: ChangePasswordDto) {
        const user = await this.usersService.findByUserame(dto.username);
        if (!user) {
            throw new NotFoundException(Errors.LOGIN_USER_NOT_FOUND);
        }

        const isAvailableSecret = bcrypt.compareSync(dto.secret, user.secret);
        if (!isAvailableSecret) {
            throw new NotFoundException(Errors.LOGIN_USER_NOT_FOUND);
        }

        const hashNewPassword = bcrypt.hashSync(dto.newPassword, AuthService.HASH_SALT);
        this.usersService.updatePassword(user.id, hashNewPassword);
    }

    private generateToken(user: User) {
        const payload = { id: user.id, username: user.username, roles: user.roles?.map(role => role.name) ?? ["user"] };
        return this.jwtService.sign(payload);
    }
}