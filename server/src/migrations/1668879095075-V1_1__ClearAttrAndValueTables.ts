import { MigrationInterface, QueryRunner } from "typeorm";

export class ClearAttrAndValueTables1668879095075 implements MigrationInterface {

    public async up(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query('DELETE FROM `values` WHERE `value` is NULL');
        await queryRunner.query('DELETE FROM `attributes` WHERE name is NULL');
    }

    public async down(queryRunner: QueryRunner): Promise<void> {
    }

}
