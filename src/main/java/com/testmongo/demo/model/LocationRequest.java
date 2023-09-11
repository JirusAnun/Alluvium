package com.testmongo.demo.model;

import lombok.Getter;

@Getter
public class LocationRequest {
    // Getters and setters (required for deserialization)
    private String top;
    private String btm;

    public void setTop(String top) {
        this.top = top;
    }

    public void setBtm(String btm) {
        this.btm = btm;
    }
}
