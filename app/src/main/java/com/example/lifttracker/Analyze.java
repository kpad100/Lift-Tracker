package com.example.lifttracker;

import android.graphics.RectF;
import java.util.ArrayList;

public class Analyze {
    private ArrayList<RectF> input;
    private ArrayList<Float> time;
    private double plateWidth;

    private double distancePixelRatio; //ratio of real-distance/pixel-distance
    private double[] positionX;
    private double[] positionY;
    private float[] velocityArr;
    private float[] accelArr;

    public Analyze(ArrayList<RectF> input, ArrayList<Float> time, double plateWidth) {
        this.input = input;
        this.time = time;
        this.plateWidth = plateWidth;

        velocityArr = new float[input.size()];
        accelArr = new float[input.size()];

        calculateRatio();
        calculatePosX();
        calculatePosY();

        calculateVelocity();
        calculateAccel();
    }

    private void calculateRatio() {
        //get average plate height in pixels
        double avgplatewidth = 0;
        for (int i = 0; i < input.size(); i++) {
            RectF rect = input.get(i);
            avgplatewidth = avgplatewidth + (rect.right - rect.left);
        }

        avgplatewidth = avgplatewidth / input.size();

        //make distance to pixel ratio
        distancePixelRatio = plateWidth / avgplatewidth;
    }

    private void calculatePosX() {
        positionX = new double[input.size()];
        for (int i = 0; i < input.size(); i++) {
            RectF rect = input.get(i);
            float midpointX = rect.centerX();
            positionX[i] = midpointX * distancePixelRatio;
        }
    }

    private void calculatePosY() {
        positionY = new double[input.size()];
        for (int i = 0; i < input.size(); i++) {
            RectF rect = input.get(i);
            float midpointY = rect.centerY();
            positionY[i] = midpointY * distancePixelRatio;
        }
    }

    private void calculateVelocity() {
        velocityArr[0] = 0;
        for(int i = 1; i < time.size(); i++) {
            double distDiff = positionY[i-1] - positionY[i];
            float timeDiff = time.get(i) - time.get(i-1);
            float velocityDiff = (float) (distDiff / timeDiff);
            velocityArr[i] = velocityArr[i-1] + velocityDiff;
        }
    }

    private void calculateAccel() {
        accelArr[0] = (float) 0;
        for(int i = 1; i < time.size(); i++) {
            float velocityDiff = velocityArr[i] - velocityArr[i-1];
            float timeDiff = time.get(i) - time.get(i-1);
            float accelDiff = velocityDiff / timeDiff;
            accelArr[i] = accelArr[i-1] + accelDiff;
        }
    }

    public float[] getVelocityArr() {
        return velocityArr;
    }

    public float[] getAccelArr() {
        return accelArr;
    }
}