import { IsNumber } from "class-validator";

export class CreateBuildComponentDto {
    @IsNumber()
    componentId: number;

    @IsNumber()
    count: number;
}