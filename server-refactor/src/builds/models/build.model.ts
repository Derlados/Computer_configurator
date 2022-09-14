
import { User } from "src/users/models/user.model";
import { Column, Entity, JoinColumn, ManyToOne, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { Comment } from "../../comments/models/comment.model";
import { BuildComponent } from "./build-component.model";

@Entity('builds')
export class Build {
    @PrimaryGeneratedColumn("increment")
    id: number;

    @Column({ name: "user_id", type: "int", nullable: true })
    userId: number;

    @Column({ type: "varchar", length: 100, nullable: true })
    name: string;

    @Column({ type: "text", nullable: true })
    description: string;

    @Column({ name: "is_public", type: "boolean", default: false })
    isPublic: string;

    @Column({ name: "publish_date", type: "datetime", default: () => "CURRENT_TIMESTAMP()" })
    publishDate: Date;

    @ManyToOne(() => User, user => user.builds)
    @JoinColumn({ name: "user_id" })
    user: User;

    @OneToMany(() => Comment, comment => comment.build)
    comments: Comment[];

    @OneToMany(() => BuildComponent, buildComponent => buildComponent.build)
    components: BuildComponent[];
}