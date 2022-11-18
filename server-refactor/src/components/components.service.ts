import { Injectable, InternalServerErrorException } from '@nestjs/common';
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

    async getComponentsById(id: number) {
        return this.componentRepository.findOne({
            where: { id: id },
            relations: ["category", "componentAttributes", "componentAttributes.attribute", "componentAttributes.value"]
        })
    }

    async getComponentsByCategoryId(categoryId: number) {
        return this.componentRepository.find({
            where: { categoryId: categoryId },
            relations: ["category", "componentAttributes", "componentAttributes.attribute", "componentAttributes.value"]
        })
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

        return this.getComponentsById(insertId);
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
            const [existAttribute, existValue] = [
                await this.attributeRepository.findOne({ name: attribute.name }),
                await this.valueRepository.findOne({ value: attribute.value })
            ];

            const [attributeId, valueId] = [
                existAttribute ? existAttribute.id : (await this.attributeRepository.insert({ name: attribute.name })).raw.insertId,
                existValue ? existValue.id : (await this.valueRepository.insert({ value: attribute.value })).raw.insertId
            ];


            inserts.push({ compoentId: componentId, attributeId: attributeId, valueId: valueId })
        }

        await this.componentAttributeRepository.insert(inserts)
    }

}
