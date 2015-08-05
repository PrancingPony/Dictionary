package com.wan.yalandan.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import com.wan.yalandan.app.R;
import com.wan.yalandan.app.model.Word;
import com.wan.yalandan.app.util.DictionaryReader;
import com.wan.yalandan.app.util.DownloadFileProcess;
import com.wan.yalandan.app.util.XmlParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {
    private final static int NUMBER_OF_OPTIONS = 4;

    private List<RadioButton> radioButtons = new ArrayList<>(NUMBER_OF_OPTIONS);
    private ArrayList<Word> words = new ArrayList<>(NUMBER_OF_OPTIONS);
    private int indexOfCorrectAnswer;
    private ProgressBar progressBar;
    private Button btnAnswer;
    private TextView tv;
    private DictionaryReader dr;
    private DownloadFileProcess downloader;
    private XmlParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        btnAnswer.setOnClickListener(v -> {
            btnAnswer.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            // TODO : handle correct answer selection
            indexOfCorrectAnswer = Math.abs(new Random().nextInt() % NUMBER_OF_OPTIONS);
            List<String> randomWords = dr.getRandomWords(NUMBER_OF_OPTIONS);
            for (String word : randomWords) {
                downloader.getWordUriFromApi(word);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void init() {
        btnAnswer = (Button) findViewById(R.id.btnAnswer);
        tv = (TextView) findViewById(R.id.textView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        dr = new DictionaryReader(R.raw.american_english, this);
        parser = new XmlParser();
        DownloadFileProcess.createFolder(getApplicationInfo().dataDir, "xmls");

        DownloadFileProcess.ICallbackUri fileDownloadedCallback = uri -> {
            Word result = parser.getWordData(uri);
            if (result == null) {
                requestNewWord();
            } else {
                if (words.contains(result)) {
                    requestNewWord();
                } else {
                    words.add(result);
                    if (words.size() == NUMBER_OF_OPTIONS) {
                        updateUI();
                    }
                }
            }
        };

        downloader = new DownloadFileProcess(fileDownloadedCallback, getBaseContext());

        radioButtons.add((RadioButton) findViewById(R.id.radioButton));
        radioButtons.add((RadioButton) findViewById(R.id.radioButton2));
        radioButtons.add((RadioButton) findViewById(R.id.radioButton3));
        radioButtons.add((RadioButton) findViewById(R.id.radioButton4));
    }

    private void requestNewWord() {
        String newWord = dr.getRandomWords(1).get(0);
        downloader.getWordUriFromApi(newWord);
    }

    private void updateUI() {
        for (int i = 0; i < NUMBER_OF_OPTIONS; i++) {
            radioButtons.get(i).setText(words.get(i).getMeaningCore());
        }
    }

}

