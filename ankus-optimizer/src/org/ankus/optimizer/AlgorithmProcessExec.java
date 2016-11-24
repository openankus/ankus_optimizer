package org.ankus.optimizer;



import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.ankus.optimizer.exceoption.OptimizerException;
import org.ankus.optimizer.mapreduce.ClassifyResultCheckLabelWithInputDriver;
import org.ankus.optimizer.mapreduce.ClassifyResultCheckLabelWithInputMapper;
import org.ankus.optimizer.util.ArgumentsConstants;
import org.ankus.optimizer.util.Constants;
import org.ankus.poolmgr.AlgorithmInfo;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 커맨드 라인 기반 알고리즘 실행 클래스
 * 
 *
 * @description
 * @version 0.1
 * @date  2016. 10. 17.
 * @author JungHo Kim
 *
 */
public class AlgorithmProcessExec implements Algorithm{
	
	
	private Logger logger = LoggerFactory.getLogger(AlgorithmProcessExec.class);

	/**
	 * 정확성
	 */
	public double accuracy = -1;
	
	/**
	 * 훈련명령어
	 */
	String trainCommand = null;
	
	/**
	 * 데이터분류명령어
	 */
	String classifyCommand = null;
	
	/**
	 * 훈련 입력 데이터 파라메터 이름
	 */
	String paramInputName = null;
	
	/**
	 * 테스트 입력 데이터 파라메터 이름
	 */
	String paramInputTestName = null;
	
	/**
	 * 입력 데이터 컬럼 구분자 파라메터 이름
	 */
	String paramDelimiterName = null;
	
	/**
	 * 출력 디렉터리 파라메터 이름
	 */
	String paramOutputName = null;
	
	/**
	 * 속성 인덱스 목록 파라메터 이름
	 */
	String paramIndexListName = null;
	
	/**
	 * 수치형 속성 인덱스 목록 파라메터 이름
	 */
	String paramNumericIndexListName = null;
	
	/**
	 * 기호형 속성 인덱스 목록 파라메터 이름
	 */
	String paramNominalIndexListName = null;
	
	/**
	 * 클래스 인덱스 파라메터 이름
	 */
	String paramClassIndexName = null;
	
	/**
	 * 파라메터값 구분자
	 */
	String paramValueDelimiter = null;
	
	/**
	 * 훈련된 모델 파라메터 이름
	 */
	String paramModelPathName = null;
	
	/**
	 * 분류결과 출력파일에 대한 상대경로(population 출력 디렉터리 기준) 
	 */
	String classifyOutputRelPath = null;
	
	/**
	 * 훈련된 모델 출력파일의 절대경로
	 */
	String modelAbsPath = null;

	/**
	 * 훈련된 모델 출력파일의 상대경로(population 출력 디렉터리 기준)
	 */
	String modelRelPath = null;
	
	/**
	 * 파라메터 형식
	 */
	String paramFormat = null;
	
	/**
	 * 분류결과 출력파일 방식
	 */
	String classifyOutputMode = null;
	
	
	public AlgorithmProcessExec(
			String paramFormat
			, String trainCommand
			, String classifyCommand
			, String paramInputName
			, String paramInputTestName
			, String paramDelimiterName
			, String paramOutputName
			, String paramIndexListName
			, String paramNumericIndexListName
			, String paramNominalIndexListName
			, String paramClassIndexName
			, String paramModelPathName
			, String paramValueDelimiter
			, String classifyOutputMode
			, String classifyOutputRelPath
			, String modelAbsPath
			, String trainedModelRelPath) {
		this.paramFormat = paramFormat;
		this.trainCommand = trainCommand;
		this.classifyCommand = classifyCommand;
		this.paramInputName = paramInputName;
		this.paramInputTestName = paramInputTestName;
		this.paramDelimiterName = paramDelimiterName;
		this.paramOutputName = paramOutputName;
		this.paramIndexListName = paramIndexListName;
		this.paramNumericIndexListName = paramNumericIndexListName;
		this.paramNominalIndexListName = paramNominalIndexListName;
		this.paramClassIndexName = paramClassIndexName;
		this.paramModelPathName = paramModelPathName;
		this.paramValueDelimiter = paramValueDelimiter;
		this.classifyOutputMode = classifyOutputMode;
		this.classifyOutputRelPath = classifyOutputRelPath;
		this.modelAbsPath = modelAbsPath;
		this.modelRelPath = trainedModelRelPath;
	}
	
