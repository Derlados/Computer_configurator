"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.DataBase = void 0;
const mysql2_1 = __importDefault(require("mysql2"));
class DataBase {
    constructor() {
        this.pool = mysql2_1.default.createPool({
            connectionLimit: 5,
            host: "localhost",
            user: "root",
            password: "root",
            database: "computer_conf",
            multipleStatements: true,
            port: 3308
        }).promise();
    }
    static getDatabase() {
        if (DataBase.instance == null)
            DataBase.instance = new DataBase();
        return DataBase.instance;
    }
    getPool() {
        return this.pool;
    }
}
exports.DataBase = DataBase;
DataBase.instance = null;
//# sourceMappingURL=database.js.map