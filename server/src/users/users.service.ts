import { Injectable, ConflictException, InternalServerErrorException, NotFoundException, ForbiddenException } from "@nestjs/common";
import { InjectRepository } from "@nestjs/typeorm";
import { Repository, Like, IsNull, Not } from "typeorm";
import { CreateUserDto } from "./dto/create-user.dto";
import { GoogleSignInDto } from "./dto/google-sign-in-dto";
import { UpdateUserDto } from "./dto/update-user.dto";
import { User } from "./models/user.model";
import { Errors } from "src/constants/Errors";
import { FilesService } from "src/files/files.service";
import * as uuid from 'uuid';

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
        const insertId = (await this.usersRepository.insert({ ...dto })).raw.insertId;
        return this.findUserById(insertId);
    }

    async addGoogleAccout(id: number, dto: GoogleSignInDto) {
        const user = await this.findUserById(id);
        const existGoogleId = await this.findUserByGoogleId(dto.googleId);

        if (existGoogleId) {
            throw new ConflictException(Errors.GOOGLE_ACCOUNT_ALREADY_USED)
        }

        dto.photo = user.photo ? user.photo : dto.photo;
        await this.usersRepository.update({ id: id }, { ...dto, username: user.username });

        return this.findUserById(id);
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
