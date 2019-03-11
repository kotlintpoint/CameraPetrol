package com.example.admin.myapplication.other;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyAsyncTask extends AsyncTask<String,Void,String> {

    private final Context context;
    private final int requestCode;
    private HashMap<String,String> hashMap;

    ProgressDialog pd;
    public interface AsyncTaskListener{
        void asyncCallBack(int requestCode, String data);
    }
    AsyncTaskListener listener;
    public MyAsyncTask(Context context, int requestCode, AsyncTaskListener listener){
        this.context=context;
        this.requestCode=requestCode;
        this.listener=listener;
    }

    public void setHashMap(HashMap<String, String> hashMap) {
        this.hashMap = hashMap;
    }

    @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=ProgressDialog.show(context,"Wait","Fetching Data From Server");
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client=new OkHttpClient();
            Request.Builder builder=new Request.Builder().url(strings[0]);

            if(hashMap!=null){
                FormBody.Builder bodyBuilder=new FormBody.Builder();
                for (Map.Entry<String, String> entry:hashMap.entrySet()){
                    bodyBuilder.add(entry.getKey(),entry.getValue());
                }
                builder.post(bodyBuilder.build());
            }
            Request request=builder.build();
            Response response= null;
            try {
                response = client.newCall(request).execute();
                String data=response.body().string();
                return data;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            pd.dismiss();
            listener.asyncCallBack(requestCode,data);
            //tvData.setText(data);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }