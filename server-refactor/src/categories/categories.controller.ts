import { Controller, Get, Param } from '@nestjs/common';

@Controller('categories')
export class CategoriesController {

    @Get()
    getAll() {

    }

    @Get(':id([0-9]+)/filters')
    getFilters(@Param('id') id: number) {

    }
}
