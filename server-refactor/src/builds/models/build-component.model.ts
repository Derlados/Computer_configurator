
import { Exclude } from "class-transformer";
import { Entity, JoinColumn, ManyToOne, OneToMany, PrimaryColumn, PrimaryGeneratedColumn } from "typeorm";
import { Component } from "../../components/models/component.model";
import { Build } from "./build.model";

@Entity('build_components')
export class BuildComponent {

    @PrimaryColumn({ name: "build_id", type: "int", nullable: false })
    @Exclude()
    buildId: number;

    @PrimaryColumn({ name: "component_id", type: "int", nullable: false })
    @Exclude()
    componentId: number;

    @PrimaryColumn({ type: "int", default: 1 })
    count: number;

    @ManyToOne(() => Build, build => build.components, { onDelete: "CASCADE", onUpdate: "CASCADE" })
    @JoinColumn({ name: "build_id" })
    build: Build;

    @ManyToOne(() => Component, build => build.buildComponents, { onDelete: "CASCADE", onUpdate: "CASCADE" })
    @JoinColumn({ name: "component_id" })
    component: Component;
}