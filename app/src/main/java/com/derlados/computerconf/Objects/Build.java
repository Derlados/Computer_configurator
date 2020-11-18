package com.derlados.computerconf.Objects;

import android.animation.TypeEvaluator;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.TypedValue;

import com.derlados.computerconf.App;
import com.derlados.computerconf.Constants.CompatParam;
import com.derlados.computerconf.Constants.LogsKeys;
import com.derlados.computerconf.Constants.TypeGood;
import com.derlados.computerconf.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class Build implements Cloneable {

    private HashMap<TypeGood, Good> goods = new HashMap<>(); // Комплетующие
    private HashMap<TypeGood, Integer> countGoods = new HashMap<>(); // Хранится в отдельной мапе, так как только SSD, RAM и HDD можно взять несколько

    private double price = 0; // Цена сборки
    private String name = "", description = ""; // Имя и описание в сборке
    private String id;

    // Инициализация всех типов товаров
    public Build() {
        UUID randId = UUID.randomUUID();
        this.id = randId.toString().replace("-", "");
    }

    public String getCompatibility() {

        int countMsg = 0;
        String compatibilityMsg = "";

        // Подготовка всехкомплектующих
        Good cpu = goods.get(TypeGood.CPU);
        Good motherboard = goods.get(TypeGood.MOTHERBOARD);
        Good powerSupply = goods.get(TypeGood.POWER_SUPPLY);
        Good ram = goods.get(TypeGood.RAM);
        Good gpu = goods.get(TypeGood.GPU);
        Good pcCase = goods.get(TypeGood.CASE);

        if (motherboard != null) {
            if (cpu != null) {
                Good.dataBlock motherboardData = motherboard.getDataBlockByHeader("Процессор");
                Good.dataBlock cpuData = cpu.getDataBlockByHeader("Основные характеристики");

                String motherboardSocket = motherboardData.data.get(CompatParam.Motherboard.SOCKET);
                String cpuSocket = cpuData.data.get(CompatParam.Cpu.SOCKET);

                if (!(motherboardSocket.contains(cpuSocket) || cpuSocket.contains(motherboardSocket))) {
                    ++countMsg;
                    compatibilityMsg += "\n" + countMsg + ". Сокет процессора и сокет материнской платы несовместимы";
                }
            }

            if (ram != null) {
                Good.dataBlock motherboardData = motherboard.getDataBlockByHeader("Оперативная память");
                Good.dataBlock ramData = ram.getDataBlockByHeader("Основные характеристики");

                String motherboardTypeMemory = motherboardData.data.get(CompatParam.Motherboard.TYPE_MEMORY);
                String ramTypeMemory = ramData.data.get(CompatParam.Ram.TYPE_MEMORY);

                if (!motherboardTypeMemory.contains(ramTypeMemory)) {
                    ++countMsg;
                    compatibilityMsg += "\n" + countMsg + ". Тип памяти ОЗУ и материнской платы несовместимы";
                }
            }

            if (pcCase != null) {
                Good.dataBlock motherboardData = motherboard.getDataBlockByHeader("Физические характеристики");
                Good.dataBlock pcCaseData = pcCase.getDataBlockByHeader("Основные характеристики");

                String motherboardFormFactor = motherboardData.data.get(CompatParam.Motherboard.FROM_FACTOR);
                String pcCaseFormFactor = pcCaseData.data.get(CompatParam.Case.FROM_FACTOR);

                motherboardFormFactor = motherboardFormFactor.replace(" ", "").replace( "-", "");
                String[] pcFormFactors = pcCaseFormFactor.replace(" ", "").replace( "-", "").split(",");

                int i;
                for (i = 0; i < pcFormFactors.length; ++i) {
                    if (motherboardFormFactor.equals(pcFormFactors[i]))
                        break;
                }

                // Если цикл закончился успешно - следовательно не было ни одного соответствия с поддерживаемыми платами
                if (i == pcFormFactors.length) {
                    ++countMsg;
                    compatibilityMsg += "\n" + countMsg + ". Форм фактор материнской платы и корпуса не совпадают";
                }

            }
        }

        if (powerSupply != null && gpu != null) {
            Good.dataBlock gpuData = gpu.getDataBlockByHeader("Основные характеристики");
            Good.dataBlock powerSupplyData = powerSupply.getDataBlockByHeader("Основные характеристики");

            // Как правило если брать БП под рекомендованные требования  видеокарты, то в целом можно работать //TODO следует придумать поумнее проверку блока питания
            try {
                int gpuPower = Integer.parseInt(gpuData.data.get(CompatParam.Gpu.POWER).replace(" Вт", ""));
                int powerSupplyPower = Integer.parseInt(powerSupplyData.data.get(CompatParam.PowerSupply.POWER).replace(" Вт", ""));

                if (gpuPower > powerSupplyPower) {
                    ++countMsg;
                    compatibilityMsg += "\n" + countMsg + ". Мощность блока питание ниже рекомендованной";
                }
            }
            catch (Exception e) {
                // Если было выбито исключение, значит в каком то из комплектующих не хватает данных
                Log.e(LogsKeys.ERROR_LOG.toString(), e.toString());
            }
        }

        if (countMsg != 0)
            compatibilityMsg = App.getApp().getResources().getString(R.string.false_compatibility) + compatibilityMsg;
        else
            compatibilityMsg = App.getApp().getResources().getString(R.string.true_compatibility);

        return compatibilityMsg;
    }

    /* Добавление товара в сборку
     * Параметры:
     * good - само комплектующее
     * typeGood - тип комплектующего
     * */
    public void addToBuild(TypeGood typeGood, Good good) {
        goods.put(typeGood, good);

        if (typeGood == TypeGood.RAM || typeGood == TypeGood.HDD || typeGood == TypeGood.SSD)
            countGoods.put(typeGood, 1);

        this.price += good.getPrice(); // Подсчет общей цены
    }

    // Проверка полной сборки
    public boolean isComplete() {
        return goods.get(TypeGood.CPU) != null && goods.get(TypeGood.MOTHERBOARD) != null && goods.get(TypeGood.GPU) != null
                && goods.get(TypeGood.POWER_SUPPLY) != null && goods.get(TypeGood.RAM) != null
                && (goods.get(TypeGood.HDD) != null || goods.get(TypeGood.SSD) != null) && goods.get(TypeGood.CASE) != null;
    }

    public boolean isMultipleGood(TypeGood typeGood) {
        return countGoods.get(typeGood) != null;
    }

    private Integer maxLimitGood(TypeGood typeGood) {
        int limit = 1;
        Good motherboard = goods.get(TypeGood.MOTHERBOARD);
        if (motherboard == null)
            return limit;
        Good.dataBlock motherboardData;

        if (typeGood == TypeGood.RAM) {
            motherboardData = motherboard.getDataBlockByHeader("Оперативная память");
            String countRam = motherboardData.data.get(CompatParam.Motherboard.COUNT_RAM);
            if (countRam != null)
                limit = Integer.parseInt(countRam);
        }
        else {
            return 2;


            /*
            motherboardData = motherboard.getDataBlockByHeader("Внутренние разъемы и колодки");
            String[] ports = motherboardData.data.get(CompatParam.Motherboard.PORTS).split(" ");
            for (int i = 0; i < ports.length; ++i)
                if (ports[i].equals("Sata")) {
                    limit = Integer.parseInt(ports[i - 2]);
                    break;
                }
            */
        }
        return limit;
    }

    // Добавление количества объектов
    public void increaseCountGoods(TypeGood typeGood) {
        int limit = maxLimitGood(typeGood);
        int currentCount = countGoods.get(typeGood);

        if (currentCount < limit) {
            countGoods.put(typeGood, currentCount + 1);
            price += goods.get(typeGood).getPrice();
        }
    }

    // Уменьшения количества
    public void reduceCountGoods(TypeGood typeGood) {
        int currentCount = countGoods.get(typeGood);

        if (currentCount > 1) {
            countGoods.put(typeGood, currentCount - 1);
            price -= goods.get(typeGood).getPrice();
        }
    }

    // Получение количества объектов
    public int getCountGoods(TypeGood typeGood) {
        if (countGoods.get(typeGood) != null)
            return countGoods.get(typeGood);
        return 1;
    }

    public Good getGood(TypeGood typeGood) {
        return goods.get(typeGood);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<TypeGood, Good> getGoods() {
        return goods;
    }

    /* Удаление комплектующего по индексу
    * Параметры:
    * typeGood - тип комплектующего
    * index - его индекс
    * */
    public void deleteGood(TypeGood typeGood) {
        this.price -= goods.get(typeGood).getPrice();
        goods.remove(typeGood);
        countGoods.remove(typeGood);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Build build = (Build) super.clone();
        build.id = this.id;

        build.goods = new HashMap<>();
        build.goods.putAll(this.goods);

        build.countGoods = new HashMap<>();
        build.countGoods.putAll(this.countGoods);

        return build;
    }
}
