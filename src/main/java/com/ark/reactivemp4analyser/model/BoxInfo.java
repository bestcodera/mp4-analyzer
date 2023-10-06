package com.ark.reactivemp4analyser.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BoxInfo {
    private long size;
    private String type;
    private List<BoxInfo> subBoxes;

    public BoxInfo(long size, String type) {
        this.size = size;
        this.type = type;
        this.subBoxes = new ArrayList<>();
    }
}
