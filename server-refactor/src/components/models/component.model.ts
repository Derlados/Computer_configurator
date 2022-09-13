import { BuildComponent } from "src/builds/models/build-component.model";
import { Category } from "src/categories/models/category.model";
import { Column, Entity, JoinTable, ManyToOne, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { ComponentAttribute } from "./component-attribute.model";

@Entity('component')
export class Component {
    @PrimaryGeneratedColumn('increment', { name: 'id_component' })
    id: number;

    @Column({ name: "id_category", type: "int", nullable: false })
    idCategory: number;

    @Column({ type: "varchar", length: 255, nullable: true })
    name: string;

    @Column({ type: "decimal", nullable: true })
    price: number;

    @Column({ type: "text", nullable: true })
    img: string;

    @Column({ name: "url_full", type: "text", nullable: true })
    urlFull: string;

    @Column({ type: "varchar", length: 255, nullable: true })
    shop: string;

    @Column({ name: "date_updated", type: "datetime", nullable: true, default: () => "CURRENT_TIMESTAMP()" })
    dateUpdated: Date;

    @Column({ name: "is_actual", type: "boolean", nullable: true })
    isActual: boolean;

    @ManyToOne(() => Category, category => category.components, { onDelete: "CASCADE", onUpdate: "CASCADE" })
    @JoinTable()
    category: Category;

    @OneToMany(() => ComponentAttribute, componentAttribute => componentAttribute.component)
    attributes: ComponentAttribute[];

    @OneToMany(() => BuildComponent, buildComponent => buildComponent.component)
    buildComponents: BuildComponent[];
}
