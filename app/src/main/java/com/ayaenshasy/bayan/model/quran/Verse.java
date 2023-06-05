package com.ayaenshasy.bayan.model.quran;

import java.io.Serializable;
 import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

 public class Verse implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("transliteration")
    @Expose
    private String transliteration;
    private final static long serialVersionUID = 1230110422238811668L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

}
