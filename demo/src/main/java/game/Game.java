package game;

import java.util.ArrayList;
import java.util.List;

public class Game {
    public static final int fieldSizeX = 5000;
    public static final int fieldSizeY = 5000;

    public static final int sideBuffer = 100;
    //GameElements are defined such that [0] = x coord, [1] = y coord, [2] = orientation, [3] = species, [4] = size, [5] = health, [6] = energy
    public static final ArrayList<int[]> gameElements = new ArrayList<>();
    //Species are defines such that [0] = speciesID, [1] = playerID, [2,3,4] outerColor, [5,6,7] innerColor, 
    public static final ArrayList<int[]> species = new ArrayList<>();
    public static final SpatialHashMap<int[]> spatial = new SpatialHashMap<>(200);
    
    static{
        for (int i = 0; i < 1000; i++){
            gameElements.add(new int[]{randInt(-fieldSizeX/2, fieldSizeX/2), randInt(-fieldSizeY/2,fieldSizeY/2), randInt(0, 360), randInt(0, 5), randInt(0, 255), randInt(0, 255), randInt(0, 255), randInt(0, 255), randInt(0, 255), randInt(0, 255), randInt(10, 50), randInt(0, 1)});
            //gameElements.add(new int[]{randInt(-fieldSizeX/2, fieldSizeX/2), randInt(-fieldSizeY/2,fieldSizeY/2), randInt(0, 360), randInt(0, 10), randInt(10, 100), randInt(10, 100), randInt(10, 100)});
            spatial.insertObjectForPoint(Vector2D.of(gameElements.get(i)[0], gameElements.get(i)[1]), gameElements.get(i));
        }
        for (int i = 0; i < 10; i++){
            species.add(new int[]{i, i, randInt(0, 255), randInt(0, 255), randInt(0, 255), randInt(0, 255), randInt(0, 255), randInt(0, 255)});
        }
    }

    private static int randInt(int lower, int upper){
        return (int)(Math.random() * (upper - lower + 1) + lower);
    }

    public static List<int[]> getViewableGameElements(int windowCenterX, int windowCenterY, int windowWidth, int windowHeight){
        List<int[]> seenElements = spatial.getObjectsInBounds(Vector2D.of(windowCenterX - windowWidth/2, windowCenterY - windowHeight/2), Vector2D.of(windowCenterX + windowWidth/2, windowCenterY + windowHeight/2));

        return seenElements;
    }

    public static List<int[]> getSpecies(){
        return species;
    }
}
