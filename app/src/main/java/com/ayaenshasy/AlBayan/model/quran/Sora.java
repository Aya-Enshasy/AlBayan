
package com.ayaenshasy.AlBayan.model.quran;

import java.io.Serializable;
import java.util.List;
 import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

 public class Sora implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("transliteration")
    @Expose
    private String transliteration;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("total_verses")
    @Expose
    private Integer totalVerses;
    @SerializedName("verses")
    @Expose
    private List<Verse> verses;
    private final static long serialVersionUID = 4239235215146391360L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransliteration() {
        return transliteration;
    }

    public void setTransliteration(String transliteration) {
        this.transliteration = transliteration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getTotalVerses() {
        return totalVerses;
    }

    public void setTotalVerses(Integer totalVerses) {
        this.totalVerses = totalVerses;
    }

    public List<Verse> getVerses() {
        return verses;
    }

    public void setVerses(List<Verse> verses) {
        this.verses = verses;
    }

}
