import { Column, Entity, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { ComponentAttribute } from "./component-attribute.model";

@Entity('attribute_value')
export class Value {
    @PrimaryGeneratedColumn('increment', { name: "id_value" })
    id: number;

    @Column({ type: "varchar", length: 255, nullable: true, unique: true })
    value: string;

    @OneToMany(() => ComponentAttribute, componentAttribute => componentAttribute.value)
    attributes: ComponentAttribute[];
}