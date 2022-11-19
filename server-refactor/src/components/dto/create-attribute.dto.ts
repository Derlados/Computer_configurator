import { IsString, Length } from "class-validator";

export class CreateAttributeDto {
    @IsString()
    @Length(0, 150)
    name: string;

    @IsString()
    @Length(0, 255)
    value: string;
}