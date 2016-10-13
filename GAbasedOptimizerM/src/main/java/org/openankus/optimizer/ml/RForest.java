package org.openankus.optimizer.ml;

import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;

public class RForest implements Algorithm{
	public RandomForest _forest = null; 	// 랜덤 포레스트 앙상블 객체 생성
	public double _accuracy = -1; 			// 모델의 정확성
	
	@Override
	/**
	 * RandomForest(앙상블 모델) 모델 생성 및 평가 함수
	 */
	public void method(Instances data, Parameter[] parameters) {
		try {	
			this._forest = new RandomForest();
			
			for(int i=0 ; i<parameters.length ; i++){
				switch(parameters[i].getName()){
				case "MD":
					this._forest.setMaxDepth((int)parameters[i].getValue());
					break;
				case "numDT":
					this._forest.setNumIterations((int)parameters[i].getValue());
					break;
				};
			}
			
			this._forest.buildClassifier(data);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("학습 시 오류......");
		}
		
//		System.out.println(this._forest.toString());
		
		int correct = 0;
		int classIndex = data.classIndex();
		double realOutput;
		double targetOutput;
		for(int i=0 ; i<data.numInstances() ; i++){
			try {
				realOutput = this._forest.classifyInstance(data.get(i));
				targetOutput = data.get(i).value(classIndex);
				if(realOutput == targetOutput){
					correct++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		this._accuracy = (double)correct / (double)data.numInstances();
	}
	
	/**
	 * 생성된 RandomForest 모델의 정확성 반환 함수
	 */
	public double getAccuracy(){
		return this._accuracy;
	}
	
	/**
	 * 생생된 RandomForest 모델로 입력데이터의 범주를 분류한 결과 값 반환 함수
	 * (반환 값은 클래스의 인덱스임)
	 */
	public double classifyInstance(Instance inst){
		
		double retVal = -1.0f;
		
		try {
			retVal= this._forest.classifyInstance(inst);
		} catch (Exception e) {
			System.out.println("오류 00002");
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	/**
	 * 생성된 RandomForest 모델 정보 출력 함수
	 */
	public void toModelSummary(){
		System.out.println(this._forest.toString());
	}
}
