import { Exclude } from "class-transformer";
import { Column, Entity, JoinColumn, ManyToOne, PrimaryColumn, PrimaryGeneratedColumn } from "typeorm";
import { Attribute } from "./attribute.model";
import { Component } from "./component.model";
import { Value } from "./value.model";

@Entity('component_attributes')
export class ComponentAttribute {
    @PrimaryColumn({ name: "attribute_id", type: "int", nullable: false })
    @Exclude()
    attributeId: number;

    @PrimaryColumn({ name: "component_id", type: "int", nullable: false })
    @Exclude()
    compoentId: number;

    @Column({ name: "value_id", type: "int", nullable: true })
    @Exclude()
    valueId: number;

    @ManyToOne(() => Attribute, attribute => attribute.componentAttributes, { onUpdate: "CASCADE", onDelete: "CASCADE" })
    @JoinColumn({ name: "attribute_id" })
    attribute: Attribute;

    @ManyToOne(() => Component, compoent => compoent.attributes, { onUpdate: "CASCADE", onDelete: "CASCADE" })
    @JoinColumn({ name: "component_id" })
    component: Component;

    @ManyToOne(() => Value, value => value.attributes, { onUpdate: "CASCADE", onDelete: "CASCADE" })
    @JoinColumn({ name: "value_id" })
    value: Value;
}