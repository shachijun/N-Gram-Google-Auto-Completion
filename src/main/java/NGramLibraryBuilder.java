import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class NGramLibraryBuilder {
	public static class NGramMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

		int noGram;
		@Override
		public void setup(Context context) {
			Configuration conf = context.getConfiguration();
			noGram=conf.getInt("noGram", 5);
		}
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			line = line.trim().toLowerCase().replaceAll("[^a-z]"," ");
			String worlds[] = line.split("\\s+");
			if (worlds.length < 2){
				return;
			}
			StringBuilder sb;
			for (int i = 0; i < worlds.length; i++){
				sb = new StringBuilder();
				sb.append(worlds[i]);
				for (int j = 1; i+j<worlds.length && j < noGram; j ++){
					sb.append(" ");
					sb.append(worlds[i+j]);
					context.write(new Text(sb.toString()),new IntWritable(1));
				}
			}
		}
	}
	public static class NGramReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		@Override
		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable value: values) {
				sum += value.get();
			}
			context.write(key, new IntWritable(sum));
		}
	}
}

