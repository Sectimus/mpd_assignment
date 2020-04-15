package org.gcu.me.mpd_assignment.models;

import org.gcu.me.mpd_assignment.repositories.TrafficRepo;

public class PlannedRoadworks extends Roadworks {
    private static final String resource = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";

    //loads all roadworks
    public static void load(TrafficRepo.BuilderTask.TaskListener taskListener, Boolean force) {
        TrafficRepo.BuilderTask task = new TrafficRepo.BuilderTask(taskListener, force);
        task.execute(resource);
    }
}
