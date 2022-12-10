
import { Column, Entity, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { Component } from "../../components/models/component.model";
import { Filter } from "./filter.model";

@Entity('categories')
export class Category {
    @PrimaryGeneratedColumn('increment')
    id: number;

    @Column({ type: "varchar", length: 50, nullable: true })
    category: string;

    @Column({ name: 'url', type: "varchar", length: 50, nullable: true })
    url: string;

    @OneToMany(() => Component, component => component.category)
    components: Component[]

    @OneToMany(() => Filter, filter => filter.category)
    filters: Filter[];
}