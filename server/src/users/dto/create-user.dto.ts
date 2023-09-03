import { IsNotEmpty, IsOptional, IsString } from "class-validator";

export class CreateUserDto {
    @IsNotEmpty()
    @IsString()
    @IsOptional()
    id: string;

    @IsNotEmpty()
    @IsString()
    @IsOptional()
    providerId: string;

    @IsNotEmpty()
    @IsString()
    @IsOptional()
    username: string;

    @IsNotEmpty()
    @IsString()
    @IsOptional()
    email: string;

    @IsNotEmpty()
    @IsString()
    photo?: string;
}