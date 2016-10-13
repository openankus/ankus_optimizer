package org.openankus.optimizer.ga.chromset;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class SortTextKeyMap extends Mapper<LongWritable, Text, Text, Text> {
	
	private FloatWritable point = new FloatWritable();
	private Text resultText = new Text();
	
	private Path[] cacheFiles = null;
	
	Text outputKey = new Text();
	
	Text outputValue = new Text();
	
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		
		//----<1.Map 입력데이터 파싱>---------
		String[] parts = value.toString().split("\t");
		String textKey = parts[0];
		String textValue = parts[1];
		//----</1.Map 입력데이터 파싱>---------
		
		//----<2.Map 입력데이터 처리>---------
		//----</2.Map 입력데이터 처리>---------
		
		//----<3.Map 출력데이터 처리>---------
		outputKey.set(textKey);
		outputValue.set(textValue);
		context.write(outputKey, outputValue);
		//----</3.Map 출력데이터 처리>---------
		
	}

}
