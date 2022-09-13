import { Body, Controller, Get, Put, Req, UploadedFile, UseGuards, UseInterceptors } from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { GoogleSignInDto } from './dto/google-sign-in-dto';
import { updateUserDto } from './dto/update-user.dto';
import { UsersService } from './users.service';

@Controller('users')
export class UsersController {

    constructor(private usersService: UsersService) { }

    @Put(['/:id/google-sign', '/google-sign'])
    @UseGuards(JwtAuthGuard)
    addGoogleAcc(@Body() dto: GoogleSignInDto, @Req() req) {
        return this.usersService.addGoogleAcc(req.user.id, dto);
    }

    //TODO Сейчас через этот роут проходит даже обновление изображения
    @Put(['/:id/update', '/update'])
    @UseGuards(JwtAuthGuard)
    @UseInterceptors(FileInterceptor('img'))
    update(@Body() dto: updateUserDto, @Req() req, @UploadedFile('img') img?: Express.Multer.File) {
        return this.usersService.updateData(req.user.id, dto);
    }
}
