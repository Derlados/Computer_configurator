package com.derlados.computerconf.Managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.derlados.computerconf.App;
import com.derlados.computerconf.Constants.LogsKeys;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

// TODO перенести работу с файлами сюда
public class FileManager {
    final String IMAGES_DIR = "images";
    final String BUILDS_DIR = "builds";

    private File rootImages, rootBuilds;
    private Context appContext;

    private static FileManager instance;
    private FileManager(){};
    public static FileManager getFileManager() {
        if (instance == null) {
            instance = new FileManager();
            instance.init();
        }
        return instance;
    }

    // Инициализация основных данных
    private void init() {
        this.appContext = App.getApp().getApplicationContext();
        // Создание ссылок на основные дериктории
        this.rootImages = instance.appContext.getDir(instance.IMAGES_DIR, Context.MODE_PRIVATE);
        this.rootBuilds = instance.appContext.getDir(instance.BUILDS_DIR, Context.MODE_PRIVATE);
    }

    // Сохранение изображений
    public void saveImageOnDevice(Bitmap img, String imgName) {
        try {
            // Создание файла изображения
            File jpgImage = new File(rootImages, imgName);
            if (!jpgImage.exists()) {
                jpgImage.createNewFile();
                FileOutputStream fout = new FileOutputStream(jpgImage);

                // Запись изображения
                BitmapDrawable bmpDraw = new BitmapDrawable(appContext.getResources(), img);
                bmpDraw.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, fout);
            }
        }
        catch (Exception e) {
            Log.e(LogsKeys.ERROR_LOG.toString(), String.format(Locale.getDefault(),"Image %s cannot be save. Error: %s", imgName, e.toString()));
        }
    }

    // Чтение изображения с устройства
   public Bitmap restoreImageFromDevice(String imgName) {
        File imgFile = new File(rootImages.getPath() + '/' + imgName);
        if (imgFile.exists())
            return BitmapFactory.decodeFile(imgFile.getPath());
        else
            return null;
    }
}
