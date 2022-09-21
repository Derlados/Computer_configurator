
import { Exclude } from "class-transformer";
import { Entity, PrimaryGeneratedColumn, Column, ManyToOne, JoinTable, OneToMany, JoinColumn } from "typeorm";
import { BuildComponent } from "../../builds/models/build-component.model";
import { Category } from "../../categories/models/category.model";
import { ComponentAttribute } from "./component-attribute.model";

@Entity('components')
export class Component {
    @PrimaryGeneratedColumn('increment')
    id: number;

    @Column({ name: "category_id", type: "int", nullable: false })
    categoryId: number;

    @Column({ type: "varchar", length: 255, nullable: true })
    name: string;

    @Column({ type: "decimal", precision: 10, scale: 0, nullable: true })
    price: number;

    @Column({ type: "text", nullable: true })
    img: string;

    @Column({ name: "url", type: "text", nullable: true })
    url: string;

    @Column({ type: "varchar", length: 255, nullable: true })
    shop: string;

    @Column({ name: "updated_at", type: "datetime", default: () => "CURRENT_TIMESTAMP()" })
    updatedAt: Date;

    @Column({ name: "is_actual", type: "boolean", default: false })
    isActual: boolean;

    @ManyToOne(() => Category, category => category.components, { onDelete: "CASCADE", onUpdate: "CASCADE" })
    @JoinColumn({ name: "category_id" })
    @Exclude()
    category: Category;

    @OneToMany(() => ComponentAttribute, componentAttribute => componentAttribute.component)
    attributes: ComponentAttribute[];

    @OneToMany(() => BuildComponent, buildComponent => buildComponent.component)
    buildComponents: BuildComponent[];
}
