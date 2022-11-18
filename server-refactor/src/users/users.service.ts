import { Injectable, ConflictException, InternalServerErrorException, NotFoundException, ForbiddenException } from "@nestjs/common";
import { InjectRepository } from "@nestjs/typeorm";
import { Repository, Like } from "typeorm";
import { CreateUserDto } from "./dto/create-user.dto";
import { GoogleSignInDto } from "./dto/google-sign-in-dto";
import { UpdatePasswordDto } from "./dto/update-password.dto";
import { UpdateUserDto } from "./dto/update-user.dto";
import { User } from "./models/user.model";
import * as bcrypt from 'bcrypt';

@Injectable()
export class UsersService {
    constructor(@InjectRepository(User) private usersRepository: Repository<User>) { }

    async findUserById(id: number) {
        return this.usersRepository.find({ where: { id: id }, relations: ["roles"] });
    }

    async findUserByGoogleId(googleId: string) {
        return this.usersRepository.findOne({ where: { googleId: googleId }, relations: ["roles"] });
    }

    async findByUserame(username: string) {
        return this.usersRepository.findOne({ where: { username: username }, relations: ["roles"] });
    }

    async findAllByUsername(username: string) {
        return this.usersRepository.find({ where: { username: Like(`%${username}%`) }, relations: ["roles"] });
    }

    async createUser(dto: CreateUserDto | GoogleSignInDto) {
        const user = this.usersRepository.create({ ...dto });
        return this.usersRepository.save(user);
    }

    async addGoogleAccout(id: number, dto: GoogleSignInDto) {
        const { username, ...googleInfo } = dto;
        return await this.usersRepository.update({ id: id }, { ...googleInfo });
    }

    async updatePassword(id: number, dto: UpdatePasswordDto) {
        const user = await this.findByUserame(dto.username);
        if (!user) {
            throw new NotFoundException("User not found");
        }

        const isAvailablePass = bcrypt.compareSync(dto.secret, user.secret);
        if (!isAvailablePass) {
            throw new ForbiddenException();
        }

        return this.usersRepository.update({ id: id }, { password: dto.newPassword });
    }

    async updateUser(id: number, dto: UpdateUserDto) {
        try {
            await this.usersRepository.update({ id: id }, { ...dto })
            return this.findUserById(id);
        } catch (e) {
            if (e.code == 'ER_DUP_ENTRY') {
                throw new ConflictException()
            } else {
                throw new InternalServerErrorException()
            }
        }
    }

    async deleteUser(id: number) {
        await this.usersRepository.delete({ id: id });
    }

    //TODO
    saveImage() {

    }

}
