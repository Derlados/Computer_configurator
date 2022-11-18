import axios from "axios";
import parse from "node-html-parser";
import { IAttribute } from "../types/IAttribute";
import componentsService from "../services/components/components.service";
import { Parser } from "./Parser";
import { IProduct } from "../types/IProduct";


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

    override async start() {
        for (const [categoryId, category] of this.categories.entries()) {
            const maxPages = await this.getMaxPages(`${this.BASE_URL}/${category}`)
            const DBProducts = await componentsService.getComponents(categoryId);

            for (let i = 1; i <= maxPages || i <= 50; ++i) {
                const parsedProducts = await this.parseProducts(`${this.BASE_URL}/${category}/page=${i}/`);

                for (const parsedProduct of parsedProducts) {
                    const foundProduct = DBProducts.find(p => p.url == parsedProduct.url);
                    if (foundProduct) {
                        // const { id, attributes, ...productInfo } = foundProduct;
                        // const components = await componentsService.updateComponent(id, {
                        //     ...productInfo,
                        //     categoryId
                        // })

                        // console.log(components)
                    } else {
                        const { id, ...productInfo } = parsedProduct;
                        const attributes = await this.parseAttributes(parsedProduct.url);

                        const component = await componentsService.createComponent({
                            ...productInfo,
                            categoryId,
                            attributes
                        })

                        console.log(component)
                    }
                }



                await this.timeout(this.TIMEOUT);
            }
        }
    }

    override async parseProducts(pageUrl: string): Promise<IProduct[]> {
        const parsedProducts: IProduct[] = [];

        const res = await axios.get(pageUrl, {
            withCredentials: false,
            headers: {
                Cookie: "Lang=ru;"
            }
        });
        const html = parse(res.data)
        const HTMLComponents = html.querySelectorAll('div[class="br-pp br-pp-ex goods-block__item br-pcg br-series"]');

        for (const HTMLComponent of HTMLComponents) {
            const url = HTMLComponent.querySelector('a[itemprop="url"]')?.getAttribute('href');
            const productUrl = `${this.BASE_URL}${url}`.replace('/ukr', '');

            const name = HTMLComponent.querySelector('div[class="description-wrapper"]')?.querySelector('a[itemprop="url"]')
                ?.innerText.replace(/\n/g, '') ?? '';


            const price = Number(HTMLComponent.querySelector('span[itemprop="price"]')?.innerText);
            const img = HTMLComponent.querySelector('img[itemprop="image"]')?.getAttribute('data-observe-src');
            const outOfStock = HTMLComponent.querySelector('div[class="br-pp-net"]');
            const isActual = outOfStock == null;

            parsedProducts.push({
                id: -1,
                categoryId: -1,
                name,
                price,
                url: productUrl,
                img: img ?? "",
                shop: 'brain.com',
                isActual: isActual,
                attributes: []
            })
        }

        return parsedProducts;
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

    private async parseAttributes(url: string): Promise<IAttribute[]> {
        const attributes: IAttribute[] = [];

        const res = await axios.get(url, {
            withCredentials: false,
            headers: {
                Cookie: "Lang=ru;"
            }
        });
        const html = parse(res.data)

        const HTMLCharsBlocks = html.querySelectorAll('div[class="br-pr-chr-item"]');
        HTMLCharsBlocks.pop();

        for (const HTMLCharBlock of HTMLCharsBlocks) {
            const HTMLChars = HTMLCharBlock.querySelectorAll('div');
            HTMLChars.splice(0, 1);

            for (const HTMLChar of HTMLChars) {
                const name = HTMLChar.querySelectorAll('span')[0].innerText.replace(/\n/g, '');
                const value = HTMLChar.querySelector('a')?.innerText ?? HTMLChar.querySelectorAll('span')[1].innerText.replace(/\n/g, '');

                attributes.push({ name, value })
            }
        }

        return attributes;
    }
}

export default new BrainParser();