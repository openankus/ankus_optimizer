package org.ankus.optimizer;

import org.ankus.optimizer.util.ArgumentsConstants;
import org.ankus.optimizer.util.ConfigurationVariable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 분류결과 체크하는  MapReduce Driver
 *
 * @description
 * @version
 * @date  2016. 10. 13.
 * @author JungHo Kim
 *
 */
public class CheckClassResultDriver extends Configured implements Tool {
	
    private Logger logger = LoggerFactory.getLogger(CheckClassResultDriver.class);
	
	@Override
	public int run(String[] args) throws Exception {
		
		// 허용 옵션에 대한 validation 체크
        Configuration conf = this.getConf();
        if(!ConfigurationVariable.setFromArguments(args, conf))
        {
            logger.info("> MR Job Setting Failed..");
            return 1;
        }
        
		// TODO option validation 추가할 것(INPUT_PATH, OUPUT_PATH, CLASS_INDEX, DELIMITER)
		
		
	    // Job 이름 설정
	    Job job = new Job(conf, CheckClassResultDriver.class.getName());
	    
	    // 입출력 데이터 경로 설정
	    Path inputFilePath = new Path(conf.get(ArgumentsConstants.INPUT_PATH));
	    Path outputBase = new Path(conf.get(ArgumentsConstants.OUTPUT_PATH));
	    FileInputFormat.addInputPath(job, inputFilePath);
	    FileOutputFormat.setOutputPath(job, outputBase);
	    FileSystem fs = FileSystem.get(conf);
	    fs.delete(outputBase, true);
	    
	    //	컬럼 구분자 추가
	    String delimiter = conf.get(ArgumentsConstants.DELIMITER);
	    if (delimiter == null || "".equals(delimiter)){
	    	delimiter = "\t";
	    }

	    // Job 클래스 설정
	    job.setJarByClass(CheckClassResultDriver.class);
	    // Mapper 클래스 설정
	    job.setMapperClass(CheckClassResultMapper.class);
	    // Reducer 클래스 설정
	    job.setReducerClass(CheckClassResultReducer.class);
//		// 정렬 순서 설정
//	    job.setSortComparatorClass(TextKeyDescComparator.class);
	    
	    
	    // 입출력 데이터 포맷 설정
	    job.setInputFormatClass(TextInputFormat.class);
	    job.setOutputFormatClass(TextOutputFormat.class);

	    // Map 출력키 및 출력값 유형 설정
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(LongWritable.class);

	    
	    // 최종 출력키 및 출력값 유형 설정
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(LongWritable.class);
	    
	    // Job 수행(Job 수행완료까지 대기)
	    job.waitForCompletion(true);
	    
	    return 0;
	}

}
