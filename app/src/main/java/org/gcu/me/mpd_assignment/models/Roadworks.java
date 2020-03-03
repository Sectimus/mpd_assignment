package org.gcu.me.mpd_assignment.models;

public class Roadworks extends Traffic{
    public Roadworks() {
        super();
    }

    public static void load(LoaderTask.TaskListener taskListener) {
        LoaderTask task = new LoaderTask(taskListener);
        task.execute("https://trafficscotland.org/rss/feeds/roadworks.aspx");
    }
}
