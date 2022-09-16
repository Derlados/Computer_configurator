import { Type } from "class-transformer";
import { IsArray, IsBoolean, IsString, Length, ValidateNested } from "class-validator";
import { CreateBuildComponentDto } from "./create-build-component.dto";

export class CreateBuildDto {
    @IsString()
    @Length(1, 100)
    name: string;

    @IsString()
    description: string;

    @IsBoolean()
    isPublic: boolean;

    @IsArray()
    @ValidateNested()
    @Type(() => CreateBuildComponentDto)
    components: CreateBuildComponentDto[];
}