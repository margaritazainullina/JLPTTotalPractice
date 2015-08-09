package ua.hneu.languagetrainer.pages.dictionary;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.DictionaryListViewAdapter;
import ua.hneu.languagetrainer.model.EntryAbstr;
import ua.hneu.languagetrainer.model.grammar.GrammarRule;
import ua.hneu.languagetrainer.model.other.CounterWord;
import ua.hneu.languagetrainer.model.other.Giongo;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyEntry;
import ua.hneu.languagetrainer.service.DictionaryService;
import ua.hneu.languagetrainer.service.GrammarService;
import ua.hneu.languagetrainer.service.VocabularyService;

public class DictionaryActivity extends ListActivity {
    DictionaryListViewAdapter adapter1;
    boolean fromJapanese = true;
    //entries to show in list
    public static TreeSet<EntryAbstr> all = new TreeSet<>(EntryAbstr.DictionaryEntryComparator.ALPH_JAP);
    public static TreeSet<String> allJap = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    public static TreeSet<String> allTransl = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    public static List<String> entriesToShow = new ArrayList<>();
    DictionaryService ds = new DictionaryService();

    int counter = 0;
    //A ProgressDialog object
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        handleIntent(getIntent());

        new LoadViewTask().execute();
        long startTime = System.nanoTime();

        all.addAll(VocabularyService.allEntriesDictionary(App.cr).getEntries());
        all.addAll(GrammarService.allEntriesDictionary(App.cr).getEntries());
        all.addAll(App.allGiongoDictionary.getEntries());
        all.addAll(App.allCounterWordsDictionary.getEntries());

        allJap.addAll(VocabularyService.allEntriesDictionary(App.cr).getAllKanjiOrHiragana());
        allJap.addAll(GrammarService.allEntriesDictionary(App.cr).getAllRules());
        allJap.addAll(App.allGiongoDictionary.getAllGiongo());
        allJap.addAll(App.allCounterWordsDictionary.getAllWords());

        Log.d("TOTAL jap ", allJap.size() + "");
        if (App.lang == App.Languages.ENG)
            allTransl = ds.getListByLang("ENG", App.cr);
        if (App.lang == App.Languages.RUS)
            allTransl = ds.getListByLang("RUS", App.cr);
        Log.d("TOTAL translation ", allTransl.size() + "");
        long endTime = System.nanoTime();
        Log.d("Load time", "time for loading " + (endTime - startTime) + " nanoseconds.");
        showAll();

        //for test
        for(String s:allTransl){
            int number=0;
            for (EntryAbstr entry : all) {
                if(entry.translationsToString().contains(s)) number++;
            }
            if(number==0) Log.d("not found ", s);
        }
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
        String search = (String) getListAdapter().getItem(position);
        Log.d("onListItemClick", search);
        Set<EntryAbstr> result = new TreeSet<>();
        for (EntryAbstr ea : all) {
            if (fromJapanese) {
                if (ea instanceof VocabularyEntry) {
                    VocabularyEntry ve = (VocabularyEntry) ea;
                    if (ve.getKanjiOrHiragana().equals(search)) result.add(ea);
                } else {
                    if (ea.toString().equals(search)) result.add(ea);
                }
            } else {
                if (ea instanceof VocabularyEntry) {
                    VocabularyEntry ve = (VocabularyEntry) ea;
                    if (ve.translationsToString().contains(search)) result.add(ea);
                    Log.d("VocabularyEntry", ve.translationsToString());
                }
                if (ea instanceof GrammarRule) {
                    GrammarRule ve = (GrammarRule) ea;
                    if (ve.translationsToString().contains(search)) result.add(ea);
                    Log.d("GrammarRule", ve.translationsToString());
                }
                if (ea instanceof Giongo) {
                    Giongo ve = (Giongo) ea;
                    if (ve.translationsToString().contains(search)) result.add(ea);
                    Log.d("Giongo", ve.translationsToString());
                }
                if (ea instanceof CounterWord) {
                    CounterWord ve = (CounterWord) ea;
                    if (ve.translationsToString().contains(search)) result.add(ea);
                    Log.d("CounterWord", ve.translationsToString());
                }
            }
        }

        Intent intent = new Intent(this, EntryDictionaryDetail.class);

