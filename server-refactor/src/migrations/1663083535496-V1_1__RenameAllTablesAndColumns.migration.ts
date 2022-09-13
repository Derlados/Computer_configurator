import { MigrationInterface, QueryRunner } from "typeorm"

export class RenameAllTablesAndColumns1663083535496 implements MigrationInterface {

    public async up(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query('ALTER TABLE `attribute` CHANGE `id_characteristic` `id` INT NOT NULL');
        await queryRunner.query('ALTER TABLE `attribute` CHANGE `characteristic` `attribute` VARCHAR(100)	NULL');
        await queryRunner.query('ALTER TABLE `attribute` RENAME TO `attributes`');

        await queryRunner.query('ALTER TABLE `attribute_value` CHANGE `id_value` `id` INT NOT NULL');
        await queryRunner.query('ALTER TABLE `attribute_value` RENAME TO `values`');

        await queryRunner.query('ALTER TABLE `build` CHANGE `id_user` `user_id` INT NOT NULL');
        await queryRunner.query('ALTER TABLE `build` RENAME TO `builds`');

        await queryRunner.query('ALTER TABLE `build_comments` CHANGE `id_build` `build_id` INT NOT NULL');
        await queryRunner.query('ALTER TABLE `build_comments` CHANGE `id_user` `user_id` INT NOT NULL');
        await queryRunner.query('ALTER TABLE `build_comments` CHANGE `id_parent` `parent_id` INT NULL');
        await queryRunner.query('ALTER TABLE `build_comments` CHANGE `creation_date` `created_at` DATETIME NOT NULL');

        await queryRunner.query('ALTER TABLE `build_components` CHANGE `id_build` `build_id` INT NOT NULL');
        await queryRunner.query('ALTER TABLE `build_components` CHANGE `id_component` `component_id` INT NOT NULL');

        await queryRunner.query('ALTER TABLE `category` CHANGE `id_category` `category_id` INT NOT NULL');
        await queryRunner.query('ALTER TABLE `category` CHANGE `url_category` `url` VARCHAR(50) NOT NULL');
        await queryRunner.query('ALTER TABLE `category` RENAME TO `categories`');

        await queryRunner.query('ALTER TABLE `component` CHANGE `id_component` `id` INT NOT NULL');
        await queryRunner.query('ALTER TABLE `component` CHANGE `id_category` `category_id` INT NOT NULL');
        await queryRunner.query('ALTER TABLE `component` CHANGE `url_full` `url` TEXT NULL');
        await queryRunner.query('ALTER TABLE `component` CHANGE `date_updated` `updated_at` DATETIME NOT NULL');
        await queryRunner.query('ALTER TABLE `component` RENAME TO `components`');

        await queryRunner.query('ALTER TABLE `comp_attr` CHANGE `id_characteristic` `attribute_id` INT NOT NULL');
        await queryRunner.query('ALTER TABLE `comp_attr` CHANGE `id_component` `component_id` INT NOT NULL');
        await queryRunner.query('ALTER TABLE `comp_attr` CHANGE `id_value` `value_id` INT NULL');
        await queryRunner.query('ALTER TABLE `comp_attr` RENAME TO `component_attributes`');

        await queryRunner.query('ALTER TABLE `filters` CHANGE `id_category` `category_id` INT NOT NULL');
        await queryRunner.query('ALTER TABLE `filters` CHANGE `id_characteristic` `attribute_id` INT NOT NULL');
        await queryRunner.query('ALTER TABLE `filters` CHANGE `isRange` `is_range` BOOLEAN DEFAULT FALSE');

        await queryRunner.query('ALTER TABLE `users` CHANGE `photoUrl` `photo_url` VARCHAR(255) NULL');
    }

    public async down(queryRunner: QueryRunner): Promise<void> {
    }

}
