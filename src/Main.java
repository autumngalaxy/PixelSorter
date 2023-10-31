import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static java.awt.image.BufferedImage.*;

public class Main {

    public static Color[][] imageArray;
    public static BufferedImage bufferedImage;
    public static int[][] intArray;

    static float thresholdLower = .15f; // Anything beneath this value will *not* get processed (1 is passthrough) (~.2-.3ff works well)
    static float thresholdUpper = .85f; // Anything above this value will *not* get processed (0 is passthrough) (~.8f works well)
    static boolean enableThresholds = true;
    static boolean invertThresholds = false;
    static int sortType = 1; /*
     * STANDARD SORTS: 0 = luminosity; 1 = brightness; 2 = hue; 3 = saturation; 4 = red; 5 = green; 6 = blue;
     * OTHER SORTS: 7 = cool green then blue sort (SLOW); 10 = hueMapDebug (SLOW)
     */
    static boolean sortVertical = false; // true = vertical, false = horizontal
    static boolean inverted = true; // false = up/left darker, true = down/right darker
    static int publicCounter = 0;


    /*GENERAL METHODS*/
    public static void main(String[] args) {
        try {
            /*LOADING BLOCK*/
            File imagePath = new File("src/testImage.png");
            bufferedImage = ImageIO.read(imagePath);

            loadImage(bufferedImage);
            System.out.println("Loaded image!");

            /*SORTING BLOCK*/
            if(enableThresholds){
                initIntArray();
                System.out.println("Initialized int array!");

                findSublists(intArray);

                System.out.println("Starting sort!");

                for(int i = 0; i < intArray.length; i++){
                    for(int k = 0; k < intArray[i].length; k++){
                        if (intArray[i][k] > 0) {
                            imageArray[i] = quickSort(imageArray[i], k, intArray[i][k] - 1);
                        }
                    }
                }
            }
            else {
                System.out.println("Starting sort!");
                for (int i = 0; i < imageArray.length; i++) {
                    if (sortType == 7 || sortType == 10) {
                        imageArray[i] = selectionSort(imageArray[i], imageArray[i].length);
                    } else {

                        imageArray[i] = quickSort(imageArray[i], 0, imageArray[i].length - 1);
                    }
                }
            }
            System.out.println("Sorted!");

            /*ENDING / DEBUG BLOCK*/
            // Fills in every sublist with black
            //debugBlackTest();

            // Fills in every sublist with a random color
            /*for(int i = 0; i < intArray.length; i++){
                for(int j = 0; j < intArray[i].length; j++){
                    if(intArray[i][j] > 0)
                        fillInColorsDebug(imageArray[i], j, intArray[i][j] - 1);
                }
            }*/
            printImage(imageArray);


        } catch (Exception e) {
            System.out.println("An error occurred during sorting.");
            System.out.println(e.getMessage());
            e.printStackTrace();

        }
    }

    static void loadImage(BufferedImage imageToBuffer) {

        if (sortVertical) {
            imageArray = new Color[imageToBuffer.getWidth()][imageToBuffer.getHeight()];
            for (int i = 0; i < imageArray.length; i++) {
                for (int j = 0; j < imageArray[i].length; j++) {
                    imageArray[i][j] = new Color(imageToBuffer.getRGB(i, j));
                }
            }
        } else {
            imageArray = new Color[imageToBuffer.getHeight()][imageToBuffer.getWidth()];
            for (int i = 0; i < imageArray[0].length; i++) {
                for (int j = 0; j < imageArray.length; j++) {
                    imageArray[j][i] = new Color(imageToBuffer.getRGB(i, j));
                }
            }
        }
    }

