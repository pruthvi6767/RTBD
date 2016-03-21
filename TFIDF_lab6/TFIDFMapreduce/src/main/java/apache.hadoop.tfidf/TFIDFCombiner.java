/*
Reference:
https://github.com/daveEason/apache-hadoop-tfidf
*/
package apache.hadoop.tfidf;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;


public class TFIDFCombiner extends Reducer<compositekeyforTFIDF, LongWritable, compositekeyforTFIDF, LongWritable> {

        public void reduce(compositekeyforTFIDF key,
                           Iterator<LongWritable> values,Context context)
                throws IOException, InterruptedException {

            boolean dfEntry = key.getDfEntry();

            if (dfEntry) {
                context.write(key, values.next());
            } else {
                long termFreq = 0;
                while (values.hasNext()) {
                    termFreq += values.next().get();
                }
                context.write(key, new LongWritable(termFreq));
            }
        }
    }
