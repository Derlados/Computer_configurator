import { Column, Entity, Index, JoinColumn, ManyToOne, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { Build } from "../../builds/models/build.model";
import { User } from "../../users/models/user.model";

@Entity('build_comments')
export class Comment {
    @PrimaryGeneratedColumn('increment')
    id: number;

    @Column({ name: "id_build", type: "int", nullable: false })
    buildId: number;

    @Column({ name: "id_user", type: "int", nullable: false })
    userId: number;

    @Column({ name: "text", type: "text", nullable: false })
    text: string;

    @Column({ name: "creation_date", type: "datetime", default: () => "CURRENT_TIMESTAMP()" })
    creationDate: Date;

    @Column({ name: "id_parent", type: "int", nullable: true })
    parentId: number;

    @ManyToOne(() => Build, build => build.comments, { onDelete: "CASCADE", onUpdate: "CASCADE" })
    @JoinColumn({ name: "id_build" })
    build: Build;

    @ManyToOne(() => User, user => user.comments, { onDelete: "CASCADE", onUpdate: "CASCADE" })
    @JoinColumn({ name: "id_user" })
    user: User;

    @ManyToOne(() => Comment, comment => comment.children, { onDelete: "CASCADE", onUpdate: "CASCADE" })
    @JoinColumn({ name: "id_parent" })
    parent: Comment;

    @OneToMany(() => Comment, comment => comment.parent)
    children: Comment[];
}