import { Body, ClassSerializerInterceptor, Controller, Get, Param, Post, Put, UseGuards, UseInterceptors } from '@nestjs/common';
import { JwtAuthGuard } from 'src/auth/jwt-auth.guard';
import { Roles } from 'src/roles/roles.decorator';
import { RoleValues } from 'src/roles/roles.enum';
import { RolesGuard } from 'src/roles/roles.guard';
import { ComponentsService } from './components.service';
import { ComponentDto } from './dto/component.dto';
import { CreateComponentDto } from './dto/create-component.dto';

@Controller('components')
export class ComponentsController {

    constructor(private componentsService: ComponentsService) { }

    @Get('/category=:category')
    @UseInterceptors(ClassSerializerInterceptor)
    getComponents(@Param('category') category: string) {
        return this.componentsService.getComponentsByCategoryUrl(category);
    }

    @Post()
    @Roles(RoleValues.ADMIN)
    @UseGuards(JwtAuthGuard, RolesGuard)
    createComponent(dto: CreateComponentDto) {
        this.componentsService.createComponent(dto);
    }


    @Put(':id')
    @Roles(RoleValues.ADMIN)
    @UseGuards(JwtAuthGuard, RolesGuard)
    updateComponent(@Param('id') id: number, @Body() dto: ComponentDto) {
        this.componentsService.updateComponent(id, dto);
    }
}
