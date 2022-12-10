
import { Exclude } from "class-transformer";
import { AfterLoad, Column, Entity, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { Filter } from "../../categories/models/filter.model";
import { ComponentAttribute } from "./component-attribute.model";
import { Value } from "./value.model";

@Entity('attributes')
export class Attribute {
    @PrimaryGeneratedColumn()
    id: number;

    @Column({ type: "varchar", length: 150, nullable: false, unique: true })
    name: string;

    @Column({ name: "is_preview", type: "boolean", nullable: true, default: false })
    isPreview: boolean;

    @Column({ name: "preview_text", type: "varchar", length: 30, nullable: true })
    prevText: string;

    @OneToMany(() => ComponentAttribute, componentAttribute => componentAttribute.attribute)
    @Exclude()
    componentAttributes: ComponentAttribute[];

    @OneToMany(() => Filter, filter => filter.attribute)
    @Exclude()
    filters: Filter[];

    allValues: Value[];

    @AfterLoad()
    getAllValues() {
        const uniqueValues = new Map<number, Value>();
        if (this.componentAttributes) {
            this.componentAttributes.forEach(ca => uniqueValues.set(ca.value.id, ca.value));
            this.allValues = Array.from(uniqueValues.values());
        }
    }
}