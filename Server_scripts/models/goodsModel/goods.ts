export class Goods {
    public readonly id_component: number;
    public readonly name: string;
    public readonly id_category: number;
    public readonly price: number;
    public readonly count_component: number;
    public readonly img: string;
    public readonly id_description: number;

    constructor(jsonRow) {
        this.id_component = jsonRow.id_component;
        this.name = jsonRow.name;
        this.id_category = jsonRow.id_category;
        this.price = jsonRow.price;
        this.count_component = jsonRow.count_component;
        this.img = jsonRow.img;
        this.id_description = jsonRow.id_description;
    }
};