import { RowDataPacket } from "mysql2";
import { Pool } from "mysql2/promise";
import { DataBase } from "../controllers/database";
import { Attribute } from "./data-classes/Attribute";
import { Component } from "./data-classes/Component";

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

                    const sqlFullData: string = `SELECT comp_attr.id_component, attribute.characteristic AS name, comp_attr.id_value AS id, attribute_value.value, attribute.isPreview 
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
                        delete row.id_component;

                        comp.attributes.push(Object.assign(new Attribute(), row));
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
                    resolve((result as RowDataPacket[])[0][0].total)
                })
        })
    }
}