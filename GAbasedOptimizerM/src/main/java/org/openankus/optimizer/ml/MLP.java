package org.openankus.optimizer.ml;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instance;
import weka.core.Instances;

public class MLP implements Algorithm {

	public MultilayerPerceptron _mlp = null; // 신경망(다층퍼셉트론)
	public double _accuracy = -1; 	// 생성된 모델의 정확성
	
	@Override
	/**
	 * 다층퍼셈트론 모델 생성 및 평가 함수
	 */
	public void method(Instances data, Parameter[] parameters) {
		
		try {
			this._mlp = new MultilayerPerceptron();
			
			for(int i=0 ; i<parameters.length ; i++){
				switch(parameters[i].getName()){
				case "lr": 
					this._mlp.setLearningRate(parameters[i].getValue());
					break;
				case "mm":
					this._mlp.setMomentum(parameters[i].getValue());
					break;
				case "h":
					this._mlp.setHiddenLayers(String.valueOf(parameters[i].getValue()));
					break;
				}
			}
			
			this._mlp.buildClassifier(data);	// 모델 생성
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(this._mlp.toString());
		
		int correct = 0;
		int classIndex = data.classIndex();
		double realOutput;
		double targetOutput;
		for(int i=0 ; i<data.numInstances() ; i++){
			try {
				realOutput = this._mlp.classifyInstance(data.get(i));
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
	 * 생성된 다층퍼셉트론의 정확성 반환 함수
	 */
	public double getAccuracy(){
		return this._accuracy;
	}	
	
	/**
	 * 생생된 다층퍼셉트론으로 입력데이터의 범주를 분류한 결과 값 반환 함수
	 * (반환 값은 클래스의 인덱스임)
	 */
	public double classifyInstance(Instance inst){
		double retVal = -1.0;
		
		try {
			this._mlp.classifyInstance(inst);
		} catch (Exception e) {
			System.out.println("오류00003");
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	/**
	 * 생성된 다층퍼셉트론 정보 출력 함수
	 */
	public void toModelSummary(){
		System.out.println(this._mlp.toString());
	}
	
}