    static BufferedImage bufferTheImage(Color[][] imageToBuffer) {
        BufferedImage result;
        // Assumes imageToBuffer[] is of constant length
        if (sortVertical) {
            result = new BufferedImage(imageToBuffer.length, imageToBuffer[0].length, TYPE_INT_ARGB);
            for (int i = 0; i < imageToBuffer.length; i++) {
                for (int j = 0; j < imageToBuffer[i].length; j++) {
                    result.setRGB(i, j, imageToBuffer[i][j].getRGB());
                }
            }
        } else {
            result = new BufferedImage(imageToBuffer[0].length, imageToBuffer.length, TYPE_INT_ARGB);
            for (int i = 0; i < imageToBuffer[0].length; i++) {
                for (int j = 0; j < imageToBuffer.length; j++) {
                    result.setRGB(i, j, imageToBuffer[j][i].getRGB());
                }
            }
        }

        return result;
    }

    static void initIntArray(){
        intArray = new int[imageArray.length][imageArray[0].length];
        for(int i = 0; i < intArray.length;i++){
            for(int j = 0; j < intArray[i].length; j++){
                intArray[i][j] = 0;
            }
        }
    }
    static void findSublists(int[][] array){
        /* implement finding the start of sublists
         * The start of the sublist is index j, where intArray[i][j] = the length of the sublist
         * All parts of the sublist other than the start have a value of -1
         * All other indices are = 0 */
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (calculateThreshold(imageArray[i][j])) {

                    int tempIndex = j; //stores starting index of the sublist
                    boolean thresholdCont = true;

                    while (thresholdCont) {
                        if (j >= array[i].length - 1) {
                            thresholdCont = false;
                            //System.out.println("Got here");
                        }
                        j++;
                        if (thresholdCont && calculateThreshold(imageArray[i][j])) {
                            array[i][j] = -1;
                        } else {
                            thresholdCont = false;
                        }
                    }
                    //System.out.println("Storing index " + tempIndex + " a value of " + (j - tempIndex));

                    array[i][tempIndex] = j; //- tempIndex;
                }
            }
        }
    }

    static void printImage(Color[][] finalImage) {
        try {
            File outputFile = new File("src/final.png");
            BufferedImage finalBuffer = bufferTheImage(finalImage);
            ImageIO.write(finalBuffer, "png", outputFile);
            System.out.println("Finished printing!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    /*SORTING METHODS*/
    static Color[] selectionSort(Color[] sortArray, int endIndex) {

        //Color currentMaxValue = new Color(0,0,0);
        //int currentMaxValIndex = 0;

        for (int k = 0; k < endIndex; k++) {
            System.out.println("Starting sort: " + publicCounter);
            publicCounter++;
            int jMin = k;

            for (int j = k + 1; j < sortArray.length; j++) {
                if (calculateSortType(sortArray[j], sortArray[jMin])) {
                    jMin = j;

                }
                if(sortType == 7 && sortArray[j].getBlue() < sortArray[jMin].getBlue()){
                    jMin = j; // for the special sort; don't mind this
                }
            }
            if (jMin != k) {
                if (sortType != 10) {
                    /*currentMaxValue = sortArray[k];
                    currentMaxValIndex = k*/
                    Color tempColor = sortArray[k];
                    sortArray[k] = sortArray[jMin];
                    sortArray[jMin] = tempColor;

                } else {
                    Color tempColor = sortArray[k];
                    sortArray[k] = new Color((int) ((calculateHue(sortArray[k]) / 360) * 255), 0, 0);
                    sortArray[jMin] = tempColor;
                    //currentMaxValIndex = k;
                }
            }
        }
        /*Color tempColor = currentMaxValue;
        sortArray[currentMaxValIndex] = sortArray[endIndex];
        sortArray[endIndex] = tempColor;*/

        //sortArray = sortImplement(sortArray, endIndex-1);

        System.out.println("Ended sort: " + publicCounter);
        return sortArray;
    }
    static Color[] quickSort(Color[] sortArray, int start, int end){
        //https://en.wikipedia.org/wiki/Quicksort#Lomuto_partition_scheme
        if (start >= end || start < 0)
            return sortArray;

        int pivot = partition(sortArray, start, end);

        quickSort(sortArray, start, pivot - 1);
        quickSort(sortArray, pivot + 1, end);

        return sortArray;
    }

    static int partition(Color[] sortArray, int start, int end){
        Color pivot = sortArray[end];
        int i = start - 1;

        for(int j = start; j <= end; j++){
            if(calculateSortType(sortArray[j], pivot)){
                i++;
                arraySwap(sortArray, i, j);
            }
        }
        i++;
        arraySwap(sortArray, i, end);

        return i;
    }
    static void arraySwap(Color[] sortArray, int a, int b){
        Color temp = sortArray[a];
        sortArray[a] = sortArray[b];
        sortArray[b] = temp;
    }

    /*CALCULATION METHODS*/
    static float calculateLuminosity(Color color) {
        //System.out.println("Starting luminosity");
        float redComp = color.getRed() / 255f;
        float blueComp = color.getBlue() / 255f;
        float grnComp = color.getGreen() / 255f;
        float max = 0;
        float min = 1;

        if (redComp > max) max = redComp;
        if (blueComp > max) max = blueComp;
        if (grnComp > max) max = grnComp;

        if (max == 0f) return 0f;

        if (redComp < min) min = redComp;
        if (blueComp < min) min = blueComp;
        if (grnComp < min) min = grnComp;


        return max - min; //0-1
    }

    static float calculateBrightness(Color color) { //magnitude of the 3d vector of the components; max ~440
        return (float) Math.sqrt(Math.pow(color.getRed(), 2) + Math.pow(color.getBlue(), 2) + Math.pow(color.getGreen(), 2));
        //return (float) Math.sqrt(color.getRed() + color.getBlue() + color.getGreen());
    }

    static float calculateHue(Color color) {
        float redComp = color.getRed() / 255f;
        float blueComp = color.getBlue() / 255f;
        float grnComp = color.getGreen() / 255f;
        float max = 0;
        float min = 1;
        int maxColor = 0; //0 = n/a, 1 = red, 2 = blue, 3 = green

        if (redComp > max) {
            max = redComp;
            maxColor = 1;
        }
        if (blueComp > max) {
            max = blueComp;
            maxColor = 2;
        }
        if (grnComp > max) {
            max = grnComp;
            maxColor = 3;
        }


        if (redComp < min) {
            min = redComp;
        }
        if (blueComp < min) {
            min = blueComp;
        }
        if (grnComp < min) {
            min = grnComp;
        }

        if (maxColor == 1) {
            return 60 * (((grnComp - blueComp) % 6) / (max - min));
        }
        if (maxColor == 2) {
            return 60 * (2.0f + (blueComp - redComp) / (max - min));
        }
        if (maxColor == 3) {
            return 60 * (4.0f + (redComp - grnComp) / (max - min));
        }

        return 0f; //0-360
    }

    static float calculateSaturation(Color color) {
        float redComp = color.getRed() / 255f;
        float blueComp = color.getBlue() / 255f;
        float grnComp = color.getGreen() / 255f;
        float max = 0;
        float min = 1;


        if (redComp > max) {
            max = redComp;
        }
        if (blueComp > max) {
            max = blueComp;
        }
        if (grnComp > max) {
            max = grnComp;
        }

        //if(max == 0f) return 0f;

        if (redComp < min) {
            min = redComp;
        }
        if (blueComp < min) {
            min = blueComp;
        }
        if (grnComp < min) {
            min = grnComp;
        }
        float luminosity = max - min;
        if (luminosity < 0.5f) {
            return (max - min) / (max + min);
        } else {
            return (max - min) / (2 - max - min); //0-1
        }
    }

    static boolean calculateSortType(Color a, Color b) {
        boolean result = false;
        switch (sortType) {
            case 0:
                result = calculateLuminosity(a) <= calculateLuminosity(b);
                break;
            case 1:
                result = calculateBrightness(a) <= calculateBrightness(b);
                break;
            case 2:
            case 10:
                result = calculateHue(a) <= calculateHue(b);
                break;
            case 3:
                result = calculateSaturation(a) <= calculateSaturation(b);
                break;
            case 4:
                result = a.getRed() <= b.getRed();
                break;
            case 5:
            case 7:
                result =  a.getGreen() <= b.getGreen();
                break;
            case 6:
                result = a.getBlue() <= b.getBlue();
                break;
            default:
                result = false;
        }
        if(inverted)
            return !result;
        return result;
    }

    static boolean calculateThreshold(Color a){
        boolean result = false;

        if(invertThresholds){ //If the user has inverted the threshold values
            switch(sortType){
                case 0:
                    if(calculateLuminosity(a) < thresholdLower || calculateLuminosity(a) > thresholdUpper){
                        result = true;
                    }
                    break;
                case 1:
                    if(calculateBrightness(a) < thresholdLower * 442 || calculateBrightness(a) > thresholdUpper * 442){
                        result = true;
                    }
                    break;
                case 2:
                    if(calculateHue(a) < thresholdLower * 360 || calculateHue(a) > thresholdUpper * 360){
                        result = true;
                    }
                    break;
                case 3:
                    if(calculateSaturation(a) < thresholdLower || calculateSaturation(a) > thresholdUpper){
                        result = true;
                    }
                    break;
                case 4:
                    if(a.getRed() < thresholdLower * 255 || a.getRed() > thresholdUpper * 255){
                        result = true;
                    }
                    break;
                case 5:
                    if(a.getGreen() < thresholdLower * 255 || a.getGreen() > thresholdUpper * 255){
                        result = true;
                    }
                    break;
                case 6:
                    if(a.getBlue() < thresholdLower * 255 || a.getBlue() > thresholdUpper * 255){
                        result = true;
                    }
                    break;
            }

            return result;
        }


        switch(sortType){ //Normal operation
            case 0:
                if(calculateLuminosity(a) > thresholdLower && calculateLuminosity(a) < thresholdUpper){
                    result = true;
                }
                break;
            case 1:
                if(calculateBrightness(a) > thresholdLower * 442 && calculateBrightness(a) < thresholdUpper * 442){
                    result = true;
                }
                break;
            case 2:
                if(calculateHue(a) > thresholdLower * 360 && calculateHue(a) < thresholdUpper * 360){
                    result = true;
                }
                break;
            case 3:
                if(calculateSaturation(a) > thresholdLower && calculateSaturation(a) < thresholdUpper){
                    result = true;
                }
                break;
            case 4:
                if(a.getRed() > thresholdLower * 255 && a.getRed() < thresholdUpper * 255){
                    result = true;
                }
                break;
            case 5:
                if(a.getGreen() > thresholdLower * 255 && a.getGreen() < thresholdUpper * 255){
                    result = true;
                }
                break;
            case 6:
                if(a.getBlue() > thresholdLower * 255 && a.getBlue() < thresholdUpper * 255){
                    result = true;
                }
                break;
        }

            return result;

    }
    /*DEBUG METHODS*/
    static void debugBlackTest(){

        System.out.println("Starting debug fill-in!");

        for(int i = 0; i < intArray.length; i++){
            for(int j = 0; j < intArray[i].length; j++){
                if(intArray[i][j] == -1 || intArray[i][j] < 0){
                    imageArray[i][j] = new Color(0,125,12);
                }
                if(intArray[i][j] > 0){
                    imageArray[i][j] = new Color(125, 12, 0);
                }
            }
        }
        System.out.println("Ending debug fill-in!");

    }
    static void fillInColorsDebug(Color[] array, int start, int end){
        Color testColor = new Color((int) (Math.random() * 255),(int) (Math.random() * 255), (int) (Math.random() * 255));
        for(int j = start; j < end; j++){
            array[j] = testColor;
        }
    }
}