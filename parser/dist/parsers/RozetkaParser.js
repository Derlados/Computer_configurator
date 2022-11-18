"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Parser_1 = require("./Parser");
class RozetkaParser extends Parser_1.Parser {
    constructor() {
        super('');
    }
    start() {
        throw new Error("Method not implemented.");
    }
    parseProducts(pageUrl) {
        throw new Error("Method not implemented.");
    }
    getMaxPages(url) {
        throw new Error("Method not implemented.");
    }
}
exports.default = new RozetkaParser();
