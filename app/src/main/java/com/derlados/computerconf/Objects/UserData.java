package com.derlados.computerconf.Objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.derlados.computerconf.Constants.LogsKeys;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class UserData {

    private ArrayList<Build> builds = new ArrayList<>();
    private Build currentBuild;

    static UserData instance;
    private UserData() {}
    public static UserData getUserData() {
        if (instance == null)
            instance = new UserData();
        return instance;
    }

    public Build addBuild() {
        builds.add(new Build());
        currentBuild = builds.get(builds.size() - 1);
        return currentBuild;
    }

    public ArrayList<Build> getBuilds() {
        return builds;
    }

    public Build getCurrentBuild() {
        return currentBuild;
    }

    // Сохранение изображений
    public void saveImageOnDevice (Context context, Bitmap img, String imgName) {
        try {
            // Создание файла изображения
            File jpgImage = new File(context.getDir("images", Context.MODE_PRIVATE), + '/' + imgName);
            if (!jpgImage.exists())
                jpgImage.createNewFile();
            FileOutputStream fout = new FileOutputStream(jpgImage);

            // Запись изображения
            BitmapDrawable bmpDraw = new BitmapDrawable(context.getResources(), img);
            bmpDraw.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, fout);
        }
        catch (Exception ex) {
            Log.e(LogsKeys.ERROR_LOG.toString(), String.format(Locale.getDefault(),"Image %s cannot be save", imgName));
        }
    }

    // Сохранение всех сборок
    public void saveBuilds () {

    }

    //TODO
    private void restoreDataFromDevice() {

    }
}
