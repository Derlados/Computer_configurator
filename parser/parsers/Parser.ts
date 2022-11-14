import { resolve } from "path";
import { Category } from "../constants/category";
import { IProduct } from "../types/IProduct";

export abstract class Parser {
    public readonly BASE_URL: string;

    constructor(baseUrl: string) {
        this.BASE_URL = baseUrl;
    }

    abstract start(): Promise<void>;

    abstract parseProducts(pageUrl: string): Promise<IProduct[]>;

    protected abstract getMaxPages(url: string): Promise<number>;

    protected async timeout(ms: number): Promise<void> {
        return new Promise(resolve =>
            setTimeout(() => {
                resolve();
            }, ms)
        );
    }
}

