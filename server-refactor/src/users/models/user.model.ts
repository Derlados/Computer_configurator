import { Exclude } from "class-transformer";
import { Comment } from "src/comments/models/comment.model";
import { Column, CreateDateColumn, Entity, ManyToOne, OneToMany, PrimaryGeneratedColumn } from "typeorm";

@Entity('users')
export class User {
    @PrimaryGeneratedColumn('increment')
    id: number;

    @Column({ type: "varchar", length: 50, unique: false, nullable: false })
    username: string;

    @Column({ type: "varchar", length: 255, default: null })
    password: string;

    @Column({ default: null })
    secret: string;

    @Column({ unique: true, default: null, type: "varchar", length: 255, nullable: true })
    email: string;

    @Column({ unique: true, default: null, type: "varchar", length: 255, nullable: true })
    googleId: string;

    @Column({ default: null, type: "varchar", length: 255, nullable: true })
    photoUrl: string;

    @Column({ type: "datetime", default: () => "CURRENT_TIMESTAMP()" })
    date: Date;

    @OneToMany(() => Comment, comment => comment.user)
    comments: Comment[];
}