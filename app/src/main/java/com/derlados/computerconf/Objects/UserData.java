package com.derlados.computerconf.Objects;

import java.util.ArrayList;

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

    //TODO
    private void restoreDataFromDevice() {

    }
}
