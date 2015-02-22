package ua.hneu.languagetrainer.model.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;

import android.util.Log;

import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.App.Languages;
import ua.hneu.languagetrainer.model.DictionaryAbstr;
import ua.hneu.languagetrainer.model.EntryAbstr;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyDictionary;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyEntry;
import ua.hneu.languagetrainer.service.GrammarService;
import ua.hneu.languagetrainer.service.VocabularyService;

public class GrammarDictionary extends DictionaryAbstr {

	private ArrayList<GrammarRule> entries;

	public ArrayList<GrammarRule> getEntries() {
		return entries;
	}

	public GrammarDictionary() {
		this.entries = new ArrayList<GrammarRule>();
	}

    public int size() {
        return entries.size();
    }

	public void add(GrammarRule e) {
		entries.add(e);
	}

	public void remove(GrammarRule e) {
		entries.remove(e);
	}

	public GrammarRule get(int idx) {
		return entries.get(idx);
	}

	public GrammarRule fetchRandom() {
		int a = new Random().nextInt(entries.size() - 1);
		return entries.get(a);
	}

	public Set<GrammarRule> getRandomEntries(int size) {
		Set<GrammarRule> random = new HashSet<GrammarRule>();
        if(App.userInfo.getNumberOfGrammarInLevel()<=App.userInfo.getLearnedGrammar()) return random;
		Random rn = new Random();

		while (random.size() <= size) {
			int i = rn.nextInt(entries.size());
			random.add(entries.get(i));

		}
		return random;
	}

	public Hashtable<GrammarExample, GrammarRule> getRandomExamplesWithRule(
			int size) {
		Hashtable<GrammarExample, GrammarRule> random = new Hashtable<GrammarExample, GrammarRule>();
		Random rn = new Random();
		// TODO: what if wasn't learned words number is less than size
		if (this.size() <= size) {
			for (GrammarRule gr : this.entries) {
				int j = rn.nextInt(gr.getExamples().size());
				if (!random.contains(gr.getExamples())) {
					random.put(gr.getExamples().get(j), gr);
				}
			}
		} else {
			int counter = 0;
			while (counter <= size) {
				int i = rn.nextInt(entries.size());
				GrammarRule g = entries.get(i);
				int j = rn.nextInt(g.getExamples().size());
				if (!random.contains(g.getExamples())) {
					random.put(g.getExamples().get(j), g);
					counter++;
				}
			}
		}
		return random;
	}

    public void addEntriesToDictionaryAndGet(int size) {
        //old entries
        Set<GrammarRule> gr = new HashSet<GrammarRule>();
        gr.addAll(this.entries);
        //get new entries from db, not learned
        //if there are too much learned - just return
        GrammarDictionary newWords=GrammarService.getNLeastLearned(size-gr.size(),  App.cr);
        //merge and return
        gr.addAll(newWords.entries);
        ArrayList<GrammarRule> l = new ArrayList<GrammarRule>();
        l.addAll(gr);
        this.entries = l;
    }

	public ArrayList<String> getAllRules() {
		ArrayList<String> rules = new ArrayList<String>();
		for (GrammarRule e : entries) {
			rules.add(e.getRule());
		}
		return rules;
	}

	public ArrayList<String> getAllDescriptions() {
		ArrayList<String> descriptions = new ArrayList<String>();
		for (GrammarRule e : entries) {
			descriptions.add(e.getDescription());
		}
		return descriptions;
	}

	public GrammarDictionary search(String query) {
		boolean isFound = false;
		GrammarDictionary result = new GrammarDictionary();
		for (GrammarRule ve : entries) {
			if ((ve.getRule().toLowerCase()).contains(query.toLowerCase())
					|| (ve.getDescription().toLowerCase()).contains(query
							.toLowerCase())) {
				result.add(ve);
			} else {
				for (String transl : ve.getAllTranslations()) {
					if ((transl.toLowerCase()).startsWith(query.toLowerCase())) {
						result.add(ve);
						isFound = true;
						break;
					}
					if (isFound) {
						isFound = false;
						break;
					}
				}
			}
		}
		return result;
	}

	public GrammarRule getByRule(String rule) {
		GrammarRule gr = null;
		for (GrammarRule r : entries) {
			if (r.getDescription().equals(rule)) {
				gr = r;
				break;
			}
		}
		return gr;
	}
    public ArrayList<String> allToString(){
        return getAllRules();
    }

    public void addAll(ArrayList<GrammarRule> newEntries){
        entries.addAll(newEntries);
    }
 }
