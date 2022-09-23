import { Body, ClassSerializerInterceptor, Controller, Param, Post, Req, SerializeOptions, UseGuards, UseInterceptors } from '@nestjs/common';
import { JwtAuthGuard } from 'src/auth/jwt-auth.guard';
import { AccessGroups } from 'src/constants/AccessGroups';
import { CommentsService } from './comments.service';
import { CreateCommentDto } from './dto/create-comment.dto';

@Controller('comments')
export class CommentsController {

    constructor(private commentsService: CommentsService) { }

    @Post(':id/answer')
    @UseGuards(JwtAuthGuard)
    @SerializeOptions({ groups: [AccessGroups.ALL_USERS] })
    @UseInterceptors(ClassSerializerInterceptor)
    answerComment(@Param('id') id: number, @Req() req, @Body() dto: CreateCommentDto) {
        return this.commentsService.answerComment(id, req.user.id, dto);
    }
}
