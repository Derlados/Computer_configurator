import { Pool, ResultSetHeader, RowDataPacket } from "mysql2/promise";
import { DataBase } from "../controllers/database";
import { Build } from "../types/Build";
import { Component } from "../types/Component";

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
        let sql = ` SELECT * FROM build 
                    JOIN build_components ON build_components.id_build = id
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
                build.id = row.id;
                build.idUser = row.id_user;
                build.name = row.name;
                build.description = row.description;
                build.isPublic = row.is_public;

                builds.set(row.id, build);
            }

            const build = builds.get(row.id);
            const component = components.get(row.id_component);
            build.price += component.price;
            build.components.push(component);
        }

        return Array.from(builds, ([key, value]) => (value));
    }

    public async addBuild(build: Build, idComponents: number[]): Promise<void> {
        try {
            // Формирование запроса на добавление сборки
            const sqlAddBuild = `   INSERT INTO build(id_user, name, description, price, is_public) 
                                    VALUES (?, ?, ?, ?, ?)`;
            const dataAddBuild = [build.idUser.toString(), build.name, build.description, build.price.toString(), build.isPublic ? "1" : "0"]
            const res: ResultSetHeader = (await this.pool.execute(sqlAddBuild, dataAddBuild))[0] as ResultSetHeader;

            // Формирование запроса на добавление компонентов 
            let sqlAddComponents = `    INSERT INTO build_components(id_build, id_component) 
                                        VALUES `;
            const dataAddComponents = new Array<string>();
            for (const id of idComponents) {
                sqlAddComponents += '(?, ?),';
                dataAddComponents.push(res.insertId.toString(), id.toString());
            }
            sqlAddComponents = sqlAddComponents.slice(0, -1); // Необходимо удалить последнюю запятую
            await this.pool.execute(sqlAddComponents, dataAddComponents);
        } catch (err) {
            console.error(err);
            throw err;
        }
    }

    /**
     * Обновление сборки. Происходит в 2 этапа:
     * 1 - обновление данных о самой сборке (название, описание, флаг публикации)
     * 2 - удаление и добавление новых id копмлектующих
     * @param idUser - id пользователя
     * @param idBuild - id сборки
     * @param updatedBuild - данные обновленной сборки
     * @param newIdComponents - id новых комплектующих
     */
    public async updateBuild(idUser: number, idBuild: number, updatedBuild: Build, newIdComponents: number[]): Promise<void> {
        // Запрос на обновление данных в сборке
        const sqlUpdateBuild = `UPDATE build SET 
                                name = ?,
                                description = ?,
                                is_public = ?
                                WHERE id_user = ?`;
        const dataUpdateBuild: string[] = [updatedBuild.name, updatedBuild.description, updatedBuild.isPublic ? "1" : "0", idUser.toString()];

        // Запрос на обновление комплектующих
        let sqlUpdateComponents = ` DELETE FROM build_components WHERE id_build = ?;
                                    INSERT INTO build_components(id_build, id_component) VALUES `;
        const inject = "23; DELETE FROM build WHERE build.id = 21";

        const dataUpdateComponents = new Array<string>(inject.toString());
        for (const id of newIdComponents) {
            sqlUpdateComponents += '(?, ?),';
            dataUpdateComponents.push(idBuild.toString(), id.toString());
        }
        sqlUpdateComponents = sqlUpdateComponents.slice(0, -1); // Необходимо удалить последнюю запятую
        console.log(dataUpdateComponents);

        try {
            await this.pool.execute(sqlUpdateBuild, dataUpdateBuild);
            await this.pool.query(sqlUpdateComponents, dataUpdateComponents);
        } catch (err) {
            console.error(err);
            throw err;
        }
    }

    public async deleteBuild(id: number): Promise<void> {
        const sql = `DELETE FROM build WHERE id = ?`;

        try {
            await this.pool.execute(sql, id.toString());
        } catch (err) {
            console.error(err);
            throw err;
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
                            attribute.id_characteristic AS idAttr, attribute.characteristic AS name,
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
            attribute.name = attribute.isPreview ? row.previewText : row.name

            const component = components.get(row.id);
            component.attributes[row.idAttr] = attribute;
        }

        return components;
    }
}

