import { Component } from "./Component";

export class BuildComponent {
    component: Component;
    count: number;

    constructor(component: Component, count: number) {
        this.component = component;
        this.count = count;
    }
}