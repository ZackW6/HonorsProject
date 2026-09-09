package game;

import java.util.Objects;

public class Vector2D implements Comparable<Vector2D>{
    public int x, y;

    public Vector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D() {}

    public static Vector2D of(int x, int y){
        return new Vector2D(x, y);
    }

    public static Vector2D of(Vector2D other){
        return new Vector2D(other.x, other.y);
    }

    public Vector2D add(Vector2D other) {
        return new Vector2D(this.x + other.x, this.y + other.y);
    }

    public Vector2D subtract(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }

    public Vector2D multiply(int scalar) {
        return new Vector2D(this.x * scalar, this.y * scalar);
    }

    @Override
    public String toString(){
        return "["+x+", "+y+"]";
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public int compareTo(Vector2D other) {
        int cmpX = Integer.compare(this.x, other.x);
        return (cmpX != 0) ? cmpX : Integer.compare(this.y, other.y);
    }

    @Override 
    public boolean equals(Object other){
        boolean sameClass = other.getClass().equals(this.getClass());
        Vector2D o = ((Vector2D)other);
        return sameClass ? this.x == o.x && this.y == o.y : false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x,y);
    }
}
