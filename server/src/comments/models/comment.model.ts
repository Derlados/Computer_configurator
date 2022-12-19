import { Column, Entity, Index, JoinColumn, JoinTable, ManyToMany, ManyToOne, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { Build } from "../../builds/models/build.model";
import { User } from "../../users/models/user.model";
import { ReportedComment } from "./reported-comment.model";

@Entity('build_comments')
export class Comment {
    @PrimaryGeneratedColumn('increment')
    id: number;

    @Column({ name: "build_id", type: "int", nullable: false })
    buildId: number;

    @Column({ name: "user_id", type: "int", nullable: false })
    userId: number;

    @Column({ name: "text", type: "text", nullable: false })
    text: string;

    @Column({ name: "created_at", type: "datetime", default: () => "CURRENT_TIMESTAMP()" })
    creationDate: Date;

    @Column({ name: "parent_id", type: "int", nullable: true })
    parentId?: number;

    @ManyToOne(() => Build, build => build.comments, { onDelete: "CASCADE", onUpdate: "CASCADE" })
    @JoinColumn({ name: "build_id" })
    build: Build;

    @ManyToOne(() => User, user => user.comments, { onDelete: "CASCADE", onUpdate: "CASCADE" })
    @JoinColumn({ name: "user_id" })
    user: User;

    @ManyToOne(() => Comment, comment => comment.children, { onDelete: "CASCADE", onUpdate: "CASCADE" })
    @JoinColumn({ name: "parent_id" })
    parent: Comment;

    @OneToMany(() => Comment, comment => comment.parent)
    children: Comment[];

    @OneToMany(() => ReportedComment, bc => bc.comment)
    reportedByUsers: ReportedComment[];
}