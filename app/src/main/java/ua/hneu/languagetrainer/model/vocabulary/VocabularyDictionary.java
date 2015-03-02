package ua.hneu.languagetrainer.model.vocabulary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.util.Log;

import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.App.Languages;
import ua.hneu.languagetrainer.model.DictionaryAbstr;
import ua.hneu.languagetrainer.model.EntryAbstr;
import ua.hneu.languagetrainer.service.VocabularyService;

public class VocabularyDictionary extends DictionaryAbstr {

	private ArrayList<VocabularyEntry> entries;

	public VocabularyEntry getEntryById(int id) {
		for (VocabularyEntry entry : entries) {
			if (entry.getId() == id)
				return entry;
		}
		return null;
	}

	// all entries with kanji, transcription, romaji and translation
	public ArrayList<VocabularyEntry> getEntries() {
		return entries;
	}

	// returns ArrayList of all kanji in dictionary (without empty ones)
	public ArrayList<String> getAllKanji() {
		ArrayList<String> kanji = new ArrayList<String>();
		for (VocabularyEntry e : entries) {
			if (e.getKanji() != "")
				kanji.add(e.getKanji());
		}
		return kanji;
	}

    public ArrayList<String> getAllKanjiOrHiragana() {
        ArrayList<String> kanji = new ArrayList<String>();
        for (VocabularyEntry e : entries) {
                kanji.add(e.getKanjiOrHiragana());
        }
        return kanji;
    }


    // returns ArrayList of all ids in dictionary (without empty ones)
	public ArrayList<Integer> getAllIds() {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (VocabularyEntry e : entries) {
			if (e.getKanji() != "")
				ids.add(e.getId());
		}
		return ids;
	}

	// returns ArrayList of transcription and romaji in dictionary
	public ArrayList<String> getAllReadings() {
		ArrayList<String> readings = new ArrayList<String>();
		for (VocabularyEntry e : entries) {
			readings.add(e.readingsToString());
		}
		return readings;
	}

	// returns ArrayList of all translations in dictionary
	public ArrayList<String> getAllTranslations() {
		ArrayList<String> translation = new ArrayList<String>();
		for (VocabularyEntry e : entries) {
			translation.add(e.translationsToString() + "");
		}
		return translation;
	}

	public void addEntriesToDictionaryAndGet(int size, boolean onlyWithKanji) {
        //old entries
		HashSet<VocabularyEntry> de = new HashSet<VocabularyEntry>();
		de.addAll(this.entries);
        //get new entries from db, not learned
        //if there are too much learned - just return
        VocabularyDictionary newWords=VocabularyService.getNLeastLearned(20, App.userInfo.getLevel(), onlyWithKanji, App.cr);
        for(VocabularyEntry e : newWords.entries){
            if(de.size()>=size) break;
            else {
                boolean x=false;
                //wtf why does it add the same entry to hashset?
                for(VocabularyEntry ve : de){
                    if(e.getId()==ve.getId()) {x=true; break;}
                }
                if (!x) de.add(e);
            }
        }

        //return merged list
		ArrayList<VocabularyEntry> l = new ArrayList<VocabularyEntry>();
		l.addAll(de);
		this.entries = l;
	}

	// returns Set with stated size of unique random entries from current
	// dictionary
	public Set<VocabularyEntry> getRandomEntries(int size,
			boolean kanjiIsNessesary) {
		Set<VocabularyEntry> random = new HashSet<VocabularyEntry>();
        if(App.userInfo.getNumberOfVocabularyInLevel()<=App.userInfo.getLearnedVocabulary()) return random;
		Random rn = new Random();

		int numberOfEntriesWithKanji = 0;
		for (VocabularyEntry entry : entries) {
			if (!entry.getKanji().isEmpty())
				numberOfEntriesWithKanji++;
		}

       /* if(size>=App.vocabularyDictionary.size()) {
            random = new HashSet<>(App.vocabularyDictionary.entries);
            return random;
        }
        else */{
            while (random.size() < size) {
                int i = rn.nextInt(entries.size());
                boolean isEmpty = false;
               // if (entries.get(i).getLearnedPercentage() < 1)
               {
                    if (entries.get(i).getKanji().isEmpty())
                        isEmpty = true;

                    if (kanjiIsNessesary && !isEmpty) {
                        random.add(entries.get(i));
                    }
                    if (!kanjiIsNessesary) {
                        random.add(entries.get(i));
                    }

                    if (kanjiIsNessesary) {
                        if (numberOfEntriesWithKanji == random.size() - 1) {
                            this.addEntriesToDictionaryAndGet(size, true);
                            return random;
                        }
                    }
                }
            }
        }return random;
	}
	
	public void setEntries(ArrayList<VocabularyEntry> entries) {
		this.entries = entries;
	}

	public VocabularyDictionary() {
		this.entries = new ArrayList<VocabularyEntry>();
	}

	public void add(VocabularyEntry e) {
		entries.add(e);
	}

	public void remove(VocabularyEntry e) {
		entries.remove(e);
	}

	public int size() {
		return entries.size();
	}

	public VocabularyEntry get(int idx) {
		return entries.get(idx);
	}

	public VocabularyEntry fetchRandom() {
		int a = new Random().nextInt(entries.size() - 1);
		return entries.get(a);
	}

	public ArrayList<String> getAllKanjiWithReadings() {
		ArrayList<String> readings = new ArrayList<String>();
		for (VocabularyEntry e : entries) {
            if(!e.getKanji().isEmpty())
			readings.add(e.getKanji()+" - ["+e.readingsToString()+"]");
            else readings.add(e.readingsToString());
		}
		return readings;
	}

	public VocabularyDictionary search(String query) {
		VocabularyDictionary result = new VocabularyDictionary();
		boolean isFound = false;
		for (VocabularyEntry ve : App.allVocabularyDictionary.getEntries()) {
			if ((ve.getKanji().toLowerCase()).startsWith(query.toLowerCase())
					|| (ve.getRomaji().toLowerCase()).startsWith(query.toLowerCase())
					|| (ve.getTranscription().toLowerCase()).startsWith(query.toLowerCase())) {
				result.add(ve);
			} else {
				if (App.lang == Languages.RUS) {
					for (String transl : ve.getTranslationsRus()) {
						if ((transl.toLowerCase()).startsWith(query.toLowerCase())) {
							result.add(ve);
							isFound = true;
							break;
						}
						if (isFound) {
							break;
						}
						isFound = false;
					}
				} else {
					for (String transl : ve.getTranslationsEng()) {
						if ((transl.toLowerCase()).startsWith(query.toLowerCase())) {
							result.add(ve);
							isFound = true;
							break;
						}
						if (isFound) {
							break;
						}
						isFound = false;
					}
				}
			}
		}
		return result;
	}

	public ArrayList<String> getAllStatistics() {
		ArrayList<String> statistics = new ArrayList<String>();
		for (VocabularyEntry e : entries) {
			int a = (int) Math.round(App.numberOfEntriesInCurrentDict*e.getLearnedPercentage());
			if (e.getLearnedPercentage() == 1) statistics.add("Learned");
			else
				statistics.add(a+"/"+App.numberOfEntriesInCurrentDict);
		}
		return statistics;
	}

    public ArrayList<String> allToString(){
        return getAllKanji();
    }

    public void addAll(ArrayList<VocabularyEntry> newEntries){
        entries.addAll(newEntries);
    }
}
