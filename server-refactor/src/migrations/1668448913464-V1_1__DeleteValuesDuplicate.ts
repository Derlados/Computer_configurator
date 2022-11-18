import { MigrationInterface, QueryRunner } from "typeorm";

export class DeleteValuesDuplicate668448913464 implements MigrationInterface {

    public async up(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`
            DELETE T1 FROM values AS T1  
            INNER JOIN values AS T2   
            WHERE T1.id < T2.id AND T1.value = T2.value;  
        `);
    }

    public async down(queryRunner: QueryRunner): Promise<void> {
    }

}
