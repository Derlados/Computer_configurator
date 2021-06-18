"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
const database_1 = require("../controllers/database");
const Component_1 = require("./data-classes/Component");
class ComponentModel {
    constructor() {
        this.pool = database_1.DataBase.getDatabase().getPool();
    }
    getComponents(category, offset) {
        return __awaiter(this, void 0, void 0, function* () {
            const sql = `SELECT name, price, img AS imageUrl FROM component 
                            JOIN category ON category.id_category = component.id_category
                            WHERE url_category = ?
                            LIMIT ?`;
            const data = [category, offset.toString()];
            const components = new Array();
            return new Promise((resolve, reject) => {
                this.pool.execute(sql, data)
                    .then(result => {
                    result[0].forEach(row => {
                        components.push(Object.assign(new Component_1.Component(), row));
                    });
                    console.log(components);
                    const sqlFullData = `SELECT comp_attr.id_component, attribute.characteristic, comp_attr.id_value, attribute_value.value, attribute.isPreview 
                                                FROM comp_attr 
                                                JOIN (SELECT id_component FROM component 
                                                        JOIN category ON category.id_category = component.id_category
                                                        WHERE url_category = ?
                                                        LIMIT ?) AS curComp 
                                                ON curComp.id_component = comp_attr.id_component 
                                                JOIN attribute ON attribute.id_characteristic = comp_attr.id_characteristic
                                                JOIN attribute_value ON attribute_value.id_value = comp_attr.id_value
                                                ORDER BY comp_attr.id_component ASC
                    `;
                    return this.pool.execute(sqlFullData, data);
                })
                    .then(result => {
                });
            });
        });
    }
    getMaxPages(req, res) {
    }
    getFullData() {
    }
}
exports.default = ComponentModel;
//# sourceMappingURL=ComponentModel.js.map