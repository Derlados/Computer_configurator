
import { ClassSerializerInterceptor, Controller, Param, Post, Put, Req, UseGuards, UseInterceptors } from '@nestjs/common';
import { FirebaseGuard } from 'src/auth/firebase-auth.guard';
import { JwtAuthGuard } from 'src/auth/jwt-auth.guard';
import { CommentsService } from './comments.service';


@Controller('comments')
export class CommentsController {

    constructor(private commentsService: CommentsService) { }

    @Post(':id/report')
    @UseGuards(FirebaseGuard)
    @UseInterceptors(ClassSerializerInterceptor)
    blockComment(@Param('id') id: number, @Req() req) {
        return this.commentsService.blockComment(req.user.id, id)
    }
}
