import { MigrationInterface, QueryRunner } from "typeorm"

export class UpdateCommentParentIds1663083518431 implements MigrationInterface {

    public async up(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query('DELETE FROM `build_comments` WHERE id_parent != -1');
        await queryRunner.query('ALTER TABLE `build_comments` MODIFY `id_parent` INT NULL');
        await queryRunner.query('UPDATE`build_comments` SET `id_parent` = NULL WHERE `id_parent`= -1');
    }

    public async down(queryRunner: QueryRunner): Promise<void> {
    }

}
