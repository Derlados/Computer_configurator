package com.derlados.computerconf.Good;

import android.graphics.Bitmap;

public class Good {
    private String[] previewStats = new String[]{"Сокет", "Ядер", "Потоков", "Частота", "Техпроцесс", "Кєш"};

    private String name;
    private double rating;
    private double price;
    private Bitmap img;

    public Good(String name, double rating, double cost) {
        this.name = name;
        this.rating = rating;
        this.price = cost;
    }

    public String[] getPreviewStats() {
        return previewStats;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

    public double getPrice() {
        return price;
    }

    public Bitmap getImg() {
        return img;
    }


}
