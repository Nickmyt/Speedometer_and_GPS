package com.example.first_exersice;

public class UserIncidentData {
    public double x,y;
    public String id,timestamp;
    public int speedBefore ,speed;

    public UserIncidentData(){
        //Default constructor
    }
    public UserIncidentData(double x , double y , String id , String timestamp, int speedBefore, int speed){
        this.x = x;
        this.y = y;
        this.id = id;
        this.timestamp = timestamp;
        this.speedBefore = speedBefore;
        this.speed = speed;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }

    public int getSpeedBefore() {
        return speedBefore;
    }

    public String getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
