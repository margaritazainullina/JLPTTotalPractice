package ua.hneu.languagetrainer.model.other;

import ua.hneu.languagetrainer.model.ExampleAbstr;

public class GiongoExample extends ExampleAbstr {

    public GiongoExample(String text, String romaji, String translationEng,
                         String translationRus) {
        super();
        super.setText(text);
        super.setRomaji(romaji);
        super.setTranslationEng(translationEng);
        super.setTranslationRus(translationRus);
    }

}
