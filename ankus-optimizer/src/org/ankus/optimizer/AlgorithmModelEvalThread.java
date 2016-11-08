package org.ankus.optimizer;

import java.io.IOException;

import org.ankus.optimizer.exceoption.OptimizerException;
import org.ankus.optimizer.util.Constants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * 알고리즘 모델(개체)평가 thread
 *
 * @description
 * @version 0.1
 * @date  2016. 10. 27.
 * @author JungHo Kim
 *
 */
public class AlgorithmModelEvalThread extends Thread{
	
	/**
	 * 평가 실행상태 - 실행 시작 전
	 */
	public static final String STATE_NEW = "STATE_NEW";
	
	/**
	 * 평가 실행상태 - 실행 중
	 */
	public static final String STATE_RUNNING = "STATE_RUNNING";
	
	/**
	 * 평가 실행상태 - 완료
	 */
	public static final String STATE_COMPLETED = "STATE_COMPLETED";
	
	/**
	 * 평가 실행상태 - 실패
	 */
	public static final String STATE_FAIL = "STATE_FAIL";
	
	/**
	 * 평가항목 실행 상태 
	 */
	private String evalState = STATE_NEW;
	
	/**
	 * 평가 수행 실패 메시지
	 */
	private String evalFailMessage = null;
	
	/**
	 * 평가 수행 실패 관련 Exception
	 */
	private Throwable evalFailException = null;
	
	
	
	
	/**
	 * 모델평가 출력결과 디렉토리
	 */
	private String popOutputBase = null;
	
	/**
	 * 평가 대상 모델(개체)
	 */
	private Model model = null;
	
	/**
	 * 평가 대상 알고리즘
	 */
	private AlgorithmProcessExec algorithm = null;
	
	/**
	 * 모델(개체) 평가에 사용될 데이터
	 */
	private Instances tempData = null;
	
	/**
	 * 모델(개체) 평가에 사용될 훈련 데이터
	 */
	private Instances tempTrainData = null;

	/**
	 * 모델(개체) 평가에 사용될 테스트 데이터
	 */
	private Instances tempTestData = null;
	
	
	/**
	 * 알고리즘 최적화 대상 파라메터 목록
	 */
	private Parameter[] parameters = null;
	
	/**
	 * 유전자 알고리즘 최적화 객체
	 */
	private GA ga = null;
	
	/**
	 * 평가 대상 모델(개체) 인덱스
	 */
	private int popIndex = -1;
	
	/**
	 * 생성된 모델들의 정보를 담는 파일의 경로
	 */
	private String popInfoAllPath;

	
	public AlgorithmModelEvalThread(String popOutputBase
			, Model model
			, AlgorithmProcessExec algorithm
			, Instances tempData
			, Instances tempTrainData
			, Instances tempTestData
			, Parameter[] parameters
			, GA ga
			, int popIndex
			, String popInfoAllPath) {
		this.popOutputBase = popOutputBase;
		this.algorithm = new AlgorithmProcessExec(algorithm);
		this.tempData = new Instances(tempData);
		this.tempTrainData = new Instances(tempTrainData);
		this.tempTestData = new Instances(tempTestData);
		this.model = new Model(tempData, tempTrainData, tempTestData, algorithm);
		
		Parameter[] copyParameters = new Parameter[parameters.length];
		for (int idx =0; idx< parameters.length; idx++){
			Parameter parameter = parameters[idx];
			if (parameter instanceof ParameterInt){
				copyParameters[idx] = 
						new ParameterInt((ParameterInt) parameter);
			}else if (parameter instanceof ParameterFloat){
				copyParameters[idx] = 
						new ParameterFloat((ParameterFloat) parameter);
			}
		}
		this.parameters = copyParameters;
		
		
		this.ga = ga;
		this.popIndex = popIndex;
		this.popInfoAllPath = popInfoAllPath;
	}
	
	
	
	
	
