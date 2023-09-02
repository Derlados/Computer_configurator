import { ForbiddenException, Injectable, NotFoundException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import * as bcrypt from 'bcrypt';
import { SignInUser } from 'src/users/dto/sign-in-user.dto';
import { User } from '../users/models/user.model';
import { UsersService } from '../users/users.service';

@Injectable()
export class AuthService {
    static HASH_SALT = 5;

    constructor(private usersService: UsersService, private jwtService: JwtService) { }

    async signIn(dto: SignInUser) {
        dto.uid = await bcrypt.hash(dto.uid, AuthService.HASH_SALT);

        const user = await this.usersService.findUserByUid(dto.uid);
        if (user) {
            return this.generateToken(user);
        }

        const newUser = await this.usersService.createUser(dto);
        return this.generateToken(newUser);
    }

    private generateToken(user: User) {
        const payload = { id: user.uid, username: user.username, roles: user.roles?.map(role => role.name) ?? ["user"] };
        return this.jwtService.sign(payload);
    }
}
