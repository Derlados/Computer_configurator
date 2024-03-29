import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { User } from 'src/users/models/user.model';
import { Repository } from 'typeorm';
import { CreateCommentDto } from './dto/create-comment.dto';
import { ReportedComment } from './models/reported-comment.model';
import { Comment } from './models/comment.model';

@Injectable()
export class CommentsService {

    constructor(@InjectRepository(Comment) private commentsRepository: Repository<Comment>,
        @InjectRepository(ReportedComment) private blockedCommentRepository: Repository<ReportedComment>) { }

    async getById(id: number) {
        return this.commentsRepository.findOne({ where: { id: id }, relations: ["user"] });
    }

    async createComment(buildId: number, userId: string, dto: CreateCommentDto) {
        const insertId = (await this.commentsRepository.insert({ userId: userId, buildId: buildId, ...dto })).raw.insertId;
        return this.getById(insertId);
    }

    async answerComment(parentId: number, userId: string, dto: CreateCommentDto) {
        const comment = await this.commentsRepository.findOne({ id: parentId })
        if (!comment) {
            throw new NotFoundException()
        }

        const insertId = (await this.commentsRepository.insert({ parentId: parentId, userId: userId, buildId: comment.buildId, ...dto })).raw.insertId;
        return this.getById(insertId);
    }

    async blockComment(userId: string, commentId: number) {
        const blockedComment = await this.commentsRepository.findOne({ id: commentId });
        if (!blockedComment) {
            return new NotFoundException();
        }

        await this.blockedCommentRepository.save({ userId, commentId });
        return blockedComment;
    }
}
