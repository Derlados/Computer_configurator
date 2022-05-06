import mysql from 'mysql2';

class Database {
    public readonly pool: mysql.Pool;

    constructor() {
        this.pool = mysql.createPool({
            connectionLimit: 4,
            host: "localhost",
            user: "root",
            password: "",
            database: "computer_conf",
            multipleStatements: true,
            port: 3306
        })
    }

    getPool() {
        return this.pool.promise();
    }
}

export default new Database();