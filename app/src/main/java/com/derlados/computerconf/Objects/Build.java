package com.derlados.computerconf.Objects;

import android.animation.TypeEvaluator;
import android.graphics.Bitmap;
import android.util.TypedValue;

import com.derlados.computerconf.Constants.TypeGood;

import java.util.ArrayList;
import java.util.HashMap;

public class Build {

    /* Комплектующие, сделаны одного типа ArrayList<Good> и помещены в HashMap, так как проблематично разделить возврат объекта одного или массива.
    * Для мобильности кода выгодно чтобы они были ArrayList<Good>, хоть к примеру материнку можно взять только одну.
    * В будущем можно улучшить сборки где будут участвовать материнки на два процессора, на несколько видокарт
    * Опять же помещено в хеш мап, так как нету простых ассоциативных массивов*/
    private HashMap<TypeGood, Good> goods = new HashMap<>();
    private double price = 0; // Цена сборки
    private String name = "", description = ""; // Имя и описание в сборке

    // Инициализация всех типов товаров
    public Build() {

    }

    //TODO
    public boolean checkCompatibility() {

        /*
        // Подготовка всехкомплектующих
        Good cpu = goods.get(TypeGood.CPU).size() != 0 ? goods.get(TypeGood.CPU).get(0) : null;
        ArrayList<Good> rams = goods.get(TypeGood.RAM);
        Good motherboard = goods.get(TypeGood.MOTHERBOARD).size() != 0 ? goods.get(TypeGood.MOTHERBOARD).get(0) : null;
        Good powerSupply = goods.get(TypeGood.POWER_SUPPLY).size() != 0 ? goods.get(TypeGood.POWER_SUPPLY).get(0) : null;
        */


        return true;
    }

    /* Добавление товара в сборку
     * Параметры:
     * good - само комплектующее
     * typeGood - тип комплектующего
     * */
    public void addToBuild(TypeGood typeGood, Good good) {
        goods.put(typeGood, good);
        this.price += good.getPrice(); // Подсчет общей цены
    }

    // Проверка полной сборки
    public boolean isComplete() {
        return goods.get(TypeGood.CPU) != null && goods.get(TypeGood.MOTHERBOARD) != null && goods.get(TypeGood.GPU) != null
                && goods.get(TypeGood.POWER_SUPPLY) != null && goods.get(TypeGood.RAM) != null
                && (goods.get(TypeGood.HDD) != null || goods.get(TypeGood.SSD) != null);
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
    }
}
