package ua.hneu.languagetrainer.pages.giongo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.LearningStatistics;
import ua.hneu.languagetrainer.ListViewAdapter;
import ua.hneu.languagetrainer.model.other.Giongo;
import ua.hneu.languagetrainer.model.other.GiongoExample;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyEntry;
import ua.hneu.languagetrainer.pages.MainActivity;
import ua.hneu.languagetrainer.pages.SettingsActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class GiongoTestActivity extends Activity {
	// dictionary with random words for possible answers
	Hashtable<GiongoExample, Giongo> randomExamplesDictionary;
	GiongoExample rightAnswer;
	Giongo rightWord;
	ArrayList<String> answers = new ArrayList<String>();
	// activity elements
	ListView answersListView;
	TextView part1TextView;
	TextView part2TextView;
	TextView part3TextView;
	ImageView isCorrect;
	int answersNumber = 4;
	int currentWordNumber = -1;
	int color = 0;

	// custom adapter for ListView
	ListViewAdapter adapter;
	boolean ifWasWrong = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grammar_giongo_test);

		// Initialize views
		part1TextView = (TextView) findViewById(R.id.part1TextView);
		part2TextView = (TextView) findViewById(R.id.part2TextView);
		part3TextView = (TextView) findViewById(R.id.part3TextView);
		answersListView = (ListView) findViewById(R.id.answersListView);
		isCorrect = (ImageView) findViewById(R.id.isCorrect);	
		// at first show word and possible answers
		nextWord();
		part1TextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
		part2TextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
		part3TextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
	}

	public void nextWord() {
		// move pointer to next word
		currentWordNumber++;
		if (currentWordNumber >= App.giongoWordsDictionary.size() - 1)
			endTesting();
		//create random dictionary again for every show
		randomExamplesDictionary = App.giongoWordsDictionary
				.getRandomExamplesWithWord(App.numberOfEntriesInCurrentDict);
		// iterator for looping over dictionary with random entries
		Set<Entry<GiongoExample, Giongo>> set = randomExamplesDictionary
				.entrySet();
		Iterator<Entry<GiongoExample, Giongo>> it = set.iterator();

		answers = new ArrayList<String>();
		// rightAnswer answer - the first entry
		// rightWord is stored for fetching color and incrementing of learned
		// percentage
		Random r = new Random();
		int idx = r.nextInt(answersNumber);
		int i=0;
		while (it.hasNext() && answers.size() < answersNumber) {
			Map.Entry<GiongoExample, Giongo> entry = (Map.Entry<GiongoExample, Giongo>) it
					.next();
			if (i==idx||set.size()==1) {
				// string with right answer
				rightAnswer = entry.getKey();
				rightWord = entry.getValue();
				color = entry.getValue().getIntColor();
			}
			answers.add(entry.getKey().getPart2());
			i++;
		}

		part1TextView.setText(rightAnswer.getPart1());
		part3TextView.setText(rightAnswer.getPart3());
        if(App.isColored) {
            part1TextView.setTextColor(color);
            part2TextView.setTextColor(color);
            part3TextView.setTextColor(color);
        }
		// shuffling, because first line always stores right answer
		Collections.shuffle(answers);
		adapter = new ListViewAdapter(this, answers, true);
		// bindings adapter to ListView
		answersListView.setAdapter(adapter);
		answersListView.setOnItemClickListener(answersListViewClickListener);
		isCorrect.setImageResource(android.R.color.transparent);

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        AdView mAdView = (AdView) findViewById(R.id.adView);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.menu_statistics:
                intent = new Intent(this, LearningStatistics.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    final private transient OnItemClickListener answersListViewClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(final AdapterView<?> parent, final View view,
				final int position, final long itemID) {
            view.setSelected(true);
			String selected = answers.get(position);
			// comparing correct and selected answer
			if (selected.equals(rightAnswer.getPart2())) {
				App.gp.incrementNumberOfCorrectAnswers();
				// increment percentage of right answers if wrong answer wasn't
				// given
				if (!ifWasWrong)
					rightWord.setLearnedPercentage(rightWord
							.getLearnedPercentage()
							+ App.getPercentageIncrement());

				if (rightWord.getLearnedPercentage() >= 1) {
					makeWordLearned(rightWord);
				}

				App.gs.update(rightWord, getContentResolver());
				// change color to green and fade out
				isCorrect.setImageResource(R.drawable.yes);
				// fading out textboxes
				fadeOut(part1TextView, 750);
				fadeOut(part2TextView, 750);
				fadeOut(part3TextView, 750);
				fadeOut(isCorrect, 750);
				// fading out listview
				ListView v = (ListView) view.getParent();
				fadeOut(v, 750);

				Animation fadeOutAnimation = AnimationUtils.loadAnimation(
						v.getContext(), android.R.anim.fade_out);
				fadeOutAnimation.setDuration(750);
				v.startAnimation(fadeOutAnimation);

				fadeOutAnimation
						.setAnimationListener(new Animation.AnimationListener() {
							@Override
							public void onAnimationEnd(Animation animation) {
								// when previous information faded out
								// show next word and possible answers or go to
								// next exercise
								if (currentWordNumber < App.giongoWordsDictionary
										.size() - 1) {
									nextWord();
								} else {
									endTesting();
								}
							}

							// isn't needed, just implementation
							@Override
							public void onAnimationRepeat(Animation arg0) {
							}

							@Override
							public void onAnimationStart(Animation arg0) {
							}
						});
			} else {
				// change color of row and set text
				isCorrect.setImageResource(R.drawable.no);
				ifWasWrong = true;
				// set information about wrong answer in GPassing
				App.gp.incrementNumberOfIncorrectAnswers();
				App.gp.addProblemWord(rightWord);

			}
		}
	};

	public void fadeOut(View v, long duration) {
		Animation fadeOutAnimation = AnimationUtils.loadAnimation(
				v.getContext(), android.R.anim.fade_out);
		fadeOutAnimation.setDuration(750);
		v.startAnimation(fadeOutAnimation);
	}

	public void endTesting() {
		// go to ResultActivity		
		  Intent nextActivity = new Intent(this,
		  GiongoResultActivity.class); startActivity(nextActivity);		 
	}

	public void buttonSkipSelectOnClick(View v) {
		endTesting();
	}

	public void buttonIAlrKnow(View v) {
		makeWordLearned(rightWord);
		nextWord();

	}

    public void makeWordLearned(Giongo ve){
        App.gp.makeWordLearned(ve, getContentResolver());

        if(App.isGiongoLearned()) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.all_vocabulary_learned_toast,
                    (ViewGroup) findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText(getResources().getString(R.string.g_learned) + " N" + App.userInfo.getLevel());

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();

            Intent mainActivity = new Intent(this,
                    MainActivity.class);
            startActivity(mainActivity);
        }
    }
}
