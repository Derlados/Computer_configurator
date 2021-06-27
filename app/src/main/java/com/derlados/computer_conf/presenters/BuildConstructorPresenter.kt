package com.derlados.computer_conf.presenters

import android.os.Build
import android.os.Message
import com.derlados.computer_conf.consts.ComponentCategory
import com.derlados.computer_conf.interfaces.BuildConstructorView
import com.derlados.computer_conf.models.BuildModel

class BuildConstructorPresenter(private val view: BuildConstructorView) {

    enum class StatusBuild {
        COMPLETE,
        IS_NOT_COMPLETE,
        COMPATIBILITY_ERROR
    }

    init {

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

}
