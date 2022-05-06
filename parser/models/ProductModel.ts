import { OkPacket, ResultSetHeader, RowDataPacket } from "mysql2";
import Database from "../database/Database";
import { Filter } from "./Filter";
import { IProduct } from "./IProduct";

class ProductModel {

    async getProducts(filter?: Filter): Promise<IProduct[]> {
        let sql = "SELECT * FROM component";

        if (!filter) {
            const [rows] = await Database.getPool().execute(sql);
            if (this.isRowData(rows)) {
                return this.parseProducts(rows);
            } else {
                throw new Error(rows.toString());
            }
        }

        const keys = Object.keys(filter);
        const conditions = [];
        for (const key of keys) {
            conditions.push(`${key} = ?`)
        }

        sql = `${sql} WHERE ${conditions.join(' AND ')} `;
        const [rows] = await Database.getPool().execute(sql, Object.values(filter));

        if (this.isRowData(rows)) {
            return this.parseProducts(rows);
        } else {
            throw new Error(rows.toString());
        }
    }

    async updateProduct(id: number, price: string, isActual: boolean, img?: string): Promise<void> {
        let sql = `UPDATE component SET price = ?, is_actual = ?, date_updated = CURRENT_TIMESTAMP(), img = ? WHERE id_component = ?`;
        const params: string[] = [price, isActual ? '1' : '0', img ?? 'component.img', id.toString()]

        const [result] = await Database.getPool().execute(sql, params);
    }

    async insertProduct(): Promise<void> {


    }

    async deleteOldProducts(): Promise<void> {

    }

    private parseProducts(rows: RowDataPacket[]): IProduct[] {
        const products: IProduct[] = [];
        for (const row of rows) {
            products.push({
                idComponent: row.id_component,
                name: row.name,
                idCategory: row.id_category,
                price: row.price,
                img: row.img,
                url_full: row.url_full,
                shop: row.shop,
                updated: row.date_updated,
                isActual: row.is_actual == 1
            })
        }

        return products;
    }

    private isRowData(rows: RowDataPacket[] | RowDataPacket[][] | OkPacket | OkPacket[] | ResultSetHeader): rows is RowDataPacket[] {
        const typedRows = rows as RowDataPacket[];
        return typedRows.length != undefined && (typedRows.length == 0 || (typedRows)[0].id_component != undefined);
    }
}

export default new ProductModel();