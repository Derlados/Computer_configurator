import { Body, ClassSerializerInterceptor, Controller, Delete, Get, Param, Post, Put, Req, SerializeOptions, UseGuards, UseInterceptors } from '@nestjs/common';
import { JwtAuthGuard } from 'src/auth/jwt-auth.guard';
import { CommentsService } from 'src/comments/comments.service';
import { CreateCommentDto } from 'src/comments/dto/create-comment.dto';
import { AccessGroups } from 'src/constants/AccessGroups';
import { BuildsService } from './builds.service';
import { CreateBuildDto } from './dto/create-build.dto';
import { UpdatePublishStatusDto } from './dto/update-publish-status.dto';

@Controller('builds')
export class BuildsController {
    constructor(private buildsService: BuildsService, private commentsService: CommentsService) { }

    @Get()
    @SerializeOptions({ groups: [AccessGroups.ALL_USERS] })
    @UseInterceptors(ClassSerializerInterceptor)
    getPublicBuilds() {
        return this.buildsService.getPublicBuilds();
    }

    @Get('personal')
    @UseGuards(JwtAuthGuard)
    @SerializeOptions({ groups: [AccessGroups.USER_OWNER] })
    @UseInterceptors(ClassSerializerInterceptor)
    getPersonalBuilds(@Req() req) {
        return this.buildsService.getBuldsByUserId(req.user.id);
    }

    @Get(':id([0-9]+)')
    @SerializeOptions({ groups: [AccessGroups.ALL_USERS] })
    @UseInterceptors(ClassSerializerInterceptor)
    getBuildById(@Param('id') id: number) {
        return this.buildsService.getBuildByid(id);
    }


    @Get(':id([0-9]+)/comments')
    @SerializeOptions({ groups: [AccessGroups.ALL_USERS] })
    @UseInterceptors(ClassSerializerInterceptor)
    getBuildComments(@Param('id') id: number) {
        return this.buildsService.getBuildComments(id);
    }

    @Post()
    @UseGuards(JwtAuthGuard)
    @SerializeOptions({ groups: [AccessGroups.USER_OWNER] })
    @UseInterceptors(ClassSerializerInterceptor)
    createBuild(@Req() req, @Body() dto: CreateBuildDto) {
        return this.buildsService.createBuild(req.user.id, dto);
    }

    @Post(':id/report')
    @UseGuards(JwtAuthGuard)
    @UseInterceptors(ClassSerializerInterceptor)
    reportBuild(@Req() req, @Param('id') id: number) {
        return this.buildsService.reportBuild(req.user.id, id);
    }

    @Post(':id/comments')
    @UseGuards(JwtAuthGuard)
    @SerializeOptions({ groups: [AccessGroups.USER_OWNER] })
    @UseInterceptors(ClassSerializerInterceptor)
    createComment(@Param('id') id: number, @Req() req, @Body() dto: CreateCommentDto) {
        return this.commentsService.createComment(id, req.user.id, dto);
    }

    @Post(':id/comments/:parentId/answer')
    @UseGuards(JwtAuthGuard)
    @SerializeOptions({ groups: [AccessGroups.ALL_USERS] })
    @UseInterceptors(ClassSerializerInterceptor)
    answerComment(@Param('parentId') parentId: number, @Req() req, @Body() dto: CreateCommentDto) {
        return this.commentsService.answerComment(parentId, req.user.id, dto);
    }

    @Put(':id([0-9]+)')
    @UseGuards(JwtAuthGuard)
    @SerializeOptions({ groups: [AccessGroups.USER_OWNER] })
    @UseInterceptors(ClassSerializerInterceptor)
    updateBuild(@Req() req, @Param('id') id: number, @Body() dto: CreateBuildDto) {
        return this.buildsService.updateBuild(id, req.user.id, dto);
    }

    @Put(':id([0-9]+)/status')
    @UseGuards(JwtAuthGuard)
    @SerializeOptions({ groups: [AccessGroups.USER_OWNER] })
    @UseInterceptors(ClassSerializerInterceptor)
    chengeStatus(@Req() req, @Param('id') id: number, @Body() dto: UpdatePublishStatusDto) {
        return this.buildsService.chengeStatus(id, req.user.id, dto);
    }

    @Delete(':id([0-9]+)')
    @UseGuards(JwtAuthGuard)
    @SerializeOptions({ groups: [AccessGroups.USER_OWNER] })
    deleteBuild(@Req() req, @Param('id') id: number) {
        return this.buildsService.deleteBuild(id, req.user.id)
    }
} 
