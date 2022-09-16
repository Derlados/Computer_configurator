import { Body, ClassSerializerInterceptor, Controller, Delete, Get, Param, Post, Put, Req, SerializeOptions, UseGuards, UseInterceptors } from '@nestjs/common';
import { JwtAuthGuard } from 'src/auth/jwt-auth.guard';
import { AccessGroups } from 'src/constants/AccessGroups';
import { BuildsService } from './builds.service';
import { CreateBuildDto } from './dto/create-build.dto';
import { UpdatePublishStatusDto } from './dto/update-publish-status.dto';

@Controller('builds')
export class BuildsController {
    constructor(private buildsService: BuildsService) { }

    @Get('public')
    @SerializeOptions({ groups: [AccessGroups.ALL_USERS] })
    @UseInterceptors(ClassSerializerInterceptor)
    getPublicBuilds() {
        return this.buildsService.getPublicBuilds();
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
    @SerializeOptions({ groups: [AccessGroups.ALL_USERS] })
    @UseInterceptors(ClassSerializerInterceptor)
    createBuild(@Req() req, @Body() dto: CreateBuildDto) {
        return this.buildsService.createBuild(req.user.id, dto);
    }

    @Put(':id([0-9]+)')
    @UseGuards(JwtAuthGuard)
    @SerializeOptions({ groups: [AccessGroups.ALL_USERS] })
    @UseInterceptors(ClassSerializerInterceptor)
    updateBuild(@Req() req, @Param('id') id: number, @Body() dto: CreateBuildDto) {
        return this.buildsService.updateBuild(id, req.user.id, dto);
    }

    @Put(':id([0-9]+)/status')
    @UseGuards(JwtAuthGuard)
    @UseInterceptors(ClassSerializerInterceptor)
    chengeStatus(@Req() req, @Param('id') id: number, @Body() dto: UpdatePublishStatusDto) {
        return this.buildsService.chengeStatus(id, req.user.id, dto);
    }

    @Delete(':id([0-9]+)')
    @UseGuards(JwtAuthGuard)
    deleteBuild(@Req() req, @Param('id') id: number) {
        return this.buildsService.deleteBuild(id, req.user.id)
    }
} 
