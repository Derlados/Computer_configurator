
import { Column, Entity, JoinColumn, ManyToOne, OneToMany, PrimaryColumn } from "typeorm";
import { Attribute } from "../../components/models/attribute.model";
import { Category } from "./category.model";

@Entity('filters')
export class Filter {
    @PrimaryColumn({ name: "id_category", type: "int", nullable: false })
    categoryId: number;

    @PrimaryColumn({ name: "id_characteristic", type: "int", nullable: false })
    attributeId: number;

    @Column({ name: "isRange", type: "boolean", nullable: false })
    isRange: number;

    @Column({ type: "int", nullable: true })
    step: number;

    @ManyToOne(() => Category, category => category.filters, { onDelete: "CASCADE", onUpdate: "CASCADE" })
    @JoinColumn({ name: "id_category" })
    category: Category;

    @ManyToOne(() => Attribute, attribute => attribute.filters, { onDelete: "CASCADE", onUpdate: "CASCADE" })
    @JoinColumn({ name: "id_characteristic" })
    attribute: Attribute;
}