package com.derlados.computerconf.Objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Good {

    private String name;
    private double price;
    private String urlFullData; // Ссылка на скачивание полных данных о комплектующем
    private String imageUrl; // Ссылка на скачивание изображения
    private String imageName; // Название изображения

    /* Ассоциативные массивы
     * previewData - превью данные (характеристика:значение)
     * fullData - полные данные (название блока характеристика : ассоциативный массив (характеристика:значение))
     * */
    private HashMap<String, String> previewData;

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

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

    public ArrayList<dataBlock> getFullData() {
        return fullData;
    }

    public dataBlock getDataBlockByHeader(String header) {
        for (int i = 0; i < fullData.size(); ++i) {
            if (fullData.get(i).header.equals(header))
                return fullData.get(i);
        }
        return null;
    }

    // Данные изображения сохраняются и загружаются на устройство так как невозможно гарантировать стабильность работы с Bitmap который хранится прямо в объекте
    public Bitmap getImage() {
        return UserData.getUserData().restoreImageFromDevice(imageName);
    }

    public void setImage(Bitmap image) {
        UserData.getUserData().saveImageOnDevice(image, imageName);
    }

    public String getImageName() {
        return imageName;
    }

    public void setFullData(ArrayList<dataBlock>  fullData) {
        this.fullData = fullData;
    }


}
