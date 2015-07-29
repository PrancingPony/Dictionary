package com.wan.yalandan.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;


public class MainActivity extends Activity {

    Button btnDMTest;
    Button btnCallback;
    DownloadFileProcess dfp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        createFolder(getApplicationInfo().dataDir, "xmls");
        // String value = getDataFromInternet("http://www.dictionaryapi.com/api/v1/references/thesaurus/xml/test?key=19cb0d77-2780-4cb1-8015-207cc06d9913&word=book");
        this.btnDMTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Download Manager Passing callback

                startActivity(new Intent(getBaseContext(), DownManActivity.class));
            }
        });//listener

        final DownloadFileProcess.INewInterface downloadFinishedCallback = new DownloadFileProcess.INewInterface() {
            @Override
            public void callback(String uri) {
                Log.d("CALLED URI", uri);
            }
        };

        this.btnCallback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dfp = new DownloadFileProcess(downloadFinishedCallback, getBaseContext());
                dfp.getWordUriFromApi("book");
            }
        });

    }//Event


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dfp.unregisterReceiver();
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
//Abdülkadir ramiye yada un kapan?na gidelim
        return super.onOptionsItemSelected(item);
    }

    /*    public String getDataFromInternet(String urlstring)
        {
            AsyncTask<String, String, String> task = new RequestTask().execute(urlstring);

            return "";
        }//method    */
    public void init() {
        this.btnDMTest = (Button) findViewById(R.id.btnDMTest);
        this.btnCallback = (Button) findViewById(R.id.btnCallback);
        // Log.d("TEST",getApplicationInfo().dataDir);
    }//method

    public void createFolder(String path, String folderName) {
        File folder = new File(path + "/" + folderName);

        if (!folder.exists()) {
            boolean success = false;
            success = folder.mkdir();
            if (success) {
                Log.v("Creating Folder Proces", "Folder was created");
            } else {
                // Do something else on failure
                Log.v("Creating Folder Proces", "Folder was NOT created");
            }
        } else {
            Log.v("Creating Folder Proces", "Folder is already exist");
        }
    }
}//activity
