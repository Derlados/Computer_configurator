import { ConflictException, Injectable, InternalServerErrorException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm/dist/common/typeorm.decorators';
import { Like, Repository } from 'typeorm';
import { CreateUserDto } from './dto/create-user.dto';
import { GoogleSignInDto } from './dto/google-sign-in-dto';
import { updateUserDto } from './dto/update-user.dto';
import { User } from './models/user.model';

@Injectable()
export class UsersService {
    constructor(@InjectRepository(User) private usersRepository: Repository<User>) { }

    async createUser(dto: CreateUserDto | GoogleSignInDto) {
        const user = this.usersRepository.create({ ...dto });
        return this.usersRepository.save(user);
    }

    async addGoogleAcc(id: number, dto: GoogleSignInDto) {
        return await this.usersRepository.update({ id: id }, { ...dto });
    }

    async updatePassword(id: number, newPassword: string) {
        return this.usersRepository.update({ id: id }, { password: newPassword });
    }

    async updateData(id: number, dto: updateUserDto) {
        try {
            await this.usersRepository.update({ id: id }, { ...dto })
        } catch (e) {
            if (e.code == 'ER_DUP_ENTRY') {
                throw new ConflictException()
            } else {
                throw new InternalServerErrorException()
            }
        }
    }

    //TODO
    saveImage() {

    }

    findUserByGoogleId(googleId: string) {
        return this.usersRepository.findOne({ googleId: googleId });
    }

    findUserByUsername(username: string) {
        return this.usersRepository.findOne({ username: username });
    }

    async findAllUsersByUsername(username: string) {
        return this.usersRepository.find({ username: Like(`%${username}%`) });
    }
}
