import { MigrationInterface, QueryRunner } from "typeorm"

export class DropAllFK1663081172157 implements MigrationInterface {
    async up(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.dropForeignKey('build', 'id_user_fk');
        await queryRunner.dropForeignKey('build_comments', 'build_comments_id_build_fk');
        await queryRunner.dropForeignKey('build_comments', 'build_comments_id_user_fk');
        await queryRunner.dropForeignKey('build_components', 'id_build_fk');
        await queryRunner.dropForeignKey('build_components', 'id_component_fk');
        await queryRunner.dropForeignKey('component', 'component_category_fk');
        await queryRunner.dropForeignKey('component', 'pc_component_category_fk');
        await queryRunner.dropForeignKey('comp_attr', 'components_characteristic_component_fk');
        await queryRunner.dropForeignKey('comp_attr', 'components_characteristic_characteristic_fk');
        await queryRunner.dropForeignKey('comp_attr', 'attribute_value_fk');
        await queryRunner.dropForeignKey('comp_attr', 'characteristic_characteristic_pc_component_fk');
        await queryRunner.dropForeignKey('filters', 'id_characteristic_fk');
        await queryRunner.dropForeignKey('filters', 'id_category_fk');
    }

    async down(queryRunner: QueryRunner): Promise<void> {

    }
}