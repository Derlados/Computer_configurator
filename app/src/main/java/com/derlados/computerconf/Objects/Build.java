package com.derlados.computerconf.Objects;

import android.util.TypedValue;

import com.derlados.computerconf.Constants.TypeGood;

import java.util.ArrayList;

public class Build {

    // Комплектующие
    private Good cpu;
    private Good motherboard;
    private Good gpu;
    private ArrayList<Good> ram = new ArrayList<>();
    private ArrayList<Good> hdd = new ArrayList<>(), ssd = new ArrayList<>();
    private Good powerSupply;
    private ArrayList<Good> others = new ArrayList<>();

    private double price = 0; // Цена сборки

    // Добавление и удаление сделаны одними методом так как можно будет вызвать его для любого типа товара, передав лишь аргумент который и так будет искаться

    /* Добавление товара в сборку
    * Параметры:
    * good - само комплектующее
    * typeGood - тип комплектующего
    * */
    public void addToBuild(TypeGood typeGood, Good good) {
        // Добавление комплектующего в соответствующее поле, если переданный объект не основной для сборки - он попадает в "другие"
        switch (typeGood) {
            case CPU:
                this.cpu = good;
                break;
            case GPU:
                this.gpu = good;
                break;
            case HDD:
                this.hdd.add(good);
                break;
            case RAM:
                this.ram.add(good);
                break;
            case SSD:
                this.ssd.add(good);
                break;
            case MOTHERBOARD:
                this.motherboard = good;
                break;
            case POWER_SUPPLY:
                this.powerSupply = good;
                break;
            default:
                this.others.add(good);
                break;
        }

        // Подсчет общей цены
        this.price += good.getPrice();
    }

    /* Добавление товара в сборку. Сделано одним методом так как можно будет вызвать его для любого типа товара
     * Параметры:
     * typeGood - тип комплектующего
     * index - индекс комплектующего, если в разделе есть список
     * */
    public void deleteFromBuild(TypeGood typeGood, int index) {
        // Удаление комплектующего в соответствующее поле
        switch (typeGood) {
            case CPU:
                price -= cpu.getPrice();
                cpu = null;
                break;
            case GPU:
                price -= gpu.getPrice();
                gpu = null;
                break;
            case HDD:
                price -= hdd.get(index).getPrice();
                hdd.remove(index);
                break;
            case RAM:
                price -= ram.get(index).getPrice();
                ram.remove(index);
                break;
            case SSD:
                price -= ssd.get(index).getPrice();
                ssd.remove(index);
                break;
            case MOTHERBOARD:
                price -= motherboard.getPrice();
                motherboard = null;
                break;
            case POWER_SUPPLY:
                price -= powerSupply.getPrice();
                powerSupply = null;
                break;
            default:
                price -= others.get(index).getPrice();
                others.remove(index);
                break;
        }
    }

    // Проверка полной сборки
    public boolean isComplete() {
        return cpu != null && motherboard != null && gpu != null && powerSupply != null && ram.size() != 0 && (hdd.size() != 0 || ssd.size() != 0);
    }

    //TODO
    public boolean checkCompatibility() {
        return false;
    }

    public Good getCpu() {
        return cpu;
    }

    public Good getMotherboard() {
        return motherboard;
    }

    public Good getGpu() {
        return gpu;
    }

    public ArrayList<Good> getRam() {
        return ram;
    }

    public ArrayList<Good> getHdd() {
        return hdd;
    }

    public ArrayList<Good> getSsd() {
        return ssd;
    }

    public ArrayList<Good> getOthers() {
        return others;
    }

    public double getPrice() {
        return price;
    }

    public Good getPowerSupply() {
        return powerSupply;
    }
}