        for (EntryAbstr ea : result) {
            if (ea instanceof VocabularyEntry) {
                VocabularyEntry ve = (VocabularyEntry) ea;
                intent.putExtra("entry1", (Parcelable) ve);
                Log.d("VocabularyEntryDictionaryDetail ", ve.getKanji() + " " + ve.getRomaji() +
                        " " + ve.getLevel() + " " + ve.getTranslationsEng() + " " + ve.getTranslationsRus() + " " + ve.getLastview() + " "
                        + ve.getLearnedPercentage() + " " + ve.getShownTimes() + " ");
            }
            if (ea instanceof GrammarRule) {
                GrammarRule gr = (GrammarRule) ea;
                intent.putExtra("entry2", (Parcelable) gr);
            }
            if (ea instanceof Giongo) {
                Giongo g = (Giongo) ea;
                intent.putExtra("entry3", (Parcelable) g);
            }
            if (ea instanceof CounterWord) {
                CounterWord cw = (CounterWord) ea;
                intent.putExtra("entry4", (Parcelable) cw);
            }
        }
        startActivity(intent);
    }

    private void search(String query) {
        entriesToShow.clear();
        Log.d("query", query);
        if (fromJapanese) {
        for (EntryAbstr entry : all) {
                if (entry instanceof VocabularyEntry) {
                    VocabularyEntry ve = (VocabularyEntry) entry;
                    if ((ve.getKanji().toLowerCase()).startsWith(query.toLowerCase())
                            || (ve.getRomaji().toLowerCase()).startsWith(query.toLowerCase())
                            || (ve.getTranscription().toLowerCase()).startsWith(query.toLowerCase())) {
                        entriesToShow.add(ve.getKanjiOrHiragana());
                    }
                } else if (entry instanceof GrammarRule) {
                    GrammarRule ve = (GrammarRule) entry;
                    if ((ve.getRule().toLowerCase()).contains(query.toLowerCase())) {
                        entriesToShow.add(ve.toString());
                    }
                } else if (entry instanceof Giongo) {
                    Giongo ve = (Giongo) entry;
                    if ((ve.getRomaji().toLowerCase()).startsWith(query.toLowerCase())
                            || (ve.getWord().toLowerCase()).startsWith(query.toLowerCase()))
                        entriesToShow.add(ve.toString());
                } else if (entry instanceof CounterWord) {
                    CounterWord ve = (CounterWord) entry;
                    if ((ve.getRomaji().toLowerCase()).startsWith(query.toLowerCase())
                            || (ve.getWord().toLowerCase()).startsWith(query.toLowerCase()))
                        entriesToShow.add(ve.toString());
                }
            }} else {
                for (String transl:allTransl) {
                    String[] words= transl.split("\\s");
                    for(String word:words){
                    if(word.toLowerCase().startsWith(query.toLowerCase()))
                    entriesToShow.add(transl);
                    Log.d("found", transl);
                    }
                }

        }
        adapter1 = new DictionaryListViewAdapter(this, new ArrayList<String>(entriesToShow), fromJapanese);
        this.setListAdapter(adapter1);
    }

    private void showAll() {
        entriesToShow.clear();
        if (fromJapanese) entriesToShow.addAll(allJap);
        else entriesToShow.addAll(allTransl);
        adapter1 = new DictionaryListViewAdapter(this, new ArrayList<String>(entriesToShow), fromJapanese);
        this.setListAdapter(adapter1);
    }

    public void switchButtonOnClick(View v) {
        fromJapanese = !fromJapanese;
        showAll();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            search(query);
        }
    }


    private class LoadViewTask extends AsyncTask<Void, Integer, Void> {
        //Before running code in separate thread
        @Override
        protected void onPreExecute() { //Create a new progress dialog
            progressDialog = new ProgressDialog(DictionaryActivity.this);
            progressDialog.setTitle(getResources().getString(R.string.loading));
            progressDialog.setMessage(getResources().getString(R.string.loading_wait));
            progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.setMax(100);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
               /* //load dictionary to show asynchronously
                synchronized (this) {
                    long startTime = System.nanoTime();
                    publishProgress(0);

                    //multithreading
                    Thread[] threads = new Thread[4];

                    threads[0] = new Thread(new Runnable() {
                        public void run() {
                            DictionaryActivity.loadVocabulary();
                        }
                    });

                    threads[1] = new Thread(new Runnable() {
                        public void run() {
                            DictionaryActivity.loadGrammar();
                        }
                    });

                    threads[2] = new Thread(new Runnable() {
                        public void run() {
                            DictionaryActivity.loadGiongo();
                        }
                    });

                    threads[3] = new Thread(new Runnable() {
                        public void run() {
                            DictionaryActivity.loadCV();
                        }
                    });

                    for (int i = 0; i < 4; i++)
                        threads[i].start();

                    for (int i = 0; i < 4; i++) {
                        publishProgress(25 * i);
                        threads[i].join();
                    }


                    Thread[] threadsForLoad = new Thread[10];

                    int number = all.size() / 10;

                    for (int i = 0; i < 10; i++) {

                        publishProgress(50 + i * 10);

                        List<EntryAbstr> list = new ArrayList<>(all);
                        final Set<EntryAbstr> subSet = new LinkedHashSet<>(list.subList(number * i, number * (i + 1)));

                        threadsForLoad[i] = new Thread(new Runnable() {
                            public void run() {
                                createTreeSets(subSet);
                            }
                        });
                        threadsForLoad[i].start();
                        threadsForLoad[i].join();
                    }

                    Log.d("TOTAL jap ", allJapToShow.size() + "");
                    Log.d("TOTAL eng ", allTranslToShow.size() + "");
                    allTranslToShow = new ArrayList(temp);

                    long endTime = System.nanoTime();
                    Log.d("Load time", "time for loading " + (endTime - startTime) + " nanoseconds.");
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values) {
            //set the current progress of the progress dialog
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            showAll();
            progressDialog.dismiss();
        }
    }
}