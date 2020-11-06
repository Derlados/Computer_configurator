package com.derlados.computerconf.Objects;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;

public class Good {

    private String name;
    private double price;
    private String urlFullData; // Ссылка на скачивание полных данных о комплектующем
    private String imageUrl; // Ссылка на скачивание изображения
    private String imageName; // Название изображения
    transient private  Bitmap image; // Скачанное изображение

    /* Ассоциативные массивы
     * previewData - превью данные (характеристика:значение)
     * fullData - полные данные (название блока характеристика : ассоциативный массив (характеристика:значение))
     * */
    private HashMap<String, String> previewData;

    // Для хранения блоков характеристик о комплектующем.
    // В Java нету ассоциативных массивов, а с сервера приходят данные в нужной последовательности, приходится выкручиваться
    public class dataBlock {
        public String header;
        public HashMap<String, String> data;
    }
    private ArrayList<dataBlock> fullData;



    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public HashMap<String, String> getPreviewData() {
        return previewData;
    }

    public String getUrlFullData() {
        return urlFullData;
    }

    public ArrayList<dataBlock>  getFullData() {
        return fullData;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getImageName() {
        return imageName;
    }

    public void setFullData(ArrayList<dataBlock>  fullData) {
        this.fullData = fullData;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

}
