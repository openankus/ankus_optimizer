package org.ankus.optimizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.ankus.app.App;
import org.ankus.optimizer.exceoption.OptimizerException;
import org.ankus.optimizer.util.Constants;
import org.ankus.poolmgr.AlgorithmInfo;
import org.ankus.poolmgr.ManagerData;
import org.ankus.poolmgr.OptimizerConfigInfo;
import org.ankus.poolmgr.ParamInfo;
import org.ankus.poolmgr.PoolInfo;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 최적화 알고리즘 실행 메인
 *
 * @description
 * @version 0.1
 * @date  2016. 10. 17.
 * @author JungHo Kim
 *
 */
public class GAMain{

	private Logger logger = LoggerFactory.getLogger(GAMain.class);

	
	public static void main(String[] args) throws Exception {

		
	}
	
	/**
	 * 최적화 실행
	 * 
	 * @param mangerData
	 * @throws Exception
	 */
	public static void optimize(ManagerData managerData) throws Exception{
		GA ga	= null;
		
//		//GA 환경변수 설정
//		//cmd 예: -s 1 -p 10 -mG 3 -cp 0.9 -mp 0.5 -bs 5 -in D:/Programs/data/iris.arff
//		int 	seed = 10;
////		int 	popSize = 300;
////		int		maxGeneration = 500;
//		int 	popSize = 2;
//		int		maxGeneration = 0;
//		float 	crossProb = 0.9f;
//		float	mutProb = 0.5f;
////		int		binaryStrSize = 5;	// 이진문자열 크기
//		int		binaryStrSize = 3;	// 이진문자열 크기
////		String  inputFile = "D:/Programs/data/nursery_shuffle.arff";
//		String  inputFile = "/data/iris.csv";

		//GA 환경변수 설정
		OptimizerConfigInfo optConfig = managerData.getOptimizerConfigInfo();
		int 	seed = optConfig.getRandomSeed();
		int 	popSize = optConfig.getPopSize();
		int		maxGeneration = optConfig.getMaxGeneration();
		float 	crossProb = optConfig.getCrossProbabilty();
		float	mutProb = optConfig.getMutationProbabilty();
		int		binaryStrSize = optConfig.getBinaryStringSize();	// 이진문자열 크기
		String  inputFile = optConfig.getInput().getPath();
		
		
		// GA 객체 생성
		ga = new GA();
		ga.setParameters(popSize,seed,crossProb,mutProb);

		//	output root 디렉토리 설정
		Calendar now = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String outputRootBase = "/OptiOutput_"+sdf.format(now.getTime());
		
		
    	//--<입력데이터의 속성 개수 추출 및 데이터 객체 생성>---
		//	속성개수
    	int numAttri = 0;	// 속성 개수
    	int classIndex = optConfig.getInput().getClassIndex();	// 클래스 인덱스
		String delimiter = optConfig.getInput().getDelimiter();	// 데이터 컬럼 구분자
    	numAttri = getNumAttribute(inputFile, delimiter);
    	System.out.println("속성 개수:"+numAttri);

    	//	데이터 객체 생성
    	ArrayList<Integer> attributeIndexList = new ArrayList<Integer>();
    	for (int idx=0; idx<numAttri; idx++){
    		if (idx != classIndex)
    			attributeIndexList.add(idx);
    	}
    	Instances data = new Instances(inputFile, delimiter, attributeIndexList, classIndex);
    	
		
    	// 훈련 및 테스트 데이터 파일 생성 및 데이터 객체 생성
    	String trainDataPath = outputRootBase+ File.separator + "trainData.txt";
    	String testDataPath = outputRootBase+ File.separator + "testData.txt";
		try{
    		// 클래스 별 훈련 및 테스트 데이터를 골고루 추출하기 위한 토글 맵
    		Map<String,Boolean> toggleMap = new HashMap<String,Boolean>();
    		List<String> classLabelList = managerData.getOptimizerConfigInfo().getInput().getClassLabelList();
    		for (String label : classLabelList){
    			toggleMap.put(label, true);
    		}
    		
    		
    		// 클래스별 데이터개수에 따른 훈련 및 테스트 데이터 추출(훈련:테스트 = 50: 50)
    		FileSystem fs = FileSystem.get(new Configuration());
    		FSDataOutputStream trainDataOs = fs.create(new Path(trainDataPath));
    		FSDataOutputStream testDataOs = fs.create(new Path(testDataPath));
    		FSDataInputStream is = fs.open(new Path(inputFile));
    		InputStreamReader ir = new InputStreamReader(is);
    		BufferedReader br = new BufferedReader(ir);
    		String rl = null;
    		while ((rl = br.readLine()) != null){
    			StringTokenizer st = new StringTokenizer(rl, delimiter);
    			int idx=0; 
        		String label = null;
    			while (st.hasMoreTokens()){
    				String tmpValue = st.nextToken();
    				if (classIndex == idx){
    					label = tmpValue;
    					break;
    				}
    				idx++;
    			}
    			
    			if (toggleMap.get(label)){
    				// true인 경우, 훈련 데이터 쓰기
    				trainDataOs.write(rl.getBytes());
    				trainDataOs.write("\n".getBytes());
    				toggleMap.put(label, false);
    			}else{
    				// false인 경우, 훈련 데이터 쓰기
    				testDataOs.write(rl.getBytes());
    				testDataOs.write("\n".getBytes());
    				toggleMap.put(label, true);
    			}
    		}
    		br.close();
    		ir.close();
    		is.close();
    		trainDataOs.close();
    		testDataOs.close();
    		
    		
    		
    		
		}catch (IOException ex){
			throw new OptimizerException("입력데이터의 클래스별 개수 추출 중 에러발생...", ex);
		}
    	Instances trainData = new Instances(trainDataPath, delimiter, attributeIndexList, classIndex);
    	Instances testData = new Instances(testDataPath, delimiter, attributeIndexList, classIndex);
    	
    	
		Model model = new Model(data,trainData, testData);
    	//--</입력데이터의 속성 개수 추출 및 데이터 객체 생성>---
    
    	
	
//		Model model = new Model(data);
//		AlgorithmProcessExec algorithm = null;
//		int			numAlgPara = -1;
//
//		ArrayList<Parameter> paramList = new ArrayList<Parameter>();
//		switch("KNN"){
//		case "C45":
//			System.out.println("kNN");
//			algorithm = 
//					new AlgorithmProcessExec(
//							"/home/hadoop/hadoop2/bin/hadoop jar /mnt/hgfs/shared_for_vm/ankus-core2.jar C45"
//							, "-input"
//							, "-delimiter"
//							, "-output"
//							, "-indexList"
//							, "-classIndex"
//							, ","
//							, "/classifying_result/part-r-00000");
//			paramList.add(new ParameterFloat(ArgumentsConstants.PURITY,0.1f,0.5f));
//			paramList.add(new ParameterInt(ArgumentsConstants.MIN_LEAF_DATA,2,80));
//			break;
//		case "MLP":
//			System.out.println("MLP");			
//			algorithm = 
//					new AlgorithmProcessExec(
//							"/home/hadoop/hadoop2/bin/hadoop jar /mnt/hgfs/shared_for_vm/ankus-core2.jar MultilayerPerceptron"
//							, "-input"
//							, "-delimiter"
//							, "-output"
//							, "-indexList"
//							, "-classIndex"
//							, ","
//							, "/classifying_result/part-r-00000");
//			paramList.add(new ParameterFloat(ArgumentsConstants.LEARNING_RATE,0.1f,1.0f));
//			paramList.add(new ParameterFloat(ArgumentsConstants.MOMENTUN,0.1f,1.0f));
//			paramList.add(new ParameterInt(ArgumentsConstants.HIDDEN_NODE_NUM,1,50));
//			
//			break;
//		case "KNN":
//			System.out.println("KNN");	
//			algorithm = 
//					new AlgorithmProcessExec(
//							"/home/hadoop/hadoop2/bin/hadoop jar /mnt/hgfs/shared_for_vm/ankus-core2.jar kNN"
//							, "-input"
//							, "-delimiter"
//							, "-output"
//							, "-indexList"
//							, "-classIndex"
//							, ","
//							, "/classifying_result/part-r-00000");
//			paramList.add(new ParameterInt(ArgumentsConstants.K_CNT,1,50));
//			break;
//		case "RandomForest":
//			System.out.println("RandomForest");	
////			algorithm = new RForest();
//			paramList.add(new ParameterFloat("MD",0.0f,50.0f));
//			paramList.add(new ParameterFloat("numDT",1.0f,100.0f));
//			
//		};
//		Parameter[] parameters = new Parameter[paramList.size()];
//		paramList.toArray(parameters);
//		numAlgPara = parameters.length;
		
		
		//	알고리즘 실행 최종 파라메터정보 추출
		List<ParamInfo> paramInfoList = new ArrayList<ParamInfo>();
		{
			//	사용자 파라메터 입력모드를 위한 대체 파라메터 map 설정
			Map<String, ParamInfo> algParamMap = new HashMap<String, ParamInfo>();
			if (optConfig.isUserAlgorithmParamMode()){
	    		for (ParamInfo paramInfo : optConfig.getUserAlgorithmParams()){
	    			algParamMap.put(paramInfo.getParamName(), paramInfo);
	    		}
			}
			
			AlgorithmInfo algInfo = App.findalgo(managerData, optConfig.getAlgorithmName());
			List<ParamInfo> params = algInfo.getParams();
			for (ParamInfo paramInfo : params){
				if (algParamMap.containsKey(paramInfo.getParamName()))
					paramInfo = algParamMap.get(paramInfo.getParamName());
				paramInfoList.add(paramInfo);
			}
		}
    	
    	
    	//----<알고리즘 프로세스 생성>----------
		PoolInfo poolInfo = App.findpool(managerData, optConfig.getPoolName());
		AlgorithmInfo algInfo = App.findalgo(managerData, optConfig.getAlgorithmName());
		
		String hadoopHome = System.getenv("HADOOP_HOME");
		if (hadoopHome == null){
			throw new OptimizerException("HADOOP_HOME 환경변수 부재!!!");
		}
		StringBuffer trainCommandSb = new StringBuffer();
		trainCommandSb.append(hadoopHome).append(File.separator).append("bin").append(File.separator).append("hadoop");
		trainCommandSb.append(" ").append("jar");
		trainCommandSb.append(" ").append(poolInfo.getFileName());
		trainCommandSb.append(" ").append(algInfo.getTrainClassName());
		for (ParamInfo paramInfo : paramInfoList){
			if (ParamInfo.PARAM_TYPE_OPTIMIZE.equals(paramInfo.getParamType()) 
					&& paramInfo.getParamValue() != null){
				trainCommandSb.append(" ").append(paramInfo.getParamName());
				trainCommandSb.append(" ").append(paramInfo.getParamValue());
			}else if (ParamInfo.PARAM_TYPE_TRAIN_ETC.equals(paramInfo.getParamType())){
				trainCommandSb.append(" ").append(paramInfo.getParamName());
				if (paramInfo.getParamValue() != null)
					trainCommandSb.append(" ").append(paramInfo.getParamValue());
			}
		}
		String trainCommand = trainCommandSb.toString();
		StringBuffer classifyCommandSb = new StringBuffer();
		classifyCommandSb.append(hadoopHome).append(File.separator).append("bin").append(File.separator).append("hadoop");
		classifyCommandSb.append(" ").append("jar");
		classifyCommandSb.append(" ").append(poolInfo.getFileName());
		classifyCommandSb.append(" ").append(algInfo.getTrainClassName());
		for (ParamInfo paramInfo : paramInfoList){
			if (ParamInfo.PARAM_TYPE_CLASSIFY_ETC.equals(paramInfo.getParamType())){
				classifyCommandSb.append(" ").append(paramInfo.getParamName());
				if (paramInfo.getParamValue() != null)
					classifyCommandSb.append(" ").append(paramInfo.getParamValue());
			}
		}
		String classifyCommand = classifyCommandSb.toString();
		String paramInputName = getParamInfo(ParamInfo.PARAM_TYPE_INPUT, paramInfoList).getParamName();
		if (paramInputName == null) paramInputName = "-input";
		String paramDelimiterName = getParamInfo(ParamInfo.PARAM_TYPE_DELIMITER, paramInfoList).getParamName();
		if (paramDelimiterName == null) paramDelimiterName = "-delimiter";
		String paramOutputName = getParamInfo(ParamInfo.PARAM_TYPE_OUTPUT, paramInfoList).getParamName();
		if (paramOutputName == null) paramOutputName = "-ouput";
		String paramIndexListName = getParamInfo(ParamInfo.PARAM_TYPE_INDEX_LIST, paramInfoList).getParamName();
		if (paramIndexListName == null) paramIndexListName = "-indexList";
		String paramClassIndexName = getParamInfo(ParamInfo.PARAM_TYPE_CLASS_INDEX, paramInfoList).getParamName();;
		if (paramClassIndexName == null) paramClassIndexName = "-classIndex";
		String paramTrainedModelPathName = getParamInfo(ParamInfo.PARAM_TYPE_MODEL_PATH, paramInfoList).getParamName();;
		if (paramClassIndexName == null) paramClassIndexName = "-modelPath";
		String paramValueDelimiter = algInfo.getParamValueDelimiter();
		if (paramValueDelimiter == null) paramValueDelimiter = ",";
		String classifyOutputRelPath = algInfo.getClassifyOutputRelPath();
		if (classifyOutputRelPath == null) classifyOutputRelPath = "/classifying_result/part-r-00000";
		String modelAbsPath = algInfo.getModelAbsPath();
		String modelRelPath = algInfo.getModelRelPath();
		AlgorithmProcessExec algorithm = 
				new AlgorithmProcessExec(
						trainCommand, 
						classifyCommand, 
						paramInputName, 
						paramDelimiterName, 
						paramOutputName, 
						paramIndexListName, 
						paramClassIndexName,
						paramTrainedModelPathName,
						paramValueDelimiter, 
						classifyOutputRelPath,
						modelAbsPath,
						modelRelPath);
    	//----</알고리즘 프로세스 생성>----------
		
		
    	//----<알고리즘 최적화 파라메터 설정>----------
		ArrayList<Parameter> paramList = new ArrayList<Parameter>();
		for (ParamInfo paramInfo : paramInfoList){
			
			if (ParamInfo.PARAM_TYPE_OPTIMIZE.equals(paramInfo.getParamType())){
				if (ParamInfo.DATA_TYPE_INT.equals(paramInfo.getDataType()) && paramInfo.getParamValue() == null){
					paramList.add(
							new ParameterInt(
									paramInfo.getParamName(), 
									Integer.parseInt(paramInfo.getMin()), 
									Integer.parseInt(paramInfo.getMax())));
				}else if (ParamInfo.DATA_TYPE_FLOAT.equals(paramInfo.getDataType()) && paramInfo.getParamValue() == null){
					paramList.add(
							new ParameterFloat(
									paramInfo.getParamName(), 
									Float.parseFloat(paramInfo.getMin()), 
									Float.parseFloat(paramInfo.getMax())));
				}
			}
			paramInfo.getParamName();
		}
		Parameter[] parameters = new Parameter[paramList.size()];
		paramList.toArray(parameters);
		int	numAlgPara = parameters.length;
    	//----</알고리즘 최적화 파라메터 설정>----------
		
		
		
		
		
		//--<GA 설정정보 출력>---
		{
			FileSystem fs = FileSystem.get(new Configuration());
			FSDataOutputStream os = fs.create(new Path(outputRootBase+"/"+Constants.FILE_NAME_GA_INFO));

			StringBuffer sb = new StringBuffer();
			sb.append("pool").append(Constants.DELIMITER_GA).append(poolInfo.getPoolName()).append("\n");
			sb.append("algorithmName").append(Constants.DELIMITER_GA).append(algInfo.getAlgorithmName()).append("\n");
			sb.append("trainClassName").append(Constants.DELIMITER_GA).append(algInfo.getTrainClassName()).append("\n");
			sb.append("classifyClassName").append(Constants.DELIMITER_GA).append(algInfo.getClassifyClassName()).append("\n");
			sb.append("classifyOutputMode").append(Constants.DELIMITER_GA).append(algInfo.getClassifyOutputMode()).append("\n");
			sb.append("classifyOutputRelPath").append(Constants.DELIMITER_GA).append(algInfo.getClassifyOutputRelPath()).append("\n");
			if (algInfo.getModelAbsPath() != null){
				sb.append("modelAbsPath").append(Constants.DELIMITER_GA).append(algInfo.getModelAbsPath()).append("\n");
			}else{
				sb.append("modelRelPath").append(Constants.DELIMITER_GA).append(algInfo.getModelRelPath()).append("\n");
			}
			sb.append("seed").append(Constants.DELIMITER_GA).append(seed).append("\n");
			sb.append("popSize").append(Constants.DELIMITER_GA).append(popSize).append("\n");
			sb.append("maxGeneration").append(Constants.DELIMITER_GA).append(maxGeneration).append("\n");
			sb.append("crossProb").append(Constants.DELIMITER_GA).append(crossProb).append("\n");
			sb.append("mutProb").append(Constants.DELIMITER_GA).append(mutProb).append("\n");
			sb.append("binaryStrSize").append(Constants.DELIMITER_GA).append(binaryStrSize).append("\n");
			sb.append("inputFile").append(Constants.DELIMITER_GA).append(inputFile).append("\n");
			sb.append("numAttri").append(Constants.DELIMITER_GA).append(numAttri).append("\n");
			sb.append("classIndex").append(Constants.DELIMITER_GA).append(classIndex).append("\n");
			sb.append("numAlgPara").append(Constants.DELIMITER_GA).append(numAlgPara).append("\n");
			sb.append("optimizeParameters").append(Constants.DELIMITER_GA);
			for (int idx=0; idx<parameters.length; idx++){
				Parameter param = parameters[idx];
				sb.append("param[");
				sb.append(param.getName());
				sb.append(":");
				sb.append(param.getDataType());
				sb.append("]=");
				sb.append("{");
				if (param instanceof ParameterInt){
					ParameterInt tmp = (ParameterInt) param;
					sb.append("minVal:");
					sb.append(tmp.getMinVal());
					sb.append(Constants.DELIMITER_GA_VALUE);
					sb.append("maxVal:");
					sb.append(tmp.getMaxVal());
				}else if (param instanceof ParameterFloat){
					ParameterFloat tmp = (ParameterFloat) param;
					sb.append("minVal:");
					sb.append(tmp.getMinVal());
					sb.append(Constants.DELIMITER_GA_VALUE);
					sb.append("maxVal:");
					sb.append(tmp.getMaxVal());
				}
				sb.append("}");
				if (idx < parameters.length -1)
					sb.append(Constants.DELIMITER_GA_PARAM);
			}
			sb.append("\n");
			os.write(sb.toString().getBytes());
			
			os.close();
		}
		//--</GA 설정정보 출력>---
		
		

		
		if(numAlgPara != -1){
			
			int generation = 0;
			
			Date startTime = Calendar.getInstance().getTime();
			
			// 개체초기화
			ga.setInitialPopulation(numAttri,numAlgPara,binaryStrSize,classIndex);
			//System.out.println("개체 초기화 완료...");
			
			// 개체평가
			String outputBase = outputRootBase+"/generation_"+String.format("%05d", generation);
			String popInfoListPath =	ga.evaluation(model,algorithm,parameters, outputBase);
			
			// 적합도 내림차순에 의한 개체평가결과 출력
			String sortedPopInfoListPath = outputBase+"/"+Constants.FILE_NAME_ORDERED_POP_INFO_LIST;
			ga.writeSortedPopInfoList(popInfoListPath, sortedPopInfoListPath);
			
			// 세대 정보 출력 
			String genInfoListPath = outputRootBase+"/"+Constants.FILE_NAME_GENERATION_TOP_LIST;
			writeGenerationInfo(generation, sortedPopInfoListPath, genInfoListPath);
			
			
			
			//System.out.println(_ga.toStringChroms());
			//System.out.println("----- elitist()");
			//System.out.println(_ga.getelitist().toStringGene()+" - "+_ga.getelitist().getFitness());
			//System.exit(1);

			System.out.println(" ************************************ "+generation+" 세대: "+ga.getelitist().getFitness());
			//System.out.println(_ga.getelitist().toStringGene());
			//System.exit(1);
			
			do{
				
				generation++;

				// 개체선택
				ga.selectMethod();
				//System.out.println("개체 선택 완료..");
			
				// 교배 확률
				ga.crossover();
				//System.out.println("개체 교배 완료..");
			
				// 돌연변이 확률
				ga.mutation(classIndex);
				//System.out.println("개체 돌연변이 완료..");

				
				// 개체평가
				//System.out.println(_ga.toStringChroms());
				outputBase = outputRootBase+"/generation_"+String.format("%05d", generation);
				popInfoListPath = ga.evaluation(model,algorithm,parameters, outputBase);
				
				// 적합도 내림차순에 의한 개체평가결과 출력
				sortedPopInfoListPath = outputBase+"/"+Constants.FILE_NAME_ORDERED_POP_INFO_LIST;
				ga.writeSortedPopInfoList(popInfoListPath, sortedPopInfoListPath);
				//System.out.println("개체평가 완료..");
				
				
				System.out.println(" ************************************ "+generation+" 세대: "+ga.getelitist().getFitness());

				if(generation == maxGeneration){
					System.out.println(ga.getelitist().toStringGene());
					ga.getelitist().toStringModel();
				}
				
				// 세대 정보 출력 
				writeGenerationInfo(generation, sortedPopInfoListPath, genInfoListPath);
				
				
	
			}while(generation <= maxGeneration);
			
			Date endTime = Calendar.getInstance().getTime();
			
			long lTime = (long) ((endTime.getTime() - startTime.getTime())/(60.0*1000.0));
			System.out.println("Start Time: "+ startTime.toString());
			System.out.println("End Time: "+endTime.toString());
			System.out.println("TIME : " + lTime + "분");
			
			//	소요시간 출력
			{
				FileSystem fs = FileSystem.get(new Configuration());
				FSDataOutputStream os = fs.append(new Path(outputRootBase+"/"+Constants.FILE_NAME_GA_INFO));
				
				StringBuffer sb = new StringBuffer();
				sb.append("StartTime").append(Constants.DELIMITER_GA).append(startTime).append("\n");
				sb.append("EndTime").append(Constants.DELIMITER_GA).append(endTime).append("\n");
				sb.append("SpentTime(min)").append(Constants.DELIMITER_GA).append(lTime).append("\n");
				os.write(sb.toString().getBytes());

				os.close();
			}

			
		}else{
			System.out.println("오류 00001");
		}
	}
	
