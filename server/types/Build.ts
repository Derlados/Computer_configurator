import { Component } from "./Component";

export class Build {
    id: number;
    idUser: number;
    name: string;
    description: string;
    price: number;
    isPublic: boolean;
    components: Array<Component>;

    constructor() {
        this.components = Array<Component>();
        this.price = 0;
    }
}