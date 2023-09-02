import { Body, ClassSerializerInterceptor, Controller, Delete, ForbiddenException, Get, Param, Patch, Post, Put, Req, SerializeOptions, UploadedFile, UseGuards, UseInterceptors } from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { AccessGroups } from 'src/constants/AccessGroups';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { UpdateUserDto } from './dto/update-user.dto';
import { UsersService } from './users.service';

@Controller('users')
export class UsersController {

    constructor(private usersService: UsersService) { }


    @Get(':id([0-9]+)')
    @SerializeOptions({ groups: [AccessGroups.ALL_USERS] })
    @UseInterceptors(ClassSerializerInterceptor)
    getPublicUserInfo(@Param('id') id: number) {
        return this.usersService.findUserById(id);
    }

    @Get('personal')
    @UseGuards(JwtAuthGuard)
    @SerializeOptions({ groups: [AccessGroups.USER_OWNER] })
    @UseInterceptors(ClassSerializerInterceptor)
    getPrivateUserInfo(@Req() req) {
        return this.usersService.findUserById(req.user.id);
    }

    @Put('personal')
    @UseGuards(JwtAuthGuard)
    @UseInterceptors(ClassSerializerInterceptor)
    @SerializeOptions({ groups: [AccessGroups.USER_OWNER] })
    update(@Req() req, @Body() dto: UpdateUserDto) {
        return this.usersService.updateUser(req.user.id, dto);
    }

    @Put('personal/photo')
    @UseGuards(JwtAuthGuard)
    @UseInterceptors(FileInterceptor('image'), ClassSerializerInterceptor)
    @SerializeOptions({ groups: [AccessGroups.USER_OWNER] })
    updatePhoto(@Req() req, @UploadedFile() img: Express.Multer.File) {
        return this.usersService.updatePhoto(req.user.id, img);
    }

    @Delete('personal')
    @UseGuards(JwtAuthGuard)
    deleteUser(@Req() req) {
        return this.usersService.deleteUser(req.user.id);
    }
}
