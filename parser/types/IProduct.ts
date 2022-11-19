export interface IProduct {
    id: number;
    categoryId: number;
    name: string;
    price: string;
    img: string;
    url: string;
    shop: string;
    isActual: boolean;
    attributes: [];
}