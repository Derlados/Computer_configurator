import { Body, ClassSerializerInterceptor, Controller, Get, NotFoundException, Param, Post, Put, UseGuards, UseInterceptors } from '@nestjs/common';
import { FirebaseGuard } from 'src/auth/firebase-auth.guard';
import { JwtAuthGuard } from 'src/auth/jwt-auth.guard';
import { Roles } from 'src/roles/roles.decorator';
import { RoleValues } from 'src/roles/roles.enum';
import { RolesGuard } from 'src/roles/roles.guard';
import { QueryFailedError } from 'typeorm';
import { ComponentsService } from './components.service';
import { ComponentDto } from './dto/component.dto';
import { CreateComponentDto } from './dto/create-component.dto';

@Controller('components')
export class ComponentsController {

    constructor(private componentsService: ComponentsService) { }

    @Get('/category/:id')
    @UseInterceptors(ClassSerializerInterceptor)
    getComponentsByCategoryId(@Param('id') id: number) {
        return this.componentsService.getComponentsByCategoryId(id);
    }

    @Get('/category=:category')
    @UseInterceptors(ClassSerializerInterceptor)
    getComponentsByCategoryName(@Param('category') category: string) {
        return this.componentsService.getComponentsByCategoryUrl(category);
    }

    @Post()
    @Roles(RoleValues.ADMIN)
    @UseGuards(FirebaseGuard, RolesGuard)
    @UseInterceptors(ClassSerializerInterceptor)
    createComponent(@Body() dto: CreateComponentDto) {
        return this.componentsService.createComponent(dto);
    }

    @Put(':id')
    @Roles(RoleValues.ADMIN)
    @UseGuards(FirebaseGuard, RolesGuard)
    @UseInterceptors(ClassSerializerInterceptor)
    updateComponent(@Param('id') id: number, @Body() dto: ComponentDto) {
        return this.componentsService.updateComponent(id, dto);
    }
}
