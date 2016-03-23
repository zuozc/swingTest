package com.zzc.model;

import java.util.ArrayList;

/**
 * Created by zuozc on 3/20/16.
 */
public class ErrorMaker {
    private int id;
    private String name;

    public ErrorMaker(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
