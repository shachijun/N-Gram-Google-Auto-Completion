import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;


import java.io.IOException;
import java.util.*;

public class LanguageModel {
	public static class Map extends Mapper<LongWritable, Text, Text, Text> {
		int threashold;
		@Override
		public void setup(Context context) {
			Configuration configuration = context.getConfiguration();
			threashold = configuration.getInt("threashold",20);
		}
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			if((value == null) || (value.toString().trim()).length() == 0) {
				return;
			}
			//this is cool\t20   default
			//output: 	key: this is   value: cool=20
			String line = value.toString().trim();
			String[] wordsPlusCount = line.split("\t");
			if(wordsPlusCount.length < 2) {
				return;
			}
			String[] words = wordsPlusCount[0].split("\\s+");
			int count = Integer.parseInt(wordsPlusCount[1]);
			if (count < threashold){
				return;
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < words.length - 1; i++){
				sb.append(words[i]);
				sb.append(" ");
			}
			String outputKey = sb.toString().trim();
			String outputValue = words[words.length - 1] + "=" + count;
			context.write(new Text(outputKey), new Text(outputValue));
		}
	}

	public static class Reduce extends Reducer<Text, Text, DBOutputWritable, NullWritable> {
		int TopK;
		// get the n parameter from the configuration TopK
		@Override
		public void setup(Context context) {
			Configuration conf = context.getConfiguration();
			TopK = conf.getInt("TopK", 5);
		}
		@Override
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			//reverse order
			TreeMap <Integer,List<String>> tm = new TreeMap <Integer,List<String>>(Collections.<Integer>reverseOrder());
			for (Text val : values) {
				//val: data =  10
				String value = val.toString().trim();
				String word = value.split("=")[0].trim();
				int count = Integer.parseInt(value.split("=")[1].trim());
				if (!tm.containsKey(count)) {
					tm.put(count, new ArrayList<String>());
				}
				tm.get(count).add(word);
			}
			Iterator<Integer> iter = tm.keySet().iterator();
			for( int j = 0; iter.hasNext() && j <TopK; ){
				int keyCount = iter.next();
				List<String> words = tm.get(keyCount);
				for (int i = 0; i < words.size() && j < TopK; i++, j++){
//				        key=sb.toString().trim();
					context.write(new DBOutputWritable(key.toString(),words.get(i),keyCount),NullWritable.get());
				}
			}
		}
	}
}
