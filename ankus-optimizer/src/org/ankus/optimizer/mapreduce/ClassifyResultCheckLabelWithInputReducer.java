package org.ankus.optimizer.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * 
 * 분류결과 체크하는  Reducer
 *
 * @description 정/오 결과를 합산하는 Reduce
 * @version 0.1
 * @date  2016. 10. 14.
 * @author JungHo Kim
 *
 */
public class ClassifyResultCheckLabelWithInputReducer extends Reducer<Text, LongWritable, Text, LongWritable>{
	
	private Text outputKey = new Text();
	private LongWritable outputValue = new LongWritable();
	
	@Override
	protected void reduce(Text key, Iterable<LongWritable> values,
			Reducer<Text, LongWritable, Text, LongWritable>.Context context) throws IOException, InterruptedException {
		String keyString = key.toString();
		long sum = 0;
		for (LongWritable value : values){
			sum += value.get();
		}
		outputValue.set(sum);
		context.write(key, outputValue);
	}

}
