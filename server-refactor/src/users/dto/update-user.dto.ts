import { IsNotEmpty, IsOptional, IsString } from "class-validator";

export class UpdateUserDto {
    @IsNotEmpty()
    @IsString()
    @IsOptional()
    username: string;
}