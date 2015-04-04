import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {

    public static class TokenizerMapper
	extends Mapper<Object, Text, Text, IntWritable>{

	private final static IntWritable one = new IntWritable(1);
	private Text word = new Text();

	/* context : c'est la que l'on dit ce que l'on va resortir (dans le context) */
	public void map(Object key, Text value, Context context
			) throws IOException, InterruptedException {
	    StringTokenizer itr = new StringTokenizer(value.toString());
	    while (itr.hasMoreTokens()) {
		word.set(itr.nextToken());
		context.write(word, one);
	    }
	}
    }

    /* reducer */
    /* en entree : mot, ocurence du mot. En sortie pareil */
    /* c'est toujours dans le context que l'on gere cela */
    public static class IntSumReducer
	extends Reducer<Text,IntWritable,Text,IntWritable> {
	private IntWritable result = new IntWritable();

	public void reduce(Text key, Iterable<IntWritable> values,
			   Context context
			   ) throws IOException, InterruptedException {
	    int sum = 0;
	    for (IntWritable val : values) {
		sum += val.get();
	    }
	    result.set(sum);
	    context.write(key, result);
	}
    }

    public static void main(String[] args) throws Exception {
	Configuration conf = new Configuration();
	Job job = Job.getInstance(conf, "word count");
	/* le driver */
	job.setJarByClass(WordCount.class);
	/* celui qui va faire le mapper */
	/* en entree un morceau de donne, en sortie une map clef valeur */
	job.setMapperClass(TokenizerMapper.class);

	/* c'est la classe qui permet de faire un reducer sur les machine en local. Cela permet de faire des calculs avant d'envoyer les données et d'éviter d'avoir trop de clefs value */
	job.setCombinerClass(IntSumReducer.class);

	job.setReducerClass(IntSumReducer.class);

	job.setOutputKeyClass(Text.class);
	
	/* type */
	job.setOutputValueClass(IntWritable.class);

	FileInputFormat.addInputPath(job, new Path(args[0]));

	FileOutputFormat.setOutputPath(job, new Path(args[1]));
	
	/* c ici que l'on attend que le jbo finisse */
	System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
