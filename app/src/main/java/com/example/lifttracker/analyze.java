package com.example.lifttracker;

import java.io.IOException;

import java.util.Arrays;

import android.graphics.RectF;

import java.util.ArrayList;

public class analyze {


    private ArrayList<RectF> input;
    private ArrayList<Double> time;


    //ratio of real-distance/pixel-distance
    private double distancepixelratio;

    private double platewidth;
    private float[] midpointsX;
    private float[] midpointsY;
    private double[] positionX;

    private double[] positionY;


    private double[] velocityX;
    private double[] velocityY;


    private double[] accelerationX;
    private double[] accelerationY;

    public analyze(ArrayList<RectF> input, ArrayList<Double> time, double platewidth) {
        this.input = input;
        this.time = time;
        this.platewidth = platewidth;


        makeratio();
        getmidpointsX();
        getmidpointsY();
        makepositionX();
        makepositionY();
        makevelocityX();
        makevelocityY();
        makeaccelerationX();
        makeaccelerationY();

//        makevelocity();
//        makeaccel();
    }


    private void makeratio(){
        //get average plate height in pixels
        double avgplatewidth = 0;
        for (int i = 0; i < input.size(); i++) {
            RectF rect = input.get(i);
            avgplatewidth = avgplatewidth + (rect.right - rect.left);
        }

        avgplatewidth = avgplatewidth/input.size();



        //make distance to pixel ratio
        distancepixelratio = platewidth/avgplatewidth;
    }


    private void getmidpointsX(){
        midpointsX = new float[input.size()];
        for (int i = 0; i < input.size(); i++) {
            RectF rect = input.get(i);
            float midpoint = rect.left + (rect.right - rect.left) / 2;
            midpointsX[i] = midpoint;
        }
    }

    private void getmidpointsY(){
        midpointsY = new float[input.size()];
        for (int i = 0; i < input.size(); i++) {
            RectF rect = input.get(i);
            float midpoint = rect.left + (rect.right - rect.left) / 2;
            midpointsY[i] = midpoint;
        }
    }

    private void makepositionX(){

        for (int i = 0; i < midpointsX.length; i++) {
            positionX[i] = midpointsX[i]*distancepixelratio;
        }
    }

    private void makepositionY(){

        for (int i = 0; i < midpointsY.length; i++) {
            positionY[i] = midpointsY[i]*distancepixelratio;
        }
    }






    private void makevelocityX(){
        velocityX= new double[positionX.length];

        // Calculate the velocity for each time step
        for (int i = 1; i < positionX.length; i++) {
            velocityX[i] = (positionX[i] - positionX[i-1]) / (time.get(i) - time.get(i - 1));
        }
        velocityX[0] = 0.0;
    }

    private void makevelocityY(){
        velocityY= new double[positionY.length];

        // Calculate the velocity for each time step
        for (int i = 1; i < positionY.length; i++) {
            velocityY[i] = (positionY[i] - positionY[i-1]) / (time.get(i) - time.get(i - 1));
        }
        velocityY[0] = 0.0;
    }

    private void makeaccelerationX(){
        accelerationX= new double[velocityX.length];

        // Calculate the velocity for each time step
        for (int i = 1; i < velocityX.length; i++) {
            accelerationX[i] = (velocityX[i] - velocityX[i-1]) / (time.get(i) - time.get(i - 1));
        }
        accelerationX[0] = 0.0;
    }

    private void makeaccelerationY(){
        accelerationY= new double[velocityY.length];

        // Calculate the velocity for each time step
        for (int i = 1; i < velocityY.length; i++) {
            accelerationY[i] = (velocityY[i] - velocityY[i-1]) / (time.get(i) - time.get(i - 1));
        }
        accelerationY[0] = 0.0;
    }

    public double[] getPositionX(){
        return positionX;
    }

    public double[] getPositionY(){
        return positionY;
    }

    public double[] getVelocityX(){
        return velocityX;
    }

    public double[] getVelocityY(){
        return velocityY;
    }

    public double[] getAccelerationX(){
        return accelerationX;
    }

    public double[] getAccelerationY(){
        return accelerationY;
    }



    public static void main(String args[]) throws IOException {
        // create a new ArrayList of RectFs for the first set of test data
        ArrayList<RectF> input = new ArrayList<RectF>();
        input.add(new RectF(50, 50, 100, 100));
        input.add(new RectF(100, 100, 150, 150));
        input.add(new RectF(150, 150, 200, 200));


        //test time data
        ArrayList<Double> time = new ArrayList<Double>();

        time.add(1.0);
        time.add(2.0);
        time.add(3.0);



        analyze analyzer = new analyze(input, time, 0.4508);



    }


}