package org.gcu.me.mpd_assignment.repositories;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import org.gcu.me.mpd_assignment.models.Roadworks;
import org.gcu.me.mpd_assignment.models.Traffic;
import org.gcu.me.mpd_assignment.models.georss.Point;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

public class TrafficRepo {
    private static List < Traffic > trafficCache;
    public static boolean isLoaded(){
        if (trafficCache != null){
            return true;
        } else{
            return false;
        }
    }

    //gets all traffic
    public static class BuilderTask extends AsyncTask < String, Double, List < Traffic >> {
        private static int currentLines=0;
        private static int maxLines=0;
        private final Boolean force;
        private final TaskListener taskListener;
        public interface TaskListener {
            void onFinished(List < Traffic > result);
            void onDownloadProgress(Double progress);
            void onBuildProgress(Double progress);
        }

        public BuilderTask(TaskListener listener, Boolean force) {
            // The listener reference is passed in through the constructor
            this.taskListener = listener;
            this.force = force;
        }

        //function used to increment the pullparser and publish the progress
        private int incrementPullParser(int eventType, XmlPullParser xpp) throws XmlPullParserException, IOException {
            if (eventType == XmlPullParser.TEXT && xpp.isWhitespace()){
                //used for counting the progress
                currentLines++;
                publishProgress((double)(currentLines/maxLines)*100);
                return xpp.next();
            }
            return eventType;
        }

        @Override
        protected List < Traffic > doInBackground(String...strings) {
            //if a force refresh is chosen and the cache is already loaded
            if (trafficCache != null && this.force){
                trafficCache = null;
            } else if(trafficCache != null){
                this.taskListener.onFinished(trafficCache);
            } else{
                //TaskListener for remote RSS data retrieval
                LoaderTask.TaskListener t = new LoaderTask.TaskListener() {
                    @Override
                    public void onFinished(String dataAsXML) {
                        //when the data has been loaded, parse the text and build the models.
                        trafficCache = new LinkedList < Traffic > ();

                        maxLines = dataAsXML.split("\n").length-2;
                        //parse the text
                        try {
                            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                            factory.setNamespaceAware(true);
                            XmlPullParser xpp = factory.newPullParser();
                            xpp.setInput(new StringReader(dataAsXML));

                            int eventType = xpp.getEventType();
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                if (eventType == XmlPullParser.START_TAG) {
                                    switch (xpp.getName().toLowerCase()) {
                                        case "channel":
                                        {
                                            // Get the next inner event
                                            eventType = incrementPullParser(eventType, xpp);
                                            if (eventType == XmlPullParser.TEXT && xpp.isWhitespace()){
                                                //used for counting the progress
                                                currentLines++;
                                                publishProgress((double)(currentLines/maxLines)*100);
                                            }

                                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                                if (eventType == XmlPullParser.START_TAG) {
                                                    switch (xpp.getName().toLowerCase()) {
                                                        case "item":
                                                        {
                                                            Roadworks r = new Roadworks();
                                                            //if it is not the end of this tag
//                                                        while (!(eventType == XmlPullParser.END_TAG && xpp.getName().toLowerCase().equals("item"))) {
                                                            while (!(eventType == XmlPullParser.END_TAG && xpp.getName().toLowerCase().equals("item"))) {
                                                                if (eventType == XmlPullParser.START_TAG) {
                                                                    switch (xpp.getName().toLowerCase()) {
                                                                        case "title": {
                                                                            r.setTitle(xpp.nextText());
                                                                            break;
                                                                        }
                                                                        case "description": {
                                                                            r.setDescription(xpp.nextText());
                                                                            break;
                                                                        }
                                                                        case "link": {
                                                                            r.setLink(xpp.nextText());
                                                                            break;
                                                                        }
                                                                        case "point": {
                                                                            //split into latlon
                                                                            String raw = xpp.nextText();
                                                                            String[] rawSplit = raw.split(" ");

                                                                            Point point = new Point(Double.parseDouble(rawSplit[0]), Double.parseDouble(rawSplit[1]));
                                                                            r.setLocation(point);
                                                                            break;
                                                                        }
                                                                        case "author": {
                                                                            r.setAuthor(xpp.nextText());
                                                                            break;
                                                                        }
                                                                        case "comments": {
                                                                            // TODO comments as list
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                                eventType = xpp.next();
                                                                eventType = incrementPullParser(eventType, xpp);
                                                            }
                                                            trafficCache.add(r);
                                                            // Get the next event
                                                            break;
                                                        }
                                                    }
                                                }
                                                eventType = xpp.next();
                                                eventType = incrementPullParser(eventType, xpp);
                                            }
                                            break;
                                        }
                                    }
                                }
                                eventType = xpp.next();
                                eventType = incrementPullParser(eventType, xpp);
                            } // End of while
                            if (taskListener != null) {
                                taskListener.onFinished(trafficCache);
                            }
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onDownloadProgress(Double progress) {
                        taskListener.onDownloadProgress(progress);
                    }
                };
                LoaderTask loader = new LoaderTask(t);

                loader.execute(strings[0]);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Double...progress) {
            this.taskListener.onBuildProgress(progress[0]);
        }
    }
    //asynctasks are always to be used over manual threads in android applications
    private static class LoaderTask extends AsyncTask < String, Double, String > {
        private final TaskListener taskListener;
        public interface TaskListener {
            void onFinished(String result);
            void onDownloadProgress(Double progress);
        }

        public LoaderTask(TaskListener listener) {
            // The listener reference is passed in through the constructor
            this.taskListener = listener;
        }

        @Override
        protected String doInBackground(String...urls) {
            String url = urls[0];
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";

            Log.e("MyTag", "in run");
            String result = "";
            try {
                Log.e("MyTag", "in try");
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
                while ((inputLine = in.readLine()) != null) {
                    //setup for progress
                    readBytes += inputLine.getBytes("ISO-8859-2").length + 2;

                    if (contentLength != -1) {
                        //calculate the progress percentage and publish
                        double progress = (Double.valueOf(readBytes) / Double.valueOf(contentLength + 2)) * Double.valueOf(100);
                        publishProgress(progress);
                    } else {
                        //unknown progress = -1
                        publishProgress(Double.valueOf(-1));
                    }
                    result = result + inputLine+"\n";
                } in .close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception");
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Double...progress) {
            this.taskListener.onDownloadProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (this.taskListener != null) {
                this.taskListener.onFinished(result);
            }
        }
    }
}