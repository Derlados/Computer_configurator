export enum Category {
    CPU = "CPU",
    GPU = "GPU",
    MOTHERBOARD = "MOTHERBOARD",
    SSD = "SSD",
    HDD = "HDD",
    RAM = "RAM",
    POWER_SUPPLY = "POWER_SUPPLY",
    CASE = "CASE",
    COOLER = "COOLER"
}
// Оптимизация, чтобы не искать Id. Реализация на сервере дает возможность корректировать id без клиента
export const categoriesId: Map<string, number> = new Map<string, number>([
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

export function getCategoryById(id) {
    for (const [category, idCategory] of categoriesId) {
        if (idCategory == id) {
            return category;
        }
    }
}