	/**
	 * 복사생성자
	 * @param algorithmProcessExec
	 */
	public AlgorithmProcessExec(AlgorithmProcessExec algorithmProcessExec){
		this.paramFormat = algorithmProcessExec.paramFormat;
		this.trainCommand = algorithmProcessExec.trainCommand;
		this.classifyCommand = algorithmProcessExec.classifyCommand;
		this.paramInputName = algorithmProcessExec.paramInputName;
		this.paramInputTestName = algorithmProcessExec.paramInputTestName;
		this.paramDelimiterName = algorithmProcessExec.paramDelimiterName;
		this.paramOutputName = algorithmProcessExec.paramOutputName;
		this.paramIndexListName = algorithmProcessExec.paramIndexListName;
		this.paramNumericIndexListName = algorithmProcessExec.paramNumericIndexListName;
		this.paramNominalIndexListName = algorithmProcessExec.paramNominalIndexListName;
		this.paramClassIndexName = algorithmProcessExec.paramClassIndexName;
		this.paramModelPathName = algorithmProcessExec.paramModelPathName;
		this.paramValueDelimiter = algorithmProcessExec.paramValueDelimiter;
		this.classifyOutputMode = algorithmProcessExec.classifyOutputMode;
		this.classifyOutputRelPath = algorithmProcessExec.classifyOutputRelPath;
		this.modelAbsPath = algorithmProcessExec.modelAbsPath;
		this.modelRelPath = algorithmProcessExec.modelRelPath;
	}
	
	public String getTrainCommand() {
		return trainCommand;
	}

	public String getClassifyCommand() {
		return classifyCommand;
	}

	public String getParamInputName() {
		return paramInputName;
	}

	public String getParamInputTestName() {
		return paramInputTestName;
	}

	public String getParamDelimiterName() {
		return paramDelimiterName;
	}


	public String getParamOutputName() {
		return paramOutputName;
	}


	public String getParamIndexListName() {
		return paramIndexListName;
	}

	public String getParamNumericIndexListName() {
		return paramNumericIndexListName;
	}

	public String getParamNominalIndexListName() {
		return paramNominalIndexListName;
	}

	public String getParamClassIndexName() {
		return paramClassIndexName;
	}
	

	public String getParamValueDelimiter() {
		return paramValueDelimiter;
	}
	
	public String getClassifyOutputMode() {
		return classifyOutputMode;
	}

	public String getClassifyOutputRelPath() {
		return classifyOutputRelPath;
	}

	public String getParamModelPathName() {
		return paramModelPathName;
	}

	public String getModelAbsPath() {
		return modelAbsPath;
	}

	public String getModelRelPath() {
		return modelRelPath;
	}

	public String getParamFormat() {
		return paramFormat;
	}

