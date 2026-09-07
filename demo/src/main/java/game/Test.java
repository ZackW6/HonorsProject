package game;

import java.util.ArrayList;

public class Test {
    public static final int fieldSizeX = 10000;
    public static final int fieldSizeY = 10000;

    public static final int sideBuffer = 100;
    //GameElements are defined such that [0] = x coord, [1] = y coord, [2] = orientation, [3] = species
    public static final ArrayList<int[]> gameElements = new ArrayList<>();
    static{
        gameElements.add(new int []{1500,1000,100,0});
        gameElements.add(new int []{1500,500,100,0});
    }

    public static ArrayList<int[]> getViewableGameElements(int windowZeroX, int windowZeroY, int windowWidth, int windowHeight){
        ArrayList<int[]> seenElements = new ArrayList<>();
        for (int[] element : gameElements){
            int[] transformElement = element.clone();
            transformElement[0]-= windowZeroX;
            transformElement[1]-= windowZeroY;
            if (transformElement[0] > -sideBuffer && transformElement[0] < windowWidth + sideBuffer 
                && transformElement[1] > -sideBuffer && transformElement[1] < windowHeight + sideBuffer)
            {
                seenElements.add(transformElement);
            }
        }
        return seenElements;
    }

}
