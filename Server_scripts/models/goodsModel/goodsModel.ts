import { QueryError, RowDataPacket } from "mysql2";
import Pool from "mysql2/typings/mysql/lib/Pool";
import { DataBase } from "../../controllers/database";
import { Goods } from "./goods";

export class GoodsModel {

    private pool: Pool;

    public constructor() {
        this.pool = DataBase.getDatabase().getPool();
    }

    /** Выборка комплектующих в соответствии с категорией и номером страницы 
     * @param idCategory - id категории в которой идет поиск 
     * @param numPage - номер страницы, является множителем сдвига для выборки товаров
     * @param callback - функция обратного вызова, принимает строки из выборки в MySQL
     */
    public getGoodsByCategory(idCategory: number, numPage: number, callback: (goodsPreview: RowDataPacket[]) => void): void {
        let sqlQuery: string = `SELECT * FROM component
                                WHERE component.id_category = ?
                                LIMIT 20 OFFSET ?`;
        let offset: number = 20 * (numPage - 1);

        this.pool.query(sqlQuery, [idCategory, offset], function(err: QueryError, result: RowDataPacket[]) {        
            callback(result);
        });

    }
    
    /** Получение всех характеристик комплектующего
     * @param id - id комплектующего
     * @param callback - колбек, принимает в качестве параметра данные о характеристика комплектующего в виде Map 
     */
    public getFullData(id: number, callback: (data: Object) => void): void {
        let sqlQuery: string = ` SELECT attributes.attribute, attribute_value.value FROM component_attributes
                            JOIN attributes ON attributes.id_attribute = component_attributes.id_attribute
                            JOIN attribute_value ON attribute_value.id_value = component_attributes.id_value
                            WHERE id_component = ?`;
        
        this.pool.query(sqlQuery, id, function(err: QueryError, result: RowDataPacket[]) {
            // Перевод данных в Map
            let data: Object = new Object();
            for (let i: number = 0; i < result.length; ++i)
                data[result[i].attribute] = result[i].value;
            callback(data);
        });
    }

    /** Получение максимального количества страниц (необходимо для пейджера)
     * @param idCategory - id категории
     * @param callback - обратный вызов, принимает числов в виде количества страниц
     */
    public getMaxPages(idCategory: number, callback: (maxPages: number) => void): void {
        let sqlQuery: string = `SELECT COUNT(*) as maxPages FROM component
                                WHERE component.id_category = ?`;
        this.pool.query(sqlQuery, idCategory, function(err: QueryError, result: RowDataPacket[]) {
            let maxPages = result[0].maxPages / 20;
            if (result[0].maxPages % 20 == 0)
                ++maxPages;

            callback(maxPages);
        })
    }
}