package com.derlados.computer_conf.models

import com.derlados.computer_conf.consts.ComponentCategory
import java.util.*
import kotlin.collections.ArrayList

class Build : Cloneable, BuildData {
    /**
     * ID атрибутов которые необходимы для проверки совместимости сборки
     */
    companion object {
        const val TDP_PORT = 10
        const val TDP_RAM = 4

        const val CPU_SOCKET = 2
        const val CPU_GPU_CORE = 7 // Для проверки полноты
        const val CPU_GPU_CORE_EXIST_VALUE = 75
        const val CPU_TDP = 11

        const val GPU_PS_CONNECTORS = 32
        const val GPU_TDP = 33
        const val GPU_FORM_FACTOR = 68

        const val MB_SOCKET = 41
        const val MB_RAM_TYPE = 47
        const val MB_RAM_COUNT = 49
        const val MB_RAM_SIZE = 50
        const val MB_PORTS = 60
        const val MB_FROM_FACTOR = 68

        const val SSD_FORM_FACTOR = 68 // Нужен только для опеределения какие слоты будет занимать SSD

        const val RAM_TYPE = 24
        const val RAM_SIZE = 83
        const val RAM_IN_PACK = 101

        const val PS_POWER = 113

        const val CASE_SUPPORT_FORM_FACTORS = 137

        enum class CompatibilityError {
            WRONG_CPU_SOCKET,
            WRONG_RAM_TYPE,
            WRONG_RAM_SIZE,
            WRONG_RAM_COUNT,
            WRONG_CASE_FORM_FACTOR,
            WRONG_SATA_COUNT,
            WRONG_M2_COUNT,
            NOT_ENOUGH_PS_POWER,
        }
    }

    override var serverId: Int = -1
    override var id: String = UUID.randomUUID().toString().replace("-", "")
    override var name: String = ""
    override var description: String = "" // Описание в сборке
    override var price: Int = 0 // Цена сборки
    // Комплетующие, разбиты по категориям, где каждый элемент пара (<комлпектующее>, <количество>)
    override var components: HashMap<ComponentCategory, ArrayList<BuildData.BuildComponent>> = hashMapOf(
        ComponentCategory.CPU to ArrayList(),
        ComponentCategory.MOTHERBOARD to ArrayList(),
        ComponentCategory.GPU to ArrayList(),
        ComponentCategory.RAM to ArrayList(),
        ComponentCategory.HDD to ArrayList(),
        ComponentCategory.SSD to ArrayList(),
        ComponentCategory.POWER_SUPPLY to ArrayList(),
        ComponentCategory.CASE to ArrayList(),
    )
    // В качестве изображение берется изображение корпуса, если он есть в сборке
    override var image: String? = null
        get() = BuildModel.editableBuild?.components?.get(ComponentCategory.CASE)?.getOrNull(0)?.component?.imageUrl
    override var isPublic: Boolean = false

    override var isCompatibility: Boolean = true
    override val isComplete: Boolean
        get() {
            val gpuCore: Component.Attribute? = components[ComponentCategory.CPU]?.getOrNull(0)?.component?.getAttrById(CPU_GPU_CORE)
            var isExistGpuCore = false
            if (gpuCore != null && gpuCore.idValue != CPU_GPU_CORE_EXIST_VALUE) {
                isExistGpuCore = true
            }

            return components[ComponentCategory.CPU]?.isNotEmpty() == true && components[ComponentCategory.MOTHERBOARD]?.isNotEmpty() == true
                    && (components[ComponentCategory.GPU]?.isNotEmpty() == true || isExistGpuCore)  && components[ComponentCategory.POWER_SUPPLY]?.isNotEmpty() == true
                    && components[ComponentCategory.RAM]?.isNotEmpty() == true && components[ComponentCategory.CASE]?.isNotEmpty() == true
                    && (components[ComponentCategory.HDD]?.isNotEmpty() == true || components[ComponentCategory.SSD]?.isNotEmpty() == true)
        }

    var lastAdded: Pair<ComponentCategory, BuildData.BuildComponent>? = null

    /**
     * Проверка можно ли добавлять несколько комплектующих той же категории
     * @param category - тип компонента
     * @return true - несколько комплектующих, false - комплектующее одно
     */
    override fun isMultipleCategory(category: ComponentCategory): Boolean {
        return (category == ComponentCategory.RAM || category == ComponentCategory.HDD || category == ComponentCategory.SSD )
    }

