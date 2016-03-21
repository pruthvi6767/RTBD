/*
Reference:
https://github.com/daveEason/apache-hadoop-tfidf
*/
package apache.hadoop.tfidf;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

import java.util.Iterator;

public class TFIDFReducer extends Reducer<compositekeyforTFIDF, LongWritable, Text, DoubleWritable> {

    int numberOfDocuments, docCountForTerm;
    String lastTerm;

    public void configure(Configuration conf) {

        numberOfDocuments = (conf.get("tfidf.num.documents") != null) ? Integer.parseInt(conf.get("tfidf.num.documents")) : 1; // set default to 1 for unit testing
        lastTerm = "";
    }

    public void reduce(compositekeyforTFIDF key,
                       Iterator<LongWritable> values, Context context)
            throws IOException, InterruptedException {

        String docID = key.getDocID();
        String term = key.getTerm();
        boolean dfEntry = key.getDfEntry();

        if (!term.equals(lastTerm)) {
            docCountForTerm = 1;
            lastTerm = term;
        } else {
            if (dfEntry) {
                docCountForTerm++;
            } else {
                long termFreq = 0;
                while (values.hasNext()) {
                    termFreq += values.next().get();
                }
                double inverseDocFreq = Math.log((double) numberOfDocuments / docCountForTerm);
                double tfidf = termFreq * inverseDocFreq;
          /* Verbose output for learning purposes. */
                String outTuple = "(" + term + ", " + docID + ")"
                        + " [tf:" + termFreq
                        + " n:" + docCountForTerm
                        + " N:" + numberOfDocuments
                        + "]";
                context.write(new Text(outTuple), new DoubleWritable(tfidf));
            }
        }
    }
}
