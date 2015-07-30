package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emre.can on 7/28/2015.
 */
public class Word {
    // TODO - use properties which is more usable
    private String headWord;
    private String functionalLabel;
    private String meaningCore;
    private String illustrativeSentence;
    private List<String> synonymList;
    private List<String> relatedWords;
    private List<String> antonyms;

    public String getHeadWord() {
        return headWord;
    }

    public String getFunctionalLabel() {
        return functionalLabel;
    }

    public String getMeaningCore() {
        return meaningCore;
    }

    public List<String> getSynonymList() {
        return synonymList;
    }

    public String getIllustrativeSentence() {
        return illustrativeSentence;
    }

    public List<String> getRelatedWords() {
        return relatedWords;
    }

    public List<String> getAntonyms() {
        return antonyms;
    }

    public Word(String headWord, String functionalLabel, String meaningCore, String illustrativeSentence, List<String> synonymList, List<String> relatedWords, List<String> antonyms) {
        this.headWord = headWord;
        this.functionalLabel = functionalLabel;
        this.meaningCore = meaningCore;
        this.illustrativeSentence = illustrativeSentence;
        this.synonymList = synonymList;
        this.relatedWords = relatedWords;
        this.antonyms = antonyms;
    }
}
