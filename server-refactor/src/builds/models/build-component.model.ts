
import { Component } from "src/components/models/component.model";
import { Entity, JoinColumn, ManyToOne, OneToMany, PrimaryColumn, PrimaryGeneratedColumn } from "typeorm";
import { Build } from "./build.model";

@Entity('build_components')
export class BuildComponent {

    @PrimaryColumn({ name: "id_build", type: "int", nullable: false })
    buildId: number;

    @PrimaryColumn({ name: "id_component", type: "int", nullable: false })
    componentId: number;

    @PrimaryColumn({ type: "int", default: 1 })
    count: number;

    @ManyToOne(() => Build, build => build.components, { onDelete: "CASCADE", onUpdate: "CASCADE" })
    @JoinColumn({ name: "id_build" })
    build: Build;

    @ManyToOne(() => Component, build => build.buildComponents, { onDelete: "CASCADE", onUpdate: "CASCADE" })
    @JoinColumn({ name: "id_component" })
    component: Component;
}