	@Override
	public void method(Instances data, Instances trainData, Instances testData, Parameter[] parameters, String outputBase) throws OptimizerException{
		
		//--<1. 모델 학습>------
		//	훈련출력 디렉터리
		String trainOutputBase = outputBase+File.separator+"trainOutput";
		{
			ArrayList<String> cmdElementList = new ArrayList<String>();

			//	명령어 설정
			{
				String[] splits = trainCommand.split(" ");
				for (String split : splits){
					cmdElementList.add(split);
				}
			}
			//	파라메터 설정
			//	- 입력파일 경로
			cmdElementList.add(getParamInputName());
			cmdElementList.add(trainData.getDataFilePath());
			//	- 출력파일의 디렉터리 경로
			cmdElementList.add(getParamOutputName());
			cmdElementList.add(trainOutputBase);
			//	파라메터 형식에 따른 속성정보 설정
			if (AlgorithmInfo.PARAM_FORMAT_ANKUS.equals(getParamFormat()) 
					|| AlgorithmInfo.PARAM_FORMAT_USER.equals(getParamFormat())){
				//	Ankus 및 사용자 구현의 경우, .....

				//	- delimiter
				cmdElementList.add(getParamDelimiterName());
				cmdElementList.add(trainData.getDelimiter());

				
				
				//	- 모델생성에 사용된 속성인덱스 목록
				if (getParamIndexListName() != null){
					//	속성의 자료형을 구분하지 않는 경우, 
					
					cmdElementList.add(getParamIndexListName());

					StringBuffer attrIndexList = new StringBuffer();
					for (int idx=0; idx<trainData.getSizeOfAttributeIndexList(); idx++){
						attrIndexList.append(trainData.getAttributeIndexAt(idx));
						if (idx < trainData.getSizeOfAttributeIndexList() - 1)
							attrIndexList.append(getParamValueDelimiter());
					}
					cmdElementList.add(attrIndexList.toString());
				}else{
					//	속성의 자료형을 구분하는 경우, 
					
					if (getParamNumericIndexListName() != null){
						//	수치형 속성의 경우,

						cmdElementList.add(getParamNumericIndexListName());
						
						StringBuffer attrIndexList = new StringBuffer();
						for (int idx=0; idx<trainData.getNumericIndexList().size(); idx++){
							Integer attrIndex = trainData.getNumericIndexList().get(idx);
							attrIndexList.append(attrIndex);
							if (idx < trainData.getNumericIndexList().size() - 1)
								attrIndexList.append(getParamValueDelimiter());
						}
						cmdElementList.add(attrIndexList.toString());
					}
					
					if (getParamNominalIndexListName() != null){
						//	기호형 속성의 경우,

						cmdElementList.add(getParamNominalIndexListName());
						
						StringBuffer attrIndexList = new StringBuffer();
						for (int idx=0; idx<trainData.getNominalIndexList().size(); idx++){
							Integer attrIndex = trainData.getNominalIndexList().get(idx);
							attrIndexList.append(attrIndex);
							if (idx < trainData.getNominalIndexList().size() - 1)
								attrIndexList.append(getParamValueDelimiter());
						}
						cmdElementList.add(attrIndexList.toString());
					}
					
				}
				
				//	- class index
				cmdElementList.add(getParamClassIndexName());
				cmdElementList.add(trainData.getClassIndex().toString());
				
			}else if (AlgorithmInfo.PARAM_FORMAT_MAHOUT_RF.equals(getParamFormat())){
				//	Mahout random forest의 경우, .....
				//	속성정보 파일 생성(attribute.info)
				String attrInfoPath = outputBase + File.separator + "attribute.info";
				try{
					FileSystem fs = FileSystem.get(new Configuration());
					if (!fs.exists(new Path(attrInfoPath))){
						writeAttrInfo(data, attrInfoPath);
					}
				}catch(IOException ex){
					throw new OptimizerException("Fail to check attribute info of the file(= "+data.getDataFilePath()+")!!! ", ex);
				}
				// - 속성정보파일 설정
				cmdElementList.add("-ds");
				cmdElementList.add(attrInfoPath);
				
				// mahout 버그처리(문제상황: ~/trainOutput 경로가 이미 존재한다며 실행오류 발생)
				try{
					FileSystem fs = FileSystem.get(new Configuration());
					Path bugPath = new Path("trainOutput");
					fs.delete(bugPath);
				}catch(IOException ex){
					ex.printStackTrace();
				}
				
			}
			//	- GA알고리즘에 의해 선택된 파라메터 설정
			for (Parameter param : parameters){
				cmdElementList.add(param.getName());
				cmdElementList.add(param.getValueString());
			}

			
			
			//	모델 학습
			String[] command = new String[cmdElementList.size()];
			cmdElementList.toArray(command);
			
			StringBuffer sb = new StringBuffer();
			for (String cmd : command){
				sb.append(cmd);
				sb.append(" ");
			}
			OptimizerLogger.println("Training shell command:"+sb.toString());
			try{
				//	프로세스 실행
				Process process = new ProcessBuilder(command).start();
				
				//	프로세스 정상 로그 출력 스레드 실행
				InputStream inputStream = process.getInputStream();
				SysOutThread sysOutThread = new SysOutThread(inputStream);
				sysOutThread.start();
				
				//	프로세스 에러 로그 출력 스레드 실행
				InputStream errorStream = process.getErrorStream();
				SysOutThread errorOutThread = new SysOutThread(process.getErrorStream());
				errorOutThread.start();
				
				//	프로세스  실행 완료까지 대기
				int res = process.waitFor();
				//	모델을 생성할 필요가 없는 알고리즘들로 인해 주석처리(for kNN)
//				if (res != 0){
//					throw new OptimizerException("Fail to process a shell command !!!\n"
//							+ "Please check out the parameter settings.\n"
//							+ "> shell command: "+ getCommandLineString(command));
//				}else{
//					OptimizerLogger.println("Complete to process a shell command\n"
//							+ "> shell command: "+ getCommandLineString(command));
//				}
				OptimizerLogger.println("Complete to process a shell command\n"
						+ "> shell command: "+ getCommandLineString(command));
				
				
				inputStream.close();
				errorStream.close();
				
			}catch(IOException ex){
				throw new OptimizerException("Fail to train", ex);
			}catch(InterruptedException ex){
				throw new OptimizerException("Fail to train", ex);
			}
			
		}

		
		//--</1. 모델 학습>------
		
		logger.debug("학습 완료!");
		

		//--<2. 모델 평가>-----
		//	분류출력  디렉터리
		String classifyOutputBase = outputBase+File.separator+"classifyOutput";
		// 분류결과 출력
		{
			ArrayList<String> cmdElementList = new ArrayList<String>();

			//	명령어 설정
			{
				String[] splits = classifyCommand.split(" ");
				for (String split : splits){
					cmdElementList.add(split);
				}
			}
			//	파라메터 설정
			//	- 모델경로
			cmdElementList.add(getParamModelPathName());
			if (getModelAbsPath() != null){
				cmdElementList.add(getModelAbsPath());
			}else {
				cmdElementList.add(trainOutputBase + File.separator + getModelRelPath());
			}
			//	- 입력파일 경로
			cmdElementList.add(getParamInputTestName());
			cmdElementList.add(testData.getDataFilePath());
			//	- 출력파일의 디렉터리 경로
			if (AlgorithmInfo.PARAM_FORMAT_ANKUS.equals(getParamFormat()) 
					|| AlgorithmInfo.PARAM_FORMAT_USER.equals(getParamFormat())){
				cmdElementList.add(getParamOutputName());
				cmdElementList.add(classifyOutputBase);
			}else if (AlgorithmInfo.PARAM_FORMAT_MAHOUT_RF.equals(getParamFormat())){
				cmdElementList.add(getParamOutputName());
				cmdElementList.add(classifyOutputBase + File.separator + "classifying_result" + File.separator + "part-r-00000");
			}
			//	파라메터 형식에 따른 속성정보 설정
			if (AlgorithmInfo.PARAM_FORMAT_ANKUS.equals(getParamFormat()) 
					|| AlgorithmInfo.PARAM_FORMAT_USER.equals(getParamFormat())){
				//	Ankus 및 사용자 구현의 경우, .....

				//	- delimiter
				cmdElementList.add(getParamDelimiterName());
				cmdElementList.add(testData.getDelimiter());

				
				
				//	- 모델생성에 사용된 속성인덱스 목록
				if (getParamIndexListName() != null){
					//	속성의 자료형을 구분하지 않는 경우, 
					
					cmdElementList.add(getParamIndexListName());

					StringBuffer attrIndexList = new StringBuffer();
					for (int idx=0; idx<testData.getSizeOfAttributeIndexList(); idx++){
						attrIndexList.append(testData.getAttributeIndexAt(idx));
						if (idx < testData.getSizeOfAttributeIndexList() - 1)
							attrIndexList.append(getParamValueDelimiter());
					}
					cmdElementList.add(attrIndexList.toString());
				}else{
					//	속성의 자료형을 구분하는 경우, 
					
					if (getParamNumericIndexListName() != null){
						//	수치형 속성의 경우,

						cmdElementList.add(getParamNumericIndexListName());
						
						StringBuffer attrIndexList = new StringBuffer();
						for (int idx=0; idx<testData.getNumericIndexList().size(); idx++){
							Integer attrIndex = testData.getNumericIndexList().get(idx);
							attrIndexList.append(attrIndex);
							if (idx < testData.getNumericIndexList().size() - 1)
								attrIndexList.append(getParamValueDelimiter());
						}
						cmdElementList.add(attrIndexList.toString());
					}
					
					if (getParamNominalIndexListName() != null){
						//	기호형 속성의 경우,

						cmdElementList.add(getParamNominalIndexListName());
						
						StringBuffer attrIndexList = new StringBuffer();
						for (int idx=0; idx<testData.getNominalIndexList().size(); idx++){
							Integer attrIndex = testData.getNominalIndexList().get(idx);
							attrIndexList.append(attrIndex);
							if (idx < testData.getNominalIndexList().size() - 1)
								attrIndexList.append(getParamValueDelimiter());
						}
						cmdElementList.add(attrIndexList.toString());
					}
					
				}
				
				//	- class index
				cmdElementList.add(getParamClassIndexName());
				cmdElementList.add(testData.getClassIndex().toString());
				
				//	- GA알고리즘에 의해 선택된 파라메터 설정
				for (Parameter param : parameters){
					cmdElementList.add(param.getName());
					cmdElementList.add(param.getValueString());
				}
				
			}else if (AlgorithmInfo.PARAM_FORMAT_MAHOUT_RF.equals(getParamFormat())){
				//	Mahout random forest의 경우, .....
				
				//	속성정보 파일 생성(attribute.info)
				String attrInfoPath = outputBase + File.separator + "attribute.info";
				try{
					FileSystem fs = FileSystem.get(new Configuration());
					if (!fs.exists(new Path(attrInfoPath))){
						writeAttrInfo(data, attrInfoPath);
					}
				}catch(IOException ex){
					throw new OptimizerException("Fail to check attribute info of the file(= "+data.getDataFilePath()+")!!! ", ex);
				}
				
				// - 속성정보파일 설정
				cmdElementList.add("-ds");
				cmdElementList.add(attrInfoPath);

			}

			
			
			//	분류결과 출력
			String[] command = new String[cmdElementList.size()];
			cmdElementList.toArray(command);
			
			StringBuffer sb = new StringBuffer();
			for (String cmd : command){
				sb.append(cmd);
				sb.append(" ");
			}
			OptimizerLogger.println("Classifying shell command :"+sb.toString());
			try{
				//	프로세스 실행
				Process process = new ProcessBuilder(command).start();
				
				//	프로세스 정상 로그 출력 스레드 실행
				InputStream inputStream = process.getInputStream();
				SysOutThread sysOutThread = new SysOutThread(inputStream);
				sysOutThread.start();
				
				//	프로세스 에러 로그 출력 스레드 실행
				InputStream errorStream = process.getErrorStream();
				SysOutThread errorOutThread = new SysOutThread(process.getErrorStream());
				errorOutThread.start();
				
				//	프로세스  실행 완료까지 대기
				int res = process.waitFor();
				if (res != 0){
					throw new OptimizerException("Fail to classify test data!!!\n"
							+ "Please check out parameter values.\n"
							+ "> shell command: "+ getCommandLineString(command));
				}else{
					OptimizerLogger.println("Complete to classify test data\n"
							+ "> shell command: "+ getCommandLineString(command));
				}
				
				
				inputStream.close();
				errorStream.close();
				
			}catch(IOException ex){
				throw new OptimizerException("Fail to classfiy test data", ex);
			}catch(InterruptedException ex){
				throw new OptimizerException("Fail to classfiy test data", ex);
			}
		}
		
		//	분류결과 출력파일로부터 정확도 추출
		String classifyingResultFilePath = 
				classifyOutputBase + File.separator + this.getClassifyOutputRelPath();
		String accuracyOutputBase = 
				classifyOutputBase + File.separator + "accuracy";
		if (AlgorithmInfo.CO_MODE_LABEL_WITH_INPUT.equals(getClassifyOutputMode())){
			//	입력데이터와 함께 분류예측 결과가 있는 경우,
			try {
				ArrayList<String> argList2 = new ArrayList<String>();
				argList2.add(ArgumentsConstants.INPUT_PATH);
				argList2.add(classifyingResultFilePath);
				argList2.add(ArgumentsConstants.OUTPUT_PATH);
				argList2.add(accuracyOutputBase);
				argList2.add(ArgumentsConstants.DELIMITER);
				argList2.add(data.getDelimiter());
				argList2.add(ArgumentsConstants.CLASS_INDEX);
				argList2.add(data.getClassIndex().toString());
				String[] args2 = new String[argList2.size()];
				argList2.toArray(args2);

				ToolRunner.run(new Configuration(), new ClassifyResultCheckLabelWithInputDriver(), args2);
			}catch (Exception ex){
				throw new OptimizerException("Fail to compute accuracy ....", ex);
			}
		}else if (AlgorithmInfo.CO_MODE_INDEX_WITHOUT_INPUT.equals(getClassifyOutputMode())){
			//	입력데이터와 없이 분류예측 결과(클래스 레이블 index)만 있는 경우,
			try{
				// 분류결과 개수 체크
				FileSystem fs = FileSystem.get(new Configuration());
		        FSDataInputStream isTest = fs.open(new Path(testData.getDataFilePath()));
		        InputStreamReader irTest = new InputStreamReader(isTest, Constants.UTF8);
		        BufferedReader brTest = new BufferedReader(irTest);
		        FSDataInputStream isRes = fs.open(new Path(classifyingResultFilePath));
		        InputStreamReader irRes = new InputStreamReader(isRes, Constants.UTF8);
		        BufferedReader brRes = new BufferedReader(irRes);
		        String rlTest = null;
		        String rlRes = null;
		        long cntCorrect = 0;
		        long cntIncorrect = 0;
		        while ((rlTest = brTest.readLine()) != null){
		        	//	실제 클래스 레이블 추출
		        	StringTokenizer tokenizerTest = new StringTokenizer(rlTest, testData.getDelimiter());
		        	int idx=0;
		        	String realClass = null;
		        	while (tokenizerTest.hasMoreTokens()){
		        		String val = tokenizerTest.nextToken();
		        		if (idx == testData.getClassIndex()){
		        			realClass = val;
		        			break;
		        		}
		        		idx++;
		        	}
		        	rlRes = brRes.readLine();
		        	String valStr = rlRes.replace("\0", ""); // null 문자 제거
		        	int classLabelIdx = (new Double(valStr)).intValue();
		        	String resClass = testData.classLabelList.get(classLabelIdx);
		        	if (realClass.equals(resClass)){
		        		cntCorrect++;
		        	}else{
		        		cntIncorrect++;
		        	}
		        }
		        brRes.close();
		        irRes.close();
		        isRes.close();
		        brTest.close();
		        irTest.close();
		        isTest.close();
		        
		        
		        
		        //	정확도 파일 출력
				if (!fs.exists(new Path(accuracyOutputBase))){
					fs.mkdirs(new Path(accuracyOutputBase));
				}
				Path accuracyOutputFilePath = 
						new Path(accuracyOutputBase + File.separator +"part-r-00000");
				FSDataOutputStream os = fs.create(accuracyOutputFilePath);
				
				os.write("correct".getBytes());
				os.write("\t".getBytes());
				os.write(String.valueOf(cntCorrect).getBytes());
				os.write("\n".getBytes());
		        
				os.write("incorrect".getBytes());
				os.write("\t".getBytes());
				os.write(String.valueOf(cntIncorrect).getBytes());
				os.write("\n".getBytes());
				
				os.close();
				
			}catch (Exception ex){
				throw new OptimizerException("Fail to compute accuracy ....", ex);
			}
			
		}
		
		//--</2. 모델 평가>-----
		
		
		//	3. accuracy 계산 및 설정
		try{
			
			Path accuracyOutputFilePath = 
					new Path(accuracyOutputBase + File.separator +"part-r-00000");
			FileSystem fs = FileSystem.get(new Configuration());
	        FSDataInputStream fin = fs.open(accuracyOutputFilePath);
	        BufferedReader br = new BufferedReader(new InputStreamReader(fin, Constants.UTF8));
	        
	        String rl = null;
	        double accuracy = 0;
	        long total = 0;
			long correct = 0;
	        long incorrect = 0;
	        
	        while ((rl = br.readLine()) != null){
	        	String[] splits = rl.split("\t");
	        	String key = splits[0];
	        	String value = splits[1];
	        	if (ClassifyResultCheckLabelWithInputMapper.OUTPUT_KEY_CORRECT.equals(key)){
	        		correct = Long.parseLong(value);
	        	}else if (ClassifyResultCheckLabelWithInputMapper.OUTPUT_KEY_INCORRECT.equals(key)){
	        		incorrect = Long.parseLong(value);
	        	}
	        }
	        total = correct + incorrect;
	        accuracy = 1.0f * correct / total;
	        
	        this.accuracy = accuracy;
			
		}catch (IOException ex){
			this.accuracy = -1;
			throw new OptimizerException("Fail to compute accuracy ....", ex);
		}catch (NumberFormatException ex){
			this.accuracy = -1;
			throw new OptimizerException("Fail to compute accuracy ....", ex);
		}
		
	}
	
