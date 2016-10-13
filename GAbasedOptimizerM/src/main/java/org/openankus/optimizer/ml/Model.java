package org.openankus.optimizer.ml;

import weka.core.Instance;
import weka.core.Instances;

public class Model {
	private Instances _Data = null;			// 입력데이터
	private Algorithm _algorithm = null;	// 예측모델을 생성핳 알고리즘 객체

	/**
	 * 생성자
	 * @param data	입력데이터
	 */
	public Model(Instances data){
		this._Data = data;
	}
	
	/**
	 * 생성자
	 * @param data	입력데이터
	 * @param a		예측모델을 생성할 알고리즘 인터페이스
	 */
	public Model(Instances data, Algorithm a){
		this._Data = new Instances (data);
		this._algorithm = a;
	}
	
	/**
	 * 입력데이터로부터 선택된 알고리즘으로 설정된 환경변수 및 속성선택 여부를 가지고
	 * 예측모델을 생성하고 평가하는 함수 
	 * @param a		알고리즘 인터페이스
	 * @param data	입력데이터
	 * @param parameters	속성선택 여부 및 알고리즘의 환경변수 설정 값
	 */
	public void methodwData(Algorithm a, Instances data, Parameter[] parameters){
		this._algorithm = a;
		this._algorithm.method(data, parameters);
	}
	
	/**
	 * 생성된 예측모델의 정확성 반환 함수
	 * @return	예측모델의 정확성
	 */
	public double getAccuracy(){
		return this._algorithm.getAccuracy();
	}
	
	/**
	 * 입력데이터 반호나 함수
	 * @return	입력데이터
	 */
	public Instances getInstance(){
		return this._Data;
	}
	
	/**
	 * 입력레고드을 분류한 클래스 아이디 반환
	 * @param inst	입력레코드
	 * @return	분류한 클래스 아이디
	 */
	public double classifyInstance(Instance inst){
		return this._algorithm.classifyInstance(inst);
	}

	/**
	 * 모델 복사 함수
	 * @return	복사된 모델 
	 */
	public Model copy() {
		return new Model(this._Data, this._algorithm);
	}

	/**
	 * 생성된 모델정보 출력 함수
	 */
	public void toSummaryString() {
		this._algorithm.toModelSummary();
	}
	
	
}
