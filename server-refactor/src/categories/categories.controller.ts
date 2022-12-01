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

    @Get(':category/filters')
    @UseInterceptors(ClassSerializerInterceptor)
    getFilters(@Param('category') categoryUrl: string) {
        return this.categoriesService.getFilters(categoryUrl);
    }
}
