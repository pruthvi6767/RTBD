/**
 * Created by Mayanka on 20-Jul-15.
 * Reference : https://github.com/shekhargulati/day20-stanford-sentiment-analysis-demo
 */

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.Properties;

public class SentimentAnalyzer {

    public TweetWithSentiment findSentiment(String line) {

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        int mainSentiment = 0;
        if (line != null && line.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(line);
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String partText = sentence.toString();
                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }

            }
        }
        if (mainSentiment == 2 || mainSentiment > 4 || mainSentiment < 0) {
            return null;
        }

        TweetWithSentiment tweetWithSentiment = new TweetWithSentiment(line, toCss(mainSentiment));
        return tweetWithSentiment;

    }

    public ArrayList<String> pop(String line,String sentiment){
        String tweetwithourl=removeUrl(line);
        String url=extractUrls(line);
        MaxentTagger tagger = new MaxentTagger("models/gate-EN-twitter-fast.model");
        String sample = tweetwithourl.replaceAll("\\W", " ");
        String tagged = tagger.tagTokenizedString(sample);
        String[] x = tagged.split(" ");
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String> result = new ArrayList<>();

        for(int i=0;i<x.length;i++)
        {
            if (x[i].substring(x[i].lastIndexOf("_")+1).startsWith("N"))
            {
                list.add(x[i].split("_")[0]);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String s : list)
        {
            sb.append(s);
            sb.append(" ");
        }
        //result[0]=sb.toString();
        //result[1]=url;
        result.add(sb.toString());
        result.add(url);
        result.add(sentiment);

        //System.out.println(sb.toString());
       // for(int i=0;i<list.size();i++)
        //{
          //  System.out.print(list.get(i));
       // }

        return result;
    }
    //Pull all links from the body for easy retrieval
    public String removeUrl(String commentstr)
    {
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr);
        int i = 0;
        while (m.find()) {
            commentstr = commentstr.replaceAll(m.group(i),"").trim();
            i++;
        }
        return commentstr;
    }
    public String extractUrls(String value){

        List<String> result = new ArrayList<String>();
        String urlPattern = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(value);
        while (m.find()) {
            result.add(value.substring(m.start(0),m.end(0)));
        }
        StringBuilder sb = new StringBuilder();
        for (String s : result)
        {
            sb.append(s);
            sb.append(" ");
        }
        return sb.toString();
    }


    public int findSentiment(String line,int i) {
    Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
            int mainSentiment = 0;
            if (line != null && line.length() > 0) {
                int longest = 0;
                Annotation annotation = pipeline.process(line);
                for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                    Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
                    int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                    String partText = sentence.toString();
                    if (partText.length() > longest) {
                        mainSentiment = sentiment;
                        longest = partText.length();

                    }

                }
            }
            if (mainSentiment == 2 || mainSentiment > 4 || mainSentiment < 0) {
                return -1;
            }

            return mainSentiment;

    }

    private String toCss(int sentiment) {
        switch (sentiment) {
            case 0:
                return "one";//very negative
            case 1:
                return "two";//negative
            case 2:
                return "three";//neutral
            case 3:
                return "four";//positive
            case 4:
                return " five";//very positive
            default:
                return "";
        }
    }

    public static void main(String[] args) {
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
        TweetWithSentiment tweetWithSentiment = sentimentAnalyzer
                .findSentiment("click here for your Sachin Tendulkar personalized digital autograph.");
        System.out.println(tweetWithSentiment);
    }
}
