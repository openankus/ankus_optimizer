package org.ankus.optimizer.mapreduce;

import java.io.IOException;
import java.util.StringTokenizer;

import org.ankus.optimizer.util.ArgumentsConstants;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 분류결과 체크하는  Mapper
 *
 * @description 분류결과와 실제결과의 정/오 개수를 추가
 * @version
 * @date  2016. 10. 13.
 * @author JungHo Kim
 *
 */
public class ClassifyResultCheckLabelWithInputMapper extends Mapper<LongWritable, Text, Text, LongWritable>{
	

	private Logger logger = LoggerFactory.getLogger(ClassifyResultCheckLabelWithInputMapper.class);
	
	
	/**
	 * 출력 Key 상수: 바른 분류 개수
	 */
	public static final String OUTPUT_KEY_CORRECT = "correct";

	/**
	 * 출력 Key 상수: 잘못된 분류 개수
	 */
	public static final String OUTPUT_KEY_INCORRECT = "incorrect";
	
	/**
	 * reduce될 중간 출력키
	 */
	Text interOutputKey = new Text();
	
	/**
	 * reduce될 중간 출력값
	 */
	LongWritable interOutputValue =  new LongWritable(1);
	
	
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, LongWritable>.Context context)
			throws IOException, InterruptedException {

		int classIdx = context.getConfiguration().getInt(ArgumentsConstants.CLASS_INDEX, -1);
		String delimiter = context.getConfiguration().get(ArgumentsConstants.DELIMITER);
//		System.out.println("구분자 : "+classIdx);
//		System.out.println("구분자 : "+delimiter);
		
		int factValueIdx = classIdx;
		int estimateValueIdx = classIdx + 1;
		String factValue = null;
		String estimateValue = null;
//		logger.debug("정확도 계산대상 문자열: " +value.toString());
//		logger.info("정확도 계산대상 문자열: " +value.toString());
		StringTokenizer tokenizer = new StringTokenizer(value.toString(), delimiter);
		int idx = 0;
		while (tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken();
			if (idx == factValueIdx) factValue = token;
			else if (idx == estimateValueIdx) estimateValue = token;
			idx++;
		}
		
		if (factValue.equals(estimateValue)){
			interOutputKey.set(ClassifyResultCheckLabelWithInputMapper.OUTPUT_KEY_CORRECT);
			context.write(interOutputKey, interOutputValue);
		}else{
			interOutputKey.set(ClassifyResultCheckLabelWithInputMapper.OUTPUT_KEY_INCORRECT);
			context.write(interOutputKey, interOutputValue);
		}
		
	}
	

}
