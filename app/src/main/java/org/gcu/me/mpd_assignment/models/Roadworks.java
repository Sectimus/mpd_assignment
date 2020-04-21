/*Amelia Magee | S1828146*/


package org.gcu.me.mpd_assignment.models;

import org.gcu.me.mpd_assignment.repositories.TrafficRepo.BuilderTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Roadworks extends Traffic{
    private LocalDateTime start;
    private LocalDateTime end;
    private static final DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private String delayInformation;

    public LocalDateTime getStart() {return start;}
    public void setStart(LocalDateTime start) {this.start = start;}

    public LocalDateTime getEnd() {return end;}
    public void setEnd(LocalDateTime end) {this.end = end;}

    public String getDelayInformation() {
        return delayInformation;
    }

    public void setDelayInformation(String delayInformation) {
        this.delayInformation = delayInformation;
    }

    private static final String resource = "https://trafficscotland.org/rss/feeds/roadworks.aspx";

    public Roadworks() {
        super();
    }

    public static void load(BuilderTask.TaskListener taskListener, Boolean force) {
        Traffic.load(resource, taskListener, force);
    }

    //loads all roadworks
//    @Override
//    public static void load(TrafficRepo.BuilderTask.TaskListener taskListener, Boolean force) {
//        TrafficRepo.BuilderTask task = new TrafficRepo.BuilderTask(taskListener, force);
//        task.execute(resource);
//    }

    //returns the duration of this roadwork in seconds
    public Long getDuration(){
        long seconds = this.start.until(this.end, ChronoUnit.SECONDS);

        return seconds;
    }

    //returns the duration of this roadwork as a string
    public String getDurationAsString(){
        long duration = this.getDuration();

        Duration d = Duration.ofSeconds(duration);
        long days = d.toDays();
        d = d.minusDays(days);
        long hours = d.toHours();
        d = d.minusHours(hours);
        long minutes = d.toMinutes();
        d = d.minusMinutes(minutes);
        long seconds = d.getSeconds();

        if (days > 0) {
            String type = "day";
            if (days > 1){
                type+="s";
            }
            return days+"  "+type;
        }
        if (hours > 0) {
            String type = "hour";
            if (hours > 1){
                type+="s";
            }
            return hours+"  "+type;
        }
        if (minutes > 0) {
            String type = "minute";
            if (minutes > 1){
                type+="s";
            }
            return minutes+"  "+type;
        }
        if (seconds > 0) {
            String type = "second";
            if (seconds > 1){
                type+="s";
            }
            return seconds+"  "+type;
        }
        return "instant";
    }

    public String getFormattedStart(){
        return this.start.format(dateformat);
    }

    public String getFormattedEnd(){
        return this.end.format(dateformat);
    }


}
