"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const HttpCodes_1 = require("../constants/HttpCodes");
const ComponentModel_1 = __importDefault(require("../models/ComponentModel"));
class ComponentController {
    constructor() {
        this.BLOCK_SIZE = 100;
        this.getComponents = (req, res) => {
            const category = req.params.category;
            const offset = req.params.block * this.BLOCK_SIZE;
            this.componentModel.getComponents(category, offset)
                .then(data => {
                res.status(HttpCodes_1.HttpCodes.OK).send(JSON.stringify(data));
            });
        };
        this.getComponentById = (req, res) => {
        };
        this.getMaxPages = (req, res) => {
        };
        this.componentModel = new ComponentModel_1.default();
    }
}
exports.default = ComponentController;
//# sourceMappingURL=ComponentController.js.map