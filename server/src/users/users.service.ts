import { Injectable, ConflictException } from "@nestjs/common";
import { InjectRepository } from "@nestjs/typeorm";
import { Repository, Like } from "typeorm";
import { UpdateUserDto } from "./dto/update-user.dto";
import { User } from "./models/user.model";
import { Errors } from "src/constants/Errors";
import { FilesService } from "src/files/files.service";
import { SignInUser } from "./dto/sign-in-user.dto";
import { CreateUserDto } from "./dto/create-user.dto";

@Injectable()
export class UsersService {
    constructor(@InjectRepository(User) private usersRepository: Repository<User>,
        private fileService: FilesService) { }

    async findUserById(id: string) {
        return this.usersRepository.findOne({ where: { id: id }, relations: ["roles"] });
    }


    async findByUserame(username: string) {
        return this.usersRepository.findOne({ where: { username: username }, relations: ["roles"] });
    }

    async findAllByUsername(username: string) {
        return this.usersRepository.find({ where: { username: Like(`%${username}%`) }, relations: ["roles"] });
    }

    async createUser(dto: CreateUserDto): Promise<User> {
        const insertId = (await this.usersRepository.insert({ ...dto })).raw.insertId;
        return this.findUserById(insertId);
    }

    async updateUser(id: string, dto: UpdateUserDto) {
        const existUser = await this.findByUserame(dto.username);
        if (existUser) {
            throw new ConflictException(Errors.NICKNAME_TAKEN)
        }

        await this.usersRepository.update({ id: id }, { ...dto })
        return this.findUserById(id);
    }

    async updatePhoto(id: string, img: Express.Multer.File) {
        const filename = await this.fileService.createFile(img);
        await this.usersRepository.update({ id: id }, { photo: filename });

        return this.findUserById(id);
    }

    async deleteUser(id: string) {
        await this.usersRepository.delete({ id: id });
    }
}
