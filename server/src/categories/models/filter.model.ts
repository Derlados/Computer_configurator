
import { Column, Entity, JoinColumn, ManyToOne, OneToMany, PrimaryColumn } from "typeorm";
import { Attribute } from "../../components/models/attribute.model";
import { Category } from "./category.model";

@Entity('filters')
export class Filter {
    @PrimaryColumn({ name: "category_id", type: "int", nullable: false })
    categoryId: number;

    @PrimaryColumn({ name: "attribute_id", type: "int", nullable: false })
    attributeId: number;

    @Column({ name: "is_range", type: "boolean", nullable: true })
    isRange: boolean;

    @Column({ type: "float", nullable: true })
    step: number;

    @ManyToOne(() => Category, category => category.filters, { onDelete: "CASCADE", onUpdate: "CASCADE" })
    @JoinColumn({ name: "category_id" })
    category: Category;

    @ManyToOne(() => Attribute, attribute => attribute.filters, { onDelete: "CASCADE", onUpdate: "CASCADE" })
    @JoinColumn({ name: "attribute_id" })
    attribute: Attribute;
}