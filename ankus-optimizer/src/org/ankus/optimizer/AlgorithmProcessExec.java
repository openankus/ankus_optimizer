package org.ankus.optimizer;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
	 * 명령어
	 */
	String command = null;
	
	
	public AlgorithmProcessExec(String command) {
		this.command = command;
	}
	
	@Override
	public void method(Instances data, Parameter[] parameters, String outputBase) {
		
		//--<1. 모델 학습>------

		ArrayList<String> cmdElementList = new ArrayList<String>();

		//	명령어 설정
		{
			String[] splits = command.split(" ");
			for (String split : splits){
				cmdElementList.add(split);
			}
		}
		//	파라메터 설정
		//	- 입력파일 경로
		cmdElementList.add(ArgumentsConstants.INPUT_PATH);
		cmdElementList.add(data.getDataFilePath());
		//	- 출력파일의 디렉터리 경로
		cmdElementList.add(ArgumentsConstants.OUTPUT_PATH);
		cmdElementList.add(outputBase);
		//	- delimiter
		cmdElementList.add(ArgumentsConstants.DELIMITER);
		cmdElementList.add(data.getDelimiter());
		//	- 모델생성에 사용된 속성인덱스 목록
		cmdElementList.add(ArgumentsConstants.TARGET_INDEX);
		StringBuffer attrIndexList = new StringBuffer();
		for (int idx=0; idx<data.getSizeOfAttributeIndexList(); idx++){
			attrIndexList.append(data.getAttributeIndexAt(idx));
			if (idx < data.getSizeOfAttributeIndexList() - 1)
				attrIndexList.append(",");
		}
		cmdElementList.add(attrIndexList.toString());
		//	- class index
		cmdElementList.add(ArgumentsConstants.CLASS_INDEX);
		cmdElementList.add(data.getClassIndex().toString());
		//	- GA알고리즘에 의해 선택된 파라메터 설정
		for (Parameter param : parameters){
			cmdElementList.add(param.getName());
			cmdElementList.add(String.valueOf((int)param.getValue()));
		}

		
		
		//	모델 학습
		String[] command = new String[cmdElementList.size()];
		cmdElementList.toArray(command);
		
		StringBuffer sb = new StringBuffer();
		for (String cmd : command){
			sb.append(cmd);
			sb.append(" ");
		}
		System.out.println("알고리즘 실행 명령어:"+sb.toString());
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
			logger.warn("학습 시 오류 ....", ex);
		}catch(InterruptedException ex){
			logger.warn("학습 시 오류 ....", ex);
		}
		
		//--</1. 모델 학습>------
		
		logger.debug("학습 완료!");
		

		//--<2. 모델 평가>-----
		String classifyingResultFilePath = 
				outputBase + "/classifying_result/part-r-00000";
		String accuracyOutputBase = 
				outputBase + "/accuracy";
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

			ToolRunner.run(new Configuration(), new CheckClassResultDriver(), args2);
		}catch (Exception ex){
			logger.warn("정확도 계산시 오류 발생 ....", ex);
		}
		//--</2. 모델 평가>-----
		
		
		//	3. accuracy 설정
		try{
			Path accuracyOutputFilePath = 
					new Path(accuracyOutputBase+"/part-r-00000");
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
	        	if (CheckClassResultMapper.OUTPUT_KEY_CORRECT.equals(key)){
	        		correct = Long.parseLong(value);
	        	}else if (CheckClassResultMapper.OUTPUT_KEY_INCORRECT.equals(key)){
	        		incorrect = Long.parseLong(value);
	        	}
	        }
	        total = correct + incorrect;
	        accuracy = 1.0f * correct / total;
	        
	        this.accuracy = accuracy;
			
		}catch (Exception ex){
			logger.warn("정확도결과 추출시 오류 발생 ....", ex);
			this.accuracy = 0;
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
					;
					String rl = null;
					while ((rl = br.readLine()) != null){
						System.out.println(rl);
					}
				}
			}catch(IOException ex){
				logger.warn("알고리즘 수행결과 중 출력 오류발생", ex);
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
