package org.gcu.me.mpd_assignment.models;

import org.gcu.me.mpd_assignment.repositories.TrafficRepo.BuilderTask;

public class Roadworks extends Traffic{
    private static final String resource = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    public Roadworks() {
        super();
    }

    //loads all roadworks
    public static void load(BuilderTask.TaskListener taskListener, Boolean force) {
        BuilderTask task = new BuilderTask(taskListener, force);
        task.execute(resource);
    }

    @Override
    public int getTrafficId() {
        return 1;
    }
}
