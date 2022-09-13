
import { Column, Entity, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { Filter } from "../../categories/models/filter.model";
import { ComponentAttribute } from "./component-attribute.model";

@Entity('attribute')
export class Attribute {
    @PrimaryGeneratedColumn("increment", { name: "id_characteristic" })
    id: number;

    @Column({ name: "characteristic", type: "varchar", length: 100, nullable: false })
    attribute: string;

    @Column({ name: "is_preview", type: "boolean", nullable: false })
    isPreview: boolean;

    @Column({ name: "preview_text", type: "varchar", length: 30, nullable: true })
    prevText: string;

    @OneToMany(() => ComponentAttribute, componentAttribute => componentAttribute.attribute)
    componentAttributes: ComponentAttribute[];

    @OneToMany(() => Filter, filter => filter.attribute)
    filters: Filter[];
}