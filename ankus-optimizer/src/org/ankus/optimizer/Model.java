package org.ankus.optimizer;


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
	 * 예측모델을 생성할 알고리즘 객체
	 */
	private Algorithm algorithm = null;
	
	
	/**
	 * 생성자
	 * 
	 * @param data 입력데이터
	 */
	public Model(Instances data){
		this.data = data;
	}
	
	
	/**
	 * 생성자
	 * 
	 * @param dataFilePath		입력데이터
	 * @param algorithm	예측모델을 생성할 알고리즘 인터페이스
	 */
	public Model(Instances data, Algorithm algorithm){
		this.data = new Instances(data);
		this.algorithm = algorithm;
	}

	
	/**
	 * 입력데이터로부터 선택된 알고리즘으로 설정된 환경변수 및 속성선택 여부를 가지고
	 * 예측모델을 생성하고 평가하는 함수 
	 * @param algorithm		알고리즘 인터페이스
	 * @param data	입력데이터
	 * @param parameters	속성선택 여부 및 알고리즘의 환경변수 설정 값
	 * @param outputBase	평가결과 출력파일을 제공하는 디렉터리 경로
	 */
	public void methodData(Algorithm algorithm, Instances data, Parameter[] parameters, String outputBase){
		this.algorithm = algorithm;
		this.algorithm.method(data, parameters, outputBase);
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
	
	
	/**
	 * 모델 복사 함수
	 * @return	복사된 모델 
	 */
	public Model copy(){
		return new Model(this.data, this.algorithm);
	}


	/**
	 * 생성된 모델정보 출력 함수
	 */
	public void toSummaryString() {
		this.algorithm.toModelSummary();
	}


}
