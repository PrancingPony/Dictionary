package com.wan.yalandan.app.util;

import android.content.Context;
import android.util.Log;
import com.wan.yalandan.app.model.Word;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by emre.can on 7/30/2015.
 */
public class XmlParser {
    private static final String LOG_TAG = XmlParser.class.getCanonicalName();
    private final String TAG_HEADWORD = "hw";
    private final String TAG_FUNCTIONAL_LABEL = "fl";
    private final String TAG_MEANING_CORE = "mc";
    private final String TAG_ILLUSTRATIVE_SENTENCE = "vi";
    private final String TAG_SYNONYM = "syn";
    private final String TAG_RELATED_WORDS = "rel";
    private final String TAG_ANTONYM = "ant";
    private final String TAG_SENSE = "sens";
    private final String TAG_IT = "it";
    private Context context=null;

    public XmlParser(Context context) {
        this.context=context;
    }

    public Word getWordData(String uri) {
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();
            InputStream inStream = context.getAssets().open(uri);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inStream, null);
            return parseXML(parser);

        } catch (XmlPullParserException e) {
            //TODO : clarify log message
            Log.e(LOG_TAG, "XmlPull cant provide stream", e);
        } catch (IOException e) {
            //TODO : clarify log message
            Log.e(LOG_TAG, "?ts Technical Problem You Do Nothing, ?f you have good ass, try IOException", e);
        }
        return null;
    }

    private Word parseXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        HashMap<String, String> hmWordFeature = new HashMap<>();
        List<String> tags = new ArrayList<>(2);
        String currentTag = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;

                case XmlPullParser.START_TAG:
                    if (TAG_IT.equals(parser.getName())) {
                        parser.next();
                        StringBuilder appendedText = new StringBuilder(parser.getText());
                        parser.next();
                        parser.next();
                        appendedText.append(parser.getText());
                        hmWordFeature.put(currentTag, hmWordFeature.get(currentTag) + appendedText);
                    }
                    currentTag = parser.getName();
                    tags.add(parser.getName());
                    eventType = parser.next();
                    continue;

                case XmlPullParser.TEXT:
                    DetermineTagsGetContents(parser, currentTag, hmWordFeature, tags);
                    break;

                case XmlPullParser.END_TAG:
                    if (TAG_SENSE.equals(parser.getName())) {
                        return new Word(hmWordFeature.get(TAG_HEADWORD), hmWordFeature.get(TAG_FUNCTIONAL_LABEL), hmWordFeature.get(TAG_MEANING_CORE), hmWordFeature.get(TAG_ILLUSTRATIVE_SENTENCE), SplitTrimString(hmWordFeature.get(TAG_SYNONYM)), SplitTrimString(hmWordFeature.get(TAG_RELATED_WORDS)), SplitTrimString(hmWordFeature.get(TAG_ANTONYM)));
                    }
                    break;
            }
            eventType = parser.next();
        }
        return null;
    }

    public void DetermineTagsGetContents(XmlPullParser parser, String currentTag, HashMap<String, String> hmWordFeature, List<String> tags) throws IOException, XmlPullParserException {

        switch (currentTag) {
            case TAG_HEADWORD:
                hmWordFeature.put(TAG_HEADWORD, parser.getText());
                break;
            case TAG_FUNCTIONAL_LABEL:
                hmWordFeature.put(TAG_FUNCTIONAL_LABEL, parser.getText());
                break;
            case TAG_MEANING_CORE:
                hmWordFeature.put(TAG_MEANING_CORE, parser.getText());
                break;
            case TAG_ILLUSTRATIVE_SENTENCE:
                hmWordFeature.put(TAG_ILLUSTRATIVE_SENTENCE, parser.getText());
                break;
            case TAG_SYNONYM:
                hmWordFeature.put(TAG_SYNONYM, parser.getText());
                break;
            case TAG_RELATED_WORDS:
                hmWordFeature.put(TAG_RELATED_WORDS, parser.getText());
                break;
            case TAG_ANTONYM:
                hmWordFeature.put(TAG_ANTONYM, parser.getText());
                break;
        }
    }

    public List<String> SplitTrimString(String listString) {
        List<String> words = Arrays.asList(listString.split(","));
        List<String> trimmedWords = new ArrayList<>();
        for (String currentWord : words) {
            trimmedWords.add(currentWord.trim());
        }
        return trimmedWords;
    }
}
