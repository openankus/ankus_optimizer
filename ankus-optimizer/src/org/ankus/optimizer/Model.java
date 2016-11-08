package org.ankus.optimizer;

import org.ankus.optimizer.exceoption.OptimizerException;

/**
 * 분류모델
 * 
 * @author JungHo Kim
 *
 */
public class Model {
	
	/**
	 * 입력데이터
	 */
	private Instances data = null;
	
	/**
	 * 훈련 데이터
	 */
	private Instances trainData = null;

	/**
	 * 테스트 데이터
	 */
	private Instances testData = null;
	
	/**
	 * 예측모델을 생성할 알고리즘 객체
	 */
	private Algorithm algorithm = null;
	
	
	/**
	 * 생성자
	 * 
	 * @param data 입력데이터
	 * @param trainData 훈련데이터
	 * @param testData 테스트데이터
	 */
	public Model(Instances data, Instances trainData, Instances testData){
		this.data = data;
		this.trainData = trainData;
		this.testData = testData;
	}
	
	
	/**
	 * 생성자
	 * 
	 * @param data 입력데이터
	 * @param trainData 훈련데이터
	 * @param testData 테스트데이터
	 * @param algorithm	예측모델을 생성할 알고리즘 인터페이스
	 */
	public Model(Instances data, Instances trainData, Instances testData, Algorithm algorithm){
		this.data = new Instances(data);
		this.trainData = new Instances(trainData);
		this.testData = new Instances(testData);
		this.algorithm = algorithm;
	}

	
	/**
	 * 입력데이터로부터 선택된 알고리즘으로 설정된 환경변수 및 속성선택 여부를 가지고
	 * 예측모델을 생성하고 평가하는 함수 
	 * @param algorithm		알고리즘 인터페이스
	 * @param data	입력데이터
	 * @param trainData 훈련데이터
	 * @param testData 테스트데이터
	 * @param parameters	속성선택 여부 및 알고리즘의 환경변수 설정 값
	 * @param outputBase	평가결과 출력파일을 제공하는 디렉터리 경로
	 */
	public void methodData(Algorithm algorithm, Instances data, Instances trainData, Instances testData, Parameter[] parameters, String outputBase) throws OptimizerException{
		this.algorithm = algorithm;
		this.algorithm.method(data, trainData, testData, parameters, outputBase);
	}


	/**
	 * 생성된 예측모델의 정확성 반환 함수
	 * @return	예측모델의 정확성
	 */
	public double getAccuracy(){
		return this.algorithm.getAccuracy();
	}
	
	
	/**
	 * 입력데이터 반환 함수
	 * @return	입력데이터
	 */
	public Instances getInstances(){
		return this.data;
	}
	
	public Instances getTrainData() {
		return trainData;
	}


	public void setTrainData(Instances trainData) {
		this.trainData = trainData;
	}


	public Instances getTestData() {
		return testData;
	}


	public void setTestData(Instances testData) {
		this.testData = testData;
	}


	/**
	 * 모델 복사 함수
	 * @return	복사된 모델 
	 */
	public Model copy(){
		return new Model(this.data, this.trainData, this.testData, this.algorithm);
	}


	/**
	 * 생성된 모델정보 출력 함수
	 */
	public void toSummaryString() {
		this.algorithm.toModelSummary();
	}


}
