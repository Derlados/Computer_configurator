"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const BrainParser_1 = __importDefault(require("./parsers/BrainParser"));
BrainParser_1.default.start();
// async function parseSite() {
//     const res = await axios.get('https://hard.rozetka.com.ua/videocards/c80087/')
//     const root = parse(res.data);
//     const body = root.querySelectorAll('div[class="goods-tile__inner"]')
//     for (const elem of body) {
//         const name = elem.querySelector('span[class="goods-tile__title"]').innerText.toString()
//         console.log(name.replace(/( \((.*))/, '').replace('PCI-Ex ', ''));
//     }
// }
// parseSite();
// ProductModel.getProducts({ shop: 'brain.com' }).then(data => {
//     console.log(data);
// });
