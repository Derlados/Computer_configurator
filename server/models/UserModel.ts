import * as fs from 'fs';
import { RowDataPacket } from "mysql2";
import { Pool, ResultSetHeader } from "mysql2/promise";
import { imageRoot } from "../app";
import { DataBase } from "../controllers/database";
import User from "../types/User";

export default class UserModel {
    private readonly IMAGE_URL_ROOT = '/images/users/'

    private userImageRoot: string = '/users';
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
                        WHERE (username=? AND password=?) OR googleId=?`

            const data: string[] = [
                user.username,
                user.password ?? null,
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

    public async updateData(idUser: number, user: User, img?: any): Promise<User> {
        return new Promise<User>((resolve, reject) => {
            // Обновление изображения пользователя
            if (img != null) {
                const imgName = idUser.toString() + `.${img.mimetype.split("/")[1]}`;
                user.photoUrl = process.env.DOMAIN + this.IMAGE_URL_ROOT + imgName; //
            }

            const sql = `UPDATE users SET
                            username=?,
                            photoUrl=IFNULL(?, photoUrl)
                        WHERE id=?`;

            const data: string[] = [
                user.username,
                user.photoUrl ?? null,
                idUser.toString()
            ];

            this.pool.execute(sql, data)
                .then(() => {
                    if (img != null) {
                        this.saveImage(idUser, img);
                    }

                    resolve(user)
                })
                .catch((err) => {
                    if (err.code == 'ER_DUP_ENTRY' && /username/.test(err.sqlMessage)) {
                        reject(UserError.USER_EXIST_USERNAME)
                    } else if (err.code == 'ER_DUP_ENTRY' && /googleId/.test(err.sqlMessage)) {
                        reject(UserError.USER_EXIST_GOOGLE_ACC)
                    } else {
                        reject(UserError.DATABASE_ERROR)
                    }
                })
        });
    }

    public async updatePassword() {

    }

    private saveImage(idUser: number, img: any) {
        const imgName = idUser.toString() + `.${img.mimetype.split("/")[1]}`;

        // Удаленее старой автарки, так как может поменятся расшерение и реплейс не сработает
        const filenames = fs.readdirSync(imageRoot + this.userImageRoot);
        for (const filename of filenames) {
            if (filename.includes(idUser.toString())) {
                fs.rmSync(imageRoot + this.userImageRoot + '/' + filename)
            }
        }

        // Сохранение изображения 
        const imgPath: string = imageRoot + this.userImageRoot + '/' + imgName;
        img.mv(imgPath);
    }
}


export enum UserError {
    USER_NOT_FOUND,
    USER_EXIST_USERNAME,
    USER_EXIST_EMAIL,
    USER_EXIST_GOOGLE_ACC,
    DATABASE_ERROR
}