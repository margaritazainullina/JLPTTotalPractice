package ua.hneu.languagetrainer;

import java.util.ArrayList;

import ua.hneu.edu.languagetrainer.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListViewAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final ArrayList<String> values;
	private boolean isJapanese;

	public ListViewAdapter(Context context, ArrayList<String> values,
			boolean isJapanese) {
		super(context, R.layout.rowlayout, values);
		this.context = context;
		this.values = values;
		this.isJapanese = isJapanese;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		if (isJapanese) {
			textView.setTypeface(App.kanjiFont, Typeface.NORMAL);
		}
		textView.setText(values.get(position));
		return rowView;
	}

	public void hideElement(View listRow, Animation anim, long duration) {
		// settings for fading of listView row
		anim.setDuration(duration);
		anim.setFillAfter(true);
		listRow.startAnimation(anim);
		// disable the row
		listRow.setActivated(false);
		listRow.setEnabled(false);
		// make it not visible
		listRow.setVisibility(View.INVISIBLE);
	}

}