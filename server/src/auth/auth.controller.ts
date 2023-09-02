import { Body, Controller, Post, Put } from '@nestjs/common';
import { ApiOperation, ApiResponse, ApiTags } from '@nestjs/swagger';
import { SignInUser } from '../users/dto/sign-in-user.dto';

import { AuthService } from './auth.service';

@ApiTags('auth')
@Controller('auth')
export class AuthController {

    constructor(private authService: AuthService) { }

    @ApiOperation({ summary: "Логин пользователя" })
    @ApiResponse({ status: 200, type: String, description: "Токен пользователя" })
    @Post('/sign-in')
    async signIn(@Body() dto: SignInUser) {
        return this.authService.signIn(dto);
    }
}
