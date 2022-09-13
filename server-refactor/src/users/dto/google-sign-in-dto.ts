import { IsNotEmpty, IsOptional, IsString } from "class-validator";

export class GoogleSignInDto {
    @IsNotEmpty()
    @IsString()
    @IsOptional()
    username: string;

    @IsNotEmpty()
    @IsString()
    googleId: string;

    @IsNotEmpty()
    @IsString()
    email: string;

    @IsNotEmpty()
    @IsString()
    @IsOptional()
    photoUrl: string;
}