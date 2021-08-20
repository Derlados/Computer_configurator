import { RowDataPacket } from "mysql2";
import { Pool } from "mysql2/promise";
import { DataBase } from "../controllers/database";
import { Component } from "../types/Component";
import { FilterAttribute } from "../types/FilterAttribute";

export type filterValue = {
    idValue: number,
    value: string
}

export default class ComponentModel {
    private pool: Pool; // Пул базы данных
    private readonly BLOCK_SIZE: number = 10000;

    constructor() {
        this.pool = DataBase.getDatabase().getPool();
    }

    /**
     * Выборка комплектующих с полным описанием
     * @see Component
     * @param category - категория комплектующих
     * @param block - номер блока данных, по сути является коефициентом сдвига в запросе, который умножается на размер одного блока
     * @returns - массив комплектующих 
     */
    public async getComponents(category: string): Promise<Array<Component>> {
        const sql: string = `SELECT id_component AS id, name, price, img AS imageUrl FROM component 
                            WHERE id_category = ?`;

        const data: string[] = [Component.CategoriesId.get(category).toString()];
        const components: Map<number, Component> = new Map<number, Component>()

        return new Promise<Array<Component>>((resolve, reject) => {
            this.pool.execute(sql, data)
                // После того как результат получен, выполняется выборка всех атрибутов комплектующего 
                .then(result => {
                    (result as RowDataPacket[])[0].forEach(row => {
                        const comp: Component = Object.assign(new Component(), row);
                        components.set(comp.id, comp);
                    });

                    const sqlFullData: string = `SELECT comp_attr.id_component, attribute.id_characteristic AS id, attribute.characteristic AS name,  attribute_value.value, 
                                                attribute_value.id_value AS idValue, attribute.is_preview AS isPreview, attribute.preview_text
                                                FROM comp_attr 
                                                JOIN (SELECT id_component FROM component
                                                        WHERE id_category = ?) AS curComp 
                                                ON curComp.id_component = comp_attr.id_component 
                                                JOIN attribute ON attribute.id_characteristic = comp_attr.id_characteristic
                                                JOIN attribute_value ON attribute_value.id_value = comp_attr.id_value`;

                    return this.pool.execute(sqlFullData, data);
                })
                // Группировка комплектующих и их атрибутов по id комплектующего
                .then(result => {
                    (result as RowDataPacket[])[0].forEach(row => {
                        const comp: Component = components.get(row.id_component);

                        const attribute: Component.Attribute = new Component.Attribute();
                        attribute.isPreview = row.isPreview == 1;
                        attribute.value = row.value;
                        attribute.idValue = row.idValue;
                        attribute.name = attribute.isPreview ? row.preview_text : row.name

                        comp.attributes[row.id] = attribute;
                    });

                    resolve(Array.from(components, ([key, value]) => (value))); // Вернуть необходимо именно массив
                })
        })
    }

    public async getFilters(category: string): Promise<Map<number, FilterAttribute>> {
        const sql: string = `SELECT DISTINCT filters.id_characteristic AS id, attribute.characteristic AS name, isRange, step, attribute_value.value AS value FROM filters
                            JOIN attribute ON attribute.id_characteristic = filters.id_characteristic
                            JOIN comp_attr ON comp_attr.id_characteristic = attribute.id_characteristic 
                            JOIN attribute_value ON attribute_value.id_value = comp_attr.id_value 
                            JOIN component ON component.id_component = comp_attr.id_component
                            WHERE component.id_category = ?
                            ORDER BY CONVERT(attribute_value.value, SIGNED), attribute_value.value ASC`;

        return new Promise<Map<number, FilterAttribute>>((resolve, reject) => {
            this.pool.execute(sql, [Component.CategoriesId.get(category).toString()])
                .then(result => {
                    const filters: Map<number, FilterAttribute> = new Map<number, FilterAttribute>();

                    (result as RowDataPacket[])[0].forEach(row => {
                        if (!filters.has(row.id)) {
                            filters.set(row.id, new FilterAttribute(row.name, row.isRange == 1, row.step));
                        }

                        filters.get(row.id).values.push(row.value);
                    })
                    this.correctRangeValues(filters)

                    resolve(filters)
                })
        })
    }

    /**
     * Корректировка фильтров с диапазоном. Значение заменяется на минимум и максимум диапазона
     * @param filters - фильтры, которые будут подвержены корректировки 
     */
    private correctRangeValues(filters: Map<number, FilterAttribute>) {
        filters.forEach((filterAttribute: FilterAttribute, value: number) => {
            if (filterAttribute.isRange) {
                const numbers = new Array<number>()
                filterAttribute.values.forEach((value) => {
                    numbers.push(parseFloat(value));
                })

                const min = Math.min.apply(null, numbers).toString()
                const max = Math.max.apply(null, numbers).toString()

                filterAttribute.values = new Array<string>(min, max)
            }
        });
    }


}