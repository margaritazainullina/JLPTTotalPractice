package ua.hneu.languagetrainer;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.model.DictionaryAbstr;
import ua.hneu.languagetrainer.model.EntryAbstr;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyDictionary;

public class DictionaryListViewAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final ArrayList<String> entries;
    private boolean isJapanese;

	TextView entryTv;

	public DictionaryListViewAdapter(Context context,  ArrayList<String> e, boolean isJapanese) {
        super(context, R.layout.dictionary_rowlayout,e);
		this.context = context;
        this.isJapanese=isJapanese;
		this.entries = e;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.dictionary_rowlayout,
				parent, false);
		TextView entryTv = (TextView) rowView.findViewById(R.id.entry);
        entryTv.setText(entries.get(position));
        entryTv.setTypeface(App.kanjiFont, Typeface.NORMAL);

		return rowView;
	}

}