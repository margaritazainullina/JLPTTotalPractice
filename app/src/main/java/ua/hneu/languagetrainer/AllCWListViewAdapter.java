package ua.hneu.languagetrainer;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.model.other.CounterWordsDictionary;

public class AllCWListViewAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> word;
    private final ArrayList<String> reading;
    private final ArrayList<String> translation;

    TextView wordTv;
    TextView readingTv;
    TextView translationTv;

    public AllCWListViewAdapter(Context context, CounterWordsDictionary d) {
        super(context, R.layout.all_counterwords_rowlayout, d.getAllWords());
        this.context = context;
        this.word = d.getAllWords();
        this.reading = d.getAllTranscriptions();
        this.translation = d.getAllTranslations();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.all_counterwords_rowlayout, parent, false);
        wordTv = (TextView) rowView.findViewById(R.id.acr_word);
        readingTv = (TextView) rowView.findViewById(R.id.acr_reading);
        translationTv = (TextView) rowView.findViewById(R.id.acr_translation);
        wordTv.setText(word.get(position));
        readingTv.setText(reading.get(position));
        translationTv.setText(translation.get(position));

        wordTv.setTypeface(App.kanjiFont, Typeface.NORMAL);
        readingTv.setTypeface(App.kanjiFont, Typeface.NORMAL);
        return rowView;
    }

}