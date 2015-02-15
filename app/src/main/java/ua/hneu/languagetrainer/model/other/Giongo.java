package ua.hneu.languagetrainer.model.other;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.App.Languages;
import ua.hneu.languagetrainer.model.DictionaryAbstr;
import ua.hneu.languagetrainer.model.EntryAbstr;
import ua.hneu.languagetrainer.model.ExampleAbstr;
import ua.hneu.languagetrainer.model.ExampleInterface;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Giongo extends EntryAbstr implements ExampleInterface, Parcelable {
    private String word;
    private String romaji;
    private String translEng;
    private String translRus;
    private double learnedPercentage;
    private int shownTimes;
    private String lastview;
    private String color;
    public ArrayList<GiongoExample> examples = new ArrayList<GiongoExample>();

    public Giongo(String word, String romaji, String translEng,
                  String translRus, double learnedPercentage, int shownTimes,
                  String lastview, String color, ArrayList<GiongoExample> examples) {
        super();
        this.word = word;
        this.romaji = romaji;
        this.translEng = translEng;
        this.translRus = translRus;
        this.learnedPercentage = learnedPercentage;
        this.shownTimes = shownTimes;
        this.lastview = lastview;
        this.color = color;
        this.examples = examples;
    }

    @Override
    public ArrayList<String> getAllExamplesText() {
        ArrayList<String> text = new ArrayList<String>();
        for (GiongoExample ge : examples) {
            text.add(ge.getText().trim());
        }
        return text;
    }

    public ArrayList<String> getAllExamplesRomaji() {
        ArrayList<String> text = new ArrayList<String>();
        for (GiongoExample ge : examples) {
            text.add(ge.getRomaji().trim());
        }
        return text;
    }

    public ArrayList<String> getAllTranslations() {
        ArrayList<String> text = new ArrayList<String>();
        boolean isEng = App.lang == Languages.ENG;
        for (GiongoExample ge : examples) {
            if (isEng)
                text.add(ge.getTranslationEng().trim());
            else
                text.add(ge.getTranslationRus().trim());
        }
        return text;
    }

    public String getLastview() {
        return lastview;
    }

    public int getShownTimes() {
        return shownTimes;
    }

    public double getLearnedPercentage() {
        return learnedPercentage;
    }

    public void setLastview(String lastview) {
        this.lastview = lastview;
    }

    public void setShownTimes(int shownTimes) {
        this.shownTimes = shownTimes;
    }

    public void setLearnedPercentage(double learnedPercentage) {
        this.learnedPercentage = learnedPercentage;
    }

    public Giongo() {
        examples = new ArrayList<GiongoExample>();
    }

    public String getWord() {
        return word;
    }

    public String getRomaji() {
        return romaji;
    }

    public String getTranslEng() {
        return translEng;
    }

    public String getTranslRus() {
        return translRus;
    }

    public String getColor() {
        return color;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setRomaji(String romaji) {
        this.romaji = romaji;
    }

    public void setTranslEng(String translEng) {
        this.translEng = translEng;
    }

    public void setTranslRus(String translRus) {
        this.translRus = translRus;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ArrayList<GiongoExample> getExamples() {
        return examples;
    }

    public void setExamples(ArrayList<GiongoExample> examples) {
        this.examples = examples;
    }

    public String getRule() {
        return word;
    }

    public void setRule(String rule) {
        this.word = rule;
    }

    public void setDescEng(String descEng) {
        this.translEng = descEng;
    }

    public void setDescRus(String descRus) {
        this.translRus = descRus;
    }

    @Override
    public String translationsToString(){
        if (App.lang == Languages.ENG)
            return translEng;
        else
            return translRus;
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

    @Override
    public String toString(){
        return word;
    }


    /* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(word);
        out.writeString(romaji);
        out.writeString(translEng);
        out.writeString(translRus);
        out.writeDouble(learnedPercentage);
        out.writeInt(shownTimes);
        out.writeString(lastview);
        out.writeString(color);
        out.writeList(examples);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Giongo> CREATOR = new Parcelable.Creator<Giongo>() {
        public Giongo createFromParcel(Parcel in) {
            return new Giongo(in);
        }

        public Giongo[] newArray(int size) {
            return new Giongo[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Giongo(Parcel in) {

        id = in.readInt();
        word= in.readString();
        romaji= in.readString();
        translEng= in.readString();
        translRus= in.readString();
        learnedPercentage= in.readDouble();
        shownTimes= in.readInt();
        lastview= in.readString();
        color= in.readString();
        color= in.readString();
        ArrayList<Giongo> examples = new ArrayList<Giongo>();
        in.readList(examples, ArrayList.class.getClassLoader());
    }
}
