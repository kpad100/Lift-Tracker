package com.example.lifttracker;

import java.io.IOException;

import java.util.Arrays;

public class analyze {



    private double[] positionX;
    private double[] time;
    private double[] velocity;
    private double[] acceleration;

    public analyze(double[] positionX, double[] positionY) {
        this.positionX = positionX;
        time = positionY;

        makevelocity();
        makeaccel();
    }

    private void makevelocity(){
        velocity= new double[positionX.length];

        // Calculate the velocity for each time step
        for (int i = 1; i < positionX.length; i++) {
            velocity[i] = (positionX[i] - positionX[i-1]) / (time[i] - time[i-1]);
        }
    }


    public double[] getvelocity() {

        return velocity;
    }

    private void makeaccel(){
        acceleration= new double[velocity.length];

        // Calculate the acceleration for each time step
        for (int i = 1; i < velocity.length; i++) {
            acceleration[i] = (velocity[i] - velocity[i-1]) / (time[i] - time[i-1]);
        }
    }

    public double[] getaccel() {
        return acceleration;
    }

    public static void main(String args[]) throws IOException {
        double[] xValues = {1.2, 2.4, 3.1, 4.5, 5.7, 6.3, 7.1, 8.2, 9.0, 10.2};
        double[] yValues = {4.5, 5.7, 6.3, 7.1, 8.2, 9.0, 10.2, 11.3, 12.1, 13.4};
        analyze analyzer = new analyze(xValues, yValues);
        System.out.println(Arrays.toString(analyzer.getvelocity()));
        System.out.println(Arrays.toString(analyzer.getaccel()));

    }


}