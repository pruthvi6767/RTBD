/*
Reference:
https://github.com/daveEason/apache-hadoop-tfidf
*/
package apache.hadoop.tfidf;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;
//import org.apache.hadoop.mapred.JobConf;
//import org.apache.hadoop.mapred.Partitioner;


public class TFIDFPartitioner extends Partitioner<compositekeyforTFIDF, LongWritable> {

//        public void configure(JobConf job) {}

        public int getPartition(compositekeyforTFIDF key, LongWritable value, int numReduceTasks) {
            Text term = key.term;
            return (term.hashCode() & Integer.MAX_VALUE) % numReduceTasks;
        }
    }
