
import { Column, Entity, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { Filter } from "../../categories/models/filter.model";
import { ComponentAttribute } from "./component-attribute.model";

@Entity('attributes')
export class Attribute {
    @PrimaryGeneratedColumn()
    id: number;

    @Column({ type: "varchar", length: 100, nullable: true })
    name: string;

    @Column({ name: "is_preview", type: "boolean", nullable: true })
    isPreview: boolean;

    @Column({ name: "preview_text", type: "varchar", length: 30, nullable: true })
    prevText: string;

    @OneToMany(() => ComponentAttribute, componentAttribute => componentAttribute.attribute)
    componentAttributes: ComponentAttribute[];

    @OneToMany(() => Filter, filter => filter.attribute)
    filters: Filter[];
}