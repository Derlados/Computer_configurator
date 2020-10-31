package com.derlados.computerconf.Objects;

import android.animation.TypeEvaluator;
import android.util.TypedValue;

import com.derlados.computerconf.Constants.TypeGood;

import java.util.ArrayList;
import java.util.HashMap;

public class Build {

    /* Комплектующие, сделаны одного типа ArrayList<Good> и помещены в HashMap, так как проблематично разделить возврат объекта одного или массива.
    * Для мобильности кода выгодно чтобы они были ArrayList<Good>, хоть к примеру материнку можно взять только одну.
    * В будущем можно улучшить сборки где будут участвовать материнки на два процессора, на несколько видокарт
    * Опять же помещено в хеш мап, так как нету простых ассоциативных массивов*/
    private HashMap<TypeGood, ArrayList<Good>> goods = new HashMap<>();
    private double price = 0; // Цена сборки

    // Инициализация всех типов товаров
    public Build() {
        goods.put(TypeGood.CPU, new ArrayList<Good>());
        goods.put(TypeGood.GPU, new ArrayList<Good>());
        goods.put(TypeGood.MOTHERBOARD, new ArrayList<Good>());
        goods.put(TypeGood.HDD, new ArrayList<Good>());
        goods.put(TypeGood.SSD, new ArrayList<Good>());
        goods.put(TypeGood.POWER_SUPPLY, new ArrayList<Good>());
        goods.put(TypeGood.RAM, new ArrayList<Good>());
        goods.put(TypeGood.OTHERS, new ArrayList<Good>());
    }

    /* Добавление товара в сборку
    * Параметры:
    * good - само комплектующее
    * typeGood - тип комплектующего
    * */
    public void addToBuild(TypeGood typeGood, Good good) {
        ArrayList<Good> current = getGoodList(typeGood);

        current.add(good);
        this.price += good.getPrice(); // Подсчет общей цены
    }

    /* Добавление товара в сборку
     * Параметры:
     * typeGood - тип комплектующего
     * index - индекс комплектующего, если в разделе есть список
     * */
    public void deleteFromBuild(TypeGood typeGood, int index) {
        ArrayList<Good> current = goods.get(typeGood);

        if (current == null)
            return;

        price -= current.get(index).getPrice();
        current.remove(index);
    }

    public ArrayList<Good> getGoodList(TypeGood typeGood) {
        return goods.get(typeGood);
    }

    public Good getGoodByIndex(TypeGood typeGood, int index) {
        ArrayList<Good> current = goods.get(typeGood);
        return current.get(index);
    }

    public double getPrice() {
        return price;
    }

    // Проверка полной сборки
    public boolean isComplete() {
        return goods.get(TypeGood.CPU).size() != 0 && goods.get(TypeGood.MOTHERBOARD).size() != 0 && goods.get(TypeGood.GPU).size() != 0
                && goods.get(TypeGood.POWER_SUPPLY).size() != 0 && goods.get(TypeGood.RAM).size() != 0
                && (goods.get(TypeGood.HDD).size() != 0 || goods.get(TypeGood.SSD).size() != 0);
    }

    //TODO
    public boolean checkCompatibility() {
        return false;
    }

}
