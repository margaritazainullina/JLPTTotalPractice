package ua.hneu.languagetrainer.pages.grammar;

import java.util.ArrayList;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.AllCVListViewAdapter;
import ua.hneu.languagetrainer.AllGrammarListViewAdapter;
import ua.hneu.languagetrainer.AllVocabularyListViewAdapter;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.ListViewAdapter;
import ua.hneu.languagetrainer.model.grammar.GrammarDictionary;
import ua.hneu.languagetrainer.model.other.GiongoDictionary;
import ua.hneu.languagetrainer.pages.test.MockTestActivity;
import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AllGrammar extends ListActivity {
	AllGrammarListViewAdapter adapter1;
	ListView kanjiListView;
	GrammarDictionary entries;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_allvocabulary);
		handleIntent(getIntent());
		showAll();

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
	public boolean onSearchRequested() {
		return super.onSearchRequested();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.searchview_in_menu, menu);

		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
		if (searchView != null) {
			searchView
					.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

						public boolean onQueryTextSubmit(String submit) {
							return false;
						}

						public boolean onQueryTextChange(String change) {
							if (change.isEmpty())
								showAll();
							search(change);
							return false;
						}

					});
			searchView.setOnCloseListener(new OnCloseListener() {

				@Override
				public boolean onClose() {
					showAll();
					return false;
				}
			});
		}

		return super.onCreateOptionsMenu(menu);
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, AllGrammarExamples.class);
		String desc = entries.get(position).getDescription();
		intent.putExtra("rule", desc);
		startActivity(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			search(query);
		}
	}

	private void search(String query) {
		entries = entries.search(query);
		adapter1 = new AllGrammarListViewAdapter(this, entries);
		this.setListAdapter(adapter1);
	}

	private void showAll() {
		entries = App.allGrammarDictionary;
		adapter1 = new AllGrammarListViewAdapter(this, entries);
		this.setListAdapter(adapter1);
	}
}
