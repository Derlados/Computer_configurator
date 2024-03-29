import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Comment } from 'src/comments/models/comment.model';
import { Repository } from 'typeorm';
import { threadId } from 'worker_threads';
import { CreateBuildDto } from './dto/create-build.dto';
import { UpdatePublishStatusDto } from './dto/update-publish-status.dto';
import { BuildComponent } from './models/build-component.model';
import { Build } from './models/build.model';

@Injectable()
export class BuildsService {

    constructor(@InjectRepository(Build) private buildsRepository: Repository<Build>,
        @InjectRepository(Comment) private commentsRepository: Repository<Comment>,
        @InjectRepository(BuildComponent) private buildComponentsRepository: Repository<BuildComponent>,
    ) {

    }

    async getPublicBuilds() {
        return await this.buildsRepository.find({ relations: ["user", "buildComponents", "buildComponents.component", "buildComponents.component.category"] });
    }

    async getBuldsByUserId(userId: string) {
        return this.buildsRepository.find({
            where: { userId: userId },
            relations: [
                "user",
                "buildComponents",
                "buildComponents.component",
                "buildComponents.component.componentAttributes",
                "buildComponents.component.componentAttributes.attribute",
                "buildComponents.component.componentAttributes.value",
                "buildComponents.component.category"
            ]
        });
    }

    async getBuildByid(buildId: number) {
        return await this.buildsRepository.findOne({
            where: { id: buildId },
            relations: [
                "user",
                "buildComponents",
                "buildComponents.component",
                "buildComponents.component.componentAttributes",
                "buildComponents.component.componentAttributes.attribute",
                "buildComponents.component.componentAttributes.value",
                "buildComponents.component.category"
            ]
        });
    }

    async getBuildComments(buildId: number) {
        return await this.commentsRepository.find({ where: { buildId: buildId }, relations: ["user"] });
    }

    async createBuild(userId: string, dto: CreateBuildDto) {
        const { components: buildComponents, ...buildData } = dto;
        const buildId: number = (await this.buildsRepository.insert({ userId: userId, ...buildData })).raw.insertId;

        const buildComponentsValues = buildComponents.map(bc => { return { buildId: buildId, ...bc } });
        await this.buildComponentsRepository.insert(buildComponentsValues);

        return this.getBuildByid(buildId);
    }

    async reportBuild(userId: string, buildId: number) {
        const build = await this.buildsRepository.findOne({ id: buildId });
        if (build) {
            await this.buildsRepository.update({ id: build.id }, { reports: build.reports + 1 })
        }
    }

    async updateBuild(buildId: number, userId: string, dto: CreateBuildDto) {
        const { components: buildComponents, ...buildData } = dto;
        const res = await this.buildsRepository.update({ id: buildId, userId: userId }, { ...buildData });
        if (res.affected === 0) {
            throw new NotFoundException("Build not found");
        }

        // Замена путем удаление старых и добавления новых компонент
        const buildComponentsValues = buildComponents.map(bc => { return { buildId: buildId, ...bc } });
        await this.clearBuildComponents(buildId);
        await this.buildComponentsRepository.insert(buildComponentsValues);

        return this.getBuildByid(buildId);
    }

    async chengeStatus(buildId: number, userId: string, dto: UpdatePublishStatusDto) {
        await this.buildsRepository.update({ id: buildId, userId: userId }, { ...dto });
    }

    async deleteBuild(buildId: number, userId: string) {
        const res = await this.buildsRepository.delete({ id: buildId, userId: userId });
        if (res.affected === 0) {
            throw new NotFoundException("Build not found");
        }
    }

    private async clearBuildComponents(buildId: number) {
        await this.buildComponentsRepository.delete({ buildId: buildId })
    }
}
