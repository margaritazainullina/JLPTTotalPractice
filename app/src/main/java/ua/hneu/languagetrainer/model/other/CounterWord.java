package ua.hneu.languagetrainer.model.other;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;

import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.App.Languages;
import ua.hneu.languagetrainer.model.DictionaryAbstr;
import ua.hneu.languagetrainer.model.EntryAbstr;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class CounterWord extends EntryAbstr implements Parcelable {
    private String sectionEng;
    private String sectionRus;
    private String word;
    private String hiragana;
    private String romaji;
    private String translationEng;
    private String translationRus;
    private double learnedPercentage;
    private int shownTimes;
    private String lastview;
    private String color;

    public CounterWord(String sectionEng, String sectionRus, String word,
                       String hiragana, String romaji, String translationEng,
                       String translationRus, double learnedPercentage, int shownTimes,
                       String lastview, String color) {
        super();
        this.sectionEng = sectionEng;
        this.sectionRus = sectionRus;
        this.word = word;
        this.hiragana = hiragana;
        this.romaji = romaji;
        this.translationEng = translationEng;
        this.translationRus = translationRus;
        this.learnedPercentage = learnedPercentage;
        this.shownTimes = shownTimes;
        this.lastview = lastview;
        this.color = color;
    }

    public String getSectionEng() {
        return sectionEng;
    }

    public String getSectionRus() {
        return sectionRus;
    }

    public String getWord() {
        return word;
    }

    public String getHiragana() {
        return hiragana;
    }

    public String getRomaji() {
        return romaji;
    }

    public String getTranslationEng() {
        return translationEng;
    }

    public String getTranslationRus() {
        return translationRus;
    }

    public double getLearnedPercentage() {
        return learnedPercentage;
    }

    public int getShownTimes() {
        return shownTimes;
    }

    public String getLastview() {
        return lastview;
    }

    public String getColor() {
        return color;
    }

    public void setSectionEng(String sectionEng) {
        this.sectionEng = sectionEng;
    }

    public void setSectionRus(String sectionRus) {
        this.sectionRus = sectionRus;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setHiragana(String hiragana) {
        this.hiragana = hiragana;
    }

    public void setRomaji(String romaji) {
        this.romaji = romaji;
    }

    public void setTranslationEng(String translationEng) {
        this.translationEng = translationEng;
    }

    public void setTranslationRus(String translationRus) {
        this.translationRus = translationRus;
    }

    public void setLearnedPercentage(double learnedPercentage) {
        this.learnedPercentage = learnedPercentage;
    }

    public void setShownTimes(int shownTimes) {
        this.shownTimes = shownTimes;
    }

    public void setLastview(String lastview) {
        this.lastview = lastview;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String translationsToString(){
        if (App.lang == Languages.ENG)
            return translationEng;
        else
            return translationRus;
    }

    @Override
    public String toString(){
        return word;
    }

    public int getIntColor() {
        String[] rgb = this.color.split(",");
        int color = Color.rgb(Integer.parseInt(rgb[0]),
                Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
        return color;
    }

    public void incrementShowntimes() {
        shownTimes++;

    }

    // sets current time
    public void setLastView() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss.SS");
        String now = dateFormat.format(new Date());
        this.lastview = now;
    }

    public String getTranscription() {
        if (App.isShowRomaji)
            return hiragana + " " + romaji;
        else
            return hiragana;
    }

    public ArrayList<String> getSplittedWord() {
        ArrayList<String> d = new ArrayList<String>();
            String[] s = word.split("(;|,|/)");
            for(String ss : s) {
                /*ss=ss.replaceAll("\\d+[\\)|\\.]","");
                ss=ss.trim();
                ss=ss.replaceAll("\\d+[\\)|\\.]","");
                ss=ss.replaceAll("(\\[|\\(|\\]|\\))","");
                ss=ss.replaceAll("[?.!。]+$","");*/
                ss=ss.toLowerCase();
                ss=ss.trim();
                d.add(ss);
            }
        return d;
        }

    public ArrayList<String> getSplittedDescriptions() {
        ArrayList<String> d = new ArrayList<String>();
        if (App.lang == Languages.ENG){
            String[] s = translationEng.split("[;,]");
            for(String ss : s) {
                ss=ss.toLowerCase();
                ss=ss.replaceAll("\\((.*?)\\)","");
                ss=ss.replaceAll("\\[(.*?)\\]","");
                ss=ss.trim();
                ss=ss.replaceAll("[?.!。]+$","");
                ss=ss.trim();
                if(!ss.isEmpty()) d.add(ss);
            }
        }
        else{
            String[] s = translationRus.split(".|;|,|/");
            for(String ss : s) {
                ss=ss.replaceAll("\\((.*?)\\)","");
                ss=ss.trim();
                if(!ss.isEmpty()) d.add(ss);
            }
        }
        return d;
    }

    /* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(sectionEng);
        out.writeString(sectionRus);
        out.writeString(word);
        out.writeString(hiragana);
        out.writeString(romaji);
        out.writeString(translationEng);
        out.writeString(translationRus);
        out.writeDouble(learnedPercentage);
        out.writeInt(shownTimes);
        out.writeString(lastview);
        out.writeString(color);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<CounterWord> CREATOR = new Parcelable.Creator<CounterWord>() {
        public CounterWord createFromParcel(Parcel in) {
            return new CounterWord(in);
        }

        public CounterWord[] newArray(int size) {
            return new CounterWord[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private CounterWord(Parcel in) {
        id = in.readInt();
        sectionEng= in.readString();
        sectionRus= in.readString();
        word= in.readString();
        hiragana= in.readString();
        romaji= in.readString();
        translationEng= in.readString();
        translationRus= in.readString();
        learnedPercentage= in.readDouble();
        shownTimes= in.readInt();
        lastview= in.readString();
        color= in.readString();
    }

    @Override
    public int compareTo(Object another) {
        return this.toString().compareTo(another.toString());
    }

    public  String getTranslation() {
        if (App.lang == Languages.RUS)
            return this.translationRus;
        return this.translationEng;
    }
}
