package game;

import java.util.ArrayList;

public class Test {
    int fieldSizeX = 10000;
    int fieldSizeY = 10000;
    //GameElements are defined such that [0] = x coord, [1] = y coord, [2] = orientation, [3] = species
    public static ArrayList<int[]> gameElements = new ArrayList<>();
    static{
        gameElements.add(new int []{1000,1000,100,0});
    }

    public static ArrayList<int[]> getViewableGameElements(int windowCenterX, int windowCenterY, int windowWidth, int windowHeight){
        ArrayList<int[]> seenElements = new ArrayList<>();
        for (int[] element : gameElements){
            if (
                (element[0] - element[2]/2 < windowCenterX + windowWidth/2
                || element[0] + element[2]/2 > windowCenterX - windowWidth/2)
                &&
                (element[1] - element[2]/2 < windowCenterY + windowHeight/2
                || element[1] + element[2]/2 > windowCenterY - windowHeight/2)
            ){
                seenElements.add(element);
            }
        }
        return seenElements;
    }

}
