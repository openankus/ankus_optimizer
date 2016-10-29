package org.ankus.optimizer;



import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.ankus.optimizer.exceoption.OptimizerException;
import org.ankus.optimizer.mapreduce.ClassifyResultCheckLabelWithInputDriver;
import org.ankus.optimizer.mapreduce.ClassifyResultCheckLabelWithInputMapper;
import org.ankus.optimizer.util.ArgumentsConstants;
import org.ankus.optimizer.util.Constants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;
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
	 * 입력 파라메터 이름
	 */
	String paramInputName = null;
	
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
	
	public AlgorithmProcessExec(
			String trainCommand
			, String classifyCommand
			, String paramInputName
			, String paramDelimiterName
			, String paramOutputName
			, String paramIndexListName
			, String paramClassIndexName
			, String paramModelPathName
			, String paramValueDelimiter
			, String classifyOutputRelPath
			, String modelAbsPath
			, String trainedModelRelPath) {
		this.trainCommand = trainCommand;
		this.classifyCommand = classifyCommand;
		this.paramInputName = paramInputName;
		this.paramDelimiterName = paramDelimiterName;
		this.paramOutputName = paramOutputName;
		this.paramIndexListName = paramIndexListName;
		this.paramClassIndexName = paramClassIndexName;
		this.paramModelPathName = paramModelPathName;
		this.paramValueDelimiter = paramValueDelimiter;
		this.classifyOutputRelPath = classifyOutputRelPath;
		this.modelAbsPath = modelAbsPath;
		this.modelRelPath = trainedModelRelPath;
	}
	
	/**
	 * 복사생성자
	 * @param algorithmProcessExec
	 */
	public AlgorithmProcessExec(AlgorithmProcessExec algorithmProcessExec){
		this.trainCommand = algorithmProcessExec.trainCommand;
		this.classifyCommand = algorithmProcessExec.classifyCommand;
		this.paramInputName = algorithmProcessExec.paramInputName;
		this.paramDelimiterName = algorithmProcessExec.paramDelimiterName;
		this.paramOutputName = algorithmProcessExec.paramOutputName;
		this.paramIndexListName = algorithmProcessExec.paramIndexListName;
		this.paramClassIndexName = algorithmProcessExec.paramClassIndexName;
		this.paramModelPathName = algorithmProcessExec.paramModelPathName;
		this.paramValueDelimiter = algorithmProcessExec.paramValueDelimiter;
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


	public String getParamDelimiterName() {
		return paramDelimiterName;
	}


	public String getParamOutputName() {
		return paramOutputName;
	}


	public String getParamIndexListName() {
		return paramIndexListName;
	}


	public String getParamClassIndexName() {
		return paramClassIndexName;
	}
	

	public String getParamValueDelimiter() {
		return paramValueDelimiter;
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
			//	- delimiter
			cmdElementList.add(getParamDelimiterName());
			cmdElementList.add(trainData.getDelimiter());
			//	- 모델생성에 사용된 속성인덱스 목록
			cmdElementList.add(getParamIndexListName());
			StringBuffer attrIndexList = new StringBuffer();
			for (int idx=0; idx<trainData.getSizeOfAttributeIndexList(); idx++){
				attrIndexList.append(trainData.getAttributeIndexAt(idx));
				if (idx < trainData.getSizeOfAttributeIndexList() - 1)
					attrIndexList.append(getParamValueDelimiter());
			}
			cmdElementList.add(attrIndexList.toString());
			//	- class index
			cmdElementList.add(getParamClassIndexName());
			cmdElementList.add(trainData.getClassIndex().toString());
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
			System.out.println("알고리즘 학습 출력  명령어:"+sb.toString());
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
				System.out.println("실행결과: "+res);
				
				
				inputStream.close();
				errorStream.close();
				
			}catch(IOException ex){
				throw new OptimizerException("분류알고리즘 학습시 오류발생", ex);
			}catch(InterruptedException ex){
				throw new OptimizerException("분류알고리즘 학습시 오류발생", ex);
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
			cmdElementList.add(getParamInputName());
			cmdElementList.add(testData.getDataFilePath());
			//	- 출력파일의 디렉터리 경로
			cmdElementList.add(getParamOutputName());
			cmdElementList.add(classifyOutputBase);
			//	- delimiter
			cmdElementList.add(getParamDelimiterName());
			cmdElementList.add(testData.getDelimiter());
			//	- 모델생성에 사용된 속성인덱스 목록
			cmdElementList.add(getParamIndexListName());
			StringBuffer attrIndexList = new StringBuffer();
			for (int idx=0; idx<testData.getSizeOfAttributeIndexList(); idx++){
				attrIndexList.append(testData.getAttributeIndexAt(idx));
				if (idx < testData.getSizeOfAttributeIndexList() - 1)
					attrIndexList.append(getParamValueDelimiter());
			}
			cmdElementList.add(attrIndexList.toString());
			//	- class index
			cmdElementList.add(getParamClassIndexName());
			cmdElementList.add(testData.getClassIndex().toString());
			//	- GA알고리즘에 의해 선택된 파라메터 설정
			for (Parameter param : parameters){
				cmdElementList.add(param.getName());
				cmdElementList.add(param.getValueString());
			}

			
			
			//	분류결과 출력
			String[] command = new String[cmdElementList.size()];
			cmdElementList.toArray(command);
			
			StringBuffer sb = new StringBuffer();
			for (String cmd : command){
				sb.append(cmd);
				sb.append(" ");
			}
			System.out.println("알고리즘 분류결과 출력 명령어:"+sb.toString());
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
				System.out.println("실행결과: "+res);
				
				
				inputStream.close();
				errorStream.close();
				
			}catch(IOException ex){
				throw new OptimizerException("분류알고리즘 분류결과 출력시 오류발생", ex);
			}catch(InterruptedException ex){
				throw new OptimizerException("분류알고리즘 분류결과 출력시  오류발생", ex);
			}
		}
		
		//	분류결과 출력파일로부터 정확도 추출
		String classifyingResultFilePath = 
				classifyOutputBase + File.separator + this.getClassifyOutputRelPath();
		String accuracyOutputBase = 
				classifyOutputBase + File.separator + "accuracy";
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
			throw new OptimizerException("정확도 계산시 오류 발생 ....", ex);
		}
		//--</2. 모델 평가>-----
		
		
		//	3. accuracy 설정
		try{
			Path accuracyOutputFilePath = 
					new Path(accuracyOutputBase + File.separator +"part-r-00000");
			FileSystem fs = FileSystem.get(new Configuration());
	        FSDataInputStream fin = fs.open(accuracyOutputFilePath);
	        BufferedReader br = new BufferedReader(new InputStreamReader(fin, Constants.UTF8));
	        
	        String rl = null;
	        boolean hasAccuracy = false;
	        long total = 0;
			long correct = 0;
	        long incorrect = 0;
	        double accuracy = 0.0;
	        
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
			
		}catch (Exception ex){
			this.accuracy = -1;
			throw new OptimizerException("정확도결과 추출시 오류 발생 ....", ex);
		}
		
	}
	
	@Override
	public double getAccuracy() {
		return accuracy;
	}

	@Override
	public void toModelSummary() {
		System.out.println("MR기반의 kNN 모델입니다. 블라블라~");
	}
	
	
	/**
	 * 입력스트림을 읽어 System.out으로 출력하는 thread
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
						System.out.println(rl);
					}
					this.sleep(500);
				}
			}catch(IOException ex){
				System.out.println("알고리즘 수행결과 중 출력 오류발생");
			}catch(InterruptedException ex){
				System.out.println("알고리즘 수행결과 중 출력 오류발생");
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

	
	
}
