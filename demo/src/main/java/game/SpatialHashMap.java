package game;
//https://gamedev.net/tutorials/programming/general-and-gameplay-programming/spatial-hashing-r2697

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpatialHashMap <T>{
    private int cellSize;
    private HashMap<Vector2D, ArrayList<T>> contents;
    public SpatialHashMap(int cellSize){
        this.cellSize = cellSize;
        contents = new HashMap<>();
    }

    private Vector2D hash(Vector2D point){
        return new Vector2D((int)(point.x/cellSize), (int)(point.y/cellSize));
    }

    public void insertObjectForPoint(Vector2D point, T element){
        Vector2D hash = hash(point);
        contents.putIfAbsent(hash, new ArrayList<>());
        contents.get(hash).add(element);
    }

    public boolean removeObjectForPoint(Vector2D point, T element){
        Vector2D hash = hash(point);
        contents.putIfAbsent(hash, new ArrayList<>());
        return contents.get(hash).remove(element);
    }

    public List<T> getObjectsAtPoint(Vector2D point){
        Vector2D hash = hash(point);
        contents.putIfAbsent(hash, new ArrayList<>());
        return contents.get(hash);
    } 

    public List<T> getObjectsAtHash(Vector2D hash){
        contents.putIfAbsent(hash, new ArrayList<>());
        return contents.get(hash);
    } 

    /**
     * Gets all objects that are contained in the cells collided with by the bounds, plus those within
     * This is for ints, i don't care if you give a double.
     * @param pointOne
     * @param pointTwo
     * @return All elements T as described above
     */
    public List<T> getObjectsInBounds(Vector2D pointOne, Vector2D pointTwo) {
        Vector2D max = hash(new Vector2D(Math.max(pointOne.x, pointTwo.x), Math.max(pointOne.y, pointTwo.y)));
        Vector2D min = hash(new Vector2D(Math.min(pointOne.x, pointTwo.x), Math.min(pointOne.y, pointTwo.y)));
        
        ArrayList<T> objects = new ArrayList<>();     
        for (int x = min.x; x <= max.x; x++){
            for (int y = min.y; y <= max.y; y++){
                objects.addAll(getObjectsAtHash(Vector2D.of(x,y)));
            }
        }
        return objects;
    }
}
