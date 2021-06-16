import Pool from "mysql2/typings/mysql/lib/Pool";
import MySQL from "mysql2";

export class DataBase {

    private static instance: DataBase = null;
    private pool: Pool;

    private constructor () {
        this.pool = MySQL.createPool({
            connectionLimit: 5,
            host: "localhost",
            user: "root",
            password: "root",
            database: "compconf",
            multipleStatements: true,
            port: 3307
        });
    }

    public static getDatabase(): DataBase {
        if (DataBase.instance == null)
            DataBase.instance = new DataBase();
        return DataBase.instance;
    }

    public getPool(): Pool {
        return this.pool;
    }

}