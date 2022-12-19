import { Module } from '@nestjs/common';
import { CommentsService } from './comments.service';
import { CommentsController } from './comments.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Comment } from './models/comment.model';
import { ReportedComment } from './models/reported-comment.model';

@Module({
  imports: [TypeOrmModule.forFeature([Comment, ReportedComment])],
  providers: [CommentsService],
  controllers: [CommentsController],
  exports: [CommentsService]
})
export class CommentsModule { }
