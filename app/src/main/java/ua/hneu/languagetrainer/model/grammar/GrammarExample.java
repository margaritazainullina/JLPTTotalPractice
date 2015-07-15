package ua.hneu.languagetrainer.model.grammar;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import ua.hneu.languagetrainer.model.ExampleAbstr;

public class GrammarExample extends ExampleAbstr implements Parcelable, Serializable {

    private String text;
    private String romaji;
    private String translationEng;
    private String translationRus;

	public GrammarExample(String text, String romaji, String translationEng,
			String translationRus) {
		//super();
		/*super.setText(text);
        super.setRomaji(romaji);
        super.setTranslationEng(translationEng);
        super.setTranslationRus(translationRus);*/
        this.text=text;
        this.romaji=romaji;
        this.translationEng=translationEng;
        this.translationRus=translationRus;
	}

    @Override
      public int describeContents() {
        return 0;
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(romaji);
        dest.writeString(translationEng);
        dest.writeString(translationRus);
    }

    private GrammarExample(Parcel in) {
        text = in.readString();
        romaji = in.readString();
        translationEng = in.readString();
        translationRus = in.readString();
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<GrammarExample> CREATOR = new Parcelable.Creator<GrammarExample>() {
        public GrammarExample createFromParcel(Parcel in) {
            return new GrammarExample(in);
        }

        public GrammarExample[] newArray(int size) {
            return new GrammarExample[size];
        }
    };
}
