package ua.hneu.languagetrainer.pages.counterwords;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.LearningStatistics;
import ua.hneu.languagetrainer.model.User;
import ua.hneu.languagetrainer.model.other.CounterWord;
import ua.hneu.languagetrainer.pages.MainActivity;
import ua.hneu.languagetrainer.pages.SettingsActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class CounterWordsResultActivity extends Activity {
	TextView learnedWordsTextView;
	TextView recommendationsTextView;
	TextView sessionPercentageTextView;
	TextView totalPercentageTextView;
	TextView cautionTextView;
	TextView mistakesTextView;

    private InterstitialAd mInterstitialAd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        // Create an ad request.
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        // Optionally populate the ad request builder.
        adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

        // Set an AdListener.
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
            }

            @Override
            public void onAdClosed() {
            }
        });

        // Start loading the ad now so that it is ready by the time the user is ready to go to
        // the next level.
        mInterstitialAd.loadAd(adRequestBuilder.build());


		setContentView(R.layout.activity_learning_result);

		learnedWordsTextView = (TextView) findViewById(R.id.learnedWordsTextView);
		totalPercentageTextView = (TextView) findViewById(R.id.totalPercentageTextView);
		recommendationsTextView = (TextView) findViewById(R.id.recommendationsTextView);
		sessionPercentageTextView = (TextView) findViewById(R.id.sessionPercentageTextView);
		cautionTextView = (TextView) findViewById(R.id.cautionTextView);
		mistakesTextView = (TextView) findViewById(R.id.mistakesTextView);

		// information about learned words
		int numberOfLearnedWords = 0;
		StringBuffer sb = new StringBuffer();
		for (CounterWord entry : App.cwp.getLearnedWords().getEntries()) {
			if (entry.getLearnedPercentage() >= 1) {
				numberOfLearnedWords++;
				sb.append(entry.getWord());
				sb.append(", ");
			}
		}
		if (sb.length() != 0)
			sb.delete(sb.length() - 3, sb.length() - 1);
		StringBuffer sb1 = new StringBuffer();
		if (numberOfLearnedWords == 0)
			sb1.append(this.getString(R.string.you_havent_learned_any_word));
		else {
			sb1.append(this.getString(R.string.words_learned) + " ");
			sb1.append(numberOfLearnedWords + ": ");
		}
		sb1.append(sb);
		learnedWordsTextView.setText(sb1);

		// total
		int all = App.userInfo.getNumberOfCounterWordsInLevel();
		int learned = App.userInfo.getLearnedCounterWords();
		double totalPercentage = Math
				.round(((double) learned / (double) all) * 100);
		totalPercentageTextView.setText(this
				.getString(R.string.by_now_youve_learned)
				+ " "
				+ totalPercentage
				+ " "
				+ this.getString(R.string.percentage_of_cw));

		// mistakes
		StringBuffer sb2 = new StringBuffer();
		sb2.append(this.getString(R.string.pay_attention_to) + " ");
		int numberOfProblemWords = 0;
		for (CounterWord entry : App.cwp.getProblemWords().keySet()) {
			if (App.cwp.getProblemWords().get(entry) >= 2) {
				sb2.append(entry.getWord());
				sb2.append(", ");
				numberOfProblemWords++;
			}
		}
		sb2.delete(sb2.length() - 3, sb2.length() - 1);
		if (numberOfProblemWords != 0)
			mistakesTextView.setText(sb2);

		// information about session result
		int correct = App.cwp.getNumberOfCorrectAnswers();
		int incorrect = App.cwp.getNumberOfIncorrectAnswers();
		int success = (int) Math.round(((double) (correct - incorrect)
				/ (correct + incorrect) * 100));
		if (success < 0)
			success = 0;

		if (success > 80)
			sessionPercentageTextView.setText(this.getString(R.string.great)
					+ " ");
		else if (success > 60)
			sessionPercentageTextView.setText(this.getString(R.string.good)
					+ " ");
		else
			sessionPercentageTextView.setText(this
					.getString(R.string.more_attentive) + " ");
		sessionPercentageTextView.append(this
				.getString(R.string.correct_answer_rate) + " " + success + "%");

		// cautions
		int num = App.cwp.getNumberOfPassingsInARow();
		if (num > 3)
			cautionTextView.setText(this.getString(R.string.enough));

		// clear information about passing
		App.cwp.clearInfo();

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

	public void buttonRepeatTrainingOnClick(View v) {
		// go to introduction again
		Intent matchWordsIntent = new Intent(this,
				CounterWordsIntroductionActivity.class);
		startActivity(matchWordsIntent);
		App.cwp.incrementNumberOfPassingsInARow();
	}

	public void buttonToMainMenuOnClick(View v) {
		// go to master/detail flow
		Intent main = new Intent(this, MainActivity.class);
		startActivity(main);
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

}
