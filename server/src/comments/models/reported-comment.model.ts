import { User } from "src/users/models/user.model";
import { Entity, JoinColumn, ManyToOne, PrimaryColumn } from "typeorm";
import { Comment } from "./comment.model";

@Entity('reported_comments')
export class ReportedComment {
    @PrimaryColumn({ name: "user_id" })
    userId: string;

    @PrimaryColumn({ name: "comment_id" })
    commentId: number;

    @ManyToOne(() => User, user => user.reportedComments, { onUpdate: "CASCADE", onDelete: "CASCADE" })
    @JoinColumn({ name: "user_id" })
    users: User;

    @ManyToOne(() => Comment, comment => comment.reportedByUsers, { onUpdate: "CASCADE", onDelete: "CASCADE" })
    @JoinColumn({ name: "comment_id" })
    comment: Comment;
}