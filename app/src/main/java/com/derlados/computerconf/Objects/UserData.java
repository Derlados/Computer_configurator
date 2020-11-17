package com.derlados.computerconf.Objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.util.Log;

import com.derlados.computerconf.App;
import com.derlados.computerconf.Constants.HandlerMessages;
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
import android.os.Handler;

public class UserData {
    final String IMAGES_DIR = "images";
    final String BUILDS_DIR = "builds";

    // Две основные деректории где хранятся данные пользователя
    File rootImages, rootBuilds;
    private Context appContext;
    private boolean allDataRestore = false;

    private ArrayList<Build> builds = new ArrayList<>();

    // Для работы с текущей сборкой
    private Build oldCurrentBuild; // Первоначальная версия выбранной сборка
    private Build currentBuild; // Копия текущей сборки с которой пользователь работает
    private boolean currentBuildIsSaved = false;

    // Хендлер потока который вызывает загрузку данных с устройства
    Handler handler = null;
    ArrayList<Build> bufferBuilds = new ArrayList<Build>();
    final int BUFFER_BUILDS_SIZE = 3;

    static UserData instance;
    private UserData() {}
    public static UserData getUserData() {
        if (instance == null) {
            instance = new UserData();
            instance.appContext = App.getApp().getApplicationContext();
            // Создание ссылок на основные дериктории
            instance.rootImages = instance.appContext.getDir(instance.IMAGES_DIR, Context.MODE_PRIVATE);
            instance.rootBuilds = instance.appContext.getDir(instance.BUILDS_DIR, Context.MODE_PRIVATE);

            // Чтение сохраненных сборок
            Runnable restoring = new Runnable() {
                @Override
                public void run() {
                    instance.restoreBuildsFromDevice();
                }
            };
            Thread thread = new Thread(restoring);
            thread.start();
        }
        return instance;
    }

    public void addNewBuild() {
        builds.add(new Build());
        setCurrentBuild(builds.size() - 1); // Новая сборка становится текущей
    }

    public void getBuilds(Handler handler) {
        this.handler = handler;

        // Если данные выгружены, нету смысла ждать и прогонять через буфер
        if (allDataRestore) {
            sendBuildToHandler(builds, true);
            return;
        }
    }

    public Build getBuildByIndex(int i) {
        return builds.get(i);
    }

    public Build getCurrentBuild() {
        return currentBuild;
    }

    public void setCurrentBuild(int buildIndex) {
        currentBuildIsSaved = false;
        oldCurrentBuild = builds.get(buildIndex);
        // Текущий объект копируется для того, чтобы можно было откатить изменения
        try {
            this.currentBuild = (Build) oldCurrentBuild.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void discardCurrentBuild(boolean delete) {
        if (delete)
            builds.remove(oldCurrentBuild);
        currentBuild = null;
        oldCurrentBuild = null;
    }

    /* Отправка данных объекту который требует их через соответствующий хендлер
    * Параметры:
    * build - сборка
    * send - флаг того надо отправить данные сейчас или нет
    * */
    private void sendBuildToHandler(ArrayList<Build> builds, boolean finish) {

        if (this.handler != null) {
            Message msg = handler.obtainMessage();
            msg.obj = builds;

            if (finish)
                msg.what = HandlerMessages.FINISH.ordinal();

            this.handler.sendMessage(msg);
            bufferBuilds = new ArrayList<>();
        }
    }

    // Сохранение изображений
    protected void saveImageOnDevice(Bitmap img, String imgName) {
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

    // Сохранение всех сборок в память
    public void saveCurrentBuild() {
        // При сохранении копия заменяется на новоизменнную сборку и разумеется необходимо создать снова копию сохраненной
        int index = builds.indexOf(oldCurrentBuild);
        builds.set(index, this.currentBuild);
        setCurrentBuild(index);


        currentBuildIsSaved = true;
        Gson gson = new Gson();

        // Сохраненние изображений всех комплектующих
        HashMap<TypeGood, Good> buildGoods = currentBuild.getGoods();
        for (HashMap.Entry<TypeGood, Good> entry : buildGoods.entrySet()) {
            Good good = entry.getValue();
            saveImageOnDevice(good.getImage(), good.getImageName());
        }

        // Сохраненние самой сборки в файл, где имя файла - имя самой сборки
        try {
            String fileNameBuild = currentBuild.getId();
            File buildFile = new File(rootBuilds, fileNameBuild);
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
                    bufferBuilds.add(build);
                    builds.add(build);

                    // При достижении буфера, передаются сборки в главный потко
                    if (bufferBuilds.size() >= BUFFER_BUILDS_SIZE) {
                        sendBuildToHandler(bufferBuilds, false);
                        bufferBuilds.clear();
                    }
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e(LogsKeys.ERROR_LOG.toString(), String.format(Locale.getDefault(), "File %s can't be read. Error: %s", file.getName(), e.toString()));
                }
            }

            sendBuildToHandler(bufferBuilds, true); // Отправка остатка
            allDataRestore = true;
        }
    }

    // Чтение изображения с устройства
    protected Bitmap restoreImageFromDevice(String imgName) {
        File imgFile = new File(rootImages.getPath() + '/' + imgName);
        if (imgFile.exists())
            return BitmapFactory.decodeFile(imgFile.getPath());
        else
            return null;
    }

    // Удаление сборки
    public void deleteBuildByIndex(int index) {
        File file = new File(rootBuilds + "/" + builds.get(index).getId());
        file.delete();
        builds.remove(index);
    }

    public boolean isCurrentBuildIsSaved() {
        return currentBuildIsSaved;
    }
}
