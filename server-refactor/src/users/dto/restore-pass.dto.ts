import { IsNotEmpty, IsString } from "class-validator";

export class RestorePassDto {
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