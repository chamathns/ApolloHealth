package com.example.apollohealth;

public class MetricGenerator {
    private final float WALKING_MET = 3f;
    private final float ASCENDING_MET = 8.5f;
    private final float DESCENDING_MET = 3.5f;

    private final float WALKING_SPEED = 5;
    private final float WALKING_SPEED_UP = 1.8f;
    private final float WALKING_SPEED_DOWN = 2.5f;

    private final float STEPS_PER_FLIGHT = 15;

    private float height;
    private float weight;

    public MetricGenerator(float height, float weight) {

        this.height = height;
        this.weight = weight;
    }

    public float stepsToKm(float steps) {
        float kms = steps * 0.74f * 0.001f;

        return kms;
    }

    public float caloriesBurned(int steps, int flights) {
        float meanFlights = (float) flights / 2;

        float cbWalking = ((WALKING_MET * weight * 3.5f) / 200) * (stepsToKm(steps) / WALKING_SPEED) * 60;

        float cbAscending = ((ASCENDING_MET * weight * 3.5f) / 200) * (stepsToKm(meanFlights * STEPS_PER_FLIGHT) / WALKING_SPEED_UP) * 60;

        float cbDescending = ((DESCENDING_MET * weight * 3.5f) / 200) * (stepsToKm(meanFlights * STEPS_PER_FLIGHT) / WALKING_SPEED_DOWN) * 60;

        float cbTotal = cbWalking + cbAscending + cbDescending;

        return cbTotal;
    }

    public float calculateBMI(float height, float weight){
        float heightInMeters = height/100;

        float bmi = weight/(heightInMeters * heightInMeters);

        return bmi;
    }
}
