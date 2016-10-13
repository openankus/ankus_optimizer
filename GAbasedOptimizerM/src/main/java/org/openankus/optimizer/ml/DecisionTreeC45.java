package org.openankus.optimizer.ml;

import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

public class DecisionTreeC45 implements Algorithm {
	
	public J48 _decisionTree = null; 	// 의사결정트리 객체
	public double _accuracy = -1; 		// 모델의 정확성
	
	@Override
	/**
	 * 의사결정트리,C45 모델 생성 및 평가 함수
	 */
	public void method(Instances data, Parameter[] parameters) {
		try {	
			this._decisionTree = new J48();
			
			for(int i=0 ; i<parameters.length ; i++){
				switch(parameters[i].getName()){
				case "CF":
					this._decisionTree.setConfidenceFactor(parameters[i].getValue());
					break;
				case "min":
					this._decisionTree.setMinNumObj((int)parameters[i].getValue()); 
					break;
				};
			}
			
			this._decisionTree.buildClassifier(data);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("학습 시 오류......");
		}
		
		//System.out.println(this._decisionTree.toString());
		//System.exit(1);
		int correct = 0;
		int classIndex = data.classIndex();
		double realOutput;
		double targetOutput;
		for(int i=0 ; i<data.numInstances() ; i++){
			try {
				realOutput = this._decisionTree.classifyInstance(data.get(i));
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
	 * 생성된 의사결정트리의 정확성 반환 함수
	 */
	public double getAccuracy(){
		return this._accuracy;
	}
	
	/**
	 * 생생된 의사결정트리로 입력데이터의 범주를 분류한 결과 값 반환 함수
	 * (반환 값은 클래스의 인덱스임)
	 */
	public double classifyInstance(Instance inst){
		
		double retVal = -1.0f;
		
		try {
			retVal= this._decisionTree.classifyInstance(inst);
		} catch (Exception e) {
			System.out.println("오류 00002");
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	/**
	 * 생성된 의사결정트리 정보 출력 함수
	 */
	public void toModelSummary(){
		System.out.println(this._decisionTree.toString());
	}
}
