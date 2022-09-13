import { Comment } from "src/comments/models/comment.model";
import { Column, Entity, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { BuildComponent } from "./build-component.model";

@Entity('build')
export class Build {
    @PrimaryGeneratedColumn("increment")
    id: number;

    @Column({ name: "id_user", type: "int", nullable: false })
    userId: number;

    @Column({ type: "varchar", length: 100, nullable: false })
    name: string;

    @Column({ type: "text", nullable: false })
    description: string;

    @Column({ name: "is_public", type: "boolean", default: false })
    isPublic: string;

    @Column({ name: "publish_date", type: "datetime", default: () => "CURRENT_TIMESTAMP()" })
    publishDate: Date;

    @OneToMany(() => Comment, comment => comment.build)
    comments: Comment[];

    @OneToMany(() => BuildComponent, buildComponent => buildComponent.build)
    components: BuildComponent[];
}