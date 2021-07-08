import { Attribute } from "./Attribute";

export class Component {
    id: number;
    name: string;
    price: number;
    imageUrl: string;
    attributes: Object;

    constructor() {
        this.attributes = {};
    }
}