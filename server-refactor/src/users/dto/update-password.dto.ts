import { IsNotEmpty, IsString } from "class-validator";

export class UpdatePasswordDto {
    @IsNotEmpty()
    @IsString()
    username: string;

    @IsNotEmpty()
    @IsString()
    secret: string;

    @IsNotEmpty()
    @IsString()
    newPassword: string;
}