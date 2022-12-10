import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { ComponentAttribute } from 'src/components/models/component-attribute.model';
import { Repository } from 'typeorm';
import { Category } from './models/category.model';
import { Filter } from './models/filter.model';
import { FilterAttribute } from './types/FilterAttribute';

@Injectable()
export class CategoriesService {

    constructor(@InjectRepository(Category) private categoryRepository: Repository<Category>,
        @InjectRepository(Filter) private filtersRepository: Repository<Filter>,
        @InjectRepository(ComponentAttribute) private componentAttributesRepository: Repository<ComponentAttribute>,
    ) { }

    async getAll() {
        return this.categoryRepository.find();
    }

    /**
     * Получение фильтров в категории, массив значений для фильтров дополнительно фильтруется, 
     * чтобы не было значений из других категорий.
     * @param categoryUrl 
     * @returns 
     */
    //TODO сортировку перенести в клиент (функция с прошлой версии)
    async getFilters(categoryUrl: string): Promise<Map<number, FilterAttribute>> {
        const filters = await this.filtersRepository.find({
            where: { category: { url: categoryUrl } },
            relations: ["category", "attribute", "attribute.componentAttributes", "attribute.componentAttributes.value"]
        });

        const filterValues = (await this.componentAttributesRepository.find({
            where: { component: { category: { url: categoryUrl } } },
            relations: ["component", "component.category", "value"]
        })).map(v => v.value.value)

        const mapFilters = new Map<number, FilterAttribute>();

        for (const filter of filters) {
            const { id, name } = filter.attribute;
            const { isRange, step } = filter;

            //TODO в базе данных присутствуют грязные данные
            const uniqueValues = new Set(filter.attribute.allValues.map(v => v.value.replace(/^\s+/g, '')).filter(v => filterValues.includes(v)))

            if (!isRange) {
                const sortFilters = (a: string, b: string) => {
                    if (!isNaN(parseFloat(a))) {
                        return this.sortNumbers(a, b);
                    } else {
                        return this.sortStrings(a, b);
                    }
                }

                mapFilters.set(id, {
                    name,
                    isRange,
                    step,
                    values: Array.from(uniqueValues).sort(sortFilters)
                });
            } else {
                const floatValues = Array.from(uniqueValues).map(v => parseFloat(v))

                const min = Math.min(...floatValues).toString()
                const max = Math.max(...floatValues).toString()

                mapFilters.set(id, {
                    name,
                    isRange,
                    step,
                    values: [min, max]
                });
            }

        }

        return mapFilters;
    }

    //TODO сортировку перенести в клиент
    private sortStrings(a: string, b: string) {
        if (a < b) {
            return -1;
        }

        if (a > b) {
            return 1;
        }

        return 0;
    }

    //TODO сортировку перенести в клиент
    private sortNumbers(a: string, b: string) {
        return parseFloat(a) - parseFloat(b);
    }

}
