import { IsNotEmpty, IsString, Length } from "class-validator";

export class CreateUserDto {
    @IsNotEmpty()
    @IsString()
    username: string;

    @IsNotEmpty()
    @IsString()
    @Length(6, 50)
    password: string;

    @IsNotEmpty()
    @IsString()
    secret: string;
}   