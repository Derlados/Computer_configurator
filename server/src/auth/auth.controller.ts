import { Body, Controller, Post, Put } from '@nestjs/common';
import { ApiOperation, ApiResponse, ApiTags } from '@nestjs/swagger';
import { SignInUser } from '../users/dto/sign-in-user.dto';

import { AuthService } from './auth.service';
import { CheckAuthStatusDto } from './dto/check-auth-status.dto';

@ApiTags('auth')
@Controller('auth')
export class AuthController {

    constructor(private authService: AuthService) { }

    @Post('/status')
    async isAuthenticated(@Body() dto: CheckAuthStatusDto) {
        return this.authService.checkAuthStatus(dto);
    }

    @Post('/sign-in')
    async signIn(@Body() dto: SignInUser) {
        return this.authService.signIn(dto);
    }
}
