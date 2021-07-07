export class FilterAttribute {
    name: string;
    isRange: Boolean;
    step: number;
    values: Array<string>;

    constructor(name: string, isRange: Boolean, step: number) {
        this.name = name;
        this.isRange = isRange;
        this.step = step;
        this.values = new Array<string>();
    }
}
