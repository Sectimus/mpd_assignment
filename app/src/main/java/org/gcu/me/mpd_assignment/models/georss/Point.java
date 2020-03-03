package org.gcu.me.mpd_assignment.models.georss;

import java.util.Objects;

public class Point implements Coordinates {
    //attributes
    private Long lat;
    private Long lon;

    //default constructor
    public Point(){
        super();
    }

    //complete constructor
    public Point(Long lat, Long lon){
        super();
        this.lat = lat;
        this.lon = lon;
    }

    //getters
    public Long getLat() { return lat; }
    public Long getLon() { return lon; }

    //setters
    public void setLat(Long lat) { this.lat = lat; }
    public void setLon(Long lon) { this.lon = lon; }

    @Override
    public String toString() {
        return "Point{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
        Point point = (Point) o;
        return lat.equals(point.lat) &&
                lon.equals(point.lon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }
}