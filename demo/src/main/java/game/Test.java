package game;

import java.util.ArrayList;

public class Test {
    public static final int fieldSizeX = 10000;
    public static final int fieldSizeY = 10000;

    public static final int sideBuffer = 100;
    //GameElements are defined such that [0] = x coord, [1] = y coord, [2] = orientation, [3] = species, [4][5][6] rgb inner, [7][8][9] rgb outer, [10] size, [11] shape, [12+] genome...
    public static final ArrayList<int[]> gameElements = new ArrayList<>();
    static{
        for (int i = 0; i < 10000; i++){
            gameElements.add(new int[]{randInt(-1000, 6000), randInt(-1000,6000), randInt(0, 360), randInt(0, 5), randInt(0, 255), randInt(0, 255), randInt(0, 255), randInt(0, 255), randInt(0, 255), randInt(0, 255), randInt(10, 50), randInt(0, 1)});
        }
    }

    private static int randInt(int lower, int upper){
        return (int)(Math.random() * (upper - lower + 1) + lower);
    }

    public static ArrayList<int[]> getViewableGameElements(int windowZeroX, int windowZeroY, int windowWidth, int windowHeight){
        System.out.println(windowWidth+"  "+windowHeight);
        ArrayList<int[]> seenElements = new ArrayList<>();
        for (int[] element : gameElements){
            int[] transformElement = element.clone();
            transformElement[0]-= windowZeroX;
            transformElement[1]-= windowZeroY;
            if ( Math.abs(transformElement[0]) < windowWidth + sideBuffer 
                && Math.abs(transformElement[1]) < windowHeight + sideBuffer)
            {
                seenElements.add(transformElement);
            }
        }
        System.out.println(seenElements.size());
        return seenElements;
    }

}
