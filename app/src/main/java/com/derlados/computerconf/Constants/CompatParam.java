package com.derlados.computerconf.Constants;

public class CompatParam {

    public static class Cpu {
        public final static String SOCKET = "Тип сокета";
    }

    public static class Ram {
        public final static String TYPE_MEMORY = "Тип памяти";
        public final static String SIZE_MEMORY = "Объем памяти";
    }

    public static class PowerSupply {
        public final static String POWER = "Мощность";
    }

    public static class Case {
        public final static String FROM_FACTOR = "Поддерживаемые материнские платы";
    }

    public static class Motherboard {
        public final static String SOCKET = "Сокет";
        public final static String TYPE_MEMORY = "Тип оперативной памяти";
        public final static String MAX_SIZE_MEMORY = "Максимальний объем памяти";
        public final static String FROM_FACTOR = "Форм-фактор";
        public final static String COUNT_RAM = "Количество разъемов, шт.";
    }

    public static class Gpu {
        public final static String POWER = "Минимальная рекомендованная мощность блока питания";
    }

}
