import { Module } from "@nestjs/common";
import { UsersModule } from './users/users.module';
import { BuildsModule } from './builds/builds.module';
import { ComponentsModule } from './components/components.module';
import { CommentsModule } from './comments/comments.module';
import { TypeOrmModule } from "@nestjs/typeorm";
import { ConfigModule } from "@nestjs/config";
import { User } from "./users/models/user.model";
import { AuthModule } from "./auth/auth.module";
import { Category } from "./categories/models/category.model";
import { Component } from "./components/models/component.model";
import { Value } from "./components/models/value.model";
import { Attribute } from "./components/models/attribute.model";
import { ComponentAttribute } from "./components/models/component-attribute.model";
import { Filter } from "./categories/models/filter.model";
import { Build } from "./builds/models/build.model";
import { BuildComponent } from "./builds/models/build-component.model";
import { Comment } from "./comments/models/comment.model";

@Module({
    controllers: [],
    providers: [],
    imports: [
        ConfigModule.forRoot({
            isGlobal: true,
            envFilePath: `.${process.env.NODE_ENV}.env`
        }),
        TypeOrmModule.forRoot({
            type: 'mysql',
            host: process.env.DB_HOST,
            port: Number(process.env.DB_PORT),
            username: process.env.DB_USER,
            password: process.env.DB_PASSWORD,
            database: process.env.DATABASE,
            entities: [User, Category, Component, Attribute, ComponentAttribute, Value, Filter, Build, BuildComponent, Comment],
            migrations: ["dist/migrations/*{.ts,.js}"],
            migrationsRun: true,
            synchronize: false,
            multipleStatements: true,
        }),
        AuthModule,
        UsersModule,
        BuildsModule,
        ComponentsModule,
        CommentsModule
    ]
})
export class AppModule { }