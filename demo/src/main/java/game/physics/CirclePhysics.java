package game.physics;

import java.util.List;


//This is a heavily modified file from a different project of mine, but given this projects complexity, I hope its fine
public class CirclePhysics {
    private double mass;
    private Vector2D velocity;
    private Vector2D acceleration;
    private Vector2D coords;
    private double dragCoef;
    private int radius;

    public CirclePhysics(Vector2D initCoords, int radius, double mass, Vector2D initVelocity, double dragCoef) {
        this.coords = initCoords;
        this.radius = radius;
        this.mass = mass;
        this.velocity = initVelocity;
        this.acceleration = new Vector2D(0, 0);
        this.dragCoef = dragCoef;
    }

    public Vector2D getCoords(){
        return coords;
    }

    public double getRadius(){
        return radius;
    }

    public void setAcceleration(Vector2D acceleration){
        this.acceleration = acceleration;
    }

    public void tick(int borderX1, int borderX2, int borderY1, int borderY2, double elasticity, List<? extends CirclePhysics> list){
        applyMovement();
        handleBorderCollision(borderX1, borderX2, borderY1, borderY2, elasticity);
        handleCircleCollision(list, elasticity);
    }

    public void handleBorderCollision(int borderX1, int borderX2, int borderY1, int borderY2, double elasticity){
        if (coords.x<=borderX1){
            velocity.x = Math.abs(velocity.x)*elasticity;
            coords.x = borderX1;
        }
        if (coords.x+radius>=borderX2){
            velocity.x = Math.abs(velocity.x)*-elasticity;
            coords.x  = borderX2-radius;
        }
        if(coords.y<=borderY1){
            velocity.y = Math.abs(velocity.y)*elasticity;
            coords.y  = borderY1;
        }
        if(coords.y+radius>=borderY2){
            velocity.y = Math.abs(velocity.y)*-elasticity;
            coords.y  = borderY2-radius;
        }
    }
    
    public void handleCircleCollision(List<? extends CirclePhysics> list, double elasticity){
        for (CirclePhysics other : list){
            Vector2D center1 = new Vector2D(coords.x, coords.y);
            Vector2D center2 = new Vector2D(other.coords.x, other.coords.y);

            // Distance calculation
            double dist = center1.distanceTo(center2);

            if (dist <= radius+other.radius && !equals(other)) {
                Vector2D n = center2.subtract(center1);
                double distance = n.magnitude();
                double overlap = radius + other.radius - distance;

                // Normalizing the vector
                n = n.normalize(); // Normalize before computing correction

                Vector2D t = new Vector2D(-n.y, n.x); // Tangent

                double v1n = velocity.dot(n);
                double v2n = other.velocity.dot(n);
                double v1t = velocity.dot(t);
                double v2t = other.velocity.dot(t);

                // Calculate new normal velocities after collision
                double v1nPrime = (v1n * (mass - other.mass) + 2 * other.mass * v2n) / (mass + other.mass);
                double v2nPrime = (v2n * (other.mass - mass) + 2 * mass * v1n) / (mass + other.mass);

                Vector2D v1nPrimeVec = n.multiply(v1nPrime);
                Vector2D v2nPrimeVec = n.multiply(v2nPrime);
                Vector2D v1tPrimeVec = t.multiply(v1t);
                Vector2D v2tPrimeVec = t.multiply(v2t);

                // Update velocities
                velocity = v1nPrimeVec.add(v1tPrimeVec).multiply(elasticity);
                other.velocity = v2nPrimeVec.add(v2tPrimeVec).multiply(elasticity);

                // Positional correction to avoid sinking issues
                if (overlap > 0) {
                    Vector2D correction = n.multiply(overlap);
                    coords = coords.subtract(correction.multiply(other.mass / (mass + other.mass)));
                    other.coords = other.coords.add(correction.multiply(mass / (mass + other.mass)));
                }
            }
        }
    }
    
    private long lastTimeMilli = System.currentTimeMillis();
    public void applyMovement(){
        long currentTimeMilli = System.currentTimeMillis();
        double deltaSeconds = (currentTimeMilli - lastTimeMilli)/1000.0;
        lastTimeMilli = currentTimeMilli;

        Vector2D dragAccel = new Vector2D(acceleration).subtract(velocity.multiply(dragCoef));//.multiply(deltaSeconds);
        velocity = velocity.add(dragAccel.multiply(deltaSeconds));
        coords = coords.add(velocity.multiply(deltaSeconds));
    }
}