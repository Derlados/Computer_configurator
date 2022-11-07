package com.derlados.computer_configurator.stores.entities.build

import com.derlados.computer_configurator.consts.ComponentCategory
import com.derlados.computer_configurator.stores.entities.Component
import com.derlados.computer_configurator.stores.entities.User
import java.util.*
import kotlin.collections.ArrayList

class Build : Cloneable, IEditableBuild {
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
        const val GPU_MIN_TDP = 33

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
            OK
        }
    }

    override var id: Int = -1
    override var localId: String = ""
        get() {
            if (field == "")  {
                field = UUID.randomUUID().toString().replace("-", "")
            }
            return field
        }
    override var name: String = ""
    override var description: String = ""
    override var isPublic: Boolean = false
    override var publishDate: Date = Date()
    override var price: Int = 0
    // В качестве изображение берется изображение корпуса, если он есть в сборке
    override var image: String? = null
        get() = components[ComponentCategory.CASE]?.getOrNull(0)?.component?.img
    override var user: User = User(-1, "local")

    // Комплетующие, разбиты по категориям, где каждый элемент пара (<комлпектующее>, <количество>)
    override var components: HashMap<ComponentCategory, ArrayList<BuildComponent>> = hashMapOf(
        ComponentCategory.CPU to ArrayList(),
        ComponentCategory.MOTHERBOARD to ArrayList(),
        ComponentCategory.GPU to ArrayList(),
        ComponentCategory.RAM to ArrayList(),
        ComponentCategory.HDD to ArrayList(),
        ComponentCategory.SSD to ArrayList(),
        ComponentCategory.POWER_SUPPLY to ArrayList(),
        ComponentCategory.CASE to ArrayList(),
    )
    override var usedPower: Int = 0
    override var isCompatibility: Boolean = true
    override val isComplete: Boolean
        get() {
            val gpuCore: Component.Attribute? = components[ComponentCategory.CPU]?.getOrNull(0)?.component?.getAttrById(
                CPU_GPU_CORE
            )
            var isExistGpuCore = false
            if (gpuCore != null && gpuCore.valueId != CPU_GPU_CORE_EXIST_VALUE) {
                isExistGpuCore = true
            }

            return components[ComponentCategory.CPU]?.isNotEmpty() == true && components[ComponentCategory.MOTHERBOARD]?.isNotEmpty() == true
                    && (components[ComponentCategory.GPU]?.isNotEmpty() == true || isExistGpuCore)  && components[ComponentCategory.POWER_SUPPLY]?.isNotEmpty() == true
                    && components[ComponentCategory.RAM]?.isNotEmpty() == true && components[ComponentCategory.CASE]?.isNotEmpty() == true
                    && (components[ComponentCategory.HDD]?.isNotEmpty() == true || components[ComponentCategory.SSD]?.isNotEmpty() == true)
        }

    var lastAdded: Pair<ComponentCategory, BuildComponent>? = null

    /**
     * Проверка можно ли добавлять несколько комплектующих той же категории
     * @param category - тип компонента
     * @return true - несколько комплектующих, false - комплектующее одно
     */
    override fun isMultipleCategory(category: ComponentCategory): Boolean {
        return (category == ComponentCategory.RAM || category == ComponentCategory.HDD || category == ComponentCategory.SSD )
    }

    fun getCompatibilityInfo(): ArrayList<CompatibilityError> {
        var compatibilityErrors = ArrayList<CompatibilityError>()
        for ((key, value) in components) {
            compatibilityErrors.add(checkCompatibility(key, value))
        }

        compatibilityErrors = ArrayList(compatibilityErrors.filter { c -> c != CompatibilityError.OK })
        isCompatibility =  compatibilityErrors.isEmpty()
        return compatibilityErrors
    }

    /**
     * Проверка совместимости комплектующих
     * @param category - категория комплектующего
     * @param buildComponents - список комплектующих в категории, которые необходимо проверить на совместимсоть
     * @return - ошибка (CompatibilityError) или подтверджение совместимости (CompatibilityError.OK)
     */
    private fun checkCompatibility(category: ComponentCategory, buildComponents: ArrayList<BuildComponent>): CompatibilityError {
        when (category) {
            ComponentCategory.CPU -> {
                val motherboard = components[ComponentCategory.MOTHERBOARD]?.getOrNull(0)?.component
                val cpu = buildComponents.getOrNull(0)?.component

                if (motherboard != null && cpu != null) {
                    val cpuSocket: String? = cpu.getAttrById(CPU_SOCKET)?.value
                    val mbSocket: String? = motherboard.getAttrById(MB_SOCKET)?.value

                    if (cpuSocket == null || mbSocket == null || !compareValue(cpuSocket, mbSocket)) {
                        return CompatibilityError.WRONG_CPU_SOCKET
                    }
                }
            }
            ComponentCategory.RAM -> {
                val motherboard = components[ComponentCategory.MOTHERBOARD]?.getOrNull(0)?.component
                val ram = buildComponents.getOrNull(0)?.component

                if (motherboard != null && ram != null) {
                    val ramType: String? = ram.getAttrById(RAM_TYPE)?.value
                    val ramSize: Int? =  ram.getAttrById(RAM_SIZE)?.toIntValue()

                    var ramCount = 0
                    buildComponents.forEach { buildComponent ->
                        val ramPack: Int? = buildComponent.component.getAttrById(RAM_IN_PACK)?.toIntValue()
                        ramCount += if (ramPack != null) {
                            buildComponent.count * ramPack
                        } else {
                            buildComponent.count
                        }
                    }

                    val ramTypeMB: String? = motherboard.getAttrById(MB_RAM_TYPE)?.value
                    val ramSizeMB: Int? = motherboard.getAttrById(MB_RAM_SIZE)?.toIntValue()
                    val ramCountMB: Int? = motherboard.getAttrById(MB_RAM_COUNT)?.toIntValue()

                    if (ramType == null || ramTypeMB == null || !compareValue(ramType, ramTypeMB)) {
                        return CompatibilityError.WRONG_RAM_TYPE
                    }
                    if (ramSize == null || ramSizeMB == null || ramSize > ramSizeMB) {
                        return CompatibilityError.WRONG_RAM_SIZE
                    }
                    if (ramCountMB == null || ramCount > ramCountMB) {
                        return CompatibilityError.WRONG_RAM_COUNT
                    }
                }
            }
            ComponentCategory.CASE -> {
                val motherboard = components[ComponentCategory.MOTHERBOARD]?.getOrNull(0)?.component
                val case = buildComponents.getOrNull(0)?.component

                if (motherboard != null && case != null) {
                    val caseFormFactor = case.getAttrById(CASE_SUPPORT_FORM_FACTORS)?.value
                    val mbFormFactor = motherboard.getAttrById(MB_FROM_FACTOR)?.value

                    if (caseFormFactor == null || mbFormFactor == null || !compareValue(caseFormFactor, mbFormFactor)) {
                        return CompatibilityError.WRONG_CASE_FORM_FACTOR
                    }
                }
            }
            ComponentCategory.SSD, ComponentCategory.HDD -> {
                val motherboard = components[ComponentCategory.MOTHERBOARD]?.getOrNull(0)?.component  ?: return CompatibilityError.OK
                val ssd = components[ComponentCategory.SSD]
                val hdd = components[ComponentCategory.HDD]

                val ports = motherboard.getAttrById(MB_PORTS)?.value
                ports?.let {
                    var mbM2Count = 0
                    var mbSataCount = 0
                    var m2Count = 0
                    var sataCount = 0

                    val m2Matches = Regex ("([0-9]+ x m.2)").findAll( ports.toLowerCase(Locale.ROOT))
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

                    ssd?.forEach {
                        val count = it.count
                        val formFactor = it.component.getAttrById(SSD_FORM_FACTOR)?.value
                        formFactor?.let {
                            if (formFactor.toLowerCase(Locale.ROOT).contains("m.2")) {
                                m2Count += count
                            } else {
                                sataCount += count
                            }
                        }
                    }
                    hdd?.forEach {
                        sataCount += it.count
                    }

                    if (m2Count > mbM2Count) {
                        return CompatibilityError.WRONG_M2_COUNT
                    }

                    if (sataCount > mbSataCount) {
                        return CompatibilityError.WRONG_SATA_COUNT
                    }
                }
            }
            ComponentCategory.POWER_SUPPLY -> {
                val ps = buildComponents.getOrNull(0)?.component
                ps?.getAttrById(PS_POWER)?.toIntValue()?.let {
                    if (it < usedPower) {
                        return CompatibilityError.NOT_ENOUGH_PS_POWER
                    }
                    Unit
                }
            }
        }

        return CompatibilityError.OK
    }

    fun checkCompatibility(category: ComponentCategory, component: Component): CompatibilityError {
        val componentArray = ArrayList(components[category])
        componentArray.add(BuildComponent(component, 1))

        return checkCompatibility(category, componentArray)
    }

    /**
     * Проверка максимального лимита на количество комплектующего
     * @param category - тип комплектующего
     * @return - лимит количества комплектующих
     */
    fun isMax(category: ComponentCategory): Boolean {
        when(category) {
            ComponentCategory.CPU, ComponentCategory.GPU, ComponentCategory.CASE, ComponentCategory.POWER_SUPPLY,
                    ComponentCategory.MOTHERBOARD, ComponentCategory.RAM -> {
                if (components[category]?.size != 0) {
                    return true
                }
            }
            ComponentCategory.SSD, ComponentCategory.HDD -> {
                val motherboard = components[ComponentCategory.MOTHERBOARD]?.getOrNull(0)?.component ?: return false
                val ssd = components[ComponentCategory.SSD]
                val hdd = components[ComponentCategory.HDD]

                val ports = motherboard.getAttrById(MB_PORTS)?.value
                ports?.let {
                    var mbSataCount = 0
                    var mbM2Count = 0
                    var m2Count = 0
                    var sataCount = 0

                    val sataMatches =  Regex ("([0-9]+ x sata)").findAll(ports.toLowerCase(Locale.ROOT))
                    sataMatches.forEach {
                        val countMatch = Regex("([0-9]+)").find(it.groupValues[1])?.value
                        countMatch?.let {
                            mbSataCount += countMatch.toInt()
                        }
                    }

                    val m2Matches = Regex ("([0-9]+ x m.2)").findAll(ports.toLowerCase(Locale.ROOT))
                    m2Matches.forEach {
                        val countMatch = Regex("([0-9]+)").find(it.groupValues[1])?.value
                        countMatch?.let {
                            mbM2Count += countMatch.toInt()
                        }
                    }

                    ssd?.forEach {
                        val count = it.count
                        val formFactor = it.component.getAttrById(SSD_FORM_FACTOR)?.value
                        formFactor?.let {
                            if (formFactor.toLowerCase(Locale.ROOT).contains("m.2")) {
                                m2Count += count
                            } else {
                                sataCount += count
                            }
                        }
                    }
                    hdd?.forEach {
                        sataCount += it.count
                    }
                    
                    if (m2Count == mbM2Count && sataCount == mbSataCount) {
                        return true
                    } else if (sataCount == mbSataCount && category == ComponentCategory.HDD) {
                        return true
                    }
                }

            }
            else -> {
                return true
            }
        }

        return false
    }

    private fun compareValue(value1: String, value2: String): Boolean {
        val value1ToCompare = value1.toLowerCase(Locale.ROOT).replace(" ", "")
        val value2ToCompare = value2.toLowerCase(Locale.ROOT).replace(" ", "")

        return value1ToCompare.contains(value2ToCompare) || value2ToCompare.contains(value1ToCompare)
    }

    private fun calculatePower(): Int {
        // Если присутствует видеокарта и если в ней присутствует мин. мощность БП - можно не считать
        val gpu = components[ComponentCategory.GPU]?.getOrNull(0)?.component
        gpu?.getAttrById(GPU_MIN_TDP)?.toIntValue()?.let {
            return it
        }

        var currentPower = 0

        val motherboard = components[ComponentCategory.MOTHERBOARD]?.getOrNull(0)?.component
        val cpu = components[ComponentCategory.CPU]?.getOrNull(0)?.component
        val ram = components[ComponentCategory.RAM]

        cpu?.getAttrById(CPU_TDP)?.toIntValue()?.let {
            currentPower += it
        }

        var ramCount = 0
        ram?.forEach { ramItem ->
            val ramPack: Int? = ramItem.component.getAttrById(RAM_IN_PACK)?.toIntValue()
            ramCount += if (ramPack != null) {
                ramItem.count * ramPack
            } else {
                ramItem.count
            }
        }
        currentPower += ramCount * TDP_RAM

        val ports = motherboard?.getAttrById(MB_PORTS)?.value
        if (ports != null) {
            var mbSataCount = 0
            var mbM2Count = 0
            val sataMatches =  Regex ("([0-9]+ x sata)").findAll(ports.toLowerCase(Locale.ROOT))
            sataMatches.forEach {
                val countMatch = Regex("([0-9]+)").find(it.groupValues[1])?.value
                countMatch?.let {
                    mbSataCount += countMatch.toInt()
                }
            }
            val m2Matches = Regex ("([0-9]+ x m.2)").findAll(ports.toLowerCase(Locale.ROOT))
            m2Matches.forEach {
                val countMatch = Regex("([0-9]+)").find(it.groupValues[1])?.value
                countMatch?.let {
                    mbM2Count += countMatch.toInt()
                }
            }
            currentPower = (mbSataCount + mbM2Count) * TDP_PORT
        }

        return (currentPower * 1.5).toInt()
    }

    /** Добавление комплектующего в сборку
     * Параметры:
     * @param category - тип комплектующего
     * @param component - комплектующее
     * */
    fun addToBuild(category: ComponentCategory, component: Component) {
        val newBuildComponent = BuildComponent(component, 1)

        if (components[category] == null) {
            components[category] = ArrayList()
        }
        components[category]?.add(newBuildComponent)

        price += component.price // Подсчет общей цены
        usedPower = calculatePower()
        lastAdded = Pair(category, newBuildComponent)
    }

    fun clearLastAdded() {
        lastAdded = null
    }

    fun getBuildComponent(category: ComponentCategory, idComponent: Int): BuildComponent? {
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
     * Перегрузка клонирования, для конструктора необходима работа с копией.
     * Глубокое копирование мапов не требуется, класс Component используется исключительно для чтения
     */
    public override fun clone(): Build {
        val build = super.clone() as Build
        build.localId = localId

        // Глубокое копирование мапы
        build.components = HashMap()
        for ((key, arrayComponents) in components) {
            val cloneArray = ArrayList<BuildComponent>()
            arrayComponents.forEach {
                cloneArray.add(BuildComponent(it.component, it.count))
            }
            build.components[key] = cloneArray
        }

        return build
    }
}