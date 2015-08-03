package com.wan.yalandan.app;

import android.content.Context;
import android.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DictionaryReader {
    private final static String LOG_TAG = DictionaryReader.class.getCanonicalName();
    public List<String> wordList;

    DictionaryReader(int resourcesId, Context ctx) {
        try {
            getWords(ctx, resourcesId);
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "Given resource id is invalid,skipping");
        }
    }

    private void getWords(Context ctx, int resourcesId) throws FileNotFoundException {
        Log.i(LOG_TAG, "File read process beginning");
        InputStream inputStream = ctx.getResources().openRawResource(resourcesId);
        InputStreamReader inputReader = new InputStreamReader(inputStream);
        BufferedReader buffer = new BufferedReader(inputReader);
        String line;

        wordList = new ArrayList<>();
        try {
            while ((line = buffer.readLine()) != null) {
                if (line.contains("'") || line.length() < 2) continue;
                wordList.add(line);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "File reader process has failed");
        } finally {
            Log.i(LOG_TAG, "File read process is done");
        }
    }

    public List<String> getRandomWords(int count) {
        List<String> randomWords = new ArrayList<>(4);
        Random rnd = new Random();
        int size = wordList.size();
        while (randomWords.size() < count) {
            int a = Math.abs(rnd.nextInt() % size);
            if (!randomWords.contains(wordList.get(a)))
                randomWords.add(wordList.get(a));
        }

        return randomWords;
    }
}
