import { BuildComponent } from "../types/BuildComponent";
import { Component } from "../types/Component";

export class Build {
    serverId: number;
    idUser: number;
    name: string;
    description: string;
    price: number;
    isPublic: boolean;
    components: Map<String, Array<BuildComponent>>;

    constructor() {
        this.components = new Map();
        this.price = 0;
    }

    addComponent(idCategory: number, component: Component, count: number): void {
        const category = Component.getCategoryById(idCategory);

        if (!this.components.has(category)) {
            this.components.set(category, new Array())
        }
        this.components.get(category).push(new BuildComponent(component, count));
        this.price += component.price * count;
    }
}
