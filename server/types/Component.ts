export class Component {
    // Оптимизация, чтобы не искать Id. Реализация на сервере дает возможность корректировать id без клиента
    public static readonly categoriesId: Map<string, number> = new Map<string, number>([
        ["CPU", 1],
        ["GPU", 2],
        ["MOTHERBOARD", 3],
        ["SSD", 4],
        ["HDD", 5],
        ["RAM", 6],
        ["POWER_SUPPLY", 7],
        ["CASE", 8],
        ["COOLER", 9]
    ]);

    public static getCategoryById(id) {
        for (const [category, idCategory] of Component.categoriesId) {
            if (idCategory == id) {
                return category;
            }
        }
    }

    id: number;
    name: string;
    price: number;
    imageUrl: string;
    attributes: Object;
    isActual: boolean;

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