"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const cors_1 = __importDefault(require("cors"));
const express_fileupload_1 = __importDefault(require("express-fileupload"));
const ComponentRouter_1 = require("./routers/ComponentRouter");
// import { goodsRouter as productsRouter } from "./routers/productsRouter";
const app = express_1.default();
app.use(cors_1.default());
app.use(express_1.default.urlencoded({ extended: false }));
app.use(express_1.default.json());
app.use(express_1.default.static(__dirname));
app.use(express_fileupload_1.default());
app.use('/api/components', ComponentRouter_1.componentRouter);
app.listen(3000, () => {
    console.log("START");
});
//# sourceMappingURL=app.js.map