    fun getCompatibilityInfo(): ArrayList<CompatibilityError> {
        val compatibilityErrors = ArrayList<CompatibilityError>()

        val cpu = components[ComponentCategory.CPU]?.getOrNull(0)?.component
        val motherboard = components[ComponentCategory.MOTHERBOARD]?.getOrNull(0)?.component
        val gpu = components[ComponentCategory.GPU]?.getOrNull(0)?.component
        val ram = components[ComponentCategory.RAM]?.getOrNull(0)?.component
        val case = components[ComponentCategory.CASE]?.getOrNull(0)?.component
        val hdd = components[ComponentCategory.HDD]
        val ssd = components[ComponentCategory.SSD]
        val ps = components[ComponentCategory.POWER_SUPPLY]?.getOrNull(0)?.component
        var currentPower = 0

        // Проверка сокета процессора
        if (cpu != null && motherboard != null) {
            val cpuSocket: String? = cpu.getAttrById(CPU_SOCKET)?.value
            val mbSocket: String? = motherboard.getAttrById(MB_SOCKET)?.value

            if (cpuSocket == null || mbSocket == null || !compareValue(cpuSocket, mbSocket)) {
                compatibilityErrors.add(CompatibilityError.WRONG_CPU_SOCKET)
            }

            cpu.getAttrById(CPU_TDP)?.toIntValue()?.let {
                currentPower += it
            }
        }

        // Проверка оперативной памяти
        if (ram != null && motherboard != null) {
            val ramType: String? =ram.getAttrById(RAM_TYPE)?.value
            val ramSize: Int? =  ram.getAttrById(RAM_SIZE)?.toIntValue()
            var ramCount = 0
            components[ComponentCategory.RAM]?.let { it ->
                it.forEach { buildComponent ->
                    val ramPack: Int? = buildComponent.component.getAttrById(RAM_IN_PACK)?.toIntValue()
                    ramCount += if (ramPack != null) {
                        buildComponent.count * ramPack
                    } else {
                        buildComponent.count
                    }
                }
            }
            val ramTypeMB: String? = motherboard.getAttrById(MB_RAM_TYPE)?.value
            val ramSizeMB: Int? = motherboard.getAttrById(MB_RAM_SIZE)?.toIntValue()
            val ramCountMB: Int? = motherboard.getAttrById(MB_RAM_COUNT)?.toIntValue()

            if (ramType == null || ramTypeMB == null || !compareValue(ramType, ramTypeMB)) {
                compatibilityErrors.add(CompatibilityError.WRONG_RAM_TYPE)
            }
            if (ramSize == null || ramSizeMB == null || ramSize > ramSizeMB) {
                compatibilityErrors.add(CompatibilityError.WRONG_RAM_SIZE)
            }
            if (ramCountMB == null || ramCount > ramCountMB) {
                compatibilityErrors.add(CompatibilityError.WRONG_RAM_COUNT)
            }

            currentPower += (ramCount) * TDP_RAM
        }

        // Проверка форм фактора материнской платы и корпуса
        if (case != null && motherboard != null) {
            val caseFormFactor = case.getAttrById(CASE_SUPPORT_FORM_FACTORS)?.value
            val mbFormFactor = motherboard.getAttrById(MB_FROM_FACTOR)?.value

            if (caseFormFactor == null || mbFormFactor == null || !compareValue(caseFormFactor, mbFormFactor)) {
                compatibilityErrors.add(CompatibilityError.WRONG_CASE_FORM_FACTOR)
            }
        }

        // Проверка портов SATA и M.2
        if ((hdd != null || ssd != null) && motherboard != null) {
            val ports = motherboard.getAttrById(MB_PORTS)?.value

            ports?.let {
                var mbM2Count = 0
                var mbSataCount = 0

                var sataCount = 0
                var m2Count = 0

                val m2Matches = Regex ("([0-9]+ x m.2)").findAll(ports.toLowerCase(Locale.ROOT))
                m2Matches.forEach {
                    val countMatch = Regex("([0-9]+)").find(it.groupValues[1])?.value
                    countMatch?.let {
                        mbM2Count += countMatch.toInt()
                    }
                }

                val sataMatches =  Regex ("([0-9]+ x sata)").findAll(ports.toLowerCase(Locale.ROOT))
                sataMatches.forEach {
                    val countMatch = Regex("([0-9]+)").find(it.groupValues[1])?.value
                    countMatch?.let {
                        mbSataCount += countMatch.toInt()
                    }
                }

                hdd?.forEach {
                    sataCount += it.count
                }

                ssd?.forEach {
                    val formFactor = it.component.getAttrById(SSD_FORM_FACTOR)?.value
                    formFactor?.let {
                        if (formFactor.toLowerCase(Locale.ROOT).contains("m.2")) {
                            ++m2Count
                        } else {
                            ++sataCount
                        }
                    }
                }

                if (sataCount > mbSataCount) {
                    compatibilityErrors.add(CompatibilityError.WRONG_SATA_COUNT)
                }

                if (m2Count > mbM2Count) {
                    compatibilityErrors.add(CompatibilityError.WRONG_M2_COUNT)
                }

                currentPower += (sataCount + mbM2Count) * TDP_PORT
            }

        }

        // Если присутствует видеокарта,
        if (gpu != null && motherboard != null) {
            gpu.getAttrById(GPU_TDP)?.toIntValue()?.let {
                currentPower = it
            }
        }

        // Проверка мощности блока питания
        if (ps != null && motherboard != null) {
            ps.getAttrById(PS_POWER)?.toIntValue()?.let {
                if (gpu == null && it < currentPower * 1.5) {
                    compatibilityErrors.add(CompatibilityError.NOT_ENOUGH_PS_POWER)
                } else if (gpu != null && it < currentPower) {
                    compatibilityErrors.add(Companion.CompatibilityError.NOT_ENOUGH_PS_POWER)
                }
                Unit
            }
        }

        isCompatibility =  compatibilityErrors.isEmpty()

        return compatibilityErrors
    }

