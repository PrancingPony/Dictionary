package com.wan.yalandan.app.activity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.wan.yalandan.app.R;
import com.wan.yalandan.app.data.DataStore;
import com.wan.yalandan.app.model.Word;
import com.wan.yalandan.app.util.WordUri;
import com.wan.yalandan.app.util.XmlParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyWordsActivity extends Activity {
    private XmlParser parser;
    private Random random;
    private TextView tvWord, tvMean;
    private List<WordUri> userWordList;
    private int indexOfCurrentWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mywords);
        parser = new XmlParser();
        random = new Random();
        indexOfCurrentWord = 0;
        tvWord = (TextView) findViewById(R.id.tvMyWordsWord);
        tvMean = (TextView) findViewById(R.id.tvMyWordsMeaning);
        Button btnNextWord = (Button) findViewById(R.id.btnMyWordsNextWord);
        Button btnShowMean = (Button) findViewById(R.id.btnMyWordsShowMeaning);
        btnNextWord.setOnClickListener(v -> nextWord());
        btnShowMean.setOnClickListener(v -> showMeaning());
    }

    @Override
    protected void onResume() {
        super.onResume();
        userWordList = new ArrayList<>();
        DataStore dataStore = new DataStore(this);
        Cursor cursor = dataStore.getWord(DataStore.ListName.USER_DEFINED);
        while (cursor.moveToNext()) {
            WordUri wordUri = new WordUri(cursor.getString(cursor.getColumnIndex(DataStore.TOKENWORDS_WORD)),
                    cursor.getString(cursor.getColumnIndex(DataStore.TOKENWORDS_URI)),
                    cursor.getLong(cursor.getColumnIndex(DataStore.TOKENWORDS_DATE)));
            userWordList.add(wordUri);
        }
        cursor.close();

        if(userWordList.isEmpty())
        {
            Toast.makeText(getBaseContext(),"Your own word list has not any word",Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        nextWord();
    }

    private int selectRandomWord() {
        return Math.abs(random.nextInt()) % userWordList.size();
    }
    private void nextWord() {
        indexOfCurrentWord = selectRandomWord();
        tvWord.setText(userWordList.get(indexOfCurrentWord).word);
        tvMean.setText("");
    }

    private void showMeaning() {
        Word word = parser.getWordData(userWordList.get(indexOfCurrentWord).uri);
        tvMean.setText(word.getMeaningCore());
    }

    @Override
    protected void onPause() {
        super.onPause();
        userWordList.clear();
    }
}
