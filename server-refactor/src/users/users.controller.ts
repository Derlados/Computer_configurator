import { Body, Controller, Delete, Get, Param, Post, Put, Req, UploadedFile, UseGuards, UseInterceptors } from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { GoogleSignInDto } from './dto/google-sign-in-dto';
import { RestorePassDto } from './dto/restore-pass.dto';
import { UpdateUserDto } from './dto/update-user.dto';
import { UsersService } from './users.service';

@Controller('users')
export class UsersController {

    constructor(private usersService: UsersService) { }

    @Get(':id([0-9]+)')
    getPublicUserInfo(@Param('id') id: number) {

    }

    @Get(':id([0-9]+)/private')
    @UseGuards(JwtAuthGuard)
    getPrivateUserInfo(@Param('id') id: number) {

    }

    @Put('id([0-9]+)/google-sign')
    @UseGuards(JwtAuthGuard)
    addGoogleAcc(@Body() dto: GoogleSignInDto, @Req() req) {

    }

    @Put(':id([0-9]+)/update')
    @UseGuards(JwtAuthGuard)
    @UseInterceptors(FileInterceptor('img'))
    update(@Param('id') id: number, @Body() dto: UpdateUserDto, @UploadedFile('img') img?: Express.Multer.File) {

    }

    @Put('restore-pass')
    @UseGuards(JwtAuthGuard)
    restorePassword(@Body() dto: RestorePassDto) {

    }

    @Delete(':id')
    @UseGuards(JwtAuthGuard)
    deleteUser(@Param('id') id: number) {

    }
}