    private fun compareValue(value1: String, value2: String): Boolean {
        val value1ToCompare = value1.toLowerCase(Locale.ROOT).replace(" ", "")
        val value2ToCompare = value2.toLowerCase(Locale.ROOT).replace(" ", "")

        return value1ToCompare.contains(value2ToCompare) || value2ToCompare.contains(value1ToCompare)
    }

    /** Добавление комплектующего в сборку
     * Параметры:
     * @param category - тип комплектующего
     * @param component - комплектующее
     * */
    fun addToBuild(category: ComponentCategory, component: Component) {
        val newBuildComponent = BuildData.BuildComponent(component, 1)
        components[category]?.add(newBuildComponent)
        price += component.price // Подсчет общей цены
        lastAdded = Pair(category, newBuildComponent)
    }

    fun clearLastAdded() {
        lastAdded = null
    }

    fun getBuildComponent(category: ComponentCategory, idComponent: Int): BuildData.BuildComponent? {
        return components[category]?.find { it.component.id == idComponent }
    }

    /**
     * Увеличение количества комплектующего
     * @param category - категория комплектующего
     * @param idComponent - id комплектующего
     */
    fun increaseComponents(category: ComponentCategory, idComponent: Int) {
        val buildComponent = components[category]?.find { it.component.id == idComponent }
        buildComponent?.let {
            ++buildComponent.count
            price += buildComponent.component.price
        }
    }

    /**
     * Уменьшение количества комплектующего
     * @param category - категория комплектующего
     * @param idComponent - id комплектующего
     */
    fun reduceComponents(category: ComponentCategory, idComponent: Int) {
        val buildComponent = components[category]?.find { it.component.id == idComponent }
        buildComponent?.let {
            if (buildComponent.count != 1) {
                --buildComponent.count
                price -= buildComponent.component.price
            }
        }
    }

    /**
     * Удаление комплектующего из сборки
     * @param category - категория комплектующего
     * @param idComponent - id комплектующего
     */
    fun removeComponent(category: ComponentCategory, idComponent: Int) {
        val buildComponent = components[category]?.find { it.component.id == idComponent }
        buildComponent?.let {
            price -= buildComponent.component.price * buildComponent.count
            components[category]?.remove(buildComponent)
        }
    }

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

        // Глубокое копирование мапы
        build.components = HashMap()
        for ((key, arrayComponents) in components) {
            val cloneArray = ArrayList<BuildData.BuildComponent>()
            arrayComponents.forEach {
                cloneArray.add(BuildData.BuildComponent(it.component, it.count))
            }
            build.components[key] = cloneArray
        }

        return build
    }
}