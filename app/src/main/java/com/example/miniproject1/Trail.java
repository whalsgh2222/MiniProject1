package com.example.miniproject1;

import com.skt.Tmap.TMapPoint;

import java.util.ArrayList;

public class Trail {
    public String name = "새 경로";
    public ArrayList<TMapPoint> coorList = new ArrayList<TMapPoint>();

    public Double totalDistance = 0.0;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTotalDistance(Double totalDistance) {
        //거리계산
    }
}
