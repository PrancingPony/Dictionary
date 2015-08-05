package com.wan.yalandan.app.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import com.wan.yalandan.app.R;
import com.wan.yalandan.app.data.DataStore;

import java.io.*;
import java.util.UUID;

public class DownloadFileProcess {

    private final String dictionaryApiURL;
    private long queuedTaskId;
    private Context context;
    private DataStore dbInstance;
    private DownloadManager downloadManager;
    private ICallbackUri successCallback;
    private DownloadStatusReceiver receiver;

    public DownloadFileProcess(ICallbackUri _callback, Context _context) {
        context = _context;
        successCallback = _callback;
        receiver = new DownloadStatusReceiver();
        context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        Resources resources = context.getResources();
        dictionaryApiURL = String.format("%s?key=%s&word=", resources.getString(R.string.DictionarySite), resources.getString(R.string.KeyParam));
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        dbInstance = new DataStore(context);
    }

    public static void createFolder(String path, String folderName) {
        File folder = new File(path + "/" + folderName);

        if (!folder.exists()) {
            boolean success = folder.mkdir();
            if (success) {
                Log.v("Creating Folder Proces", "Folder was created");
            } else {
                Log.v("Creating Folder Proces", "Folder was NOT created");
            }
        } else {
            Log.v("Creating Folder Proces", "Folder is already exist");
        }
    }

    public void getWordUriFromApi(String word) {
        Cursor c = dbInstance.getUri(word);
        if (c.moveToFirst()) {
            successCallback.onSuccess(c.getString(c.getColumnIndex(DataStore.TOKENWORDS_URI)));
            c.close();
        } else {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(dictionaryApiURL + word));

            String filename = String.valueOf(UUID.randomUUID());
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
            queuedTaskId = downloadManager.enqueue(request);
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

    public interface ICallbackUri {
        void onSuccess(String uri);
    }

    private class DownloadStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(queuedTaskId);
                Cursor c = downloadManager.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    Log.d("TEST", "Download result: " + c.getInt(columnIndex));
                    // TODO : handle more states
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                        String absolutePath = uriString.replace("file://", "");
                        String xmlsFilesPath = context.getApplicationInfo().dataDir + "/xmls";
                        String[] seperateToPath = uriString.split("/");
                        String fileName = seperateToPath[seperateToPath.length - 1];
                        String currentUri;
                        try {
                            // TODO : transfer file instead reading the data inside and recreate
                            String XmlContent = readFile(absolutePath);
                            createFileAndAddData(xmlsFilesPath, fileName, XmlContent);

                            String fqdnDictionary = c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI));

                            String[] seperateTofqdnDictionary = fqdnDictionary.split("=");

                            String currentWord = seperateTofqdnDictionary[seperateTofqdnDictionary.length - 1];
                            currentUri = xmlsFilesPath + "/" + fileName;

                            if (dbInstance == null)
                                dbInstance = new DataStore(context);

                            dbInstance.insertWord(currentWord, currentUri);

                            Log.d("URI ", currentWord + " > " + currentUri);
                            successCallback.onSuccess(currentUri);
                        } catch (IOException e) {
                            Log.e("readFile Error", "There is an error about IO Exception", e);
                        } finally {
                            c.close();
                        }
                    }
                }
            }
        }

        public void unregisterReceiver() {
            context.unregisterReceiver(receiver);
        }
    }
}
