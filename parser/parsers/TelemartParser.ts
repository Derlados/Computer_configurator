import { Category } from "../constants/category";
import { Parser } from "./Parser";

class TelemartParser extends Parser {

    constructor() {
        super('');
    }

    override parseGoods(category: Category, page: number): Promise<void> {
        throw new Error("Method not implemented.");
    }

    override parseFullInfo(infoUrl: string): Promise<void> {
        throw new Error("Method not implemented.");
    }

    override updateGoods(): Promise<void> {
        throw new Error("Method not implemented.");
    }

    override async heavyUpdateGoods(): Promise<void> {
    }


    protected getMaxPages(url: string): Promise<number> {
        throw new Error("Method not implemented.");
    }
}

export default new TelemartParser();