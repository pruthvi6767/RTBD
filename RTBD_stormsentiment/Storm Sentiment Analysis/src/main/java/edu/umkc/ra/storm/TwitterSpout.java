package edu.umkc.ra.storm;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;


public class TwitterSpout extends BaseRichSpout {
    SpoutOutputCollector _collector;
    LinkedBlockingQueue<Status> queue = null;
    TwitterStream _twitterStream;
    //String _username;
   // String _pwd;
    String consumer_key="iBQ80mhEysZL1eJe0TQkmq8XQ";
    String consumer_secret="2lCHDeO0txhx8uAHXggGsh1gSh1FSOVZiiS9JTORPnRwSFejLT";
    String access_token="131923775-zvFxzZmD6PIxQFTNxFQ1X8XsLH6wWHoAkJ2FozYn";
    String token_secret="V2VVc4yRUPAQB3cgDVMF9r7Y0yHAKR6oUuyz905yOAnss";
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("Status"));
    }

    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        queue = new LinkedBlockingQueue<Status>(1000);
        _collector = spoutOutputCollector;
        StatusListener listener = new StatusListener() {

            public void onException(Exception e) {

            }

            public void onStatus(Status status) {
                queue.offer(status);
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            public void onTrackLimitationNotice(int i) {

            }

            public void onScrubGeo(long l, long l1) {

            }

            public void onStallWarning(StallWarning stallWarning) {

            }
        };

        //ConfigurationBuilder cb = new ConfigurationBuilder();
        TwitterStreamFactory fact = new TwitterStreamFactory();
        _twitterStream = fact.getInstance();
        _twitterStream.setOAuthConsumer(consumer_key, consumer_secret);
        _twitterStream.setOAuthAccessToken(new AccessToken(access_token, token_secret));
        _twitterStream.addListener(listener);
        _twitterStream.filter(new FilterQuery().track(new String[]{"recipe","food","yummy"}));

    }

    public void nextTuple() {
        Status ret = queue.poll();
        if(ret==null) {
            Utils.sleep(50);
       } else {
           String s=ret.getText();

            // emit tuple to next bolt
            _collector.emit(new Values(s));
       }
    }
}
