import { Category } from "../constants/category";
import { IProduct } from "../types/IProduct";
import { Parser } from "./Parser";

class TelemartParser extends Parser {
    constructor() {
        super('');
    }

    start(): Promise<void> {
        throw new Error("Method not implemented.");
    }

    parseProducts(pageUrl: string): Promise<IProduct[]> {
        throw new Error("Method not implemented.");
    }

    protected getMaxPages(url: string): Promise<number> {
        throw new Error("Method not implemented.");
    }
}

export default new TelemartParser();