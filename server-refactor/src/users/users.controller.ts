import { Body, ClassSerializerInterceptor, Controller, Delete, ForbiddenException, Get, Param, Post, Put, Req, SerializeOptions, UploadedFile, UseGuards, UseInterceptors } from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { AccessGroups } from 'src/constants/AccessGroups';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { GoogleSignInDto } from './dto/google-sign-in-dto';
import { UpdatePasswordDto } from './dto/update-password.dto';
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
    @UseInterceptors(FileInterceptor('img'), ClassSerializerInterceptor)
    update(@Req() req, @Body() dto: UpdateUserDto, @UploadedFile('img') img?: Express.Multer.File) {
        return this.usersService.updateUser(req.user.id, dto);
    }

    @Put('personal/google-sign')
    @UseGuards(JwtAuthGuard)
    @UseInterceptors(ClassSerializerInterceptor)
    addGoogleAcc(@Req() req, @Body() dto: GoogleSignInDto) {
        return this.usersService.addGoogleAccout(req.user.id, dto);
    }

    @Put('personal/restore')
    @UseGuards(JwtAuthGuard)
    @UseInterceptors(ClassSerializerInterceptor)
    restorePassword(@Req() req, @Body() dto: UpdatePasswordDto) {
        return this.usersService.updatePassword(req.user.id, dto)
    }

    @Delete('personal')
    @UseGuards(JwtAuthGuard)
    deleteUser(@Req() req) {
        return this.usersService.deleteUser(req.user.id);
    }
}
