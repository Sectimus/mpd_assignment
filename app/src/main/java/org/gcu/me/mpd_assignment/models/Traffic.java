package org.gcu.me.mpd_assignment.models;

import android.os.AsyncTask;
import android.util.Log;

import org.gcu.me.mpd_assignment.models.georss.Coordinates;
import org.gcu.me.mpd_assignment.repositories.TrafficRepo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public abstract class Traffic {
    private String title;
    private String description;
    private String link;
    private Coordinates location;
    private String author;
    private List<String> comments;
    private Date pubDate;
    private HashMap<String, String> properties;

    public Traffic(){
        this.properties = new HashMap<>();
    }

    public HashMap<String, String> addProperty(String key, String value){
        this.properties.put(key, value);
        return this.properties;
    }

    protected static void load(String resource, TrafficRepo.BuilderTask.TaskListener taskListener, Boolean force){
        TrafficRepo.BuilderTask task = new TrafficRepo.BuilderTask(taskListener, force);
        task.execute(resource);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Coordinates getLocation() {
        return location;
    }

    public void setLocation(Coordinates location) {
        this.location = location;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public HashMap<String, String> getProperties() {return properties;}

    public void setProperties(HashMap<String, String> properties) {this.properties = properties;}
}
