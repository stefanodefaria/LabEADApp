package com.unifei.stefano.lab_ead_app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.unifei.stefano.lab_ead_app.operations.Operation;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.zip.GZIPInputStream;


public class HttpsOperation extends AsyncTask <Void, Void, Void>{

    private Context context;
    private Operation operation;
    private Exception error = null;

    public HttpsOperation(Operation operation) {
        this.context = operation.getTelaExpedidora();
        this.operation = operation;
    }

    public void start(){
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
    }

    private static String convertStreamToString(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];

            try {
                Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"),1024);
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                inputStream.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    private boolean networkIsAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected Void doInBackground(Void... params) {

        if(networkIsAvailable()) {

            int timeOut = 30000; //30 seconds timeout
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeOut);

            try {									//start query 1

                HttpResponse localResponse;
                HttpClient client = new DefaultHttpClient(httpParameters);
                HttpPost post = new HttpPost(operation.getUrl());
                StringEntity se = new StringEntity(operation.getRequest().toString());
                post.setEntity(se);
                localResponse = client.execute(post);

                InputStream instream = new GZIPInputStream(localResponse.getEntity().getContent());

                operation.setResponse(convertStreamToString(instream));
            }
            catch (Exception e) {
                error = e;
            }
        }
        else
        {
            error = new NoInternetException("No internet connection");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        Controller.receberResposta(error, operation);
    }


    public class NoInternetException extends Exception{
        public NoInternetException(){
            super();
        }
        public NoInternetException(String message){
            super(message);
        }
    }

}