import { Body, ClassSerializerInterceptor, Controller, Delete, ForbiddenException, Get, Param, Post, Put, Req, SerializeOptions, UploadedFile, UseGuards, UseInterceptors } from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { AccessGroups } from 'src/constants/AccessGroups';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { GoogleSignInDto } from './dto/google-sign-in-dto';
import { UpdatePasswordDto } from './dto/update-password.dto';
import { UpdateUserDto } from './dto/update-user.dto';
import { ChekAccessInterceptor } from './interceptors/ChekAccess.interceptor';
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

    @Get(':id([0-9]+)/private')
    @UseGuards(JwtAuthGuard)
    @SerializeOptions({ groups: [AccessGroups.USER_OWNER] })
    @UseInterceptors(ClassSerializerInterceptor, ChekAccessInterceptor)
    getPrivateUserInfo(@Param('id') id: number, @Req() req) {
        return this.usersService.findUserById(id);
    }

    @Put('id([0-9]+)/google-sign')
    @UseGuards(JwtAuthGuard)
    @UseInterceptors(ClassSerializerInterceptor, ChekAccessInterceptor)
    addGoogleAcc(@Param('id') id: number, @Req() req, @Body() dto: GoogleSignInDto) {
        return this.usersService.addGoogleAccout(id, dto);
    }

    @Put(':id([0-9]+)/update')
    @UseGuards(JwtAuthGuard)
    @UseInterceptors(FileInterceptor('img'), ClassSerializerInterceptor, ChekAccessInterceptor)
    update(@Param('id') id: number, @Req() req, @Body() dto: UpdateUserDto, @UploadedFile('img') img?: Express.Multer.File) {
        return this.usersService.updateUser(id, dto);
    }

    @Put(':id([0-9]+)/password')
    @UseGuards(JwtAuthGuard)
    @UseInterceptors(ClassSerializerInterceptor, ChekAccessInterceptor)
    restorePassword(@Param('id') id: number, @Req() req, @Body() dto: UpdatePasswordDto) {
        return this.usersService.updatePassword(id, dto)
    }

    @Delete(':id')
    @UseGuards(JwtAuthGuard)
    @UseInterceptors(ChekAccessInterceptor)
    deleteUser(@Param('id') id: number, @Req() req) {
        return this.usersService.deleteUser(id);
    }
}
