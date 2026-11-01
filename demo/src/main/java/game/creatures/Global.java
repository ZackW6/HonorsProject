package game.creatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.mapping.SpatialHashMap;
import game.mapping.Vector2D;

public class Global {
    public static final int fieldSizeX = 7500;
    public static final int fieldSizeY = 7500;

    //species["speciesID"],["playerID"],["outerColorR"],["outerColorG"],["outerColorB"],["innerColorR"],["innerColorG"],["innerColorB"],["shape"]
    public static ArrayList<HashMap<String, Integer>> globalSpecies = new ArrayList<HashMap<String, Integer>>();
    //Creatures are defined such that [0] = x coord, [1] = y coord, [2] = orientation, [3] = species, [4] = size, [5] = health, [6] = energy
    public static ArrayList<Creature> globalCreatures = new ArrayList<>();

    public static SpatialHashMap<Creature> spatial = new SpatialHashMap<>(200);

    private static Thread t;
    public static void beginCreatures(){
        for (int i = 0; i < 10; i++){
            HashMap<String, Integer> specii = new HashMap<>(Map.of(
                "speciesID", i,
                "playerID", i,
                "outerColorR", randInt(0, 255),
                "outerColorG", randInt(0, 255),
                "outerColorB", randInt(0, 255),
                "innerColorR", randInt(0, 255),
                "innerColorG", randInt(0, 255),
                "innerColorB", randInt(0, 255),
                "shape", randInt(0, 1)

            ));
            globalSpecies.add(specii);
        }

        for (int i = 0; i < 4000; i++){
            int size = randInt(10, 50);
            globalCreatures.add(new Creature(
                new game.physics.Vector2D(randInt(-fieldSizeX/2, fieldSizeX/2), randInt(-fieldSizeY/2,fieldSizeY/2)), 
                randInt(0, 9),
                randInt(0, 360), 
                size, 
                size, 
                new game.physics.Vector2D(randInt(-100, 100), randInt(-100,100)),
                .5));
        }
        
        t = new Thread(()->{
            while (true){
                SpatialHashMap<Creature> newSpatial = new SpatialHashMap<>(200);
                for (Creature c : globalCreatures){
                    Vector2D creatureCoords = new Vector2D((int)c.getCoords().x, (int)c.getCoords().y);
                    List<Creature> nearbyCreatures = spatial.getObjectsInBounds(creatureCoords.add(new Vector2D(-200,-200)), creatureCoords.add(new Vector2D(200,200)));

                    c.tickLife(-fieldSizeX/2, fieldSizeX/2, -fieldSizeY/2, fieldSizeY/2, 1, nearbyCreatures);
                    creatureCoords = new Vector2D((int)c.getCoords().x, (int)c.getCoords().y);
                    newSpatial.insertObjectForPoint(creatureCoords, c);
                }
                spatial = newSpatial;
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "physicsLoop");
        t.setDaemon(true);
        t.start();
    }

    private static int randInt(int lower, int upper){
        return (int)(Math.random() * (upper - lower + 1) + lower);
    }

    public static List<int[]> getViewableGameElements(int windowCenterX, int windowCenterY, int windowWidth, int windowHeight){
        List<Creature> creatures = spatial.getObjectsInBounds(Vector2D.of(windowCenterX - windowWidth/2, windowCenterY - windowHeight/2), Vector2D.of(windowCenterX + windowWidth/2, windowCenterY + windowHeight/2));
        ArrayList<int[]> seenElements = new ArrayList<>();
        for (Creature c : creatures){
            seenElements.add(c.getBaseData());
        }
        return seenElements;
    }

    public static List<HashMap<String, Integer>> getSpecies(){
        return globalSpecies;
    }
}
