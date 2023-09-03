
import { Exclude } from "class-transformer";
import { Categories } from "src/constants/Categories";
import { User } from "src/users/models/user.model";
import { AfterLoad, Column, Entity, JoinColumn, ManyToOne, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { Comment } from "../../comments/models/comment.model";
import { BuildComponent } from "./build-component.model";

@Entity('builds')
export class Build {
    @PrimaryGeneratedColumn("increment")
    id: number;

    @Column({ name: "user_id", type: "int", nullable: false })
    @Exclude()
    userId: string;

    @Column({ type: "varchar", length: 100, nullable: false })
    name: string;

    @Column({ type: "text", nullable: false })
    description: string;

    @Column({ name: "is_public", type: "boolean", default: false })
    isPublic: boolean;

    @Column({ name: "reports", type: "int", default: 0 })
    @Exclude()
    reports: number;

    @Column({ name: "publish_date", type: "datetime", default: () => "CURRENT_TIMESTAMP()" })
    publishDate: Date;

    price: number;

    image?: string;

    components: Map<string, BuildComponent[]>

    @ManyToOne(() => User, user => user.builds, { onUpdate: "CASCADE", onDelete: "CASCADE" })
    @JoinColumn({ name: "user_id" })
    user: User;

    @OneToMany(() => Comment, comment => comment.build)
    comments: Comment[];

    @OneToMany(() => BuildComponent, buildComponent => buildComponent.build)
    @Exclude()
    buildComponents: BuildComponent[];

    @AfterLoad()
    getPrice() {
        this.price = 0;

        if (this.buildComponents) {
            this.buildComponents.forEach(c => {
                this.price += c.component.price * c.count
            })
        }
    }

    @AfterLoad()
    getImage() {
        if (this.buildComponents) {
            this.image = this.buildComponents.find(c => c.component.categoryId == Categories.CASE)?.component.img
        }
    }

    @AfterLoad()
    getComponents() {
        this.components = new Map()
        if (this.buildComponents) {
            this.buildComponents.forEach(bc => {
                const categoryUrl = bc.component.category.url;

                if (!this.components.has(categoryUrl)) {
                    this.components.set(categoryUrl, [])
                }

                this.components.get(categoryUrl).push(bc)
            })
        }
    }
}