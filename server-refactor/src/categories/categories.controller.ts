import { ClassSerializerInterceptor, Controller, Get, Param, UseInterceptors } from '@nestjs/common';
import { CategoriesService } from './categories.service';

@Controller('categories')
export class CategoriesController {

    constructor(private categoriesService: CategoriesService) { }

    @Get()
    @UseInterceptors(ClassSerializerInterceptor)
    getAll() {
        return this.categoriesService.getAll();
    }

    @Get(':id([0-9]+)/filters')
    @UseInterceptors(ClassSerializerInterceptor)
    getFilters(@Param('id') id: number) {
        return this.categoriesService.getFilters(id);
    }
}
