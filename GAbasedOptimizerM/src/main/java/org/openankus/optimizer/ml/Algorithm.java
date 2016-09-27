package org.openankus.optimizer.ml;

import weka.core.Instance;
import weka.core.Instances;

public interface Algorithm {
	
	/**
	 * 예측모델 생성하고 평가하여 정확성을 산출하는 함수
	 * @param data		입력데이터(모델을 생성할 때 사용되는 데이터)
	 * @param parameters	설정된 알고리즘의 환경변수 값
	 */
	public abstract void method(Instances data, Parameter[] parameters);
	
	/**
	 * 예측모델의 분류 정확성 반환 함수
	 * @return	분류 정확성
	 */
	public abstract double getAccuracy();

	/**
	 * 입력데이터의 범주(클래스)를 예측하는 함수
	 * @param inst	입력데이터
	 * @return	예측된 범주(클래스)의 인덱스
	 */
	public abstract double classifyInstance(Instance inst);

	/**
	 * 예측모델 정보 출력 함수
	 */
	public abstract void toModelSummary();
	
}