package game.physics;

public class Vector2D {
    public double x, y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D add(Vector2D other) {
        return new Vector2D(this.x + other.x, this.y + other.y);
    }

    public Vector2D subtract(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }

    public Vector2D multiply(double scalar) {
        return new Vector2D(this.x * scalar, this.y * scalar);
    }

    public double dot(Vector2D other) {
        return this.x * other.x + this.y * other.y;
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y+.000000001);
    }

    public Vector2D normalize() {
        double mag = magnitude();
        return new Vector2D(x / mag, y / mag);
    }
    
    public double distanceTo(Vector2D vector){
        return Math.sqrt(Math.pow(this.x-vector.x,2)+Math.pow(this.y-vector.y,2));
    }

    public double calculateAngleBetweenPoints(Vector2D other) {
        double deltaX;
        double deltaY;
        deltaX = other.x - this.x;
        deltaY = other.y - this.y;

        double angleRadians = ((Math.atan(deltaY/deltaX)));

        double rotation = Math.toDegrees(angleRadians);
        if (this.x<=other.x){
            return rotation;
        }
        if (rotation>0){
            return rotation-180;
        }else{
            return rotation+180;
        }
    }

    @Override
    public String toString(){
        return "["+x+", "+y+"]";
    }
}