	@Override
	public double getAccuracy() {
		return accuracy;
	}

	@Override
	public void toModelSummary() {
		OptimizerLogger.println("The algorithm accuracy is "+getAccuracy()+".");
	}
	
	
	/**
	 * 입력스트림을 읽어 OptimizerLogger으로 출력하는 thread
	 *
	 * @description
	 * @version 0.1
	 * @date  2016. 10. 17.
	 * @author JungHo Kim
	 *
	 */
	class SysOutThread extends Thread{

		/**
		 * 출력할 입력스트림
		 */
		private InputStream inputStream = null;
		
		/**
		 * 출력중인지 여부
		 */
		private boolean running = false;
		
		
		/**
		 * 생성자
		 * 
		 * @param inputStream 출력할 입력스트림
		 */
		public SysOutThread(InputStream inputStream) {
			this.inputStream = inputStream;
		}
		
		@Override
		public void run() {
			running = true;
			InputStreamReader isr = new InputStreamReader(this.inputStream);
			BufferedReader br = new BufferedReader(isr);
			try{
				while (running && this.inputStream.available() > 0){
					String rl = null;
					while ((rl = br.readLine()) != null){
						OptimizerLogger.println(rl);
					}
					this.sleep(500);
				}
			}catch(IOException ex){
				OptimizerLogger.println("알고리즘 수행결과 중 출력 오류발생");
			}catch(InterruptedException ex){
				OptimizerLogger.println("알고리즘 수행결과 중 출력 오류발생");
			}
			try{
				isr.close();
				br.close();
				
			}catch(IOException ex){
				logger.warn("알고리즘 수행결과 중 출력 오류발생", ex);
			}
		}
		
