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
var __rest = (this && this.__rest) || function (s, e) {
    var t = {};
    for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p) && e.indexOf(p) < 0)
        t[p] = s[p];
    if (s != null && typeof Object.getOwnPropertySymbols === "function")
        for (var i = 0, p = Object.getOwnPropertySymbols(s); i < p.length; i++) {
            if (e.indexOf(p[i]) < 0 && Object.prototype.propertyIsEnumerable.call(s, p[i]))
                t[p[i]] = s[p[i]];
        }
    return t;
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const axios_1 = __importDefault(require("axios"));
const node_html_parser_1 = __importDefault(require("node-html-parser"));
const components_service_1 = __importDefault(require("../services/components/components.service"));
const Parser_1 = require("./Parser");
class BrainParser extends Parser_1.Parser {
    constructor() {
        super('https://brain.com.ua');
        this.TIMEOUT = 3000;
        this.categories = new Map([
            [1, 'category/Processory-c1097-128'],
            [2, 'category/Vydeokarty-c1403'],
            [3, 'category/Systemnye_materynskye_platy-c1264-226'],
            [4, 'category/SSD_dysky-c1484'],
            [5, 'https://brain.com.ua/category/Zhestkie_diski_HDD-c2817/filter=a2817-206'],
            [6, 'category/Operativnaya_pamyat-c3130/filter=a3130-209'],
            [7, 'category/Bloky_pytanyya-c1442-221'],
            [8, 'category/Korpusa-c1441-271'],
            [9, 'category/Kulery_k_processoram_termopasta-c1108'],
        ]);
    }
    start() {
        return __awaiter(this, void 0, void 0, function* () {
            for (const [categoryId, category] of this.categories.entries()) {
                const maxPages = yield this.getMaxPages(`${this.BASE_URL}/${category}`);
                const DBProducts = yield components_service_1.default.getComponents(categoryId);
                for (let i = 1; i <= maxPages || i <= 50; ++i) {
                    const parsedProducts = categoryId == 5 || categoryId == 6
                        ? yield this.parseProducts(`${this.BASE_URL}/${category};page=${i}/`)
                        : yield this.parseProducts(`${this.BASE_URL}/${category}/page=${i}/`);
                    for (const parsedProduct of parsedProducts) {
                        const { id } = parsedProduct, productInfo = __rest(parsedProduct, ["id"]);
                        const foundProduct = DBProducts.find(p => p.url == parsedProduct.url);
                        if (foundProduct) {
                            yield components_service_1.default.updateComponent(foundProduct.id, Object.assign(Object.assign({}, parsedProduct), { categoryId }));
                        }
                        else {
                            const attributes = yield this.parseAttributes(parsedProduct.url);
                            yield components_service_1.default.createComponent(Object.assign(Object.assign({}, productInfo), { categoryId,
                                attributes }));
                        }
                    }
                    yield this.timeout(this.TIMEOUT);
                }
                console.log("parsed: " + category);
            }
        });
    }
    heavyUpdate() {
        return __awaiter(this, void 0, void 0, function* () {
            for (const [categoryId, category] of this.categories.entries()) {
                const DBProducts = yield components_service_1.default.getComponents(categoryId);
                if (categoryId < 7) {
                    continue;
                }
                for (const product of DBProducts) {
                    const { id } = product, productInfo = __rest(product, ["id"]);
                    if (categoryId == 7 && id < 3079) {
                        continue;
                    }
                    try {
                        console.log(`${product.id} - ${product.url}`);
                        const attributes = yield this.parseAttributes(product.url);
                        yield components_service_1.default.updateComponent(id, Object.assign(Object.assign({}, productInfo), { attributes,
                            categoryId }));
                    }
                    catch (e) {
                        //ignored
                    }
                    yield this.timeout(this.TIMEOUT);
                }
            }
        });
    }
    parseProducts(pageUrl) {
        var _a, _b, _c, _d, _e, _f, _g;
        return __awaiter(this, void 0, void 0, function* () {
            const parsedProducts = [];
            const res = yield axios_1.default.get(pageUrl, {
                withCredentials: false,
                headers: {
                    Cookie: "Lang=ru;"
                }
            });
            const html = (0, node_html_parser_1.default)(res.data);
            const HTMLComponents = html.querySelectorAll('div[class="br-pp br-pp-ex goods-block__item br-pcg br-series"]');
            for (const HTMLComponent of HTMLComponents) {
                const url = (_a = HTMLComponent.querySelector('a[itemprop="url"]')) === null || _a === void 0 ? void 0 : _a.getAttribute('href');
                const productUrl = `${this.BASE_URL}${url}`.replace('/ukr', '');
                const name = (_d = (_c = (_b = HTMLComponent.querySelector('div[class="description-wrapper"]')) === null || _b === void 0 ? void 0 : _b.querySelector('a[itemprop="url"]')) === null || _c === void 0 ? void 0 : _c.innerText.replace(/\n/g, '').replace(/&quot;/, ' ')) !== null && _d !== void 0 ? _d : '';
                const price = (_f = (_e = HTMLComponent.querySelector('span[itemprop="price"]')) === null || _e === void 0 ? void 0 : _e.innerText) !== null && _f !== void 0 ? _f : '-1';
                const img = (_g = HTMLComponent.querySelector('img[itemprop="image"]')) === null || _g === void 0 ? void 0 : _g.getAttribute('data-observe-src');
                const outOfStock = HTMLComponent.querySelector('div[class="br-pp-net"]');
                const isActual = outOfStock == null;
                parsedProducts.push({
                    id: -1,
                    categoryId: -1,
                    name,
                    price,
                    url: productUrl,
                    img: img !== null && img !== void 0 ? img : "",
                    shop: 'brain.com',
                    isActual: isActual,
                    attributes: []
                });
            }
            return parsedProducts;
        });
    }
    getMaxPages(url) {
        var _a, _b;
        return __awaiter(this, void 0, void 0, function* () {
            const res = yield axios_1.default.get(url);
            const html = (0, node_html_parser_1.default)(res.data);
            const pager = html.querySelector("div[class='page-goods__pager']");
            const pages = pager === null || pager === void 0 ? void 0 : pager.querySelectorAll("li");
            const maxPages = (_b = (_a = pages === null || pages === void 0 ? void 0 : pages.at(-1)) === null || _a === void 0 ? void 0 : _a.querySelector('a')) === null || _b === void 0 ? void 0 : _b.innerText;
            if (maxPages) {
                return parseInt(maxPages);
            }
            else {
                throw new Error("not found max pages");
            }
        });
    }
    parseAttributes(url) {
        var _a, _b;
        return __awaiter(this, void 0, void 0, function* () {
            const attributes = [];
            const res = yield axios_1.default.get(url, {
                withCredentials: false,
                headers: {
                    Cookie: "Lang=ru;"
                }
            });
            const html = (0, node_html_parser_1.default)(res.data);
            const HTMLCharsBlocks = html.querySelectorAll('div[class="br-pr-chr-item"]');
            HTMLCharsBlocks.pop();
            for (const HTMLCharBlock of HTMLCharsBlocks) {
                const HTMLChars = HTMLCharBlock.querySelectorAll('div');
                HTMLChars.splice(0, 1);
                for (const HTMLChar of HTMLChars) {
                    const name = HTMLChar.querySelectorAll('span')[0].innerText.replace(/\n/g, '').replace(/^\s+/, '').replace(/&nbsp;/, ' ').replace(/&quot;/, ' ');
                    const value = (_b = (_a = HTMLChar.querySelector('a')) === null || _a === void 0 ? void 0 : _a.innerText) !== null && _b !== void 0 ? _b : HTMLChar.querySelectorAll('span')[1].innerText.replace(/\n/g, '').replace(/^\s+/, '').replace(/&nbsp;/, ' ').replace(/&quot;/, ' ');
                    // Ограничение по длинне
                    if (name && value && name.length <= 150 && value.length <= 255) {
                        attributes.push({ name, value });
                    }
                }
            }
            return attributes;
        });
    }
}
exports.default = new BrainParser();
