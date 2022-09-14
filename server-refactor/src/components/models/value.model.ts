import { Column, Entity, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { ComponentAttribute } from "./component-attribute.model";

@Entity('values')
export class Value {
    @PrimaryGeneratedColumn('increment')
    id: number;

    @Column({ type: "varchar", length: 200 })
    value: string;

    @OneToMany(() => ComponentAttribute, componentAttribute => componentAttribute.value)
    attributes: ComponentAttribute[];
}