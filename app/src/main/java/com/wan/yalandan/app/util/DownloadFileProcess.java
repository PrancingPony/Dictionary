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
import java.util.*;

public class DownloadFileProcess {

    private final String dictionaryApiURL;
    private Queue<Long> queuedTaskId;
    private Context context;
    private DataStore dbInstance;
    private DownloadManager downloadManager;
    private ICallbackUri successCallback;
    public DownloadStatusReceiver receiver;
    private DataStore.ListName listName;
    public DownloadFileProcess(ICallbackUri _callback, Context _context,DataStore.ListName listName) {
        context = _context;
        successCallback = _callback;
        this.listName = listName;
        receiver = new DownloadStatusReceiver();
        context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        Resources resources = context.getResources();
        dictionaryApiURL = String.format("%s?key=%s&word=", resources.getString(R.string.DictionarySite), resources.getString(R.string.KeyParam));
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        dbInstance = new DataStore(context);
        queuedTaskId = new LinkedList<>();
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
        Cursor c = dbInstance.getUri(word, DataStore.ListName.GENERAL);
        if (c.moveToFirst()) {
            successCallback.onSuccess(c.getString(c.getColumnIndex(DataStore.TOKENWORDS_URI)));
            c.close();
        } else {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(dictionaryApiURL + word));
            String filename = String.valueOf(UUID.randomUUID());
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
            queuedTaskId.add(downloadManager.enqueue(request));
        }
    }

    public void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public interface ICallbackUri {
        void onSuccess(String uri);
        void onFail(String word);
    }

    private class DownloadStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(queuedTaskId.peek());
                Cursor c = downloadManager.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    Log.d("TEST", "Download result: " + c.getInt(columnIndex));

                    String fqdnDictionary = c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI));
                    String[] seperateTofqdnDictionary = fqdnDictionary.split("=");
                    String currentWord = seperateTofqdnDictionary[seperateTofqdnDictionary.length - 1];

                    switch (c.getInt(columnIndex)) {
                        case DownloadManager.STATUS_SUCCESSFUL:
                            queuedTaskId.remove(0);
                            String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            String[] seperateToPath = uriString.split("/");
                            String fileName = seperateToPath[seperateToPath.length - 1];

                            try {
                                String absolutePath = uriString.replace("file://", "");
                                String xmlsFilesPath = context.getApplicationInfo().dataDir + "/xmls";

                                File sourceFile = new File(absolutePath);
                                File destinationFile = new File(xmlsFilesPath, fileName);
                                copyFile(sourceFile, destinationFile);

                                String currentUri = xmlsFilesPath + "/" + fileName;

                                if (dbInstance == null)
                                    dbInstance = new DataStore(context);

                                dbInstance.insertWord(currentWord, currentUri, DataStore.ListName.GENERAL);

                                Log.d("URI ", currentWord + " > " + currentUri);
                                queuedTaskId.poll();
                                successCallback.onSuccess(currentUri);

                            } catch (IOException e) {
                                Log.e("readFile Error", "There is an error about IO Exception", e);
                            } finally {
                                c.close();
                            }
                            break;

                        default:
                            successCallback.onFail(currentWord);
                            break;
                    }
                }
            }
        }

        public void unregisterReceiver() {
            context.unregisterReceiver(receiver);
        }
    }
}
