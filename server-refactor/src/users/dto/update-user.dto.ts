import { IsNotEmpty, IsOptional, IsString } from "class-validator";

export class updateUserDto {
    @IsNotEmpty()
    @IsString()
    @IsOptional()
    username: string;
}