package ua.hneu.languagetrainer.pages;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.pages.counterwords.AllCounterWords;
import ua.hneu.languagetrainer.pages.counterwords.CounterWordsIntroductionActivity;
import ua.hneu.languagetrainer.pages.dictionary.DictionaryActivity;
import ua.hneu.languagetrainer.pages.giongo.AllGiongo;
import ua.hneu.languagetrainer.pages.giongo.GiongoIntroductionActivity;
import ua.hneu.languagetrainer.pages.grammar.AllGrammar;
import ua.hneu.languagetrainer.pages.grammar.GrammarIntroductionActivity;
import ua.hneu.languagetrainer.pages.test.MockTestActivity;
import ua.hneu.languagetrainer.pages.vocabulary.AllVocabulary;
import ua.hneu.languagetrainer.pages.vocabulary.WordIntroductionActivity;
import ua.hneu.languagetrainer.service.CounterWordsService;
import ua.hneu.languagetrainer.service.GiongoService;
import ua.hneu.languagetrainer.service.GrammarService;
import ua.hneu.languagetrainer.service.VocabularyService;
import ua.hneu.languagetrainer.tabsswipe.TabsPagerAdapter;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Locale;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	 //A ProgressDialog object  
    private ProgressDialog progressDialog;


	// Tab titles
	// TODO from resources

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        getSettings();
		setContentView(R.layout.activity_main);

		String[] tabs = { getResources().getString(R.string.vocabulary),
				getResources().getString(R.string.grammar),
				getResources().getString(R.string.mock_tests),
				getResources().getString(R.string.giongo),
				getResources().getString(R.string.counter_words) };

		if (App.userInfo == null) {
			// for first time when app is launched - set default preferences
			App.editor.putString("showRomaji", this.getResources().getStringArray(R.array.showRomajiValues)[1]);
			App.editor.putString("numOfRepetations", "7");
			App.editor.putString("numOfEntries", "7");
			App.editor.apply();
			Intent greetingIntent = new Intent(this, GreetingActivity.class);
			startActivity(greetingIntent);
			return;
		}

		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}
		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		 new LoadViewTask().execute();
    }



    public void getSettings() {
        App.numberOfEntriesInCurrentDict = Integer.parseInt(App.settings.getString("numOfEntries", "7"));
        App.numberOfRepeatationsForLearning = Integer.parseInt(App.settings.getString("numOfRepetations","7"));

        String value = App.settings.getString("showRomaji", App.context.getResources().getStringArray(R.array.showRomajiValues)[1]);
        if (value.equals("always")) {
            App.isShowRomaji = true;
            App.editor.putString("showRomaji", value);
            App.editor.putBoolean("color", true);
            App.editor.putBoolean("autoplay", true);
        } else if (value.equals("only_4_5")) {
            if (App.userInfo == null)
                App.isShowRomaji = true;
            else if (App.userInfo.getLevel() == 4
                    || App.userInfo.getLevel() == 5)
                App.isShowRomaji = true;
            else
                App.isShowRomaji = false;
        } else {
            App.isShowRomaji = false;
        }

        String lang = App.settings.getString("language", App.context.getResources().getStringArray(R.array.language)[1]);
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

    }
	
	private class LoadViewTask extends AsyncTask<Void, Integer, Void>  
	    {  
	        //Before running code in separate thread  
	        @Override  
	        protected void onPreExecute()  
	        {  
	            //Create a new progress dialog  
	            progressDialog = new ProgressDialog(MainActivity.this);  
	            //Set the progress dialog to display a horizontal progress bar  
	            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
	            //Set the dialog title to 'Loading...'  
	            progressDialog.setTitle(getResources().getString(R.string.loading));
	            //Set the dialog message to 'Loading application View, please wait...'  
	            progressDialog = ProgressDialog.show(MainActivity.this,getResources().getString(R.string.loading),
                        getResources().getString(R.string.loading_wait), false, false);
	        }

			@Override
			protected Void doInBackground(Void... params) {
	            try  
	            {
                    //Get the current thread's token
	                synchronized (this)  
	                {
                        // load vocabulary
	            		App.vocabularyDictionary = VocabularyService.createCurrentDictionary(
	            				App.userInfo.getLevel(), App.numberOfEntriesInCurrentDict,
	            				App.cr);
                        Log.d("MainActivity", "loaded vocabularyDictionary");

	            		// load grammar
	            		App.grammarDictionary = GrammarService.createCurrentDictionary(
	            				App.userInfo.getLevel(), App.numberOfEntriesInCurrentDict,
	            				App.cr);

                        Log.d("MainActivity", "loaded grammarDictionary");

	            		// load giongo
	            		GiongoService gs = new GiongoService();
	            		App.giongoWordsDictionary = gs.createCurrentDictionary(
	            				App.numberOfEntriesInCurrentDict, App.cr);

                        Log.d("MainActivity", "loaded giongoWordsDictionary");

	            		// load counter words
	            		CounterWordsService cws = new CounterWordsService();
	            		App.counterWordsDictionary = cws.createCurrentDictionary(
	            				App.sectionName, App.numberOfEntriesInCurrentDict, App.cr);
                        Log.d("MainActivity", "loaded counterWordsDictionary");

                    }
	            }  
	            catch (Exception e)  
	            {  
	                e.printStackTrace();  
	            }  
	            return null;  
	        }  
			//Update the progress  
	        @Override  
	        protected void onProgressUpdate(Integer... values)  
	        {  
	            //set the current progress of the progress dialog  
	            progressDialog.setProgress(values[0]);  
	        }  
	  
	        //after executing the code in the thread  
	        @Override  
	        protected void onPostExecute(Void result)  
	        {
                //just for test! TODO:remove after testing!

              /* VocabularyService vs = new VocabularyService();
                GrammarService grs = new GrammarService();
                GiongoService gs = new GiongoService();
                CounterWordsService cws = new CounterWordsService();
                vs.makeProgress(App.cr);
                Log.d("Main activity", "fake vocabulary progress");
                grs.makeProgress(App.cr);
                Log.d("Main activity", "fake grammar progress");
                gs.makeProgress(App.cr);
                Log.d("Main activity", "fake giongo progress");
                cws.makeProgress(App.cr);
                Log.d("Main activity", "fake cw progress");*/

	            //close the progress dialog  
	            progressDialog.dismiss();
	        }
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
                intent = new Intent(this, DictionaryActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	public void onClickPracticeVocabulary(View v) {
		Intent intent = new Intent(this, WordIntroductionActivity.class);
		startActivity(intent);
	}

	public void onClickAllVocabulary(View v) {
		Intent intent = new Intent(this, AllVocabulary.class);
		startActivity(intent);
	}

	public void onClickPracticeGrammar(View v) {
		Intent intent = new Intent(this, GrammarIntroductionActivity.class);
		startActivity(intent);
	}

	public void onClickAllGrammar(View v) {
		Intent intent = new Intent(this, AllGrammar.class);
		startActivity(intent);
	}

	public void onClickPracticeGiongo(View v) {
		Intent intent = new Intent(this, GiongoIntroductionActivity.class);
		startActivity(intent);
	}

	public void onClickAllGiongo(View v) {
		Intent intent = new Intent(this, AllGiongo.class);
		startActivity(intent);

	}

	public void onClickPracticeCounterWords(View v) {
		Intent intent = new Intent(this, CounterWordsIntroductionActivity.class);
		startActivity(intent);
	}

	public void onClickAllCounterWords(View v) {
        // load cw
        App.counterWordsDictionary = CounterWordsService.createCurrentDictionary(
               "", App.numberOfEntriesInCurrentDict,
                App.cr);
		Intent intent = new Intent(this, AllCounterWords.class);
		startActivity(intent);
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
	}

	public void onClickPassTest(View v) {
		// load test
		if (!App.testName.isEmpty()) {
			Intent intent = new Intent(this, MockTestActivity.class);
			intent.putExtra("testName", App.testName);
			App.testName = "";
			startActivity(intent);
		}

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

}
