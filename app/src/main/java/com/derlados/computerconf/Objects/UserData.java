package com.derlados.computerconf.Objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.derlados.computerconf.Constants.LogsKeys;
import com.derlados.computerconf.Constants.TypeGood;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class UserData {
    final String IMAGES_DIR = "images";
    final String BUILDS_DIR = "builds";

    // Две основные деректории где хранятся данные пользователя
    File rootImages, rootBuilds;

    private ArrayList<Build> builds = new ArrayList<>();
    private Build currentBuild;
    private Context context;

    static UserData instance;
    private UserData() {}
    public static UserData getUserData(Context context) {
        if (instance == null) {
            instance = new UserData();
            instance.context = context;
            // Создание ссылок на основные дериктории
            instance.rootImages = context.getDir(instance.IMAGES_DIR, Context.MODE_PRIVATE);
            instance.rootBuilds = context.getDir(instance.BUILDS_DIR, Context.MODE_PRIVATE);

            // Чтение сохраненных сборок
            instance.restoreBuildsFromDevice();
        }
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
    public void saveImageOnDevice (Bitmap img, String imgName) {
        try {
            // Создание файла изображения
            File jpgImage = new File(rootImages, imgName);
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
    public void saveCurrentBuild () {
        Gson gson = new Gson();

        // Сохраненние изображений всех комплектующих
        HashMap<TypeGood, ArrayList<Good>> buildGoods = currentBuild.getGoods();
        for (HashMap.Entry<TypeGood, ArrayList<Good>> entry : buildGoods.entrySet()) {
            for (int i = 0; i < entry.getValue().size(); ++i) {
                Good good = entry.getValue().get(i);
                saveImageOnDevice(good.getImage(), good.getImageName());
            }
        }

        // Сохраненние самой сборки в файл, где имя файла - имя самой сборки
        try {
            //TODO
            String nameBuild = currentBuild.getName().equals("") ? "default" : currentBuild.getName();
            File buildFile = new File(rootBuilds, nameBuild);
            BufferedWriter writer = new BufferedWriter(new FileWriter(buildFile));
            String json = gson.toJson(currentBuild);
            writer.write(json);
            writer.close();
        }
        catch (IOException e) {
            Log.e(LogsKeys.ERROR_LOG.toString(), e.toString());
        }
    }

    // Чтение всех данных о сборках
    private void restoreBuildsFromDevice() {
        Gson gson = new Gson();

        // Перебор всех файлов в директории Build и их чтение с перевод из JSON
        File[] listFile = rootBuilds.listFiles();
        if (listFile != null) {
            for (File file : rootBuilds.listFiles()) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    Build build = gson.fromJson(br, Build.class);
                    restoreImagesInBuild(build);
                    builds.add(build);
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Чтение всех необходимых изображений для сборки
    private void restoreImagesInBuild(Build build) {
        HashMap<TypeGood, ArrayList<Good>> goodsList = build.getGoods();

        for (HashMap.Entry<TypeGood, ArrayList<Good>> entry : goodsList.entrySet()) {
            ArrayList<Good> goods = entry.getValue();

            // Загрузка изображений комплектующих
            for (int i = 0; i < goods.size(); ++i) {
                Good good = goods.get(i);
                Bitmap image = BitmapFactory.decodeFile(rootImages.getPath() + '/' + good.getImageName());
                good.setImage(image);
            }
        }
    }

}
