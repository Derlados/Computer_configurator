import { ClassSerializerInterceptor, Controller, Get, Param, UseInterceptors } from '@nestjs/common';
import { ComponentsService } from './components.service';

@Controller('components')
export class ComponentsController {

    constructor(private componentsService: ComponentsService) { }

    @Get('/category=:category')
    @UseInterceptors(ClassSerializerInterceptor)
    getComponents(@Param('category') category: string) {
        return this.componentsService.getComponentsByCategoryUrl(category);
    }
}
