package org.openankus.optimizer.ga.chromset;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class SortTextKeyReduce  extends Reducer<Text, Text, Text, Text>{
	
	@Override
	protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		
		Iterator<Text> iter = values.iterator();
		while(iter.hasNext()){
			context.write(key, iter.next());
		}
	}

}
