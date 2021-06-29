package com.derlados.computer_conf.models

import com.derlados.computer_conf.consts.ComponentCategory
import java.util.*

class Build : Cloneable, BuildData {
    override var id: String = UUID.randomUUID().toString().replace("-", "")

    override var components = HashMap<ComponentCategory, Component>() // Комплетующие
    override var price: Float = 0.0F // Цена сборки
    override var name: String = ""
    override var description: String = "" // Описание в сборке
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

        components[componentCategory] = component
        if (componentCategory === ComponentCategory.RAM || componentCategory === ComponentCategory.HDD || componentCategory === ComponentCategory.SSD) {
            countGoods[componentCategory] = 1
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
    fun removeComponent(componentCategory: ComponentCategory) {
        price -= components[componentCategory]?.price!!
        components.remove(componentCategory)
    }

    //TODO следует придумать поумнее проверку блока питания
    // Проверка совместимости
    val compatibility: String = "default"
    /* get() {
        var countMsg = 0
        var compatibilityMsg = ""

        // Подготовка всех комплектующих
        val cpu = goods[TypeGood.CPU]
        val motherboard = goods[TypeGood.MOTHERBOARD]
        val powerSupply = goods[TypeGood.POWER_SUPPLY]
        val ram = goods[TypeGood.RAM]
        val gpu = goods[TypeGood.GPU]
        val pcCase = goods[TypeGood.CASE]
        if (motherboard != null) {
            if (cpu != null) {
                val motherboardData = motherboard.getExDataByIdAttr("Процессор")
                val cpuData = cpu.getExDataByIdAttr("Основные характеристики")
                val motherboardSocket = motherboardData!!.data!![CompatParam.Motherboard.SOCKET]
                val cpuSocket = cpuData!!.data!![CompatParam.Cpu.SOCKET]
                if (!(motherboardSocket.contains(cpuSocket) || cpuSocket.contains(motherboardSocket))) {
                    ++countMsg
                    compatibilityMsg += "\n$countMsg. Сокет процессора и сокет материнской платы несовместимы"
                }
            }
            if (ram != null) {
                val motherboardData = motherboard.getExDataByIdAttr("Оперативная память")
                val ramData = ram.getExDataByIdAttr("Основные характеристики")
                val motherboardTypeMemory = motherboardData!!.data!![CompatParam.Motherboard.TYPE_MEMORY]
                val ramTypeMemory = ramData!!.data!![CompatParam.Ram.TYPE_MEMORY]
                if (!motherboardTypeMemory.contains(ramTypeMemory)) {
                    ++countMsg
                    compatibilityMsg += "\n$countMsg. Тип памяти ОЗУ и материнской платы несовместимы"
                }
            }
            if (pcCase != null) {
                val motherboardData = motherboard.getExDataByIdAttr("Физические характеристики")
                val pcCaseData = pcCase.getExDataByIdAttr("Основные характеристики")
                var motherboardFormFactor = motherboardData!!.data!![CompatParam.Motherboard.FORM_FACTOR]
                val pcCaseFormFactor = pcCaseData!!.data!![CompatParam.Case.FROM_FACTOR]
                motherboardFormFactor = motherboardFormFactor.replace(" ", "").replace("-", "")
                val pcFormFactors: Array<String> = pcCaseFormFactor.replace(" ", "").replace("-", "").split(",".toRegex()).toTypedArray()
                var i: Int
                i = 0
                while (i < pcFormFactors.size) {
                    if (motherboardFormFactor == pcFormFactors[i]) break
                    ++i
                }

                // Если цикл закончился успешно - следовательно не было ни одного соответствия с поддерживаемыми платами
                if (i == pcFormFactors.size) {
                    ++countMsg
                    compatibilityMsg += "\n$countMsg. Форм фактор материнской платы и корпуса не совпадают"
                }
            }
        }
        if (powerSupply != null && gpu != null) {
            val gpuData = gpu.getExDataByIdAttr("Основные характеристики")
            val powerSupplyData = powerSupply.getExDataByIdAttr("Основные характеристики")

            // Как правило если брать БП под рекомендованные требования  видеокарты, то в целом можно работать //TODO следует придумать поумнее проверку блока питания
            try {
                val gpuPower: Int = gpuData!!.data!![CompatParam.Gpu.POWER].replace(" Вт", "").toInt()
                val powerSupplyPower: Int = powerSupplyData!!.data!![CompatParam.CasePowerSupply.POWER].replace(" Вт", "").toInt()
                if (gpuPower > powerSupplyPower) {
                    ++countMsg
                    compatibilityMsg += "\n$countMsg. Мощность блока питание ниже рекомендованной"
                }
            } catch (e: Exception) {
                // Если было выбито исключение, значит в каком то из комплектующих не хватает данных
                Log.e(LogsKeys.ERROR_LOG.toString(), e.toString())
            }
        }
            compatibilityMsg = if (countMsg != 0) App.Companion.getApp().getResources().getString(R.string.false_compatibility) + compatibilityMsg else App.Companion.getApp().getResources().getString(R.string.true_compatibility)
            return compatibilityMsg
        }*/


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

    /**
     * Перегрузка клонирования, для конструктора необходима работа с копией.
     * Глубокое копирование мапов не требуется, класс Component используется исключительно для чтения
     */
    public override fun clone(): Build {
        val build = super.clone() as Build
        build.id = id
        build.components = HashMap()
        build.components.putAll(components)
        build.countGoods = HashMap()
        build.countGoods.putAll(countGoods)
        return build
    }
}