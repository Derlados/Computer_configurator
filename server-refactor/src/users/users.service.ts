import { Injectable, ConflictException, InternalServerErrorException, NotFoundException, ForbiddenException } from "@nestjs/common";
import { InjectRepository } from "@nestjs/typeorm";
import { Repository, Like } from "typeorm";
import { CreateUserDto } from "./dto/create-user.dto";
import { GoogleSignInDto } from "./dto/google-sign-in-dto";
import { UpdateUserDto } from "./dto/update-user.dto";
import { User } from "./models/user.model";
import * as bcrypt from 'bcrypt';
import { Errors } from "src/constants/Errors";
import { FilesService } from "src/files/files.service";

@Injectable()
export class UsersService {
    constructor(@InjectRepository(User) private usersRepository: Repository<User>,
        private fileService: FilesService) { }

    async findUserById(id: number) {
        return this.usersRepository.findOne({ where: { id: id }, relations: ["roles"] });
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

    async updatePassword(id: number, newPassword: string) {
        return this.usersRepository.update({ id: id }, { password: newPassword });
    }

    async updateUser(id: number, dto: UpdateUserDto) {
        const existUser = await this.findByUserame(dto.username);
        if (existUser) {
            throw new ConflictException(Errors.NICKNAME_TAKEN)
        }

        await this.usersRepository.update({ id: id }, { ...dto })
        return this.findUserById(id);
    }

    async updatePhoto(id: number, img: Express.Multer.File) {
        const filename = await this.fileService.createFile(img);
        await this.usersRepository.update({ id: id }, { photo: filename });

        return this.findUserById(id);
    }

    async deleteUser(id: number) {
        await this.usersRepository.delete({ id: id });
    }

}
