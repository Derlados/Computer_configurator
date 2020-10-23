package com.derlados.computerconf.Good;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.derlados.computerconf.Managers.RequestHelper;

import java.util.HashMap;

public class Good {
    private String name;
    private String imageUrl; // ссылка на скачивание
    private double price;

    // Ассоциативный массив (характеристика:значение), превью и полные данные соответственно
    private HashMap<String, String> stats;
    private transient HashMap<String, String> fullStats;

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public HashMap<String, String> getStats() {
        return stats;
    }

}
