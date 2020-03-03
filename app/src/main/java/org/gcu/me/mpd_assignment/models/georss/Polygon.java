package org.gcu.me.mpd_assignment.models.georss;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Polygon implements Coordinates {
    //attributes
    private List<Point> points;

    //default constructor
    public Polygon(){
        super();
    }

    //complete constructor
    public Polygon(List<Point> points) throws PolygonNotConnectedException, PolygonIncompleteException {
        super();
        this.points = points;

        this.isPolygon();
    }

    //spread operator constructor
    public Polygon(Point ...points) throws PolygonNotConnectedException, PolygonIncompleteException {
        super();
        this.points = new ArrayList<>();
        for (Point p : points){
            this.points.add(p);
        }
        this.isPolygon();
    }

    private boolean isPolygon() throws PolygonNotConnectedException, PolygonIncompleteException {
        Point first = this.points.get(0);
        Point last = this.points.get(this.points.size()-1);
        if(this.points.size()<4){
            throw new PolygonIncompleteException(this);
        }else if(!first.equals(last)){
            throw new PolygonNotConnectedException(first, last);
        }else{
            return true;
        }
    }

    //getters
    public List<Point> getPoints() { return this.points; }

    //setters
    public void setPoints(List<Point> points) { this.points = points; }

    @Override
    public String toString() {
        return "Polygon{" +
                "points=" + points +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Polygon)) return false;
        Polygon polygon = (Polygon) o;
        return points.equals(polygon.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(points);
    }

    //exceptions
    static class PolygonNotConnectedException extends Exception{
        //The last point in a polygon must be identical to the first, otherwise it is not a polygon
        public PolygonNotConnectedException(Point first, Point last){
            super("The last point in a polygon must be identical to the first, otherwise it is not a polygon! First: "+first+" Last "+last);
        }
    }
    static class PolygonIncompleteException extends Exception{
        //There must be at least four pairs, otherwise the polygon cannot connect to itself
        public PolygonIncompleteException(Polygon p){
            super("There must be at least four points associated with a polygon: "+p);
        }
    }
}