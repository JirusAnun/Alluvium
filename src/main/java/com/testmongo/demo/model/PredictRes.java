package com.testmongo.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;

@Data
public class PredictRes implements Comparable<PredictRes>{
    double score;
    double[] advantage;
    String topLeft;
    String btmRight;
    ArrayList<ArrayList<Location>> nearLocations;
    public PredictRes(double score, double[] advantage, String topLeft, String btmRight, ArrayList<ArrayList<Location>> nearLocations) {
        this.score = score;
        this.advantage = advantage;
        this.topLeft = topLeft;
        this.btmRight = btmRight;
        this.nearLocations = nearLocations;
    }

    @Override
    public int compareTo(PredictRes other) {
        if (this.score > other.score) return -1;
        else if (this.score < other.score) return 1;
        return 0;
    }
}
