import { BadRequestException, Body, Controller, Get, Post, Put } from '@nestjs/common';
import { ApiOperation, ApiResponse, ApiTags } from '@nestjs/swagger';
import { CreateUserDto } from '../users/dto/create-user.dto';
import { GoogleSignInDto } from '../users/dto/google-sign-in-dto';
import { LoginUserDto } from '../users/dto/login-user.dto';
import { RestorePassDto } from '../users/dto/restore-pass.dto';

import { AuthService } from './auth.service';

@ApiTags('auth')
@Controller('auth')
export class AuthController {

    constructor(private authService: AuthService) { }

    @ApiOperation({ summary: "Логин пользователя" })
    @ApiResponse({ status: 200, type: String, description: "Токен пользователя" })
    @Post('/login')
    async login(@Body() dto: LoginUserDto) {
        return this.authService.login(dto);
    }

    @ApiOperation({ summary: "Регистрация пользователя" })
    @ApiResponse({ status: 200, type: String, description: "Токен пользователя" })
    @Post('/reg')
    async reg(@Body() dto: CreateUserDto) {
        return this.authService.register(dto);
    }

    @ApiOperation({ summary: "Регистрация через google" })
    @ApiResponse({ status: 200, type: String, description: "Токен пользователя" })
    @Post('/google-sign-in')
    async googleSingIn(@Body() dto: GoogleSignInDto) {
        return this.authService.googleSignIn(dto);
    }
}
