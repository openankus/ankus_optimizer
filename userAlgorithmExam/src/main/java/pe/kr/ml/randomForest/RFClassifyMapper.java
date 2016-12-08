package pe.kr.ml.randomForest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import pe.kr.ml.randomForest.util.ArgumentsConstants;
import pe.kr.ml.randomForest.util.Constants;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * 토리모델들을 로딩하여 동일한 레코드에 대한 분류결과 출력
 *
 * @description
 * @version 0.1
 * @date  2016. 11. 17.
 * @author JungHo Kim
 *
 */
public class RFClassifyMapper  extends Mapper<LongWritable, Text, LongWritable, Text>{
	
	
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
	 * 모델 경로
	 */
	String modelPath;
	
	/**
	 * 출력 경로
	 */
	String outputPath;
	
	/**
	 * 속성정보 목록
	 */
	ArrayList<Attribute> attrList;

	/**
	 * tree model 목록
	 */
	LinkedHashMap<String, Classifier> classifierMap = new LinkedHashMap<String, Classifier>();
	
	/**
	 * 클래스 레이블 목록
	 */
	ArrayList<String> classLabelList = new ArrayList<String>();
	
	/**
	 * 해당 split에 훈련 데이터 집합
	 */
	Instances dataSet;
	
	
	@Override
	protected void setup(Mapper<LongWritable, Text, LongWritable, Text>.Context context)
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
		modelPath = config.get(ArgumentsConstants.TRAINED_MODEL);
		
		
		//	데이터의 전체 속성 정보 추출
		FileSystem fs = FileSystem.get(context.getConfiguration());
		InputStreamReader isr = new InputStreamReader(fs.open(new Path(dataFormatPath)));
		Instances dataFormat = new Instances(isr);
		attrList = new ArrayList<Attribute>();
		for (int idx=0; idx<dataFormat.numAttributes(); idx++){
			attrList.add(dataFormat.attribute(idx));
		}
		
		//	클래스 레이블 목록 추출
		Enumeration<Object> classLabelEnum = attrList.get(Integer.parseInt(classIndex)).enumerateValues();
		while (classLabelEnum.hasMoreElements()){
			String value = (String) classLabelEnum.nextElement();
			classLabelList.add(value);
		}
		
		
		// dataSet 객체 생성
		dataSet = new Instances("Rel", attrList, 20);
		
		// tree 모델 로드
		RemoteIterator<LocatedFileStatus> treeModelIterator = fs.listFiles(new Path(modelPath), false);
		while (treeModelIterator.hasNext()){
			LocatedFileStatus lfs = treeModelIterator.next();
			if (lfs.getPath().getName().startsWith(Constants.PREFIX_TREE_FILE)){
				String fileName = lfs.getPath().getName();
				ObjectInputStream ois = new ObjectInputStream(fs.open(lfs.getPath()));
				try{
					Classifier cls = (Classifier) ois.readObject();
					classifierMap.put(fileName, cls);
				}catch(ClassNotFoundException ex){
					RFLogger.println("Fail to load a tree model : "+ex.getMessage());
				}
				
			}
		}
		
//		RFLogger.println("트리개수: "+classifierMap.size());
		
		
	}
	
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, LongWritable, Text>.Context context)
			throws IOException, InterruptedException {

		
		//	테스트 용 데이터 레코드 만들기
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
		
		//	테스트 데이터 레코드를 위한 임시 데이터셋 생성
		Instances dataUnlabeled = new Instances("TestInstances", attrList, 0);
		try{
			dataUnlabeled.add(instance);
			dataUnlabeled.setClassIndex(Integer.parseInt(classIndex));        
		}catch(Exception ex){
			RFLogger.println("Fail to create fake dataset : "+ex.getMessage());
		}

//		RFLogger.println(value.toString());
		
//		double correctValue = instance.value(Integer.parseInt(classIndex));
		
		//	로드된 트리모델의 분류결과값을 write
		Set<Entry<String, Classifier>> entrySet = classifierMap.entrySet();
		for (Entry<String, Classifier> entry : entrySet){
			try {
				
				double predictValue = entry.getValue().classifyInstance(dataUnlabeled.firstInstance());
				String classLabel = classLabelList.get((int) predictValue);

				StringBuffer sb = new StringBuffer();
				sb.append(value.toString());
				sb.append(",");
				sb.append(classLabel);
				
//				RFLogger.println(sb.toString());
				interOutputVal.set(sb.toString().getBytes());
				context.write(key, interOutputVal);
			}catch(Exception ex){
				RFLogger.println("Fail to classify data ( treeModel="+entry.getKey()+") : "+ex.getMessage());
			}
		}
		
		
		//	data 추가
		dataSet.add(instance);
	}

	
}
