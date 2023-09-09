import { IsNotEmpty, IsOptional, IsString } from "class-validator";

export class CheckAuthStatusDto {
    @IsNotEmpty()
    @IsString()
    idToken: string;
}