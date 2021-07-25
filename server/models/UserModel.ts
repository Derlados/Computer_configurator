import { RowDataPacket } from "mysql2";
import { Pool, ResultSetHeader } from "mysql2/promise";
import { DataBase } from "../controllers/database";
import User from "./data-classes/User";

export default class UserModel {
    private pool: Pool; // Пул базы данных

    constructor() {
        this.pool = DataBase.getDatabase().getPool();
    }

    public async register(user: User): Promise<number> {
        return new Promise<number>((resolve, reject) => {
            let sql = ` INSERT INTO users(nickname, password, email, secret, google) 
                        VALUES (?, ?, ?, ?, ?)`;
            const data: string[] = [
                user.nickname,
                user.password ?? null,
                user.email ?? null,
                user.secret ?? null,
                user.google ?? null,
            ];

            this.pool.execute(sql, data)
                .then((res) => {

                    const result: ResultSetHeader = (res as RowDataPacket)[0]
                    resolve(result.insertId);
                })
                .catch((err) => {
                    if (err.code == 'ER_DUP_ENTRY') {
                        reject(UserError.USER_EXIST)
                    } else {
                        reject(UserError.DATABASE_ERROR)
                    }
                })
        });
    }

    public async login(user: User): Promise<number> {
        return new Promise<number>((resolve, reject) => {
            let sql = ` SELECT id FROM users 
                        WHERE (nickname=? AND password=? AND (email=? OR secret=?)) OR google=?`

            const data: string[] = [
                user.nickname,
                user.password ?? null,
                user.email ?? null,
                user.secret ?? null,
                user.google ?? null,
            ];

            this.pool.execute(sql, data)
                .then((res) => {
                    const row: any[] = (res as RowDataPacket)[0]
                    if (row.length == 0) {
                        reject(UserError.USER_NOT_FOUND)
                    } else {
                        resolve(row[0].id)
                    }
                })
                .catch(() => {
                    reject(UserError.DATABASE_ERROR)
                })
        });
    }
}

export enum UserError {
    USER_NOT_FOUND,
    USER_EXIST,
    DATABASE_ERROR
}