package ua.hneu.languagetrainer.tutorial;

import ua.hneu.edu.languagetrainer.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Tutorial extends Activity {

	TextView textViewInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);

	}

}