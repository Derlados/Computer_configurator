import { RowDataPacket } from "mysql2";
import { Pool, ResultSetHeader } from "mysql2/promise";
import { DataBase } from "../controllers/database";
import User from "./data-classes/User";

export default class UserModel {
    private pool: Pool; // Пул базы данных

    constructor() {
        this.pool = DataBase.getDatabase().getPool();
    }

    public async register(user: User): Promise<User> {
        return new Promise<User>((resolve, reject) => {
            const sql = ` INSERT INTO users(username, password, email, secret, googleId, photoUrl, date) 
                        VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP())`;
            const data: string[] = [
                user.username,
                user.password ?? null,
                user.email ?? null,
                user.secret ?? null,
                user.googleId ?? null,
                user.photoUrl ?? null
            ];

            this.pool.execute(sql, data)
                .then((res) => {
                    const result: ResultSetHeader = (res as RowDataPacket)[0]
                    user.id = result.insertId
                    resolve(user);
                })
                .catch((err) => {
                    if (err.code == 'ER_DUP_ENTRY' && /username/.test(err.sqlMessage)) {
                        reject(UserError.USER_EXIST_USERNAME)
                    } else if (err.code == 'ER_DUP_ENTRY' && /email/.test(err.sqlMessage)) {
                        reject(UserError.USER_EXIST_EMAIL)
                    } else {
                        reject(UserError.DATABASE_ERROR)
                    }
                })
        });
    }

    public async login(user: User): Promise<User> {
        return new Promise<User>((resolve, reject) => {
            const sql = ` SELECT * FROM users 
                        WHERE (username=? AND password=? AND (email=? OR secret=?)) OR googleId=?`

            const data: string[] = [
                user.username,
                user.password ?? null,
                user.email ?? null,
                user.secret ?? null,
                user.googleId ?? null,
            ];

            this.pool.execute(sql, data)
                .then((res) => {
                    const row: any[] = (res as RowDataPacket)[0]
                    if (row.length == 0) {
                        reject(UserError.USER_NOT_FOUND)
                    } else {
                        user.photoUrl = row[0].photoUrl;
                        user.id = row[0].id;
                        resolve(user)
                    }
                })
                .catch(() => {
                    reject(UserError.DATABASE_ERROR)
                })
        });
    }

    public async update(user: User): Promise<void> {
        return new Promise<void>((resolve, reject) => {
            const sql = `UPDATE users SET
                            username=?, 
                            password=?,
                            email = IFNULL(?, email),
                            secret = IFNULL(?, secret),
                            google= IFNULL(?, google)
                        WHERE id=?`;

            const data: string[] = [
                user.username,
                user.password ?? null,
                user.email ?? null,
                user.secret ?? null,
                user.googleId ?? null,
                user.id.toString()
            ];

            this.pool.execute(sql, data)
                .then((res) => {
                    resolve()
                })
                .catch(() => {
                    reject(UserError.DATABASE_ERROR)
                })
        });
    }
}

export enum UserError {
    USER_NOT_FOUND,
    USER_EXIST_USERNAME,
    USER_EXIST_EMAIL,
    DATABASE_ERROR
}