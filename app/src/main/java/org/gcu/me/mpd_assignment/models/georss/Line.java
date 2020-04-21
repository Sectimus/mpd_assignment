/*Amelia Magee | S1828146*/


package org.gcu.me.mpd_assignment.models.georss;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Line implements Coordinates {
    //attributes
    private List<Point> points;

    //default constructor
    public Line(){
        super();
    }

    //complete constructor
    public Line(List<Point> points) throws LineIncompleteException {
        super();
        this.points = points;
        isLine();
    }

    //spread operator constructor
    public Line(Point ...points) throws LineIncompleteException {
        super();
        this.points = new ArrayList<>();
        for (Point p : points){
            this.points.add(p);
        }
        isLine();
    }

    private boolean isLine() throws LineIncompleteException {
        if (this.points.size()<2){
            throw new LineIncompleteException(this);
        } else{
            return true;
        }
    }

    //getters
    public List<Point> getPoints() { return this.points; }

    //setters
    public void setPoints(List<Point> points) { this.points = points; }

    @Override
    public String toString() {
        return "Line{" +
                "points=" + points +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;
        Line line = (Line) o;
        return points.equals(line.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(points);
    }

    static class LineIncompleteException extends Exception{
        //There must be at least two pairs, otherwise the line cannot extend
        public LineIncompleteException(Line l){
            super("There must be at least two points associated with a line: "+l);
        }
    }
}