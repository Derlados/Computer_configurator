
import { Column, Entity, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { Component } from "../../components/models/component.model";
import { Filter } from "./filter.model";

@Entity('category')
export class Category {
    @PrimaryGeneratedColumn('increment', { name: 'id_category' })
    id: number;

    @Column({ type: "varchar", length: 50, nullable: false })
    category: string;

    @Column({ name: 'url_category', type: "varchar", length: 50, nullable: false })
    urlCategory: string;

    @OneToMany(() => Component, component => component.category)
    components: Component[]

    @OneToMany(() => Filter, filter => filter.category)
    filters: Filter[];
}