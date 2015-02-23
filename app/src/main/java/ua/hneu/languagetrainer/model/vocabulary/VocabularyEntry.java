package ua.hneu.languagetrainer.model.vocabulary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.App.Languages;
import ua.hneu.languagetrainer.model.EntryAbstr;

public class VocabularyEntry extends EntryAbstr implements Parcelable{

	private int id;
	private String kanji;
	private int level;
	private String lastview;
	private int shownTimes;
	private double learnedPercentage;
	private VocabularyMeaning meaningEng;
	private VocabularyMeaning meaningRus;
	private String color;

	public VocabularyEntry(int id, String kanji, int level,
			String transcription, String romaji, List<String> translations,
			List<String> translationsRus, String translationsStr,String translationsStrRus,
            double percentage, String lastview,
			int showntimes, String color) {

		VocabularyMeaning meaning = new VocabularyMeaning(transcription,
				romaji, translations, translationsStr);
		VocabularyMeaning meaningRus = new VocabularyMeaning(transcription,
				romaji, translationsRus, translationsStrRus);
		this.id = id;
		this.kanji = kanji;
		this.level = level;
		this.lastview = lastview;
		this.shownTimes = showntimes;
		this.learnedPercentage = percentage;
		this.meaningEng = meaning;
		this.meaningRus = meaningRus;
		this.color = color;
	}

	public int getId() {
		return id;
	}

	public String getKanji() {
		return kanji;
	}

	public int getLevel() {
		return level;
	}

	public String getLastview() {
		return lastview;
	}

	public int getShownTimes() {
		return shownTimes;
	}

	public VocabularyMeaning getMeaningEng() {
		return meaningEng;
	}

	public String getColor() {
		return this.color;
	}

	public int getIntColor() {
		String[] rgb = this.color.split(",");
		int color = Color.rgb(Integer.parseInt(rgb[0]),
				Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
		return color;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setKanji(String kanji) {
		this.kanji = kanji;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	// sets to current time
	public void setLastView() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SS");
		String now = dateFormat.format(new Date());
		this.lastview = now;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void incrementShowntimes() {
		this.shownTimes++;
	}

	public void setMeaning(VocabularyMeaning meaning) {
		this.meaningEng = meaning;
	}

	public VocabularyMeaning getMeanings() {
		return meaningEng;
	}

	public List<String> getTranslationsEng() {
		return this.meaningEng.translations;
	}

	public List<String> getTranslationsRus() {
		return this.meaningRus.translations;
	}

	public String translationsEngToString() {
		return this.meaningEng.translationsToString();
	}

	public String translationsRusToString() {
		return this.meaningRus.translationsToString();
	}

    @Override
	public String translationsToString() {
		if (App.lang == Languages.RUS)
			return this.meaningRus.translationsToString();
		return this.meaningEng.translationsToString();
	}

	public String getTranscription() {
        return this.meaningEng.hiragana;
	}

	public String getRomaji() {
		return this.meaningEng.romaji;
	}

	public void setMeanings(VocabularyMeaning meaning) {
		this.meaningEng = meaning;
	}

	public void setTranslations(List<String> translations) {
		this.meaningEng.translations = translations;
	}

	public void setTranslationsRus(List<String> translations) {
		this.meaningRus.translations = translations;
	}

	public void setTranscription(String transcription) {
		this.meaningEng.hiragana = transcription;
	}

	public void setRomaji(String romaji) {
		this.meaningEng.romaji = romaji;
	}

	public double getLearnedPercentage() {
		return learnedPercentage;
	}

	public void setLearnedPercentage(double learnedPercentage) {
		this.learnedPercentage = learnedPercentage;
	}

	public String readingsToString() {
		if (App.isShowRomaji)
			return this.getTranscription() + " - " + this.getRomaji();
		else
			return this.getTranscription();
	}

    public String getKanjiOrHiragana(){
        return ((!kanji.isEmpty())? kanji: meaningEng.hiragana);
    }

    @Override
    public String toString(){
        return getTranscription();
    }


    /* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(kanji);
        out.writeInt(level);
        out.writeString(lastview);
        out.writeInt(shownTimes);
        out.writeDouble(learnedPercentage);

        out.writeString(meaningEng.hiragana);
        out.writeString(meaningEng.romaji);
        out.writeString(meaningEng.translationsToString());

        out.writeString(meaningRus.hiragana);
        out.writeString(meaningRus.romaji);
        out.writeString(meaningRus.translationsToString());

        out.writeString(color);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<VocabularyEntry> CREATOR = new Parcelable.Creator<VocabularyEntry>() {
        public VocabularyEntry createFromParcel(Parcel in) {
            return new VocabularyEntry(in);
        }

        public VocabularyEntry[] newArray(int size) {
            return new VocabularyEntry[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private VocabularyEntry(Parcel in) {
        id = in.readInt();
        kanji = in.readString();
        level = in.readInt();
        lastview = in.readString();
        shownTimes = in.readInt();
        learnedPercentage = in.readDouble();
        meaningEng=new VocabularyMeaning();
        meaningEng.hiragana = in.readString();
        meaningEng.romaji = in.readString();

        meaningEng.translationsToString =  in.readString();

        meaningRus=new VocabularyMeaning();
        meaningRus.hiragana = in.readString();
        meaningRus.romaji = in.readString();

        /*List<String> translations1 = new ArrayList<>();
        in.readList(translations,List.class.getClassLoader());
        meaningRus.translations = translations1;*/
        meaningRus.translationsToString =  in.readString();

        color = in.readString();

        Log.d("Parcel ", kanji+" "+
                " "+level+" "+meaningEng+" "+meaningRus+" "+lastview+" "
                +learnedPercentage+" "+shownTimes+" ");
    }

    @Override
    public int compareTo(Object another) {
        //Log.d("compareTo", this.getTranscription() + " - " + ((VocabularyEntry) another).getTranscription());
        if(this.toString().startsWith("'")) return -1;
        return this.toString().compareTo(another.toString());
    }

    public List<String> getTranslations() {
        if(this.getRomaji().equals("akudoi"))
    {
        int t=0;
    }
        if (App.lang == Languages.RUS)
            return this.meaningRus.translations;
        return this.meaningEng.translations;
    }
}