	@Override
	public void run() {
		
		//	평가상태 설정
		this.setEvalState(STATE_RUNNING);
		
		try{
			
//			OptimizerLogger.printf(">pop[%d] parameters after call: ",popIndex);
//			for (Parameter param : parameters){
//				OptimizerLogger.printf("param[%s]=%s, ", param.getName(), param.getValueString());
//			}
//			OptimizerLogger.println("");
			
			// 모델 생성 및 평가
			evaluateModel();
			
			// 생성된 모델 및 평가 출력
			writeModelAndEvaluation();
			
			
			//	평가상태 설정
			this.setEvalState(STATE_COMPLETED);
			
		}catch (OptimizerException ex){
			ex.printStackTrace();
			this.setEvalState(STATE_FAIL);
			this.setEvalFailMessage(ex.getMessage());
			this.setEvalFailException(ex);
		}
	}
	
	
	

	/**
	 * 모델 생성 및 평가
	 */
	private void evaluateModel() throws OptimizerException{
		
		//모델 생성 및 평가
		model.methodData(algorithm,tempData, tempTrainData, tempTestData, parameters, popOutputBase);
	
		//모델 생성 및 평가 결과를 ga객체에 설정
		ga.getPop()[popIndex].setFitness((float)model.getAccuracy());
		ga.getPop()[popIndex].setModel(model);
	}
	
	
	
	/**
	 * 생성된 모델 및 평가 출력
	 */
	private void writeModelAndEvaluation() throws OptimizerException{
		
		// 생성된 모델 및 평가 출력 (개체 단위 출력)
		try{
			//	출력 스트림 열기
			Path outputPath = new Path(popInfoAllPath);
			FileSystem fs = FileSystem.get(new Configuration());
			FSDataOutputStream out = null;
			if (fs.exists(outputPath)){
				out = fs.append(outputPath);
			}else{
				out = fs.create(outputPath);
			}
			
			//	개체표현(염색체 기반 표시)
			out.write(ga.getPop()[popIndex].toStringGene().getBytes());
			out.write(Constants.DELIMITER_GA.getBytes());
			//	적합도 표시
			out.write(String.format("%.10f", ga.getPop()[popIndex].getFitness()).getBytes());
			out.write(Constants.DELIMITER_GA.getBytes());
			//	환경변수 설정값
			for (int idx=0; idx < parameters.length ; idx++){
				StringBuffer sb = new StringBuffer();
				sb.append("param["+ parameters[idx].getName() +"]");
				sb.append("=");
				sb.append(parameters[idx].getValueString());
				if (idx < parameters.length - 1)
					sb.append(Constants.DELIMITER_GA_PARAM);
				out.write(sb.toString().getBytes());
			}
			out.write(Constants.DELIMITER_GA.getBytes());
			//	선택된 입력속성
			{
				StringBuffer sb = new StringBuffer();
				sb.append("attributes=");
				int sizeAttr = tempData.getSizeOfAttributeIndexList();
				for (int idx=0; idx < sizeAttr ; idx++){
					sb.append(tempData.getAttributeIndexAt(idx));
					if (idx < sizeAttr-1)
						sb.append(Constants.DELIMITER_GA_VALUE);
				}
				out.write(sb.toString().getBytes());
			}
			out.write("\n".getBytes());
			out.close();
			
		}catch(IOException ex){
			throw new OptimizerException("Fail to evaluate a model!!!", ex);
		}
		
	}





	public String getEvalState() {
		return evalState;
	}

	public void setEvalState(String evalState) {
		this.evalState = evalState;
	}

	public String getEvalFailMessage() {
		return evalFailMessage;
	}

	public void setEvalFailMessage(String evalFailMessage) {
		this.evalFailMessage = evalFailMessage;
	}

	public Throwable getEvalFailException() {
		return evalFailException;
	}

	public void setEvalFailException(Throwable evalFailException) {
		this.evalFailException = evalFailException;
	}

	public int getPopIndex() {
		return popIndex;
	}
	
	
	

}
