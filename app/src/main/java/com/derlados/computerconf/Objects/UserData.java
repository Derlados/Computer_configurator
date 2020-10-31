package com.derlados.computerconf.Objects;

import java.util.ArrayList;

public class UserData {

    private ArrayList<Build> builds;
    private Build currentBuild;

    static UserData instance;
    private UserData() {}
    public  static UserData getUserData() {
        if (instance == null)
            instance = new UserData();
        return instance;
    }

    public void addBuild() {
        builds.add(new Build());
        currentBuild = builds.get(builds.size() - 1);
    }

    public ArrayList<Build> getBuilds() {
        return builds;
    }

    public Build getCurrentBuild() {
        return currentBuild;
    }
}
