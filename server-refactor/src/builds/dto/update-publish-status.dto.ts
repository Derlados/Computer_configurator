import { IsBoolean } from "class-validator";

export class UpdatePublishStatusDto {
    @IsBoolean()
    isPublic: boolean;
}