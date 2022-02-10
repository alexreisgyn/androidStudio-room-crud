package com.example.roomdb.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "loli")
public class Loli implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String loliName;
    private String loliAge;
    private String imgUrl;

    public Loli() {
    }

    public Loli(String loliName, String loliAge, String imgUrl) {
        this.loliName = loliName;
        this.loliAge = loliAge;
        this.imgUrl = imgUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLoliName() {
        return loliName;
    }

    public void setLoliName(String loliName) {
        this.loliName = loliName;
    }

    public String getLoliAge() {
        return loliAge;
    }

    public void setLoliAge(String loliAge) {
        this.loliAge = loliAge;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
