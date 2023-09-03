import { Body, ClassSerializerInterceptor, Controller, Delete, ForbiddenException, Get, Param, Patch, Post, Put, Req, SerializeOptions, UploadedFile, UseGuards, UseInterceptors } from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { FirebaseGuard } from 'src/auth/firebase-auth.guard';
import { AccessGroups } from 'src/constants/AccessGroups';
import { UpdateUserDto } from './dto/update-user.dto';
import { UsersService } from './users.service';

@Controller('users')
export class UsersController {

    constructor(private usersService: UsersService) { }

    @Get(':id([0-9]+)')
    @SerializeOptions({ groups: [AccessGroups.ALL_USERS] })
    @UseInterceptors(ClassSerializerInterceptor)
    getPublicUserInfo(@Param('id') id: string) {
        return this.usersService.findUserById(id);
    }

    @Get('personal')
    @UseGuards(FirebaseGuard)
    @SerializeOptions({ groups: [AccessGroups.USER_OWNER] })
    @UseInterceptors(ClassSerializerInterceptor)
    getPrivateUserInfo(@Req() req) {
        return this.usersService.findUserById(req.user.id);
    }

    @Put('personal')
    @UseGuards(FirebaseGuard)
    @UseInterceptors(ClassSerializerInterceptor)
    @SerializeOptions({ groups: [AccessGroups.USER_OWNER] })
    update(@Req() req, @Body() dto: UpdateUserDto) {
        return this.usersService.updateUser(req.user.id, dto);
    }

    @Put('personal/photo')
    @UseGuards(FirebaseGuard)
    @UseInterceptors(FileInterceptor('image'), ClassSerializerInterceptor)
    @SerializeOptions({ groups: [AccessGroups.USER_OWNER] })
    updatePhoto(@Req() req, @UploadedFile() img: Express.Multer.File) {
        return this.usersService.updatePhoto(req.user.id, img);
    }

    @Delete('personal')
    @UseGuards(FirebaseGuard)
    deleteUser(@Req() req) {
        return this.usersService.deleteUser(req.user.id);
    }
}
