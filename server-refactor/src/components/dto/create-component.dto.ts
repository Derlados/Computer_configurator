import { ComponentDto } from "./component.dto";
import { CreateAttributeDto } from "./create-attribute.dto";

export class CreateComponentDto extends ComponentDto {
    attributes: CreateAttributeDto[];
}
