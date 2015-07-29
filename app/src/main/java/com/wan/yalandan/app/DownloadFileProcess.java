package com.wan.yalandan.app;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.util.UUID;


public class DownloadFileProcess {

    Context ctx;
    private long enqueue;
    private DownloadManager dm;
    private INewInterface mainClass; //for callback
    private DownloadStatusReceiver receiver;

    public DownloadFileProcess(INewInterface mClass, Context _ctx) {
        ctx = _ctx;
        mainClass = mClass;
        receiver = new DownloadStatusReceiver(_ctx);
        ctx.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public String getWordUriFromApi(String word) {
        /*
          if(word in DB)
           return word's Uri from DB
         */
        //else
        {
            String filename = String.valueOf(UUID.randomUUID());

            dm = (DownloadManager) ctx.getSystemService(ctx.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new
                    DownloadManager.Request(Uri.parse("http://www.dictionaryapi.com/api/v1/references/thesaurus/xml/test?key=19cb0d77-2780-4cb1-8015-207cc06d9913&word=" + word/*book"*/));

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, filename);
            enqueue = dm.enqueue(request);
        }
        return "";
    }

    public void file_CreateAndAddData(String path, String filemane, String data) {

        File file = new File(path, filemane);
        FileOutputStream fos;

        byte[] veri = data.getBytes();
        try {
            fos = new FileOutputStream(file);
            fos.write(veri);
            fos.flush();
            fos.close();
            Log.i("file_Create Method", "File was created succesfully");
        } catch (FileNotFoundException e) {
            Log.e("file_Create Method", "File was not found");
        } catch (IOException e) {
            Log.e("file_Create Method", "There is an exception in file_Create Method");
        }
    }

    String readFile(String PathAndfileName) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new FileReader(new
                File(/* getApplicationInfo().dataDir + "/xmls/" +  fileName*/ PathAndfileName)));
        String read;
        StringBuilder builder = new StringBuilder("");

        while ((read = bufferedReader.readLine()) != null) {
            builder.append(read);
        }
        Log.d("Output", builder.toString());
        bufferedReader.close();

        return builder.toString();
    }

    public interface INewInterface {
        void callback(String uri);
    }

    public void unregisterReceiver() {
        receiver.unregisterReceiver();
    }

    private class DownloadStatusReceiver extends BroadcastReceiver {

        private Context context;

        public DownloadStatusReceiver(Context context) {
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

                        Uri myUri = Uri.parse(uriString);
                        File myFile = new File(myUri.toString());
                        Log.d("TEST", "Absolute Path : " + myFile.getAbsolutePath().replace("/file:", ""));

                        String[] seperateToPath = myFile.getAbsolutePath().split("/");//this row is for accessing filename. Because filemane is after last slash(/).
                        String fileName = seperateToPath[seperateToPath.length - 1];//we took the filename
                        Log.d("Split Name Result : ", fileName);

                        try {
                            String strXmlContent = readFile(myFile.getAbsolutePath().replace("/file:", ""));
                            Log.d("readFile's Return Value", strXmlContent);
                            file_CreateAndAddData(ctx.getApplicationInfo().dataDir + "/xmls", fileName, strXmlContent);
                            Log.d("readFile Aim", readFile(ctx.getApplicationInfo().dataDir + "/xmls/" + fileName));

                            File file = new File(myFile.getAbsolutePath().replace("/file:", ""));
                            boolean deleted = file.delete();
                            if (deleted) Log.d("Delete File", "File was deleted succesfully");
                            else Log.d("Delete File", "File was not deleted");
                            mainClass.callback(ctx.getApplicationInfo().dataDir + "/xmls/" + fileName);
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
