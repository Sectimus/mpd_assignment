package org.gcu.me.mpd_assignment.models;

import org.gcu.me.mpd_assignment.repositories.TrafficRepo;

public class Roadworks extends Traffic{
    public Roadworks() {
        super();
    }

    public static void load(TrafficRepo.BuilderTask.TaskListener taskListener) {
        TrafficRepo.BuilderTask task = new TrafficRepo.BuilderTask(taskListener);
        task.execute("https://trafficscotland.org/rss/feeds/roadworks.aspx");
    }


}
