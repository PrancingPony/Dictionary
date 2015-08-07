package com.wan.yalandan.app.activity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.wan.yalandan.app.R;
import com.wan.yalandan.app.data.DataStore;
import com.wan.yalandan.app.model.Word;
import com.wan.yalandan.app.util.DownloadFileProcess;
import com.wan.yalandan.app.util.WordUri;
import com.wan.yalandan.app.util.XmlParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AddWordsActivity extends Activity {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy   hh:mm:ss");
    private Button btnAddWord;
    private EditText edtAddText;
    private ListView listOfAddedWords;
    private ProgressBar progressBarWordLoading;
    private DataStore dataStore;
    private XmlParser parser;
    private List<WordUri> userWordList = new ArrayList<>();
    private DownloadFileProcess downloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addwords);
        init();
        btnAddWord.setOnClickListener(v -> {
            updateUI(true);
            String word = edtAddText.getText().toString();
            for (WordUri w : userWordList) {
                if (w.word.equals(word)) {
                    Toast.makeText(this, "Word already added.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            downloader.getWordUriFromApi(edtAddText.getText().toString());
        });
    }

    private void init() {
        btnAddWord = (Button) findViewById(R.id.btnAddWord);
        edtAddText = (EditText) findViewById(R.id.edtAddText);
        listOfAddedWords = (ListView) findViewById(R.id.wordList);
        progressBarWordLoading = (ProgressBar) findViewById(R.id.progressBarAddWords);
        parser = new XmlParser();
        dataStore = new DataStore(this);
        DownloadFileProcess.ICallbackUri fileDownloadedCallback = new DownloadFileProcess.ICallbackUri() {
            @Override
            public void onSuccess(String uri) {
                Word word = parser.getWordData(uri);
                if (word == null) {
                    dataStore.delete(uri);
                    Toast.makeText(getBaseContext(), "Meaning of the word not found", Toast.LENGTH_SHORT).show();
                } else {
                    Cursor c = dataStore.getUri(word.getHeadWord(), DataStore.ListName.USER_DEFINED);
                    if (c.moveToFirst()) {
                        WordUri wordUri = new WordUri(c.getString(c.getColumnIndex(DataStore.TOKENWORDS_WORD)),
                                c.getString(c.getColumnIndex(DataStore.TOKENWORDS_URI)),
                                c.getLong(c.getColumnIndex(DataStore.TOKENWORDS_DATE)));
                        userWordList.add(0, wordUri);
                    }
                }
                updateUI(false);
            }
            @Override
            public void onFail(String word) {
            }
        };
        downloader = new DownloadFileProcess(fileDownloadedCallback, getBaseContext(), DataStore.ListName.USER_DEFINED);
        Cursor c = dataStore.getWord(DataStore.ListName.USER_DEFINED);
        while (c.moveToNext()) {
            WordUri wordUri = new WordUri(c.getString(c.getColumnIndex(DataStore.TOKENWORDS_WORD)),
                    c.getString(c.getColumnIndex(DataStore.TOKENWORDS_URI)),
                    c.getLong(c.getColumnIndex(DataStore.TOKENWORDS_DATE)));
            userWordList.add(wordUri);
        }
        c.close();
        listOfAddedWords.setAdapter(new MobileArrayAdapter(this, userWordList));
    }

    private void updateUI(boolean state) {
        if (state) {
            btnAddWord.setVisibility(View.GONE);
            progressBarWordLoading.setVisibility(View.VISIBLE);
        } else {
            btnAddWord.setVisibility(View.VISIBLE);
            progressBarWordLoading.setVisibility(View.GONE);
            listOfAddedWords.setAdapter(new MobileArrayAdapter(this, userWordList));
        }
    }

    public class MobileArrayAdapter extends ArrayAdapter<WordUri> {
        private final Context context;
        private final List<WordUri> wordUris;

        public MobileArrayAdapter(Context context, List<WordUri> wordUris) {
            super(context, R.layout.add_words_listview_item_layout, wordUris);
            this.context = context;
            this.wordUris = wordUris;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.add_words_listview_item_layout, parent, false);
            TextView wordView = (TextView) rowView.findViewById(R.id.tvListViewWord);
            TextView dateView = (TextView) rowView.findViewById(R.id.tvListViewDate);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(wordUris.get(position).date);
            wordView.setText(wordUris.get(position).word);
            dateView.setText(simpleDateFormat.format(c.getTime()));
            return rowView;
        }
    }
}

