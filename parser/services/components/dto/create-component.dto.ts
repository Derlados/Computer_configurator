import { ComponentDto } from "./component.dto";
import { CreateAttributeDto } from "./create-attribute.dto";

export interface CreateComponentDto extends ComponentDto {
    attributes: CreateAttributeDto[];
}
