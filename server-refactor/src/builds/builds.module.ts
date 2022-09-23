import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { CommentsModule } from 'src/comments/comments.module';
import { CommentsService } from 'src/comments/comments.service';
import { Comment } from 'src/comments/models/comment.model';
import { BuildsController } from './builds.controller';
import { BuildsService } from './builds.service';
import { BuildComponent } from './models/build-component.model';
import { Build } from './models/build.model';

@Module({
    imports: [TypeOrmModule.forFeature([Build, BuildComponent, Comment]), CommentsModule],
    controllers: [BuildsController],
    providers: [BuildsService]
})
export class BuildsModule { }
