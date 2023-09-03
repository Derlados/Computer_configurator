import { IsNotEmpty, IsOptional, IsString } from "class-validator";

export class SignInUser {
    @IsNotEmpty()
    @IsString()
    idToken: string;

    @IsNotEmpty()
    @IsString()
    @IsOptional()
    username?: string;
}