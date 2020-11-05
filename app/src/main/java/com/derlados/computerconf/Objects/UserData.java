package com.derlados.computerconf.Objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.derlados.computerconf.Constants.LogsKeys;
import com.derlados.computerconf.Constants.TypeGood;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class UserData {
    final String IMAGES_DIR = "images";
    final String BUILDS_DIR = "builds";

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
            File jpgImage = new File(context.getDir(IMAGES_DIR, Context.MODE_PRIVATE), + '/' + imgName);
            if (!jpgImage.exists())
                jpgImage.createNewFile();
            FileOutputStream fout = new FileOutputStream(jpgImage);

            // Запись изображения
            BitmapDrawable bmpDraw = new BitmapDrawable(context.getResources(), img);
            bmpDraw.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, fout);
        }
        catch (Exception e) {
            Log.e(LogsKeys.ERROR_LOG.toString(), String.format(Locale.getDefault(),"Image %s cannot be save. Error: %s", imgName, e.toString()));
        }
    }

    // Сохранение всех сборок в память
    public void saveCurrentBuild (Context context) {
        File rootBuilds = context.getDir(BUILDS_DIR, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        // Сохраненние всех изображений сборки
        HashMap<TypeGood, ArrayList<Good>> allGoods = currentBuild.getGoods();
        for (HashMap.Entry<TypeGood, ArrayList<Good>> entry : allGoods.entrySet())
            for (int i = 0; i < entry.getValue().size(); ++i) {
                Good good = entry.getValue().get(i);
                //TODO
                // Имя надо бы узнавать как то прозрачнее
                String[] splitUrl = good.getImageUrl().split("/");
                String imgName = splitUrl[splitUrl.length - 1];
                saveImageOnDevice(context, good.getImage(), imgName);
            }

        // Сохраненние самой сборки в файл, где имя файла - имя самой сборки
        try {
            //TODO
            String nameBuild = currentBuild.getName().equals("") ? "default" : currentBuild.getName();
            File buildFile = new File(rootBuilds, '/' + nameBuild);
            BufferedWriter writer = new BufferedWriter(new FileWriter(buildFile));
            writer.write(gson.toJson(currentBuild));
        }
        catch (IOException e) {
            Log.e(LogsKeys.ERROR_LOG.toString(), e.toString());
        }
    }

    //TODO
    private void restoreDataFromDevice() {
    }
}