		/**
		 * Sytem.out 출력을 중단
		 */
		public void stopSysOut(){
			running = false;
		}
		
	}
	
	
	/**
	 * 데이터 속성정보를 JSon 형식의 파일로 저장(경로기준: hadoop file system)
	 * 
	 * @param data	속성정보를 저장할 데이터
	 * @param outputPath	출력파일 경로
	 * @throws Exception
	 */
	private void writeAttrInfo(Instances data, String outputPath) throws OptimizerException{
		
		List<Map<String,Object>> attrInfoList = new ArrayList<Map<String,Object>>();
		Set<Integer> numericIdxSet = new HashSet<Integer>();
		numericIdxSet.addAll(data.getNumericIndexList());
		Set<Integer> nominalIdxSet = new HashSet<Integer>();
		nominalIdxSet.addAll(data.getNominalIndexList());
		
		for (int idx=0; idx<data.getNumAllAttr(); idx++){
			

			Integer attrIdx = idx;

			Map<String, Object> attrInfo = new HashMap<String, Object>();
			if (numericIdxSet.contains(attrIdx)){
				//	수치형 속성의 경우
				attrInfo.put("type", "numerical");
				attrInfo.put("label", false);
				attrInfo.put("values", null);
			}else if (nominalIdxSet.contains(attrIdx)){
				//	기호형 속성의 경우
				attrInfo.put("type", "categorical");
				attrInfo.put("label", false);
				attrInfo.put("values", null);
			}else{
				attrInfo.put("type", "ignored");
				attrInfo.put("label", false);
				attrInfo.put("values", null);
			}
			
			if (data.getClassIndex().equals(attrIdx)){
				attrInfo.put("type", "categorical");
				attrInfo.put("label", true);
				attrInfo.put("values", data.classLabelList);
			}
			
			attrInfoList.add(attrInfo);
		}
		
		try{
			ObjectMapper mapper = new ObjectMapper();
			String jsonVal = mapper.writeValueAsString(attrInfoList);
			FileSystem fs = FileSystem.get(new Configuration());
			FSDataOutputStream os = fs.create(new Path(outputPath));
			os.write(jsonVal.getBytes());
			os.close();
			
//			OptimizerLogger.println("속성정보:"+jsonVal);
		}catch(IOException ex){
			throw new OptimizerException("Fail to write attribute info on a file!!!", ex);
		}
	}
	
	/**
	 * 명령어 문자열 반환
	 * 
	 * @param command 명령어 문자 배열
	 * @return
	 */
	private String getCommandLineString(String[] command){
		StringBuffer sb = new StringBuffer();
		for (String val : command){
			sb.append(val);
			sb.append(" ");
		}
		return sb.toString();
		
	}

	
	
}
