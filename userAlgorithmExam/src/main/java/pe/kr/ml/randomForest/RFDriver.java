package pe.kr.ml.randomForest;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pe.kr.ml.randomForest.util.ArgumentsConstants;
import pe.kr.ml.randomForest.util.ConfigurationVariable;
import pe.kr.ml.randomForest.util.Constants;

public class RFDriver extends Configured implements Tool {

	private Logger logger = LoggerFactory.getLogger(RFDriver.class);
	
	private static final String[] jarList = {"bounce-0.18.jar" , "java-cup-11b-2015.03.26.jar", "java-cup-11b-runtime-2015.03.26.jar", "mtj-1.0.4.jar", "weka-stable-3.8.0.jar"};
	
	public static void main(String args[]) throws Exception{
		
		int res = ToolRunner.run(new RFDriver(), args);
		System.exit(res);
	}

	@Override
	public int run(String[] args) throws Exception {
		
		Configuration conf = this.getConf();
		FileSystem fs = FileSystem.get(conf);
		
		
		// conf.set("fs.default.name", "hdfs://localhost:9000");
		if (!ConfigurationVariable.setFromArguments(args, conf)) {
			logger.info("> MR Job Setting Failed..");
			return 1;
		}

		
		
		
		boolean isTraining = false;
		if (conf.get(ArgumentsConstants.TRAINED_MODEL, null) == null)
			isTraining = true;

		String outputBase = conf.get(ArgumentsConstants.OUTPUT_PATH, null);
		String delimiter = conf.get(ArgumentsConstants.DELIMITER, "\t");
		conf.set(ArgumentsConstants.DELIMITER, delimiter);
		
		
		
		if (isTraining) {
			// In the case of training MR job
			
			//---<분포도를 달리한 임시 훈련 입력데이터 생성>----
			//	트레이닝용 데이터 생성 
			String inputPath = conf.get(ArgumentsConstants.INPUT_PATH);
			Path inputParentPath = (new Path(inputPath)).getParent();
			UUID uid = UUID.randomUUID();
			Date now = Calendar.getInstance().getTime();
			Path bootstrapTrainDataPath = new Path(inputParentPath, "trainData_"+uid.toString()+"_"+now.getTime()+".txt");

			long numTree = conf.getLong(ArgumentsConstants.NUM_PARTITION, 2);
			long dataCopy = numTree + 1;
			if(fs.exists(bootstrapTrainDataPath)) fs.delete(bootstrapTrainDataPath, true);
			FSDataOutputStream os = fs.create(bootstrapTrainDataPath);
			for (int idx=0; idx<dataCopy; idx++){
				FSDataInputStream is = fs.open(new Path(inputPath));
				byte[] buffer = new byte[1024];
				int len = -1;
				while ((len = is.read(buffer)) >= 0){
					os.write(buffer, 0, len);
				}
				os.write("\n".getBytes());
				is.close();
			}
			os.close();
			//---</분포도를 달리한 임시 훈련 입력데이터 생성>----

			
			// Split할 파일 사이즈(split 개수 만큼 tree 생성)
//			long sizeInput = fs.getFileStatus(new Path(inputPath)).getLen();
			long sizeInput = fs.getFileStatus(bootstrapTrainDataPath).getLen();
			long splitSize = sizeInput/numTree;
			System.out.println("Partition(Split) size in byte: "+ splitSize);
			logger.info("Partition(Split) size in byte: "+ splitSize);
//			conf.set("-Dmapred.max.split.size", String.valueOf(splitSize));
			conf.set("mapred.max.split.size", String.valueOf(splitSize));		
//			conf.set("mapreduce.input.fileinputformat.split.maxsize", String.valueOf(splitSize));
			
			

			logger.info("Confidence Factor for Pruning: " + conf.get(ArgumentsConstants.CONFIDENCE_FACTOR));
			logger.info("Minimum Leaf Node Count for Pruning: " + conf.get(ArgumentsConstants.MIN_LEAF_DATA));
			logger.info("> Random Forest Classification Training MapReduce JOB Started..");

			
			
			// Job 이름 설정
			Job job = new Job(conf, RFDriver.class.getName());

			// Machine Learning 알고리즘 관련 jar에 대한 class path 추가
			FileSystem hdfs = FileSystem.get(conf);
			for (String jar : jarList){
				Path jarPath = new Path(Constants.DIR_LIB + File.separator + jar);
				if (!fs.exists(jarPath)){
					StringBuffer sb = new StringBuffer();
					sb.append("The jar(="+jar+") does not exist in the /lib.\n Please store jar files in the directory of '/lib' on HDFS.\n");
					sb.append("-> ");
					for (String tmp : jarList){
						sb.append(tmp);
						sb.append(" ");
					}
					sb.append("\n");
					throw new FileNotFoundException(sb.toString());
				}
				DistributedCache.addFileToClassPath(jarPath, job.getConfiguration());
			}

			// 입출력 데이터 경로 설정
//			Path inputFilePath = new Path(conf.get(ArgumentsConstants.INPUT_PATH));
			FileInputFormat.addInputPath(job, bootstrapTrainDataPath);
			Path outputPath = new Path(outputBase);
//			FileInputFormat.addInputPath(job, inputFilePath);
			FileOutputFormat.setOutputPath(job, new Path(outputBase));
			fs.delete(outputPath, true);
			

			

			
			
			
			

			// Job 클래스 설정
			job.setJarByClass(RFDriver.class);

			// Mapper 클래스 설정
			job.setMapperClass(RFTrainMapper.class);

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
			
			// 임시 훈련 입력데이터 삭제
			fs.delete(bootstrapTrainDataPath, true);

			logger.info("> Random Forest Classification Training MapReduce JOB Finished..");
		} else {
			// In the case of classifying MR job

			
			// Split할 파일 사이즈
			long numPart = conf.getLong(ArgumentsConstants.NUM_PARTITION, 2);
			String inputPath = conf.get(ArgumentsConstants.INPUT_PATH);
			long sizeInput = fs.getFileStatus(new Path(inputPath)).getLen();
			long splitSize = sizeInput/numPart;
			logger.info("Partition(Split) size in byte: "+ splitSize);
//			conf.set("-Dmapred.max.split.size", String.valueOf(splitSize));
			conf.set("mapred.max.split.size", String.valueOf(splitSize));
			
			
			// Job 이름 설정
			Job job = new Job(conf, RFDriver.class.getName());

			// Machine Learning 알고리즘 관련 jar에 대한 class path 추가
			FileSystem hdfs = FileSystem.get(conf);
			for (String jar : jarList){
				Path jarPath = new Path(Constants.DIR_LIB + File.separator + jar);
				if (!fs.exists(jarPath)){
					StringBuffer sb = new StringBuffer();
					sb.append("The jar(="+jar+") does not exist in the /lib.\n Please store jar files in the directory of '/lib' on HDFS.\n");
					sb.append("-> ");
					for (String tmp : jarList){
						sb.append(tmp);
						sb.append(" ");
					}
					sb.append("\n");
					throw new FileNotFoundException(sb.toString());
				}
				DistributedCache.addFileToClassPath(jarPath, job.getConfiguration());
			}

			// 입출력 데이터 경로 설정
			Path inputFilePath = new Path(conf.get(ArgumentsConstants.INPUT_PATH));
			Path outputPath = new Path(conf.get(ArgumentsConstants.OUTPUT_PATH));
			FileInputFormat.addInputPath(job, inputFilePath);
			FileOutputFormat.setOutputPath(job, outputPath);
			fs.delete(outputPath, true);

			// Job 클래스 설정
			job.setJarByClass(RFDriver.class);
			// Mapper 클래스 설정(동일 레코드에 대한 각 트리모델의 분류결과값을 출력)
			job.setMapperClass(RFClassifyMapper.class);
			// Reducer 클래스 설정(한 데이터레고드에 대한 분류결과값들을 하나의 분류결과으로 병합)
			job.setReducerClass(RFClassifyReducer.class);

			// 입출력 데이터 포맷 설정
			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);

			// Map 출력키 및 출력값 유형 설정
			job.setMapOutputKeyClass(LongWritable.class);
			job.setMapOutputValueClass(Text.class);

			// 최종 출력키 및 출력값 유형 설정
			job.setOutputKeyClass(NullWritable.class);
			job.setOutputValueClass(Text.class);

			// Job 수행(Job 수행완료까지 대기)
			job.waitForCompletion(true);
		}

		// temp delete process
		if (conf.get(ArgumentsConstants.TEMP_DELETE, "true").equals("true")) {

		}

		return 0;
	}

}