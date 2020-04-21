/*Amelia Magee | S1828146*/


package org.gcu.me.mpd_assignment.repositories;

import android.os.AsyncTask;
import android.util.Log;

import org.gcu.me.mpd_assignment.models.Incident;
import org.gcu.me.mpd_assignment.models.PlannedRoadworks;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TrafficRepo {
    private static List <Traffic> trafficCache;
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
                return null;
            }
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

                                        //initialize the array as planned roadworks, this will be updated once the initial link has been found.
                                        Class<?> trafficType = PlannedRoadworks.class;
                                        while (eventType != XmlPullParser.END_DOCUMENT) {
                                            if (eventType == XmlPullParser.START_TAG) {
                                                switch (xpp.getName().toLowerCase()) {
                                                    case "link":{
                                                        String link = xpp.nextText();
                                                        switch(link){
                                                            case "https://trafficscotland.org/roadworks/":{
                                                                trafficType = Roadworks.class;
                                                                break;
                                                            }
                                                            case "https://trafficscotland.org/plannedroadworks/":{
                                                                trafficType = PlannedRoadworks.class;
                                                                break;
                                                            }
                                                            case "https://trafficscotland.org/currentincidents/":{
                                                                trafficType = Incident.class;
                                                                break;
                                                            }
                                                        }
                                                        break;
                                                    }
                                                    case "item":
                                                    {
                                                        Traffic r;
                                                        if(trafficType == PlannedRoadworks.class){
                                                            r = new PlannedRoadworks();
                                                        } else if(trafficType == Roadworks.class){
                                                            r = new Roadworks();
                                                        } else if(trafficType == Incident.class){
                                                            r = new Incident();
                                                        } else{
                                                            //default to planned roadworks
                                                            r = new PlannedRoadworks();
                                                        }
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
                                                                        String description = xpp.nextText();
                                                                        /*split the text using regex to check for the breaks, this is a regex i created to not only check for
                                                                         the typical <br /> that can be seen in the datasets, but also <br/>, <br / >, < br / > etc.
                                                                         This gives the application some stability should the format change slightly in the data feeds over time
                                                                         */
                                                                        String[] brsections = description.split("\\<\\s*br\\s*/?\\s*>");

                                                                        //get the data from the encoded string using colons
                                                                        for (String brsection : brsections) {
                                                                            String[] keyvalues = brsection.split(":", 2);

                                                                            /*check if there is no alternative info to get from the description, sometimes (mostly in current incidents)
                                                                            there is only a description, instead of the predesignated format of two break tags and CRLF seperated sections*/
                                                                            if(brsections.length < 3 && keyvalues.length < 2){
                                                                                r.setDescription(brsection);
                                                                            } else{
                                                                                String key = keyvalues[0].trim();
                                                                                String value = keyvalues[1].trim();

                                                                                switch(key.toLowerCase()){
                                                                                    case "start date": {
                                                                                        //EXAMPLE --- Start Date: Friday, 20 March 2020 - 00:00
                                                                                        //parse the start date
                                                                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy - HH:mm");
                                                                                        LocalDateTime dt = LocalDateTime.parse(value, formatter);

                                                                                        ((Roadworks) r).setStart(dt);
                                                                                        break;
                                                                                    }
                                                                                    case "end date": {
                                                                                        //EXAMPLE --- End Date: Saturday, 21 March 2020 - 00:00
                                                                                        //parse the end date
                                                                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy - HH:mm");
                                                                                        LocalDateTime dt = LocalDateTime.parse(value, formatter);

                                                                                        ((Roadworks) r).setEnd(dt);
                                                                                        break;
                                                                                    }
                                                                                    case "type":{
                                                                                        //EXAMPLE --- TYPE : AWP Location : The M6 northbound entry slip at junction J43 Lane Closures : Lanes 1 and 2 wi
                                                                                        r.setDescription(brsection);

                                                                                        //remove type initiator
                                                                                        brsection = brsection.substring(6);

                                                                                        ArrayList<String> properties = new ArrayList<>();
                                                                                        properties.addAll(Arrays.asList(brsection.split("  ")));

                                                                                        for (String property : properties) {
                                                                                            //each property has its key/value split with a single newline
                                                                                            keyvalues = property.split(":");
                                                                                            key = keyvalues[0].trim();
                                                                                            value = keyvalues[1].trim();

                                                                                        /*the colon ":" could be leftover at the end of the key,
                                                                                        this strips it away if it exists*/
                                                                                            String lastchar = key.substring(key.length() - 1);
                                                                                            if(lastchar.equals(":")){
                                                                                                key = key.substring(0, key.length()-1).trim();
                                                                                            }

                                                                                            r.addProperty(key, value);
                                                                                        }
                                                                                        break;
                                                                                    }
                                                                                    case "works":{
                                                                                        //EXAMPLE --- Works: Cyclic Maintenance Traffic Management: Lane Closure.
                                                                                        r.setDescription(brsection);
                                                                                        //From here on, all properties are split by double newlines
                                                                                        ArrayList<String> properties = new ArrayList<>();
                                                                                        properties.addAll(Arrays.asList(brsection.split("\n\n")));

                                                                                        //check if any of the properties has only one line, sometimes diversion info is shown as below
                                                                                    /*
                                                                                    Diversion Information:

                                                                                    1.	North St - Charing X EB on - End
                                                                                    2.	N/A
                                                                                    3.	M8 EB - Jct 14 EB off - Return Jct 14 WB On - Jct 16 WB Off - Dobbies Loan - Phoenix Rd - St Georges Rd - End
                                                                                    */

                                                                                        int i = 0;
                                                                                        List<String> toRemove = new ArrayList<String>();
                                                                                        for (String property : properties){
                                                                                            //check if the last char is a newline (trimming ending whitespace before the check). Sometimes this occurs. So handle this edge case
                                                                                            //EXAMPLE: "take 3rd exit onto Kilbarchan Onslip Northbound - rejoin A737 Northbound, continue.\n "
                                                                                            //trim() alone strips the newlines so it is sufficient to handle this situation.
                                                                                            property = property.trim();
                                                                                            if(property.split("\n").length < 2){
                                                                                                //add the next property along as the value
                                                                                                //if this is the last property and its empty, assume its a value that's not attached like the example below
                                                                                            /*
                                                                                            Diversion Information:
                                                                                            Div a:Continue to Rbt at Paisley Road West - re join at Jct 23 EB on slip

                                                                                            Div b: Continue Helen St - Edmiston Drive - Broomloan Road  - M8 Jct 23 EB on slip
                                                                                             */

                                                                                                if(properties.size()-1 == i){
                                                                                                    String oneBehind = properties.get(i-1);
                                                                                                    oneBehind+=property;
                                                                                                    properties.set(i, oneBehind);
                                                                                                    toRemove.add(property);
                                                                                                } else{
                                                                                                    String oneAhead = properties.get(i+1);
                                                                                                    property+=oneAhead;
                                                                                                    properties.set(i, property);
                                                                                                    toRemove.add(oneAhead);
                                                                                                }
                                                                                            }
                                                                                            i++;
                                                                                        }
                                                                                        properties.removeAll(toRemove);


                                                                                        for (String property : properties) {
                                                                                            //each property has its key/value split with a single newline
                                                                                            keyvalues = property.split(":");
                                                                                            key = keyvalues[0].trim();

                                                                                            if(keyvalues.length < 2){
                                                                                                System.out.println(property);
                                                                                            }
                                                                                            value = keyvalues[1].trim();

                                                                                        /*the colon ":" could be leftover at the end of the key,
                                                                                        this strips it away if it exists*/
                                                                                            String lastchar = key.substring(key.length() - 1);
                                                                                            if(lastchar.equals(":")){
                                                                                                key = key.substring(0, key.length()-1).trim();
                                                                                            }

                                                                                            r.addProperty(key, value);
                                                                                        }
                                                                                        break;
                                                                                    }
                                                                                    case "delay information":{
                                                                                        ((Roadworks) r).setDelayInformation(value.trim());
                                                                                        break;
                                                                                    }
                                                                                    default: {
                                                                                        //EXAMPLE --- Works: Cyclic Maintenance Traffic Management: Lane Closure.
                                                                                        r.setDescription(brsection);
                                                                                    }
                                                                                }
                                                                            }

                                                                        }
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