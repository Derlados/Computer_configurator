"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.componentRouter = void 0;
const express_1 = __importDefault(require("express"));
const ComponentController_1 = __importDefault(require("../controllers/ComponentController"));
exports.componentRouter = express_1.default.Router();
const componentController = new ComponentController_1.default();
exports.componentRouter.get('/category=:category/block=:block([0-9]+)', componentController.getComponents);
exports.componentRouter.get('/category=:category/max-blocks', componentController.getComponents);
//# sourceMappingURL=ComponentRouter.js.map