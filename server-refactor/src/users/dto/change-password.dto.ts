import { IsNotEmpty, IsString } from "class-validator";

export class ChangePasswordDto {
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