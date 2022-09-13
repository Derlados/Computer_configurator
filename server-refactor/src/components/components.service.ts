import { Injectable } from '@nestjs/common';
import { Pool, RowDataPacket } from 'mysql2/promise';
import { categoriesId, Category } from './constants/categories';
import { Component } from './models/component.model';

@Injectable()
export class ComponentsService {
    // private pool: Pool; // Пул базы данных

    // constructor() {
    //     this.pool = DataBase.getDatabase().getPool();
    // }

    // async getAllByCategory(category: Category) {
    //     const sql: string = `SELECT id_component AS id, name, price, img AS imageUrl, is_actual as isActual FROM component 
    //                         WHERE id_category = ?`;
    //     const data: string[] = [categoriesId.get(category).toString()];
    //     const components: Map<number, Component> = new Map<number, Component>();

    //     const result = (await this.pool.execute(sql, data))[0] as Component[];

    //     return result;
    // }

    async getFiltersByCategory(category: Category) {

    }
}
