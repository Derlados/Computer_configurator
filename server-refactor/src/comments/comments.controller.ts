import { Controller, Param, Post } from '@nestjs/common';

@Controller('comments')
export class CommentsController {
    @Post()
    createComment() {

    }

    @Post(':parentId([0-9]+)')
    answerComment(@Param('parentId') parentId: number) {

    }
}
