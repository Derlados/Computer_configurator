package com.derlados.computer_conf.models

import com.derlados.computer_conf.consts.ComponentCategory
import java.util.*

class Build : Cloneable, BuildData {
    override var components = HashMap<ComponentCategory, Component>() // Комплетующие
    override var price: Float = 0.0F // Цена сборки
    override var name: String = ""
    override var description: String = "" // Описание в сборке

    var id: String = UUID.randomUUID().toString().replace("-", "")

    override var countGoods = HashMap<ComponentCategory, Int>() // Хранится в отдельной мапе, так как только SSD, RAM и HDD можно взять несколько

    val isComplete: Boolean
        get() = components[ComponentCategory.CPU] != null && components[ComponentCategory.MOTHERBOARD] != null
                && components[ComponentCategory.GPU] != null && components[ComponentCategory.POWER_SUPPLY] != null
                && components[ComponentCategory.RAM] != null && components[ComponentCategory.CASE] != null
                && (components[ComponentCategory.HDD] != null || components[ComponentCategory.SSD] != null)

    /** Добавление комплектующего в сборку
     * Параметры:
     * @param component - комплектующее
     * @param componentCategory - тип комплектующего
     * */
    fun addToBuild(component: Component, componentCategory: ComponentCategory) {

        components.put(componentCategory, component)
        if (componentCategory === ComponentCategory.RAM || componentCategory === ComponentCategory.HDD || componentCategory === ComponentCategory.SSD) {
            countGoods.put(componentCategory, 1)
        }
        price += component.price // Подсчет общей цены
    }

    /**
     * Проверка можно ли добавлять несколько комплектующих той же категории
     * @param componentCategory - тип компонента
     * @return true - несколько комплектующих, false - комплектующее одно
     */
    fun isMultipleGood(componentCategory: ComponentCategory): Boolean {
        return countGoods[componentCategory] != null
    }

    // Добавление количества объектов
    fun increaseCountGoods(componentCategory: ComponentCategory) {
        val limit = maxLimitGood(componentCategory)
        val currentCount = countGoods[componentCategory]!!
        if (currentCount < limit) {
            countGoods.put(componentCategory, currentCount + 1)
            price += components[componentCategory]?.price!!
        }
    }

    // Уменьшения количества
    fun reduceCountGoods(componentCategory: ComponentCategory) {
        val currentCount = countGoods[componentCategory]!!
        if (currentCount > 1) {
            countGoods[componentCategory] = currentCount - 1
            // price -= components[typeComp].getPrice()
        }
    }

    // Получение количества объектов
    fun getCountComponents(componentCategory: ComponentCategory): Int {
        return if (countGoods[componentCategory] != null) countGoods[componentCategory]!! else 1
    }

    fun getComponent(componentCategory: ComponentCategory): Component? {
        return components[componentCategory]
    }

    /** Удаление комплектующего по типу
    * @param typeGood - тип комплектующего
    * */
    fun deleteGood(componentCategory: ComponentCategory) {
        price -= components[componentCategory]?.price!!
        components.remove(componentCategory)
    }

    //TODO (Нужно сделать удаление комплектующих одного типа, но разных (накопители и может даже ОЗУ))

    /**
     * Проверка максимального лимита на количество комплектующего
     * @param componentCategory - тип комплектующего
     * @return - лимит количества комплектующих
     */
    private fun maxLimitGood(componentCategory: ComponentCategory): Int {
//        var limit = 1
//        val motherboard = components[TypeComp.MOTHERBOARD] ?: return limit
//
//        if (typeComp === TypeComp.RAM) {
//            val countRam: String = motherboard.getExDataByIdAttr(0).data // TODO (Нужны константы)
//            val countRam: String? = motherboardData.data [].get()
//
//
//        } else {
//            return 2
//
//            // TODO(Нужна проверка портов)
//            /*
//            motherboardData = motherboard.getDataBlockByHeader("Внутренние разъемы и колодки");
//            String[] ports = motherboardData.data.get(CompatParam.Motherboard.PORTS).split(" ");
//            for (int i = 0; i < ports.length; ++i)
//                if (ports[i].equals("Sata")) {
//                    limit = Integer.parseInt(ports[i - 2]);
//                    break;
//                }
//            */
//        }
//        return limit
        return 0
    }

    // Перегрузка клонирования так как надо буквально копировать сборку для создания временной копии (изменения в сборке до её сохранения)
    public override fun clone(): Any {
        val build = super.clone() as Build
        build.id = id
        build.components = HashMap()
        build.components.putAll(components)
        build.countGoods = HashMap()
        build.countGoods.putAll(countGoods)
        return build
    }
}