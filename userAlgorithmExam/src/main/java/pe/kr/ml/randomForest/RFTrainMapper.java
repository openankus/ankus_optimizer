package pe.kr.ml.randomForest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pe.kr.ml.randomForest.util.ArgumentsConstants;
import pe.kr.ml.randomForest.util.Constants;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.filters.unsupervised.attribute.Remove;

public class RFTrainMapper extends Mapper<LongWritable, Text, Text, Text>{
	
  private static final Logger log = LoggerFactory.getLogger(RFTrainMapper.class);
	
	/**
	 * 중간 출력 키
	 */
	Text interOutputKey = new Text();
	
	/**
	 * 중간 출력 값
	 */
	Text interOutputVal = new Text();
	
	/**
	 * 구분자
	 */
	String delimiter;
	
	/**
	 * 수치형 index 목록
	 */
	String numericIndexList;
	/**
	 * 기호형 index 목록
	 */
	String nominalIndexList;
	
	/**
	 * 클래스 index
	 */
	String classIndex;
	
	/**
	 * 클래스 레이블 목록
	 */
	String classLabel;
	
	/**
	 * 데이터 형식 파일 경로
	 */
	String dataFormatPath;
	
	/**
	 * 모델 생성용 수치형 속성 index 집합
	 */
	HashSet<Integer> numericIndexSet;

	/**
	 * 모델 생성용 기호형 속성 index 집합
	 */
	HashSet<Integer> nominalIndexSet;
	
	/**
	 * 출력 경로
	 */
	String outputPath;
	
	/**
	 * 속성정보 목록
	 */
	ArrayList<Attribute> attrList;

	/**
	 * 단말 노드의 최소 데이터 개수
	 */
	int minLeafData = 5;

	/**
	 * 가지치기를 위한 confidence factor(즉, purity)
	 */
	float confidenceFactor = 0.25f;
	
	/**
	 * 해당 split에 훈련 데이터 집합
	 */
	Instances dataSet;
	
	@Override
	protected void setup(Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		super.setup(context);
		
		// 파라메터 값 얻기
		Configuration config = context.getConfiguration();
		delimiter = config.get(ArgumentsConstants.DELIMITER, ",");
		numericIndexList = config.get(ArgumentsConstants.NUMERIC_INDEX);
		nominalIndexList = config.get(ArgumentsConstants.NOMINAL_INDEX);
		classIndex = config.get(ArgumentsConstants.CLASS_INDEX);
		classLabel = config.get(ArgumentsConstants.CLASS_LABEL);
		outputPath = config.get(ArgumentsConstants.OUTPUT_PATH);
		dataFormatPath = config.get(ArgumentsConstants.DATA_FORMAT_PATH);

		//	logging
		FileSystem fs = FileSystem.get(context.getConfiguration());
//		RFLogger.println("delimiter:"+delimiter);
//		RFLogger.println("numericIndexList:"+numericIndexList);
//		RFLogger.println("nominalIndexList:"+nominalIndexList);
//		RFLogger.println("classIndex:"+classIndex);
//		RFLogger.println("classLabel:"+classLabel);
		
		//	모델 생성 용 수치 및 기호형 속성 인덱스 추출
		numericIndexSet = new HashSet<Integer>();
		for (String val : numericIndexList.split(",")){
			numericIndexSet.add(new Integer(val));
		}
		nominalIndexSet = new HashSet<Integer>();
		for (String val : nominalIndexList.split(",")){
			nominalIndexSet.add(new Integer(val));
		}
		
		//	데이터의 전체 속성 정보 추출
		InputStreamReader isr = new InputStreamReader(fs.open(new Path(dataFormatPath)));
		Instances dataFormat = new Instances(isr);
		attrList = new ArrayList<Attribute>();
		for (int idx=0; idx<dataFormat.numAttributes(); idx++){
			attrList.add(dataFormat.attribute(idx));
		}
		
		// dataSet 객체 생성
		dataSet = new Instances("Rel", attrList, 20);
		dataSet.setClassIndex(Integer.parseInt(classIndex));
	
	}
	
	
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		
		//	데이터 레코드 만들기
//		FileSystem fs = FileSystem.get(context.getConfiguration());
//		log(fs, value.toString()+"\n");
		StringTokenizer st = new StringTokenizer(value.toString(), delimiter);
		Instance instance = new DenseInstance(attrList.size());
		Integer idx = 0;
		while(st.hasMoreTokens()){
			if (attrList.get(idx).isNumeric()){
				instance.setValue(attrList.get(idx), Double.parseDouble(st.nextToken()));
			}else if (attrList.get(idx).isNominal()){
				instance.setValue(attrList.get(idx), st.nextToken());
			}
			idx++;
		}
		
		
		//	data 추가
		dataSet.add(instance);
	}
	
	
	
	@Override
	protected void cleanup(Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		super.cleanup(context);
		
		Configuration conf = context.getConfiguration();
		
		//	현재 partition의 index(즉, 수행되는 map task의 index)
		int partition = conf.getInt("mapred.task.partition", -1);
		//	현재 map의 개수
		int numMapTasks = conf.getInt("mapred.map.tasks", -1);
		
		
		try {
			//	Decistion tree 객체
			J48 dt = new J48();
			dt.setConfidenceFactor(0.8f);
			dt.setMinNumObj( conf.getInt(ArgumentsConstants.MIN_LEAF_DATA, minLeafData));
			dt.setConfidenceFactor(conf.getFloat(ArgumentsConstants.CONFIDENCE_FACTOR, confidenceFactor));
			
			//	모델에서 제외될 속성 목록 추출 
			ArrayList<Integer> removedIndexList = new ArrayList<Integer>();
			for (Integer idx=0; idx<attrList.size(); idx++){
				if (!numericIndexSet.contains(idx) && !nominalIndexSet.contains(idx)){
					removedIndexList.add(idx);
				}
			}
			Remove rm = new Remove();
			int[] removedIndexArray = new int[removedIndexList.size()];
			for (int idx=0; idx<removedIndexList.size(); idx++){
				removedIndexArray[idx] = removedIndexList.get(idx);
			}
			rm.setAttributeIndicesArray(removedIndexArray);
			
			
			//	훈련용 속성들로만 Decistion tree 모델 생성
			FilteredClassifier fc = new FilteredClassifier();
			fc.setFilter(rm);
			fc.setClassifier(dt);
			fc.buildClassifier(dataSet);
			
			FileSystem fs = FileSystem.get(context.getConfiguration());
			
			Path treeFilePath = new Path(outputPath + File.separator + Constants.PREFIX_TREE_FILE + String.format("%05d", partition));
			if (fs.exists(treeFilePath)){
				fs.delete(treeFilePath, true);
			}
			FSDataOutputStream os = fs.create(treeFilePath);
			SerializationHelper.write(os, fc);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		
	}
	
	

	

}
