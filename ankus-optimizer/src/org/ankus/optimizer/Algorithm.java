package org.ankus.optimizer;


/**
 * Classification MapReduce 알고리즘
 * 
 * @author JungHo Kim
 *
 */
public interface Algorithm {

	/**
	 * 예측모델 생성하고 평가하여 정확성을 산출하는 함수
	 * @param data		입력데이터(모델을 생성할 때 사용되는 데이터)
	 * @param parameters	설정된 알고리즘의 환경변수 값
	 * @param outputBase	출력파일을 제공하는 디렉터리 경로
	 */
	public abstract void method(Instances data, Parameter[] parameters, String outputBase);
	
	/**
	 * 예측모델의 분류 정확성 반환 함수
	 * @return	분류 정확성
	 */
	public abstract double getAccuracy();
	
	
	/**
	 * 예측모델 정보 출력 함수
	 */
	public abstract void toModelSummary();
	
}
