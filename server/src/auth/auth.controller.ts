import { Body, Controller, Post, Put } from '@nestjs/common';
import { ApiOperation, ApiResponse, ApiTags } from '@nestjs/swagger';
import { ChangePasswordDto } from 'src/users/dto/change-password.dto';
import { CreateUserDto } from '../users/dto/create-user.dto';
import { GoogleSignInDto } from '../users/dto/google-sign-in-dto';
import { LoginUserDto } from '../users/dto/login-user.dto';

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

    @Put('/restore')
    async changePassword(@Body() dto: ChangePasswordDto) {
        return this.authService.changePassword(dto);
    }
}
