import { resolve } from "path";
import { Category } from "../constants/category";

export abstract class Parser {
    public readonly BASE_URL: string;

    constructor(baseUrl: string) {
        this.BASE_URL = baseUrl;
    }

    abstract parseProducts(category: Category, page: number): Promise<void>;

    abstract parseFullInfo(infoUrl: string): Promise<void>;

    abstract heavyUpdateProducts(): Promise<void>;

    protected abstract getMaxPages(url: string): Promise<number>;

    protected async timeout(ms: number): Promise<void> {
        return new Promise(resolve =>
            setTimeout(() => {
                resolve();
            }, ms)
        );
    }
}

