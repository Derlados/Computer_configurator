import { RowDataPacket } from "mysql2";
import { Pool } from "mysql2/promise";
import { DataBase } from "../controllers/database";
import { Attribute } from "./data-classes/Attribute";
import { Component } from "./data-classes/Component";

export type filterValue = {
    idValue: number,
    value: string
}

export default class ComponentModel {
    private pool: Pool; // Пул базы данных
    private readonly BLOCK_SIZE: number = 100;

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
    public async getComponents(category: string, block: number): Promise<Array<Component>> {
        const sql: string = `SELECT id_component AS id, name, price, img AS imageUrl FROM component 
                            JOIN category ON category.id_category = component.id_category
                            WHERE url_category = ?
                            LIMIT ? OFFSET ?`;

        const offset = (block - 1) * this.BLOCK_SIZE;
        const data: string[] = [category, this.BLOCK_SIZE.toString(), offset.toString()];
        const components: Map<number, Component> = new Map<number, Component>()

        return new Promise<Array<Component>>((resolve, reject) => {
            this.pool.execute(sql, data)
                // После того как результат получен, выполняется выборка всех атрибутов комплектующего 
                .then(result => {
                    (result as RowDataPacket[])[0].forEach(row => {
                        const comp: Component = Object.assign(new Component(), row);
                        components.set(comp.id, comp);
                    });

                    const sqlFullData: string = `SELECT comp_attr.id_component, attribute.characteristic AS name, comp_attr.id_value AS id, attribute_value.value, 
                                                    attribute.is_preview AS isPreview, attribute.preview_text
                                                FROM comp_attr 
                                                JOIN (SELECT id_component FROM component 
                                                        JOIN category ON category.id_category = component.id_category
                                                        WHERE url_category = ?
                                                        LIMIT ? OFFSET ?) AS curComp 
                                                ON curComp.id_component = comp_attr.id_component 
                                                JOIN attribute ON attribute.id_characteristic = comp_attr.id_characteristic
                                                JOIN attribute_value ON attribute_value.id_value = comp_attr.id_value`;

                    return this.pool.execute(sqlFullData, data);
                })
                // Группировка комплектующих и их атрибутов по id комплектующего
                .then(result => {
                    (result as RowDataPacket[])[0].forEach(row => {
                        const comp: Component = components.get(row.id_component);

                        const attribute: Attribute = new Attribute();
                        attribute.isPreview = row.isPreview == 1;
                        attribute.id = row.id;
                        attribute.value = row.value;
                        attribute.name = attribute.isPreview ? row.preview_text : row.name

                        comp.attributes.push(attribute);
                    });

                    resolve(Array.from(components, ([key, value]) => (value))); // Вернуть необходимо именно массив
                })
        })
    }

    /**
     * Получение максимального количества "блоков" комплектующих
     * @param category - категория комплектующих
     * @returns - максимальное число блоков
     */
    public async getMaxBlocks(category: string): Promise<number> {
        const sql: string = `SELECT CEIL(COUNT(*) / ${this.BLOCK_SIZE}) AS total FROM component 
                            JOIN category ON category.id_category = component.id_category
                            WHERE category.url_category = ?`;

        return new Promise<number>((resolve, reject) => {
            this.pool.execute(sql, [category])
                .then(result => {
                    resolve((result as RowDataPacket[])[0][0].total);
                })
        })
    }

    public async getFilters(category: string): Promise<Map<string, Array<string>>> {
        const sql: string = `SELECT DISTINCT attribute.characteristic, attribute_value.value FROM comp_attr 
        JOIN attribute ON attribute.id_characteristic = comp_attr.id_characteristic 
        JOIN attribute_value ON attribute_value.id_value = comp_attr.id_value 
        JOIN component ON component.id_component = comp_attr.id_component
        JOIN category ON category.id_category = component.id_category
        WHERE comp_attr.id_characteristic IN (SELECT id_characteristic FROM filters WHERE category.url_category = "CPU") 
                AND (category.url_category = "CPU")
        ORDER BY CONVERT(attribute_value.value, INT), attribute_value.value ASC`;

        return new Promise<Map<string, Array<string>>>((resolve, reject) => {
            this.pool.execute(sql, [category, category])
                .then(result => {
                    const filters: Map<string, Array<string>> = new Map<string, Array<string>>();

                    (result as RowDataPacket[])[0].forEach(row => {
                        if (!filters.has(row.characteristic)) {
                            filters.set(row.characteristic, Array());
                        }

                        filters.get(row.characteristic).push(row.value);
                    })

                    console.log(filters)
                    resolve(filters)
                })
        })
    }
}