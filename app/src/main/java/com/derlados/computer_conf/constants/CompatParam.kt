package com.derlados.computer_conf.Constants;

class CompatParam {

    class Cpu {
        companion object {
            const val SOCKET: String = "Тип сокета";
        }
    }

    class Ram {
        companion object {
            const val TYPE_MEMORY = "Тип памяти";
            const val SIZE_MEMORY = "Объем памяти";
        }
    }

    class PowerSupply {
        companion object {
            const val POWER = "Мощность";
        }
    }

    class Case {
        companion object {
            const val FROM_FACTOR = "Поддерживаемые материнские платы";
        }
    }

    class Motherboard {
        companion object {
            const val SOCKET = "Сокет"
            const val TYPE_MEMORY = "Тип оперативной памяти"
            const val MAX_SIZE_MEMORY = "Максимальний объем памяти"
            const val FORM_FACTOR = "Форм-фактор"
            const val COUNT_RAM = "Количество разъемов, шт."
            const val PORTS = "Внутренние разъемы и порты"
        }
    }

    class Gpu {
        companion object {
            const val POWER = "Минимальная рекомендованная мощность блока питания"
        }
    }
}
