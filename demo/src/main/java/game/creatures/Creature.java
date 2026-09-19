package game.creatures;

import java.util.List;

import game.physics.CirclePhysics;
import game.physics.Vector2D;

public class Creature extends CirclePhysics{

    private int[] baseData = new int[7];
    private int orientation = 0;
    private int species = 0;

    public Creature(Vector2D initCoords, int species, int orientation, int radius, double mass, Vector2D initVelocity) {
        super(initCoords, radius, mass, initVelocity);
        this.orientation = orientation;
        this.species = species;
    }

    public int[] getBaseData(){
        return baseData;
    }
    
    public int getSpecies(){
        return species;
    }

    public void tickLife(int borderX1, int borderX2, int borderY1, int borderY2, double elasticity, List<Creature> list) {
        super.tick(borderX1, borderX2, borderY1, borderY2, elasticity, list);
        Creature closest = null;
        for (Creature c : list){
            if (c.getCoords().distanceTo(getCoords()) < 0){

            }
        }
        baseData[0] = (int)getCoords().x;
        baseData[1] = (int)getCoords().y;
        baseData[2] = orientation;
        baseData[3] = species;
        //TODO Implement
        baseData[4] = (int)getRadius();
        baseData[5] = 0;
        baseData[6] = 0;

    }
    
}