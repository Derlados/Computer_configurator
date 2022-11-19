import { MigrationInterface, QueryRunner } from "typeorm";

export class DeleteValuesDuplicate1663081172156 implements MigrationInterface {

    public async up(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`
            DELETE T1 FROM  \`attribute_value\` AS T1  
            INNER JOIN  \`attribute_value\` AS T2   
            WHERE T1.id_value < T2.id_value AND T1.value = T2.value;  
        `);

        await queryRunner.query('ALTER TABLE `attribute_value` CHANGE `value` `value` VARCHAR(255) NOT NULL');
    }

    public async down(queryRunner: QueryRunner): Promise<void> {
    }

}
