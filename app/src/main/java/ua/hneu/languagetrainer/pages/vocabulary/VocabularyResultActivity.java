package ua.hneu.languagetrainer.pages.vocabulary;

import android.app.Activity;
import android.os.Bundle;
import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.LearningStatistics;
import ua.hneu.languagetrainer.model.User;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyEntry;
import ua.hneu.languagetrainer.pages.MainActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.*;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.*;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.pages.SettingsActivity;

/**
 * Main Activity. Inflates main activity xml and child fragments.
 */
public class VocabularyResultActivity  extends Activity {
    private InterstitialAd mInterstitialAd;

    TextView learnedWordsTextView;
    TextView recommendationsTextView;
    TextView sessionPercentageTextView;
    TextView totalPercentageTextView;
    TextView cautionTextView;
    TextView mistakesTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an interstitial ad. When a natural transition in the app occurs (such as a
        // level ending in a game), show the interstitial. In this simple example, the press of a
        // button is used instead.
        //
        // If the button is clicked before the interstitial is loaded, the user should proceed to
        // the next part of the app (in this case, the next level).
        //
        // If the interstitial is finished loading, the user will view the interstitial before
        // proceeding.
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
        for (VocabularyEntry entry : App.vp.getLearnedWords().getEntries()) {
            if (entry.getLearnedPercentage() >= 1) {
                numberOfLearnedWords++;
                if (!entry.getKanji().isEmpty())
                    sb.append(entry.getKanji());
                else
                    sb.append(entry.getTranscription());
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
        int all = App.userInfo.getNumberOfVocabularyInLevel();
        int learned = App.userInfo.getLearnedVocabulary();
        double totalPercentage = Math
                .round(((double) learned / (double) all) * 100);
        totalPercentageTextView.setText(this
                .getString(R.string.by_now_youve_learned)
                + " "
                + totalPercentage
                + " "
                + this.getString(R.string.percentage_of_voc));

        // mistakes
        StringBuffer sb2 = new StringBuffer();
        sb2.append(this.getString(R.string.pay_attention_to) + " ");
        int numberOfProblemWords = 0;
        for (VocabularyEntry entry : App.vp.getProblemWords().keySet()) {
            if (App.vp.getProblemWords().get(entry) >= 2) {
                if (!entry.getKanji().isEmpty())
                    sb2.append(entry.getKanji());
                else
                    sb2.append(entry.getTranscription());
                sb2.append(", ");
                numberOfProblemWords++;
            }
        }
        sb2.delete(sb2.length() - 3, sb2.length() - 1);
        if (numberOfProblemWords != 0)
            mistakesTextView.setText(sb2);

        // information about session result
        int correct = App.vp.getNumberOfCorrectAnswersInMatching()
                + App.vp.getNumberOfCorrectAnswersInTranslation()
                + App.vp.getNumberOfCorrectAnswersInTranscription();
        int incorrect = App.vp.getNumberOfIncorrectAnswersInMatching()
                + App.vp.getNumberOfIncorrectAnswersInTranslation()
                + App.vp.getNumberOfIncorrectAnswersInTranscription();
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
        int num = App.vp.getNumberOfPassingsInARow();
        if (num > 3)
            cautionTextView.setText(this.getString(R.string.enough));

        // clear information about passing
        App.vp.clearInfo();
        if (App.isAllLearned()) {
            //TODO: something more cute!
            Toast.makeText(this, this.getString(R.string.congratulations), Toast.LENGTH_LONG);
        }
    }
    public void buttonRepeatTrainingOnClick(View v) {
        // go to introduction again
        Intent matchWordsIntent = new Intent(this,
                WordIntroductionActivity.class);
        startActivity(matchWordsIntent);
        App.vp.incrementNumberOfPassingsInARow();
    }
    public void buttonToMainMenuOnClick(View v) {
        // go to master/detail flow
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.word_practice_result, menu);
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