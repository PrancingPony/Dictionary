package com.wan.yalandan.app;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.util.UUID;

public class DownloadFileProcess {

    Context ctx;
    DatabaseAdapter dbAdapter;
    private long enqueue;
    private DownloadManager dm;
    private ICallbackUri callback;
    private DownloadStatusReceiver receiver;

    public DownloadFileProcess(ICallbackUri _callback, Context _ctx) {
        ctx = _ctx;
        callback = _callback;
        receiver = new DownloadStatusReceiver(_ctx);
        // FIXME : Make broadcast receiver global
        ctx.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public static void createFolder(String path, String folderName) {
        File folder = new File(path + "/" + folderName);

        if (!folder.exists()) {
            boolean success = false;
            success = folder.mkdir();
            if (success) {
                Log.v("Creating Folder Proces", "Folder was created");
            } else {
                Log.v("Creating Folder Proces", "Folder was NOT created");
            }
        } else {
            Log.v("Creating Folder Proces", "Folder is already exist");
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void getWordUriFromApi(String word) {

        if (dbAdapter == null)
            dbAdapter = new DatabaseAdapter(ctx);
        Cursor c = dbAdapter.getUri(word);
        if (c.moveToFirst()) {
            callback.callback(c.getString(c.getColumnIndex(DatabaseAdapter.TOKENWORDS_URI)));
        }
        else {
            String filename = String.valueOf(UUID.randomUUID());
            dm = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);

            Resources resources = ctx.getResources();
            String fqdnDictionary = String.format("%s?key=%s&word=%s", resources.getString(R.string.DictionarySite), resources.getString(R.string.KeyParam), word);

            DownloadManager.Request request = new
                    DownloadManager.Request(Uri.parse(fqdnDictionary));

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, filename);
            enqueue = dm.enqueue(request);
        }
    }

    public void createFileAndAddData(String path, String fileName, String data) {

        File file = new File(path, fileName);
        FileOutputStream fos;

        byte[] buffer = data.getBytes();
        try {
            fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("file_Create Method", "File not found", e);
        } catch (IOException e) {
            Log.e("file_Create Method", "There is an exception in file_Create Method", e);
        }
    }

    public String readFile(String pathAndFileName) throws IOException {

        Log.d("FILEPATH", pathAndFileName);
        File file = new File(pathAndFileName);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String read;
        StringBuilder builder = new StringBuilder("");

        while ((read = bufferedReader.readLine()) != null) {
            builder.append(read);
        }
        bufferedReader.close();
        fileReader.close();


        return builder.toString();
    }

    public void unregisterReceiver() {
        receiver.unregisterReceiver();
    }

    public interface ICallbackUri {
        void callback(String uri);
    }

    private class DownloadStatusReceiver extends BroadcastReceiver {

        private Context context;

        public DownloadStatusReceiver(Context context) {
            super();
            this.context = context;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(enqueue);
                Cursor c = dm.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                        String absolutePath = uriString.replace("file://", "");
                        String xmlsFilesPath = ctx.getApplicationInfo().dataDir + "/xmls";
                        String[] seperateToPath = uriString.split("/");
                        String fileName = seperateToPath[seperateToPath.length - 1];
                        try {
                            String XmlContent = readFile(absolutePath);
                            createFileAndAddData(xmlsFilesPath, fileName, XmlContent);

                            String fqdnDictionary = c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI));
                            String[] seperateTofqdnDictionary = fqdnDictionary.split("=");

                            String currentWord = seperateTofqdnDictionary[seperateTofqdnDictionary.length - 1];
                            String currentUri = xmlsFilesPath + "/" + fileName;

                            if (dbAdapter == null)
                                dbAdapter = new DatabaseAdapter(ctx);

                            dbAdapter.insertWord(currentWord, currentUri);

                            callback.callback(currentUri);
                        } catch (IOException e) {
                            Log.e("readFile Error", "There is an error about IO Exception", e);
                        }
                    }
                }
            }
        }

        public void unregisterReceiver() {
            ctx.unregisterReceiver(receiver);
        }
    }
}
