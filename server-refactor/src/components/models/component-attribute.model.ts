import { Column, Entity, JoinColumn, ManyToOne, PrimaryColumn, PrimaryGeneratedColumn } from "typeorm";
import { Attribute } from "./attribute.model";
import { Component } from "./component.model";
import { Value } from "./value.model";

@Entity('comp-attr')
export class ComponentAttribute {
    @PrimaryColumn({ name: "id_characteristic" })
    attributeId: number;

    @PrimaryColumn({ name: "id_component" })
    compoentId: number;

    @Column({ name: "id_value", unique: true })
    valueId: number;

    @ManyToOne(() => Attribute, attribute => attribute.componentAttributes, { onUpdate: "CASCADE", onDelete: "CASCADE" })
    @JoinColumn({ name: "id_characteristic" })
    attribute: Attribute;

    @ManyToOne(() => Component, compoent => compoent.attributes, { onUpdate: "CASCADE", onDelete: "CASCADE" })
    @JoinColumn({ name: "id_component" })
    component: Component;

    @ManyToOne(() => Value, value => value.attributes, { onUpdate: "CASCADE", onDelete: "CASCADE" })
    @JoinColumn({ name: "id_value" })
    value: Value;
}