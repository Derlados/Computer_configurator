import { Module } from '@nestjs/common';
import { BuildsController } from './builds.controller';
import { BuildsService } from './builds.service';

@Module({
  controllers: [BuildsController],
  providers: [BuildsService]
})
export class BuildsModule { }
