package ua.hneu.languagetrainer.model;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Margarita on 2015/02/13.
 */
public class ExampleAbstr {

    private String text;
    private String romaji;
    private String translationEng;
    private String translationRus;

    public String getText() {
        return text;
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

    public void setText(String text) {
        this.text = text;
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

    public String getPart1() {
        String[] parts = text.split("\\\\t");
        return parts[0];
    }

    public String getPart2() {
        String[] parts = text.split("\\\\t");
        return parts[1];
    }

    public String getPart3() {
        String[] parts = text.split("\\\\t");
        return parts[2];
    }

}
