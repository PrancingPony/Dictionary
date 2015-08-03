package com.wan.yalandan.app.util;

import android.content.Context;
import android.util.Log;
import com.wan.yalandan.app.model.Word;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
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
    private Context context = null;

    public XmlParser(Context context) {
        this.context = context;
    }

    public Word getWordData(String uri) {
        XmlPullParserFactory pullParserFactory;
        InputStream inStream=null;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();
            Log.d(LOG_TAG, "Uri:" + uri);

           File file = new File(uri);
           inStream= new FileInputStream(file);

            //  File file=context.getFileStreamPath(uri);
            //  inStream=new FileInputStream(file);
            Log.d(LOG_TAG, "instreambitti:");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inStream, null);
            return parseXML(parser);

        } catch (XmlPullParserException e) {
            //TODO : clarify log message
            Log.e(LOG_TAG, "xml pull cant provide stream", e);
        } catch (IOException e) {
            //TODO : clarify log message
            Log.e(LOG_TAG, "wrong input,?o exception", e);
        } finally {
            if(inStream!=null){
                try {
                    inStream.close();
                } catch (IOException e) {
                  Log.e(LOG_TAG,"when stream openning occured error"+  e);
                }
            }

        }
        return null;
    }

    private Word parseXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        HashMap<String, String> tagTextMap = new HashMap<>();
        String currentTag = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (TAG_IT.equals(parser.getName())) {
                        parser.next();
                        StringBuilder appendedText = new StringBuilder(parser.getText());
                        parser.next();
                        parser.next();
                        appendedText.append(tagTextMap.get(currentTag));
                        appendedText.append(parser.getText());
                        tagTextMap.put(currentTag, appendedText.toString());
                    }
                    currentTag = parser.getName();
                    break;
                case XmlPullParser.TEXT:
                    determineTagsAndGetContents(parser, currentTag, tagTextMap);
                    break;
                case XmlPullParser.END_TAG:
                    if (TAG_SENSE.equals(parser.getName())) {
                        return new Word(tagTextMap.get(TAG_HEADWORD),
                                tagTextMap.get(TAG_FUNCTIONAL_LABEL),
                                tagTextMap.get(TAG_MEANING_CORE),
                                tagTextMap.get(TAG_ILLUSTRATIVE_SENTENCE),
                                splitTrimString(tagTextMap.get(TAG_SYNONYM)),
                                splitTrimString(tagTextMap.get(TAG_RELATED_WORDS)),
                                splitTrimString(tagTextMap.get(TAG_ANTONYM)));
                    }
                    break;
            }
            eventType = parser.next();
        }
        return null;
    }

    public void determineTagsAndGetContents(XmlPullParser parser, String currentTag, HashMap<String, String> tagTextMap) throws IOException, XmlPullParserException {
        switch (currentTag) {
            case TAG_HEADWORD:
                tagTextMap.put(TAG_HEADWORD, parser.getText());
                break;
            case TAG_FUNCTIONAL_LABEL:
                tagTextMap.put(TAG_FUNCTIONAL_LABEL, parser.getText());
                break;
            case TAG_MEANING_CORE:
                tagTextMap.put(TAG_MEANING_CORE, parser.getText());
                break;
            case TAG_ILLUSTRATIVE_SENTENCE:
                tagTextMap.put(TAG_ILLUSTRATIVE_SENTENCE, parser.getText());
                break;
            case TAG_SYNONYM:
                tagTextMap.put(TAG_SYNONYM, parser.getText());
                break;
            case TAG_RELATED_WORDS:
                tagTextMap.put(TAG_RELATED_WORDS, parser.getText());
                break;
            case TAG_ANTONYM:
                tagTextMap.put(TAG_ANTONYM, parser.getText());
                break;
        }
    }

    public List<String> splitTrimString(String listString) {
        if (listString != null) {
            List<String> words = Arrays.asList(listString.split(","));
            List<String> trimmedWords = new ArrayList<>();
            for (String currentWord : words) {
                trimmedWords.add(currentWord.trim());
            }
            return trimmedWords;
        }
        return new ArrayList<>();
    }
}
