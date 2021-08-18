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

export namespace Component {
    export class Attribute {
        name: string;
        value: string;
        idValue: number;
        isPreview: boolean;
    }
}