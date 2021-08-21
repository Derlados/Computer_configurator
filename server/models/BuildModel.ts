import { Pool, ResultSetHeader, RowDataPacket } from "mysql2/promise";
import { DataBase } from "../controllers/database";
import { Build } from "./Build";
import { Component } from "../types/Component";
import { Pair } from "../types/Pair";

export default class BuildModel {
    private pool: Pool; // Пул базы данных

    constructor() {
        this.pool = DataBase.getDatabase().getPool();
    }

    /**
     * Получение списка сборок. Если задан пользователь, вернуться сборки только этого пользователя,
     * иначе вернуться все публичные сборки всех пользователей
     * @param idUser - id пользователя
     * @returns список соответствующих сборок с полной информацией
     */
    public async getBuilds(idUser?: number): Promise<Build[]> {
        let sql = ` SELECT build.id, build.id_user, build.name, build.description, build.is_public, build_components.id_component, 
                        build_components.count_components AS countUsed, component.id_category
                    FROM build 
                    JOIN build_components ON build_components.id_build = id
                    JOIN component ON component.id_component = build_components.id_component
                    ${idUser ? 'WHERE build.id_user = ?' : 'WHERE is_public = true'}`;
        const data = new Array<string>();

        if (idUser) {
            data.push(idUser.toString())
        }

        const rows = (await this.pool.execute(sql, data))[0] as RowDataPacket[];
        const components = await this.getAllUsedComponents();

        const builds: Map<any, Build> = new Map();
        for (const row of rows) {
            if (!builds.has(row.id)) {
                const build = new Build();
                build.serverId = row.id;
                build.idUser = row.id_user;
                build.name = row.name;
                build.description = row.description;
                build.isPublic = row.is_public == 1;

                builds.set(row.id, build);
            }

            const build = builds.get(row.id);
            const component = components.get(row.id_component);
            build.addComponent(Number(row.id_category), component, Number(row.countUsed));
        }

        return Array.from(builds, ([key, value]) => (value));
    }

    public async addBuild(idUser: number, build: Build, components: Pair[]): Promise<number> {
        // Формирование запроса на добавление сборки
        const sqlAddBuild = `   INSERT INTO build(id_user, name, description, is_public) 
                                VALUES (?, ?, ?, ?)`;
        const dataAddBuild = [idUser.toString(), build.name, build.description, build.isPublic ? "1" : "0"]
        const res: ResultSetHeader = (await this.pool.execute(sqlAddBuild, dataAddBuild))[0] as ResultSetHeader;

        // Формирование запроса на добавление компонентов 
        let sqlAddComponents = `    INSERT INTO build_components(id_build, id_component, count_components) 
                                    VALUES `;
        const dataAddComponents = new Array<string>();
        for (const component of components) {
            sqlAddComponents += '(?, ?, ?),';
            dataAddComponents.push(res.insertId.toString(), component.first.toString(), component.second.toString());
        }
        sqlAddComponents = sqlAddComponents.slice(0, -1); // Необходимо удалить последнюю запятую
        await this.pool.execute(sqlAddComponents, dataAddComponents);

        return res.insertId;
    }

    /**
     * Обновление сборки. Происходит в 2 этапа:
     * 1 - обновление данных о самой сборке (название, описание, флаг публикации)
     * 2 - удаление и добавление новых id копмлектующих
     * @param idUser - id пользователя
     * @param idBuild - id сборки
     * @param updatedBuild - данные обновленной сборки
     * @param newComponents - id новых комплектующих
     */
    public async updateBuild(idUser: number, idBuild: number, updatedBuild: Build, newComponents: Pair[]): Promise<void> {
        // Запрос на обновление данных в сборке
        const sqlUpdateBuild = `UPDATE build SET 
                                name = ?,
                                description = ?
                                WHERE id_user = ? AND id = ?`;
        const dataUpdateBuild: string[] = [updatedBuild.name, updatedBuild.description, idUser.toString(), idBuild.toString()];

        // Запрос на обновление комплектующих
        let sqlUpdateComponents = ` DELETE FROM build_components WHERE id_build = ?;
                                    INSERT INTO build_components(id_build, id_component, count_components) VALUES `;

        const dataUpdateComponents = new Array<string>(idBuild.toString());
        for (const component of newComponents) {
            sqlUpdateComponents += '(?, ?, ?),';
            dataUpdateComponents.push(idBuild.toString(), component.first.toString(), component.second.toString());
        }
        sqlUpdateComponents = sqlUpdateComponents.slice(0, -1); // Необходимо удалить последнюю запятую

        try {
            await this.pool.execute(sqlUpdateBuild, dataUpdateBuild);
            await this.pool.query(sqlUpdateComponents, dataUpdateComponents);
        } catch (err) {
            console.error(err);
            throw err;
        }
    }

    public async updatePublicStatus(idUser: number, idBuild: number, isPublic: boolean): Promise<boolean> {
        const sql = `   UPDATE build SET 
                        is_public = ?
                        WHERE id_user = ? AND id = ?`;
        const data = [isPublic ? "1" : "0", idUser, idBuild];
        const res = (await this.pool.execute(sql, data))[0] as ResultSetHeader;

        if (res.affectedRows == 0) {
            throw BuildError.BUILD_NOT_FOUND;
        }

        return isPublic;
    }

    public async deleteBuild(idUser: number, id: number): Promise<void> {
        const sql = `DELETE FROM build WHERE id = ? AND id_user = ?`;
        const res = (await this.pool.execute(sql, [id.toString(), idUser.toString()]))[0] as ResultSetHeader;;

        if (res.affectedRows == 0) {
            throw BuildError.BUILD_NOT_FOUND;
        }
    }

    /**
     * Получение всех комплектующих которые используются в сборка.
     * ВАЖНО! Этот запрос должен быть закеширован в случае если нагрузка станет слишком большой, 
     * может быть даже закешированы все комплектующие
     * @returns ассоциативный список <id комплетующего, комлектющее со всей информацией>
     */
    private async getAllUsedComponents(): Promise<Map<number, Component>> {
        const sql = `   SELECT component.id_component AS id, component.name, component.price, component.img AS imageUrl, 
                                attribute.id_characteristic AS idAttr, attribute.characteristic AS nameAttr,
                                attribute_value.value, attribute_value.id_value AS idValue, attribute.is_preview AS isPreview, 
                                attribute.preview_text AS previewText
                            FROM component 
                            JOIN build_components ON build_components.id_component = component.id_component 
                            JOIN comp_attr ON comp_attr.id_component = component.id_component 
                            JOIN attribute ON attribute.id_characteristic = comp_attr.id_characteristic 
                            JOIN attribute_value ON attribute_value.id_value = comp_attr.id_value`;
        const rows = (await this.pool.execute(sql))[0] as RowDataPacket[];

        const components: Map<number, Component> = new Map<number, Component>();
        for (const row of rows) {
            if (!components.has(row.id)) {
                const component = new Component();
                component.id = Number(row.id);
                component.name = row.name;
                component.price = Number(row.price);
                component.imageUrl = row.imageUrl;
                components.set(row.id, component);
            }

            const attribute: Component.Attribute = new Component.Attribute();
            attribute.idValue = row.idValue;
            attribute.value = row.value;
            attribute.isPreview = row.isPreview == 1;
            attribute.name = attribute.isPreview ? row.previewText : row.nameAttr

            const component = components.get(row.id);
            component.attributes[row.idAttr] = attribute;
        }

        return components;
    }
}

export enum BuildError {
    BUILD_NOT_FOUND
}