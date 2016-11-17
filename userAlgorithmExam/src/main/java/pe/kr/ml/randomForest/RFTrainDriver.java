package pe.kr.ml.randomForest;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileStatus;
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

import pe.kr.ml.randomForest.util.ArgumentsConstants;
import pe.kr.ml.randomForest.util.Constants;

public class RFTrainDriver extends Configured implements Tool {
	

	
	@Override
	public int run(String[] args) throws Exception {

		Configuration conf = this.getConf();

		// Job 이름 설정
		Job job = new Job(conf, RFTrainDriver.class.getName());
		
		// Machine Learning 알고리즘 관련 jar에 대한 class path 추가
		FileSystem hdfs = FileSystem.get(conf);
//		FileStatus[] fileStatuses = hdfs.listStatus(new Path(DIR_LIB));
//		for (FileStatus fileStatus : fileStatuses) {
//			if (!fileStatus.isDir() && fileStatus.getPath().getName().endsWith(".jar"))
//				DistributedCache.addFileToClassPath(fileStatus.getPath(), job.getConfiguration());
//		}
		DistributedCache.addFileToClassPath(new Path(Constants.DIR_LIB+"/bounce-0.18.jar"), job.getConfiguration());
		DistributedCache.addFileToClassPath(new Path(Constants.DIR_LIB+"/java-cup-11b-2015.03.26.jar"), job.getConfiguration());
		DistributedCache.addFileToClassPath(new Path(Constants.DIR_LIB+"/java-cup-11b-runtime-2015.03.26.jar"), job.getConfiguration());
		DistributedCache.addFileToClassPath(new Path(Constants.DIR_LIB+"/mtj-1.0.4.jar"), job.getConfiguration());
		DistributedCache.addFileToClassPath(new Path(Constants.DIR_LIB+"/weka-stable-3.8.0.jar"), job.getConfiguration());
		
		

		// 입출력 데이터 경로 설정
		Path inputFilePath = new Path(conf.get(ArgumentsConstants.INPUT_PATH));
		Path outputBase = new Path(conf.get(ArgumentsConstants.OUTPUT_PATH));
		FileInputFormat.addInputPath(job, inputFilePath);
		FileOutputFormat.setOutputPath(job, outputBase);
		FileSystem fs = FileSystem.get(conf);
		fs.delete(outputBase, true);

		// 컬럼 구분자 추가
		String delimiter = conf.get(ArgumentsConstants.DELIMITER);
		if (delimiter == null || "".equals(delimiter)) {
			delimiter = "\t";
		}

		// Job 클래스 설정
		job.setJarByClass(RFTrainDriver.class);
		 // Mapper 클래스 설정
		 job.setMapperClass(RFTrainMapper.class);
//		// Combiner 클래스 설정
//		job.setCombinerClass(RFTrainCombiner.class);
//		// Reducer 클래스 설정
//		job.setReducerClass(RFTrainReducer.class);

		// 입출력 데이터 포맷 설정
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		// Map 출력키 및 출력값 유형 설정
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		// 최종 출력키 및 출력값 유형 설정
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		// Job 수행(Job 수행완료까지 대기)
		job.waitForCompletion(true);

		return 0;
	}

}
