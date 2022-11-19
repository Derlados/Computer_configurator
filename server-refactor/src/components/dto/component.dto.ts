import { IsBoolean, IsDecimal, IsNumber, IsString } from "class-validator";

export class ComponentDto {
    @IsNumber()
    categoryId: number;

    @IsString()
    name: string;

    @IsDecimal()
    price: number;

    @IsString()
    img: string;

    @IsString()
    url: string;

    @IsString()
    shop: string;

    @IsBoolean()
    isActual: boolean;
}