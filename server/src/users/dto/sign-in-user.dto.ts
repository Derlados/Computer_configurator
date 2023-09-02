import { IsNotEmpty, IsOptional, IsString } from "class-validator";

export class SignInUser {
    @IsNotEmpty()
    @IsString()
    uid: string;

    @IsNotEmpty()
    @IsString()
    providerId: string;

    @IsNotEmpty()
    @IsString()
    username: string;

    @IsNotEmpty()
    @IsString()
    email: string;

    @IsNotEmpty()
    @IsString()
    @IsOptional()
    photo?: string;
}