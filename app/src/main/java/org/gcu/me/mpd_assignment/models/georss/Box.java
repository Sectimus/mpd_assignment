/*Amelia Magee | S1828146*/


package org.gcu.me.mpd_assignment.models.georss;

import java.util.Objects;

public class Box implements Coordinates {
    //attributes
    private Point lower;
    private Point upper;

    //default constructor
    public Box(){
        super();
    }

    //complete constructor
    public Box(Point lowerLeft, Point upperRight) throws BoxIncompleteException {
        super();
        this.lower = lowerLeft;
        this.upper = upperRight;
        isBox();
    }

    private boolean isBox() throws BoxIncompleteException {
        if (this.lower == null || this.upper == null){
            throw new BoxIncompleteException(this);
        } else{
            return true;
        }
    }

    //getters
    public Point getLower() { return lower; }
    public Point getUpper() { return upper; }

    //setters
    public void setLower(Point lowerLeft) { this.lower = lowerLeft; }
    public void setUpper(Point upperRight) { this.upper = upperRight; }

    @Override
    public String toString() {
        return "Box{" +
                "lower=" + lower +
                ", upper=" + upper +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Box)) return false;
        Box box = (Box) o;
        return lower.equals(box.lower) &&
                upper.equals(box.upper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lower, upper);
    }

    static class BoxIncompleteException extends Exception{
        //There must be at least two pairs, otherwise the line cannot extend
        public BoxIncompleteException(Box l){
            super("There must be at least two points associated with a box: "+l);
        }
    }
}