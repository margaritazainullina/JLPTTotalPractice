package ua.hneu.languagetrainer;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyDictionary;

public class AllVocabularyListViewAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> kanji;
    private final ArrayList<String> reading;
    private final ArrayList<String> translation;
    private final ArrayList<String> stat;

    TextView kanjiTv;
    TextView hiraganaTv;
    TextView romajiTv;
    TextView translationTv;
    TextView statTv;
    ImageView learned_img;

    public AllVocabularyListViewAdapter(Context context, VocabularyDictionary d) {
        super(context, R.layout.all_vocabulary_rowlayout, d.getAllKanji());
        this.context = context;
        this.kanji = d.getAllKanji();
        this.reading = d.getAllReadings();
        this.translation = d.getAllTranslations();
        // TODO: replace
        this.stat = d.getAllStatistics();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.all_vocabulary_rowlayout,
                parent, false);
        kanjiTv = (TextView) rowView.findViewById(R.id.avr_kanji);
        hiraganaTv = (TextView) rowView
                .findViewById(R.id.avr_hiragana);
        romajiTv = (TextView) rowView.findViewById(R.id.avr_romaji);
        translationTv = (TextView) rowView
                .findViewById(R.id.avr_translation);
        statTv = (TextView) rowView.findViewById(R.id.avr_stat);
        learned_img = (ImageView) rowView.findViewById(R.id.learned_img);
        if (!kanji.get(position).isEmpty()) {
            kanjiTv.setText(kanji.get(position));
        } else {
            String[] s = reading.get(position).split(" - ");
            kanjiTv.setText(s[0]);
        }

        translationTv.setText(translation.get(position));
        statTv.setText(stat.get(position));

        String[] s = reading.get(position).split(" - ");
        hiraganaTv.setText(s[0]);
        if (App.isShowRomaji) {
            romajiTv.setText(s[1]);
        } else {
            romajiTv.setText("");
            romajiTv.setHeight(0);
        }
        hiraganaTv.setTypeface(App.kanjiFont, Typeface.NORMAL);
        kanjiTv.setTypeface(App.kanjiFont, Typeface.NORMAL);
        if (!stat.get(position).equals("Learned")) {
            learned_img.setVisibility(View.INVISIBLE);
            learned_img.setMaxHeight(0);
        }
        return rowView;
    }

}