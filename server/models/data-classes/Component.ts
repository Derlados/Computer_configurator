import { Attribute } from "./Attribute";

export class Component {
    id: number;
    name: string;
    price: number;
    imageUrl: string;
    attributes: Array<Attribute>;

    constructor() {
        this.attributes = new Array<Attribute>();
    }
}