package org.gcu.me.mpd_assignment.models;

import org.gcu.me.mpd_assignment.repositories.TrafficRepo;

public class Incident extends Traffic{
    private static final String resource = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";

    public static void load(TrafficRepo.BuilderTask.TaskListener taskListener, Boolean force) {
        Traffic.load(resource, taskListener, force);
    }
}
