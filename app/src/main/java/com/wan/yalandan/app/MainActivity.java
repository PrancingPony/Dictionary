package com.wan.yalandan.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import model.Word;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class MainActivity extends Activity {
    private static final String LOG_TAG = "MainActivity";
    private final String TAG_HEADWORD = "hw";
    private final String TAG_FUNCTIONALLABEL = "fl";
    private final String TAG_MEANINGCORE = "mc";
    private final String TAG_ILLUSTRATIVESENTENCE = "vi";
    private final String TAG_SYNONYM = "syn";
    private final String TAG_RELATEDWORDS = "rel";
    private final String TAG_ANTONYM = "ant";
    private final String TAG_SENSE = "sens";
    private final String TAG_IT = "it";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String uri = "corresponding.xml";
        Word word = XmlPullParser(uri);

        Log.d(LOG_TAG, "inComingWord:" + word.getHeadWord() + "FL: " + word.getFunctionalLabel() + "MC: " + word.getMeaningCore() + "V?: " + word.getIllustrativeSentence() + " REL:" + word.getRelatedWords() + "SYN:" + word.getSynonymList() + "ANT:" + word.getAntonyms());
    }

    public Word XmlPullParser(String uri) {
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();
            InputStream inStream = getApplicationContext().getAssets().open(uri);
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
        String previousTag = null;

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

                case XmlPullParser.END_TAG:
                    if (TAG_SENSE.equals(parser.getName())) {
                        return new Word(hmWordFeature.get(TAG_HEADWORD), hmWordFeature.get(TAG_FUNCTIONALLABEL), hmWordFeature.get(TAG_MEANINGCORE), hmWordFeature.get(TAG_ILLUSTRATIVESENTENCE), SplitTrimString(hmWordFeature.get(TAG_SYNONYM)), SplitTrimString(hmWordFeature.get(TAG_RELATEDWORDS)), SplitTrimString(hmWordFeature.get(TAG_ANTONYM)));
                    }
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
            case TAG_FUNCTIONALLABEL:
                hmWordFeature.put(TAG_FUNCTIONALLABEL, parser.getText());
                break;
            case TAG_MEANINGCORE:
                hmWordFeature.put(TAG_MEANINGCORE, parser.getText());
                break;
            case TAG_ILLUSTRATIVESENTENCE:
                hmWordFeature.put(TAG_ILLUSTRATIVESENTENCE, parser.getText());
                break;
            case TAG_SYNONYM:
                hmWordFeature.put(TAG_SYNONYM, parser.getText());
                break;
            case TAG_RELATEDWORDS:
                hmWordFeature.put(TAG_RELATEDWORDS, parser.getText());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
