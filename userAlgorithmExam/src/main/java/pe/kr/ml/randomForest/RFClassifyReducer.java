package pe.kr.ml.randomForest;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import pe.kr.ml.randomForest.util.ArgumentsConstants;

/**
 * 데이터 레코드별 분류결과값들을 병합하는 기능
 *
 * @description
 * @version 0.1
 * @date  2016. 11. 17.
 * @author JungHo Kim
 *
 */
public class RFClassifyReducer extends Reducer<LongWritable, Text, NullWritable, Text>{

	/**
	 * 출력 값
	 */
	Text outputVal = new Text();
	
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
	
	@Override
	protected void setup(Reducer<LongWritable, Text, NullWritable, Text>.Context context)
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
	
	}
	
	
	@Override
	protected void reduce(LongWritable key, Iterable<Text> values,
			Reducer<LongWritable, Text, NullWritable, Text>.Context context) throws IOException, InterruptedException {
		
		//	Voting 방식의 결과값 최합
		HashMap<String, Integer> voteMap = new HashMap<String, Integer>();
		
		//	데이터레코드를 담은 문자열
		StringBuffer dataRecSb = new StringBuffer();

		//	예측된 분류결과값에 대한 회수 추출
		int cntRec = 0;
		for (Text value : values){
			StringTokenizer st = new StringTokenizer(value.toString(), delimiter);
			String predictClassVal = null;
			
			if (cntRec == 0){
				while (st.hasMoreTokens()){
					String tmpVal = st.nextToken();
					if (!st.hasMoreTokens()){
						predictClassVal = tmpVal;
					}else{
						dataRecSb.append(tmpVal);
						dataRecSb.append(delimiter);
					}
				}
			}else{
				while (st.hasMoreTokens()){
					String tmpVal = st.nextToken();
					if (!st.hasMoreTokens()){
						predictClassVal = tmpVal;
					}
				}
			}
			
			
			Integer count = voteMap.get(predictClassVal);
			if (count == null) count = 1;
			else count++;
			voteMap.put(predictClassVal, count);
			cntRec++;
		}

		//	최고 득표율의 분류결과값을 최종값으로 인쇄
		String topClassVal = null;
		int topCount = -1;
//		StringBuffer sb = new StringBuffer();
		for (Entry<String, Integer> entry : voteMap.entrySet()){
//			sb.append(entry.getKey());
//			sb.append("(");
//			sb.append(String.valueOf(entry.getValue()));
//			sb.append(")");
//			sb.append(",");
			if (topCount < entry.getValue()){
				topClassVal = entry.getKey();
				topCount = entry.getValue();
			}
		}
//		RFLogger.println(sb.toString());
		
		//	최총 출력
		dataRecSb.append(topClassVal);
		outputVal.set(dataRecSb.toString().getBytes());
		context.write(NullWritable.get(), outputVal);
	
	}

}
