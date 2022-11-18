import { Type } from "class-transformer";
import { IsArray, ValidateNested } from "class-validator";
import { ComponentDto } from "./component.dto";
import { CreateAttributeDto } from "./create-attribute.dto";

export class CreateComponentDto extends ComponentDto {
    @IsArray()
    @Type(() => CreateAttributeDto)
    @ValidateNested({ each: true })
    attributes: CreateAttributeDto[];
}
