package edu.umkc.ra.storm;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import edu.umkc.ra.coreNLP.SentimentAnalyzer;
import edu.umkc.ra.coreNLP.TweetWithSentiment;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mayanka on 24-Sep-15.
 */
public class SentimentBolt extends BaseBasicBolt {
    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
            String tweet=tuple.toString();
            String s = tuple.getStringByField("Status");
            SentimentAnalyzer sa = new SentimentAnalyzer();
            TweetWithSentiment tw = sa.findSentiment(s);
            //System.out.println(tw.getLine() + tw.getCssClass());
            insertIntoMongoDB(tw.getLine(), tw.getCssClass(), tweet);

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    public static void insertIntoMongoDB(String tweet, String sentiment ,String total) {
        try {
            URL url = new URL("https://api.mongolab.com/api/1/databases/twitterstorm/collections/test?apiKey=5TUyQEJSzJzLZDi3pSuiWLup8oIb7hMf");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String input = "{\"tweet\":\"" + tweet + " \",\"sentiment\":\"" + sentiment +  "\",\"time\":\"" + System.currentTimeMillis() + "\"}";

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("The code is " + conn.getResponseMessage());
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();
        } catch (Exception e) {

            e.printStackTrace();

        }

    }
}
