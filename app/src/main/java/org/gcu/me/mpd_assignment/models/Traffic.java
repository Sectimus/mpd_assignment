package org.gcu.me.mpd_assignment.models;

import android.os.AsyncTask;
import android.util.Log;

import org.gcu.me.mpd_assignment.models.georss.Coordinates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;

public abstract class Traffic {
    private String title;
    private String description;
    private String link;
    private Coordinates location;
    private String author;
    private List<String> comments;
    private Date pubDate;

    //asynctasks are always to be used over manual threads in android applications
    public static class LoaderTask extends AsyncTask<String, Double, String> {
        public interface TaskListener {
            void onFinished(String result);
            void onStatusUpdate(Double progress);
        }

        private final TaskListener taskListener;

        public LoaderTask(TaskListener listener) {
            // The listener reference is passed in through the constructor
            this.taskListener = listener;
        }

        @Override
        protected String doInBackground(String... urls) {
            String url = urls[0];
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";

            Log.e("MyTag","in run");
            String result="";
            try
            {
                Log.e("MyTag","in try");
                URL aurl = new URL(url);
                yc = aurl.openConnection();
                //int contentLength = yc.getContentLength();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                //
                // Throw away the first 2 header lines before parsing
                //
                //
                //
                int readBytes = 0;
                int contentLength = yc.getContentLength();
                while ((inputLine = in.readLine()) != null)
                {
                    //setup for progress
                    readBytes += inputLine.getBytes("ISO-8859-2").length+2;

                    if (contentLength != -1) {
                        //calculate the progress percentage and publish
                        double progress = (Double.valueOf(readBytes)/Double.valueOf(contentLength+2))*Double.valueOf(100);
                        publishProgress(progress);
                    } else {
                        //unknown progress = -1
                        publishProgress(Double.valueOf(-1));
                    }


                    result = result + inputLine;
                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception");
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Double... progress){
            this.taskListener.onStatusUpdate(progress[0]);
        }

        @Override
        protected void onPostExecute(String result){
            if(this.taskListener != null) {
                this.taskListener.onFinished(result);
            }
        }
    }

}
