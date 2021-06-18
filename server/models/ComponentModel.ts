import { RowDataPacket } from "mysql2";
import { Pool } from "mysql2/promise";
import { DataBase } from "../controllers/database";
import { Component } from "./data-classes/Component";

export default class ComponentModel {
    private pool: Pool; // Пул базы данных

    constructor() {
        this.pool = DataBase.getDatabase().getPool();
    }

    public async getComponents(category: string, offset: number): Promise<Array<Component>> {
        const sql: string = `SELECT name, price, img AS imageUrl FROM component 
                            JOIN category ON category.id_category = component.id_category
                            WHERE url_category = ?
                            LIMIT ?`;

        const data: string[] = [category, offset.toString()];
        const components: Array<Component> = new Array<Component>()

        return new Promise<Component[]>((resolve, reject) => {
            this.pool.execute(sql, data)
                .then(result => {
                    (result as RowDataPacket[])[0].forEach(row => {
                        components.push(Object.assign(new Component(), row));
                    })
                    console.log(components)

                    const sqlFullData: string = `SELECT comp_attr.id_component, attribute.characteristic, comp_attr.id_value, attribute_value.value, attribute.isPreview 
                                                FROM comp_attr 
                                                JOIN (SELECT id_component FROM component 
                                                        JOIN category ON category.id_category = component.id_category
                                                        WHERE url_category = ?
                                                        LIMIT ?) AS curComp 
                                                ON curComp.id_component = comp_attr.id_component 
                                                JOIN attribute ON attribute.id_characteristic = comp_attr.id_characteristic
                                                JOIN attribute_value ON attribute_value.id_value = comp_attr.id_value
                                                ORDER BY comp_attr.id_component ASC
                    `;

                    return this.pool.execute(sqlFullData, data);
                })
                .then(result => {
                })
        })
    }

    public getMaxPages(req: any, res: Response): void {

    }

    private getFullData() {

    }
}