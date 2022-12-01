import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Category } from './models/category.model';
import { Filter } from './models/filter.model';

@Injectable()
export class CategoriesService {

    constructor(@InjectRepository(Category) private categoryRepository: Repository<Category>,
        @InjectRepository(Filter) private filtersRepository: Repository<Filter>,
    ) { }

    async getAll() {
        return this.categoryRepository.find();
    }

    //TODO
    async getFilters(categoryId: number) {
        return this.filtersRepository.find({
            where: { categoryId: categoryId },
            relations: ["attribute", "attribute.componentAttributes", "attribute.componentAttributes.value", "attribute.componentAttributes.component"]
        });
    }
}
