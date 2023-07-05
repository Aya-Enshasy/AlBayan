package com.ayaenshasy.AlBayan.model.quran;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class Verse implements Serializable {
    @PrimaryKey()
    @SerializedName("id")
    @Expose
    private Integer id;

    @ColumnInfo(name = "text")
    @SerializedName("text")
    @Expose
    private String text;

    @ColumnInfo(name = "transliteration")
    @SerializedName("transliteration")
    @Expose
    private String transliteration;
     private float fontSize;
    private final static long serialVersionUID = 1230110422238811668L;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTransliteration() {
        return transliteration;
    }

    public void setTransliteration(String transliteration) {
        this.transliteration = transliteration;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
