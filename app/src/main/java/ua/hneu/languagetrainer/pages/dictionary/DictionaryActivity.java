package ua.hneu.languagetrainer.pages.dictionary;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.SyncStateContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.AllVocabularyListViewAdapter;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.DictionaryListViewAdapter;
import ua.hneu.languagetrainer.model.DictionaryAbstr;
import ua.hneu.languagetrainer.model.EntryAbstr;
import ua.hneu.languagetrainer.model.grammar.GrammarDictionary;
import ua.hneu.languagetrainer.model.grammar.GrammarRule;
import ua.hneu.languagetrainer.model.other.CounterWord;
import ua.hneu.languagetrainer.model.other.Giongo;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyDictionary;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyEntry;
import ua.hneu.languagetrainer.service.GrammarService;
import ua.hneu.languagetrainer.service.VocabularyService;

public class DictionaryActivity extends ListActivity {
    DictionaryListViewAdapter adapter1;
    ArrayList<EntryAbstr> allSortedByJap = new ArrayList<>();
    ArrayList<EntryAbstr> allSortedByTransl = new ArrayList<>();

    boolean fromJapanese = true;
    int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        handleIntent(getIntent());
        VocabularyDictionary vd = VocabularyService.allEntriesDictionary(App.cr);
        GrammarDictionary gd = GrammarService.allEntriesDictionary(App.cr);
        allSortedByJap.addAll(vd.getEntries());
        allSortedByJap.addAll(gd.getEntries());
        allSortedByJap.addAll(App.giongoWordsDictionary.getEntries());
        allSortedByJap.addAll(App.counterWordsDictionary.getEntries());

        allSortedByTransl = (ArrayList<EntryAbstr>)allSortedByJap.clone();

        Collections.sort(allSortedByJap,
                EntryAbstr.DictionaryEntryComparator.ALPH_JAP);
        Collections.sort(allSortedByTransl,
                EntryAbstr.DictionaryEntryComparator.ALPH_TRANSLATED);
        showAll();
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
        EntryAbstr ea;
        if(fromJapanese)
             ea = allSortedByJap.get(position);
        else  ea = allSortedByTransl.get(position);

        if(ea instanceof VocabularyEntry){
            VocabularyEntry ve = (VocabularyEntry) ea;
            Intent intent = new Intent(this, VocabularyEntryDictionaryDetail.class);
            intent.putExtra("entry",ve);
            startActivity(intent);
        }
        if(ea instanceof GrammarRule){
            GrammarRule gr = (GrammarRule) ea;
            Intent intent = new Intent(this, GrammarEntryDictionaryDetail.class);
            intent.putExtra("entry",gr);
            startActivity(intent);
        }
        if(ea instanceof Giongo){
            Giongo g = (Giongo) ea;
            Intent intent = new Intent(this, GiongoEntryDictionaryDetail.class);
            intent.putExtra("entry",g);
            startActivity(intent);
        }
        if(ea instanceof CounterWord){
            CounterWord cw = (CounterWord) ea;
            Intent intent = new Intent(this, CWEntryDictionaryDetail.class);
            intent.putExtra("entry",cw);
            startActivity(intent);
        }

    }

    private void search(String query) {
        ArrayList<EntryAbstr>  entriestoShow1 = new ArrayList<EntryAbstr>();
        boolean isFound = false;

            if(fromJapanese) {
                //if VocabularyEntry
                for (EntryAbstr entry : allSortedByJap) {
                    if (entry instanceof VocabularyEntry) {
                        VocabularyEntry ve = (VocabularyEntry) entry;
                        if ((ve.getKanji().toLowerCase()).startsWith(query.toLowerCase())
                                || (ve.getRomaji().toLowerCase()).startsWith(query.toLowerCase())
                                || (ve.getTranscription().toLowerCase()).startsWith(query.toLowerCase())) {
                            entriestoShow1.add(ve);
                        }
                    } else if (entry instanceof GrammarRule) {
                        GrammarRule ve = (GrammarRule) entry;
                        if ((ve.getRule().toLowerCase()).contains(query.toLowerCase())) {
                            entriestoShow1.add(ve);
                        }
                    } else if (entry instanceof Giongo) {
                        Giongo ve = (Giongo) entry;
                        if ((ve.getRomaji().toLowerCase()).startsWith(query.toLowerCase())
                                || (ve.getWord().toLowerCase()).startsWith(query.toLowerCase()))
                            entriestoShow1.add(ve);
                    } else if (entry instanceof CounterWord) {
                        CounterWord ve = (CounterWord) entry;
                        if ((ve.getRomaji().toLowerCase()).startsWith(query.toLowerCase())
                                || (ve.getWord().toLowerCase()).startsWith(query.toLowerCase()))
                            entriestoShow1.add(ve);
                    }
                }
            }
            else {
                for (EntryAbstr entry : allSortedByTransl) {
                    //if VocabularyEntry
                    if (entry instanceof VocabularyEntry) {
                        VocabularyEntry ve = (VocabularyEntry) entry;
                        if (ve.translationsToString().toLowerCase().contains(query.toLowerCase())) {
                            entriestoShow1.add(ve);
                        }
                    } else if (entry instanceof GrammarRule) {
                        GrammarRule ve = (GrammarRule) entry;
                        if (ve.getDescription().toLowerCase().contains(query.toLowerCase())) {
                            entriestoShow1.add(ve);
                        }
                    } else if (entry instanceof Giongo) {
                        Giongo ve = (Giongo) entry;
                        if (ve.translationsToString().toLowerCase().contains(query.toLowerCase()))
                            entriestoShow1.add(ve);
                    } else if (entry instanceof CounterWord) {
                        CounterWord ve = (CounterWord) entry;
                        if (ve.translationsToString().toLowerCase().contains(query.toLowerCase()))
                            entriestoShow1.add(ve);
                    }
                }
            }

        if(fromJapanese){
            ArrayList<String> entriesToShow = new ArrayList<>();
            for(EntryAbstr ea : allSortedByJap) entriesToShow.add(ea.toString());
            adapter1 = new DictionaryListViewAdapter(this, entriesToShow, fromJapanese);
        }
        else {
            ArrayList<String> entriesToShow = new ArrayList<>();
            for(EntryAbstr ea : allSortedByJap) entriesToShow.add(ea.translationsToString());
            adapter1 = new DictionaryListViewAdapter(this, entriesToShow, fromJapanese);
        }
        this.setListAdapter(adapter1);
    }

    private void showAll() {
        if(fromJapanese){
            ArrayList<String> entriesToShow = new ArrayList<>();
            for(EntryAbstr ea : allSortedByJap) entriesToShow.add(ea.toString());
            adapter1 = new DictionaryListViewAdapter(this, entriesToShow, fromJapanese);
        }
        else {
            ArrayList<String> entriesToShow = new ArrayList<>();
            for(EntryAbstr ea : allSortedByJap) entriesToShow.add(ea.translationsToString());
            adapter1 = new DictionaryListViewAdapter(this, entriesToShow, fromJapanese);
        }
        this.setListAdapter(adapter1);
    }
    public void switchButtonOnClick(View v){
        fromJapanese=!fromJapanese;
        showAll();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            search(query);
        }
    }
}