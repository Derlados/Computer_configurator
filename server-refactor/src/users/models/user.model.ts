import { Exclude, Expose } from "class-transformer";
import { Build } from "src/builds/models/build.model";
import { AccessGroups } from "src/constants/AccessGroups";
import { Role } from "src/roles/models/role.model";
import { AfterLoad, Column, CreateDateColumn, Entity, JoinTable, ManyToMany, ManyToOne, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { Comment } from "../../comments/models/comment.model";

@Entity('users')
export class User {
    @PrimaryGeneratedColumn('increment')
    @Expose({ groups: [AccessGroups.ALL_USERS] })
    id: number;

    @Column({ type: "varchar", length: 50, nullable: true })
    @Expose({ groups: [AccessGroups.USER_OWNER, AccessGroups.ALL_USERS] })
    username: string;

    @Column({ type: "varchar", length: 255, nullable: true, default: null })
    @Exclude()
    password?: string;

    @Column({ type: "varchar", length: 255, nullable: true, default: null })
    @Exclude()
    secret?: string;

    @Column({ unique: true, type: "varchar", length: 255, nullable: true, default: null })
    @Expose({ groups: [AccessGroups.USER_OWNER] })
    email?: string;

    @Column({ unique: true, type: "varchar", length: 255, nullable: true, default: null })
    @Exclude()
    googleId?: string;

    @Column({ default: null, type: "varchar", length: 255, nullable: true })
    @Expose({ groups: [AccessGroups.USER_OWNER, AccessGroups.ALL_USERS] })
    photo?: string;

    @Column({ type: "datetime", default: () => "CURRENT_TIMESTAMP()" })
    @Exclude()
    date: Date;

    @OneToMany(() => Build, builds => builds.user)
    @Exclude()
    builds: Build[];

    @OneToMany(() => Comment, comments => comments.user)
    @Exclude()
    comments: Comment[];

    @ManyToMany(() => Role, roles => roles.users, { onUpdate: "CASCADE", onDelete: "CASCADE" })
    @JoinTable()
    @Exclude()
    roles: Role[];

    @AfterLoad()
    getPhoto() {
        if (this.photo && !this.photo.includes('http')) {
            this.photo = `${process.env.STATIC_API}/${this.photo}`
        }
    }
}