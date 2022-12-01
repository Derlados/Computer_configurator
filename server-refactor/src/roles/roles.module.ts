import { Module } from '@nestjs/common';
import { RolesService } from './roles.service';
import { RolesController } from './roles.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Role } from './models/role.model';

@Module({
    imports: [TypeOrmModule.forFeature([Role])],
    providers: [RolesService],
    controllers: [RolesController]
})
export class RolesModule { }