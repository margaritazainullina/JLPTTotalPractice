package ua.hneu.languagetrainer.model.other;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import ua.hneu.languagetrainer.model.ExampleAbstr;

public class GiongoExample extends ExampleAbstr  implements Parcelable, Serializable{
    private String text;
    private String romaji;
    private String translationEng;
    private String translationRus;

    public GiongoExample(String text, String romaji, String translationEng,
                         String translationRus) {
       /* super();
        super.setText(text);
        super.setRomaji(romaji);
        super.setTranslationEng(translationEng);
        super.setTranslationRus(translationRus);*/

        this.text=text;
        this.romaji=romaji;
        this.translationEng=translationEng;
        this.translationRus=translationRus;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(romaji);
        dest.writeString(translationEng);
        dest.writeString(translationRus);
    }

    private GiongoExample(Parcel in) {
        text = in.readString();
        romaji = in.readString();
        translationEng = in.readString();
        translationRus = in.readString();
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<GiongoExample> CREATOR = new Parcelable.Creator<GiongoExample>() {
        public GiongoExample createFromParcel(Parcel in) {
            return new GiongoExample(in);
        }

        public GiongoExample[] newArray(int size) {
            return new GiongoExample[size];
        }
    };
}
