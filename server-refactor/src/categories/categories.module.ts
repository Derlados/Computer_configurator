import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ComponentAttribute } from 'src/components/models/component-attribute.model';
import { CategoriesController } from './categories.controller';
import { CategoriesService } from './categories.service';
import { Category } from './models/category.model';
import { Filter } from './models/filter.model';

@Module({
    imports: [TypeOrmModule.forFeature([Category, Filter, ComponentAttribute])],
    controllers: [CategoriesController],
    providers: [CategoriesService]
})
export class CategoriesModule { }
