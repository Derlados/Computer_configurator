import { Exclude } from "class-transformer";
import { Build } from "src/builds/models/build.model";
import { Column, CreateDateColumn, Entity, ManyToOne, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { Comment } from "../../comments/models/comment.model";

@Entity('users')
export class User {
    @PrimaryGeneratedColumn('increment')
    id: number;

    @Column({ type: "varchar", length: 50, nullable: true })
    username: string;

    @Column({ type: "varchar", length: 255, nullable: true, default: null })
    password: string;

    @Column({ type: "varchar", length: 255, nullable: true, default: null })
    secret: string;

    @Column({ unique: true, default: null, type: "varchar", length: 255, nullable: true })
    email: string;

    @Column({ unique: true, default: null, type: "varchar", length: 255, nullable: true })
    googleId: string;

    @Column({ default: null, type: "varchar", length: 255, nullable: true })
    photo: string;

    @Column({ type: "datetime", default: () => "CURRENT_TIMESTAMP()" })
    date: Date;

    @OneToMany(() => Build, builds => builds.user)
    builds: Build[];

    @OneToMany(() => Comment, comment => comment.user)
    comments: Comment[];
}