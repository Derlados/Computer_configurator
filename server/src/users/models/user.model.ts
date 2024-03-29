import { Exclude, Expose } from "class-transformer";
import { Build } from "src/builds/models/build.model";
import { ReportedComment } from "src/comments/models/reported-comment.model";
import { AccessGroups } from "src/constants/AccessGroups";
import { Role } from "src/roles/models/role.model";
import { AfterLoad, Column, Entity, JoinTable, ManyToMany, ManyToOne, OneToMany, PrimaryColumn, PrimaryGeneratedColumn } from "typeorm";
import { Comment } from "../../comments/models/comment.model";

@Entity('users')
export class User {
    @PrimaryColumn({ type: "varchar", length: 255 })
    @Exclude()
    id: string;

    @Column({ type: "varchar", length: 120, nullable: true })
    @Expose({ groups: [AccessGroups.USER_OWNER, AccessGroups.ALL_USERS] })
    username: string;

    @Column({ type: "varchar", length: 255, nullable: true, default: null })
    @Exclude()
    providerId: string;

    @Column({ type: "varchar", length: 255, nullable: true, default: null })
    @Exclude()
    email: string;

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
    @Expose({ groups: [AccessGroups.USER_OWNER, AccessGroups.ALL_USERS] })
    getPhoto() {
        if (this.photo && !this.photo.includes('http')) {
            this.photo = `${process.env.STATIC_API}/${this.photo}`
        }
    }

    @AfterLoad()
    getUsername() {
        if (this.username.includes(process.env.GOOGLE_ADD_NICKNAME_SPEC_SYMBOLS)) {
            const usernameParts = this.username.split('-');
            usernameParts.splice(0, Number(process.env.GOOGLE_ADD_NICKNAME_SPEC_SYMBOLS));

            this.username = usernameParts.join('-');
        }
    }

    @OneToMany(() => ReportedComment, bc => bc.users)
    reportedComments: ReportedComment[];
}