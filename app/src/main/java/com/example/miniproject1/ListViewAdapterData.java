package com.example.miniproject1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapterData {
    private Double totalDistance;
    private String name;
    private String startAddr;
    private String endAddr;
    private String jsonList;

    public String getName() {
        return name;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public String getEndAddr() {
        return endAddr;
    }

    public String getJsonList() {
        return jsonList;
    }

    public String getStartAddr() {
        return startAddr;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEndAddr(String endAddr) {
        this.endAddr = endAddr;
    }

    public void setJsonList(String jsonList) {
        this.jsonList = jsonList;
    }

    public void setStartAddr(String startAddr) {
        this.startAddr = startAddr;
    }
}
