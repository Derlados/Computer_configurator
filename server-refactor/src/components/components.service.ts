import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { QueryDeepPartialEntity } from 'typeorm/query-builder/QueryPartialEntity';
import { ComponentDto } from './dto/component.dto';
import { CreateAttributeDto } from './dto/create-attribute.dto';
import { CreateComponentDto } from './dto/create-component.dto';
import { Attribute } from './models/attribute.model';
import { ComponentAttribute } from './models/component-attribute.model';
import { Component } from './models/component.model';
import { Value } from './models/value.model';

@Injectable()
export class ComponentsService {
    constructor(@InjectRepository(Component) private componentRepository: Repository<Component>,
        @InjectRepository(Attribute) private attributeRepository: Repository<Attribute>,
        @InjectRepository(Value) private valueRepository: Repository<Value>,
        @InjectRepository(ComponentAttribute) private componentAttributeRepository: Repository<ComponentAttribute>) {

    }

    async getComponentsByCategoryUrl(categoryUrl: string) {
        return this.componentRepository.find({
            where: { category: { url: categoryUrl } },
            relations: ["category", "componentAttributes", "componentAttributes.attribute", "componentAttributes.value"]
        })
    }

    async createComponent(dto: CreateComponentDto) {
        const { attributes, ...component } = dto;

        const result = await this.componentRepository.insert({ ...component });
        const insertId = result.raw.insertId;
        await this.addAttributes(insertId, attributes);
    }

    /**
     * Updating of component is done without updating of it's attributes
     * @param id - component id
     * @param dto - dto without attributes
     */
    async updateComponent(id: number, dto: ComponentDto) {
        await this.componentRepository.update({ id: id }, { ...dto });
    }

    private async addAttributes(componentId: number, attributes: CreateAttributeDto[]) {
        const inserts: QueryDeepPartialEntity<ComponentAttribute>[] = []

        for (const attribute of attributes) {
            const attributeProm = this.attributeRepository.save({ name: attribute.name });
            const valueProm = this.valueRepository.save({ value: attribute.value });

            const [savedAttribute, savedValue] = [await attributeProm, await valueProm];
            inserts.push({ compoentId: componentId, attributeId: savedAttribute.id, valueId: savedValue.id })
        }

        await this.componentAttributeRepository.insert(inserts)
    }

}
