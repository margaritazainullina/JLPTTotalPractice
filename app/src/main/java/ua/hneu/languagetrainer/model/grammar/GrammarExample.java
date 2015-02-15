package ua.hneu.languagetrainer.model.grammar;

import ua.hneu.languagetrainer.model.ExampleAbstr;

public class GrammarExample extends ExampleAbstr {

	public GrammarExample(String text, String romaji, String translationEng,
			String translationRus) {
		super();
		super.setText(text);
        super.setRomaji(romaji);
        super.setTranslationEng(translationEng);
        super.setTranslationRus(translationRus);
	}

}