	/**
	 * 파라메터 유형에 해당하는 파라메터정보 반환
	 * 
	 * @param paramType 검색하려는 파라메터 유형
	 * @param paramInfoList 파라메터정보 목록
	 * @return
	 */
	private static ParamInfo getParamInfo(String paramType, List<ParamInfo> paramInfoList){
		ParamInfo retParamInfo = null;
		for (ParamInfo paramInfo : paramInfoList){
			if (paramType.equals(paramInfo.getParamType())){
				retParamInfo = paramInfo;
				break;
			}
		}
		return retParamInfo;
	}
	
	
	/**
	 * 입력파일의 속성 개수 반환
	 * 
	 * @param inputFile 입력파일
	 * @param delimiter 레코드 컬럼 구분자
	 * @return
	 * @throws Exception
	 */
	private static int getNumAttribute(String inputFile, String delimiter) throws Exception{

		int numAttri = 0;
		
		FileSystem fs = FileSystem.get(new Configuration());
        FSDataInputStream fin = fs.open(new Path(inputFile));
        BufferedReader br = new BufferedReader(new InputStreamReader(fin, Constants.UTF8));
        String rl = br.readLine();
    	StringTokenizer st = new StringTokenizer(rl, delimiter);
    	while (st.hasMoreTokens()){
    		st.nextToken();
    		numAttri++;
    	}
    	br.close();
    	fin.close();
		
    	return numAttri;
	}
	
	
	/**
	 * 세대정보를 출력
	 * 
	 * @param generation	세대수
	 * @param sortedPopInfoListPath	정렬된 개체정보목록 파일
	 * @param outputPath	출력 파일 경로
	 * @throws Exception
	 */
	private static void writeGenerationInfo(int generation, String sortedPopInfoListPath, String outputPath) throws Exception{

		//--<1. 최상의 개체정보 취득>--
		//	최상위 개체정보 문자열 얻기
		FileSystem fs = FileSystem.get(new Configuration());
		FSDataInputStream fin = fs.open(new Path(sortedPopInfoListPath));
		InputStreamReader ir = new InputStreamReader(fin);
		BufferedReader br = new BufferedReader(ir);
		String topPopInfo = br.readLine();
		br.close();
		ir.close();
		fin.close();
		
		//	개체정보 문자열
		String[] splits = topPopInfo.split(Constants.DELIMITER_GA);
		String topGene = splits[0];
		String topFitness = splits[1];
		String topParamList = splits[2];
		String topAttriList = splits[3];
		//--</1. 최상의 개체정보 취득>--

		
		
		//--<2.세대정보 출력>----
		Path genInfoListPath = new Path(outputPath);
		FSDataOutputStream os = null;
		if (fs.exists(genInfoListPath)){
			os = fs.append(genInfoListPath);
		}else{
			os = fs.create(genInfoListPath);
		}
		os.write(String.format("%05d", generation).getBytes());
		os.write(Constants.DELIMITER_GA.getBytes());
		os.write(topPopInfo.getBytes());
		os.write("\n".getBytes());
		os.close();
		//--</2. 세대정보 출력>----
	}
	
}
