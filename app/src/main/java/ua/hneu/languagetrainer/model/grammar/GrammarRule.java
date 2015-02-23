package ua.hneu.languagetrainer.model.grammar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.App.Languages;
import ua.hneu.languagetrainer.model.DictionaryAbstr;
import ua.hneu.languagetrainer.model.EntryAbstr;
import ua.hneu.languagetrainer.model.ExampleInterface;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class GrammarRule extends EntryAbstr implements ExampleInterface, Parcelable{
    private String rule;
    private int level;
    private String descEng;
    private String descRus;
    private double learnedPercentage;
    private String lastview;
    private int shownTimes;
    private String color;
	public ArrayList<GrammarExample> examples = new ArrayList<GrammarExample>();

	public GrammarRule(String rule, int level, String descEng, String descRus,
			double learnedPercentage, String lastview, int shownTimes,
			String color, ArrayList<GrammarExample> examples) {
		super();
		this.rule = rule;
		this.level = level;
		this.descEng = descEng;
		this.descRus = descRus;
		this.learnedPercentage = learnedPercentage;
		this.lastview = lastview;
		this.shownTimes = shownTimes;
		this.color = color;
		this.examples = examples;
	}

    @Override
	public ArrayList<String> getAllExamplesText() {
		ArrayList<String> text = new ArrayList<String>();
		for (GrammarExample ge : examples) {
			text.add(ge.getText());
		}
		return text;
	}

	public ArrayList<String> getAllExamplesRomaji() {
		ArrayList<String> text = new ArrayList<String>();
		for (GrammarExample ge : examples) {
			text.add(ge.getRomaji());
		}
		return text;
	}

	public ArrayList<String> getAllTranslations() {
		ArrayList<String> text = new ArrayList<String>();
		boolean isEng = App.lang == Languages.ENG;
		for (GrammarExample ge : examples) {
			if (isEng)
				text.add(ge.getTranslationEng());
			else
				text.add(ge.getTranslationRus());
		}
		return text;
	}

    @Override
    public String translationsToString(){
        StringBuffer sb = new StringBuffer();
        for(String s : getAllTranslations()) sb.append(s+"; ");
        return  sb.toString();
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

	public GrammarRule() {
	}

	public ArrayList<GrammarExample> getExamples() {
		return examples;
	}

	public void setExamples(ArrayList<GrammarExample> examples) {
		this.examples = examples;
	}

	public String getRule() {
		return rule;
	}

	public int getLevel() {
		return level;
	}

	public String getDescEng() {
		return descEng;
	}

	public String getDescRus() {
		return descRus;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setDescEng(String descEng) {
		this.descEng = descEng;
	}

	public void setDescRus(String descRus) {
		this.descRus = descRus;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getDescription() {
		if (App.lang == Languages.ENG)
			return descEng;
		else
			return descRus;
	}

    public ArrayList<String> getSplittedDescriptions() {
        ArrayList<String> d = new ArrayList<String>();
        if (App.lang == Languages.ENG){
            descEng=descEng.replaceAll("\\((.*?)\\)","");
            String[] s = descEng.split("[;,./]");
            for(String ss : s) {
                ss=ss.toLowerCase();
                ss = ss.replaceAll("\\[(.*?)\\]", "");
                //ss=ss.replaceAll("\\d+[\\)|\\.]","");
                ss=ss.trim();
                ss = ss.replaceAll("^((an|a|the|to)(\\s))+", "");
                ss = ss.replaceAll("^((be|become|being)(\\s))+", "");
                ss=ss.replaceAll("[?.!。]+$","");
                ss=ss.trim();
                if(!ss.isEmpty())d.add(ss);
            }
        }
        else{
                String[] s = descRus.split(".|;|,|/");
                for(String ss : s) {
                    ss=ss.replaceAll("\\(((|\\[).*?()|\\])\\)","");
                    ss=ss.trim();
                    if(!ss.isEmpty())d.add(ss);
                }
            }
            return d;
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
        return rule;
    }

    /* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {

        out.writeInt(id);
        out.writeString(rule);
        out.writeInt(level);
        out.writeString(descEng);
        out.writeString(descRus);
        out.writeDouble(learnedPercentage);
        out.writeString(lastview);
        out.writeInt(shownTimes);
        out.writeString(color);
        out.writeTypedList(examples);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<GrammarRule> CREATOR = new Parcelable.Creator<GrammarRule>() {
        public GrammarRule createFromParcel(Parcel in) {
            return new GrammarRule(in);
        }

        public GrammarRule[] newArray(int size) {
            return new GrammarRule[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private GrammarRule(Parcel in) {
        id = in.readInt();
        rule = in.readString();
        level = in.readInt();
        descEng = in.readString();
        descRus = in.readString();
        learnedPercentage = in.readDouble();
        lastview = in.readString();
        shownTimes = in.readInt();
        color = in.readString();
        in.readTypedList((List<GrammarExample>)examples, GrammarExample.CREATOR);
    }

    @Override
    public int compareTo(Object another) {
        try{
            if(another==null||this==null) return 0;
            GrammarRule gr = (GrammarRule) another;
            Log.d("compareTo",this.toString()+" - "+gr.toString());
        return this.toString().compareTo(another.toString());
        }
        //all can be
        catch(Exception e){
            return 0;
        }
    }

}
