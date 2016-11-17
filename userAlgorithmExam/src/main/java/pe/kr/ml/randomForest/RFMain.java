package pe.kr.ml.randomForest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.ProgramDriver;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import pe.kr.ml.randomForest.util.ArgumentsConstants;
import pe.kr.ml.randomForest.util.Constants;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

/**
 * Random Forest Main class
 *
 * @description
 * @version 0.1
 * @date  2016. 11. 14.
 * @author JungHo Kim
 *
 */
public class RFMain {
	
	public static void main(String[] args) {
		
        ProgramDriver programDriver = new ProgramDriver();
        try{
        	programDriver.addClass(Constants.DRIVER_RANDOM_FOREST, RFDriver.class, "Random Forest Classification Algorithm");

            programDriver.driver(args);
        	
        	// Success
        	System.exit(0);
        
        }catch (Throwable ex){
        	ex.printStackTrace();
        	System.exit(-1);
        }
		
	}
	
	
	public static void temp(String[] args) throws Exception {

//		{
//			BufferedReader reader = new BufferedReader(new FileReader("iris.arff"));
//			Instances data = new Instances(reader);
//			for (int idx=0; idx<data.numAttributes(); idx++){
//				Attribute attr = data.attribute(idx);
//				System.out.println(attr.name());
//				System.out.println(attr.type());
//				
//			}
//			data.numAttributes();
//		}
		
		
		// 파라메터 validation
		String inputPath = "/data/iris.csv";
		String dataFormatPath = "/data/iris.arff";
		String trainOutputPath = "/output/userRF/train";
		String classifyOutputPath = "/output/userRF/classify";
		String delimiter = ",";
		String numericIndexList = "0,1,2,3";
		String nominalIndexList = "4";
		String classIndex = "4";
		String classLabel = "Iris-setosa,Iris-versicolor,Iris-virginica";
		String modelPath = "/output/userRF/train";
		int numTree = 2;
		
		
		
		
		
		

		//----<데이터 셋 구성>-------
		
		
		
		
		//----</데이터 셋 구성>-------
		
		
		
//		//----<Training>------
//		{
//			Configuration config = new Configuration();
//			config.set(ArgumentsConstants.DATA_FORMAT_PATH, dataFormatPath);
//			config.set(ArgumentsConstants.INPUT_PATH, inputPath);
//			config.set(ArgumentsConstants.OUTPUT_PATH, trainOutputPath);
//			config.set(ArgumentsConstants.DELIMITER, delimiter);
//			config.set(ArgumentsConstants.NUMERIC_INDEX, numericIndexList);
//			config.set(ArgumentsConstants.NOMINAL_INDEX, nominalIndexList);
//			config.set(ArgumentsConstants.TARGET_INDEX, classIndex);
//			config.set(ArgumentsConstants.CLASS_LABEL, classLabel);
//			config.set(ArgumentsConstants.MODEL_PATH, modelPath);
//
//			FileSystem fs = FileSystem.get(config);
//			
//			// Split할 파일 사이즈
//			long sizeInput = fs.getFileStatus(new Path(inputPath)).getLen();
//			long splitSize = sizeInput/numTree;
//			System.out.println("스플릿 크기: "+ splitSize);
////			config.set("-Dmapred.max.split.size", String.valueOf(splitSize));
//			config.set("mapred.max.split.size", String.valueOf(splitSize));
//			
//			ToolRunner.run(config, new RFTrainDriver(), new String[0]);
//		}
//		//----</Training>------

		
		
		
		
		
		
		//----<Testing>------
		{
			Configuration config = new Configuration();
			config.set(ArgumentsConstants.DATA_FORMAT_PATH, dataFormatPath);
			config.set(ArgumentsConstants.INPUT_PATH, inputPath);
			config.set(ArgumentsConstants.OUTPUT_PATH, classifyOutputPath);
			config.set(ArgumentsConstants.DELIMITER, delimiter);
			config.set(ArgumentsConstants.NUMERIC_INDEX, numericIndexList);
			config.set(ArgumentsConstants.NOMINAL_INDEX, nominalIndexList);
			config.set(ArgumentsConstants.CLASS_INDEX, classIndex);
			config.set(ArgumentsConstants.CLASS_LABEL, classLabel);
			config.set(ArgumentsConstants.TRAINED_MODEL, modelPath);

			FileSystem fs = FileSystem.get(config);
			
			// Split할 파일 사이즈
			long sizeInput = fs.getFileStatus(new Path(inputPath)).getLen();
			long splitSize = sizeInput/numTree;
			System.out.println("스플릿 크기: "+ splitSize);
//			config.set("-Dmapred.max.split.size", String.valueOf(splitSize));
			config.set("mapred.max.split.size", String.valueOf(splitSize));
			
			ToolRunner.run(config, new RFClassifyDriver(), new String[0]);
		}
		//----</Testing>------
	}

	
	
}
