package game.creatures;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import game.physics.CirclePhysics;
import game.physics.Vector2D;

public class Creature extends CirclePhysics{

    private int[] baseData = new int[7];
    private int orientation = 0;
    private int speciesID = 0;
    private double health = radius * 10;
    private double energy = 0;
    private Supplier<Integer> maxHealth = () -> radius * 10;
    private Supplier<Integer> maxEnergy = () -> radius * 10;

    private HashMap<String, Integer> species;

    public Creature(Vector2D initCoords, int species, int orientation, int radius, double mass, Vector2D initVelocity, double dragCoef) {
        super(initCoords, radius, mass, initVelocity, dragCoef);
        this.orientation = orientation;
        this.speciesID = species;
        this.species = Global.getSpecies().get(speciesID);
    }

    //Only use for duplicating
    private void setHealth(int value) {
        this.health = value;
    }

    public int[] getBaseData(){
        return baseData;
    }
    
    public int getSpeciesID(){
        return speciesID;
    }

    public HashMap<String, Integer> getSpecies(){
        return species;
    }

    

    public void tickLife(int borderX1, int borderX2, int borderY1, int borderY2, double elasticity, List<Creature> list) {
        super.tick(borderX1, borderX2, borderY1, borderY2, elasticity, list);
        healthTick();
        if (species.get("shape") == 0) {
            producerLife();
        } else if (species.get("shape") == 1){
            consumerLife(list);
        }
        duplicateOrGrow();
        if (energy > maxEnergy.get()) {
            energy = maxEnergy.get();
        }
        if (health > maxHealth.get()) {
            health = maxHealth.get();
        }
        baseData[0] = (int)getCoords().x;
        baseData[1] = (int)getCoords().y;
        baseData[2] = orientation;
        baseData[3] = speciesID;
        //TODO Implement
        baseData[4] = (int)getRadius();
        baseData[5] = (int)health;
        baseData[6] = (int)energy;
    }

    public void producerLife() {
        //TODO
        energy += .01 * radius;
    }

    public void consumerLife(List<Creature> list) {
        energy -= .6;
        Creature closest = null;
        double minDist = Double.MAX_VALUE;
        for (Creature c : list){
            double dist = c.getCoords().distanceTo(getCoords());
            if (dist < minDist && c.speciesID != this.speciesID){
                minDist = dist;
                closest = c;
            }
        }
        orientation = (int)velocity.angleDegrees();
        if (closest == null) {
            return;
        }
        energy -= .01;
        if (closest.speciesID != this.speciesID) {
            acceleration = (closest.coords.subtract(this.coords)).normalize().multiply(species.get("maxAccell"));
            return;
        }
        
        acceleration = (closest.coords.subtract(this.coords)).normalize().multiply(-species.get("maxAccell"));
    }

    @Override
    public void onCollide(CirclePhysics other) {
        if (species.get("shape") == 0) {
            return;
        }
        Creature otherCreature = (Creature)other;
        if (otherCreature.speciesID == this.speciesID) {
            return;
        }
        otherCreature.damage((int)Math.sqrt(this.velocity.magnitude() + Math.sqrt(this.radius)));
        energy += (int)Math.sqrt(this.velocity.magnitude() * this.radius);
    }

    public void damage(double amount) {
        health -= amount;
        if (health < 0) {
            Global.killCreature(this);
        }
    }

    public void healthTick() {
        if (energy > 0 && health < maxHealth.get()) {
            health += .1;
            energy -= .1;
        }
        if (energy < 0) {
            damage(.3);
        }
    }    

    public void duplicateOrGrow() {
        if (health != maxHealth.get()) {
            return;
        }
        if (radius < species.get("maxSize") && energy > 10) {
            energy -= 5;
            radius++;
            mass++;
            return;
        }
        if (energy > radius * 7) {
            Creature child = new Creature(coords, speciesID, orientation, radius/2, mass/2, Vector2D.randDirection(species.get("maxAccell")), dragCoef);
            child.setHealth(radius * 3);
            Global.addCreature(child);
            energy -= radius * 7;
        }
    }
}