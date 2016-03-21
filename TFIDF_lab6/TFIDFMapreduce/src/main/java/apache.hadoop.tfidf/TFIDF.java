/*
Reference:
https://github.com/daveEason/apache-hadoop-tfidf
*/
package apache.hadoop.tfidf;


import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class TFIDF extends Configured implements Tool {

  public int run(String[] args) throws Exception {

//    JobConf conf = new JobConf(getConf(), TFIDF.class);
//    conf.setJobName(this.getClass().getName());

    /*
     * Instantiate a Job object for your job's configuration.
     */
      Job job = new Job();

    /*
     * Specify the jar file that contains your driver, mapper, and reducer.
     * Hadoop will transfer this jar file to nodes in your cluster running
     * mapper and reducer tasks.
     */
      job.setJarByClass(TFIDF.class);

    /*
     * Specify an easily-decipherable name for the job.
     * This job name will appear in reports and logs.
     */
      job.setJobName("TFIDF");

    FileInputFormat.setInputPaths(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    job.setInputFormatClass(KeyValueTextInputFormat.class);

    job.setMapperClass(TFIDFMapper.class);
    job.setCombinerClass(TFIDFCombiner.class);
    job.setPartitionerClass(TFIDFPartitioner.class);
    job.setReducerClass(TFIDFReducer.class);

    job.setMapOutputKeyClass(compositekeyforTFIDF.class);
    job.setMapOutputValueClass(LongWritable.class);

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(DoubleWritable.class);

    /*
     * Start the MapReduce job and wait for it to finish.
     * If it finishes successfully, return 0. If not, return 1.
     */
      boolean success = job.waitForCompletion(true);
      return success ? 0 : 1;
  }

  public static void main(String[] args) throws Exception {

      int exitCode;

      if (args.length != 4) {
          System.out.println();
          System.out.println( "Usage:");
          System.out.println(
                  "  hadoop jar <jarFile> apache.hadoop.tfidf.TFIDF -D tfidf.num.documents=<n> <input> <output>"
          );
          System.out.println();
          System.out.println( "<input> records should of the form: keyAsText <tab> value");
          System.out.println("(works with shakespeare-stream data)");
          System.out.println();
          exitCode = -1;
      }
      else
      {
          exitCode = ToolRunner.run(new TFIDF(), args);
      }

      System.exit(exitCode);
  }
}
