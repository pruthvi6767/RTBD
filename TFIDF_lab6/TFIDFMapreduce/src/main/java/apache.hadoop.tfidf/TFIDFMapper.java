/*
Reference:
https://github.com/daveEason/apache-hadoop-tfidf
*/
package apache.hadoop.tfidf;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;


public class TFIDFMapper extends Mapper<Text, Text, compositekeyforTFIDF, LongWritable> {

        public void map(Text key,
                        Text value,
                        org.apache.hadoop.mapreduce.Mapper.Context context)
                throws IOException, InterruptedException {

            LongWritable ONE = new LongWritable(1);

      /* Strip "@..." from key if present. */
            String docID = key.toString();
            int i = docID.indexOf("@");
            if (i != -1) {
                docID = docID.substring(0, i);
            }

            String s = value.toString();
            for (String term : s.split("\\W+")) {
        /* Ignore empty terms and uppercase terms over 1 letter.
           (Added for titles and speakers in Shakespeare.
         */
                if (term.length() > 0 && !((term.length() > 1 ) &  (term.equals(term.toUpperCase())))) {
                    term = term.toLowerCase();
                    context.write(new compositekeyforTFIDF(term, docID, true), ONE);
                    context.write(new compositekeyforTFIDF(term, docID, false), ONE);
                }
            }
        }
    }
