package com.wan.yalandan.app;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.util.UUID;

public class DownloadFileProcess {

    Context ctx;
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

    public void getWordUriFromApi(String word) {
        // TODO : Check first if it is in DB else { below codes will run
        String filename = String.valueOf(UUID.randomUUID());

        dm = (DownloadManager) ctx.getSystemService(ctx.DOWNLOAD_SERVICE);

        Resources resources = ctx.getResources();
        String fqdnDictionary = resources.getString(R.string.DictionarySite) + "?key=" + resources.getString(R.string.KeyParam) + "&word=" + word;

        DownloadManager.Request request = new
                DownloadManager.Request(Uri.parse(fqdnDictionary));

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, filename);
        enqueue = dm.enqueue(request);
    }

    public  void fileCreateAndAddData(String path, String filemane, String data) {

        File file = new File(path, filemane);
        FileOutputStream fos;

        byte[] buffer = data.getBytes();
        try {
            fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("file_Create Method", "File not found",e);
        } catch (IOException e) {
            Log.e("file_Create Method", "There is an exception in file_Create Method",e);
        }
    }

   public  String readFile(String PathAndfileName) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(PathAndfileName)));
        String read;
        StringBuilder builder = new StringBuilder("");

        while ((read = bufferedReader.readLine()) != null) {
            builder.append(read);
        }
        bufferedReader.close();

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

                        Uri stringUri = Uri.parse(uriString);
                        File uristringsFile = new File(stringUri.toString());

                        String absolutePath = uristringsFile.getAbsolutePath().replace("/file:", "");
                        String xmlsFilesPath = ctx.getApplicationInfo().dataDir + "/xmls";
                        String[] seperateToPath = uristringsFile.getAbsolutePath().split("/");
                        String fileName = seperateToPath[seperateToPath.length - 1];

                        try {
                            String XmlContent = readFile(absolutePath);
                            fileCreateAndAddData(xmlsFilesPath, fileName, XmlContent);

                            File file = new File(absolutePath);
                            boolean deleted = file.delete();
                            if (deleted) Log.d("Delete File", "File was deleted succesfully");
                            else Log.d("Delete File", "File was not deleted");
                            callback.callback(xmlsFilesPath +"/" + fileName);
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
