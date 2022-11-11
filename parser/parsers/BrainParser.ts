import axios from "axios";
import parse from "node-html-parser";
import { Category } from "../constants/category";
import ProductModel from "../models/ProductModel";
import { Parser } from "./Parser";


class BrainParser extends Parser {
    private readonly TIMEOUT = 5000;
    private readonly categories: Map<number, string>;

    constructor() {
        super('https://brain.com.ua');
        this.categories = new Map([
            [1, 'category/Processory-c1097-128'],
            [2, 'category/Vydeokarty-c1403'],
            [3, 'category/Systemnye_materynskye_platy-c1264-226'],
            [4, 'category/SSD_dysky-c1484'],
            [5, 'category/Vynchestery_HDD-c1361-260'],
            [6, 'category/Moduly_pamyaty-c1334'],
            [7, 'category/Bloky_pytanyya-c1442-221'],
            [8, 'category/Korpusa-c1441-271'],
            [9, 'category/Kulery_k_processoram_termopasta-c1108'],

        ])
    }

    override async parseFullInfo(infoUrl: string): Promise<void> {

    }

    override async heavyUpdateProducts(): Promise<void> {

    }

    override async parseProducts(): Promise<void> {
        for (const [id, category] of this.categories.entries()) {
            const maxPages = await this.getMaxPages(`${this.BASE_URL}/${category}`)

            for (let i = 1; i <= maxPages || i <= 50; ++i) {
                const url = `${this.BASE_URL}/${category}/page=${i}/`;
                const res = await axios.get(`${this.BASE_URL}/${category}/page=${i}/`, {
                    withCredentials: false,
                    headers: {
                        Cookie: "Lang=ru;"
                    }
                });
                const html = parse(res.data)
                const components = html.querySelectorAll('div[class="br-pp br-pp-ex goods-block__item br-pcg br-series"]');

                for (const component of components) {
                    const url = component.querySelector('a[itemprop="url"]')?.getAttribute('href');
                    const productUrl = `${this.BASE_URL}${url}`.replace('/ukr', '');

                    const price = component.querySelector('span[itemprop="price"]')?.innerText;
                    const img = component.querySelector('img[itemprop="image"]')?.getAttribute('data-observe-src');
                    const outOfStock = component.querySelector('div[class="br-pp-net"]');
                    const isActual = outOfStock == null;

                    const product = (await ProductModel.getProducts({ url_full: productUrl }))[0];
                    if (product && price) {
                        ProductModel.updateProduct(product.idComponent, price, isActual, img);
                        console.log("updated: " + productUrl);
                    } else {
                        this.parseAttributes
                    }
                }

                await this.timeout(this.TIMEOUT);
            }
        }

    }

    protected async getMaxPages(url: string): Promise<number> {
        const res = await axios.get(url);
        const html = parse(res.data);

        const pager = html.querySelector("div[class='page-goods__pager']");
        const pages = pager?.querySelectorAll("li");
        const maxPages = pages?.at(-1)?.querySelector('a')?.innerText;

        if (maxPages) {
            return parseInt(maxPages);
        } else {
            throw new Error("not found max pages");
        }
    }


    private async parseAttributes(url: string) {

    }
}

export default new BrainParser();