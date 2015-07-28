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
    public Context ctx;
    public List<String> wordlist;

    DictionaryReader(int resourcesid,Context ctx) {
        this.resourcesid = resourcesid;
        this.ctx = ctx;
        try {
            getWords(ctx);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void getWords(Context ctx) throws FileNotFoundException {

        InputStream inputStream = ctx.getResources().openRawResource(resourcesid);
        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;

        wordlist = new ArrayList<>();
        try {
            while ((line = buffreader.readLine()) != null) {
                if(line.contains("'") || line.length()<2)continue;
                wordlist.add(line);
            }
        } catch (IOException e) {
            Log.e("FILE","File reader process is fail");

        }
        finally {
            Log.i("FILE","File read process is done");
        }
    }

    public List<String> get4RandomWord() {

        List<String> randwords = new ArrayList<>(4);
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
