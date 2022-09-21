import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Component } from './models/component.model';

@Injectable()
export class ComponentsService {
    constructor(@InjectRepository(Component) private componentRepository: Repository<Component>) { }

    async getComponentsByCategoryUrl(categoryUrl: string) {
        return this.componentRepository.find({
            where: { category: { url: categoryUrl } },
            relations: ["category", "attributes", "attributes.attribute", "attributes.value"]
        })
    }
}
