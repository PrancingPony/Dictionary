package com.wan.yalandan.app;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by abdulkadir.karabas on 27.07.2015.
 */
public class DictionaryReader {
    public int resourcesid;
    public List<String> wordlist;

    DictionaryReader(int resourcesid) {
        this.resourcesid = resourcesid;
    }

    public void getWords(InputStream _inputStream) throws FileNotFoundException {
        InputStream inputStream = _inputStream;
        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;

        wordlist = new ArrayList<String>();
        try {
            while ((line = buffreader.readLine()) != null) {
                if(line.length()<2)continue;
                if(line.contains("'"))continue;
                wordlist.add(line);
            }
        } catch (IOException e) {
            Log.e("Dosya okuma hatas?","DictionaryReader");
            return;
        }
    }

    public List<String> get4RandomWord() {
        List<String> randwords = new ArrayList<String>();

        Random rnd = new Random();
        int size = wordlist.size();
        while (randwords.size() < 4) {

            int a = Math.abs(rnd.nextInt()%size);
            if (!randwords.contains(wordlist.get(a)))
                randwords.add(wordlist.get(a));

        }

        return randwords;
    }
}
