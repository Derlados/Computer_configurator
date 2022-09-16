import { Controller, Get, Param } from '@nestjs/common';
import { ComponentsService } from './components.service';
import { Category } from './constants/categories';

@Controller('components')
export class ComponentsController {

    constructor(private componentsService: ComponentsService) { }

    @Get('/category=:category')
    getComponents(@Param('category') category: string) {

    }
}
