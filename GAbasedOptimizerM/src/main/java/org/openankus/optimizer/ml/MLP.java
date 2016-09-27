package org.openankus.optimizer.ml;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instance;
import weka.core.Instances;

public class MLP implements Algorithm {

	public MultilayerPerceptron _mlp = null; // �Ű��(�����ۼ�Ʈ��)
	public double _accuracy = -1; 	// ������ ���� ��Ȯ��
	
	@Override
	/**
	 * �����ۼ�Ʈ�� �� ���� �� �� �Լ�
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
			
			this._mlp.buildClassifier(data);	// �� ����
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
	 * ������ �����ۼ�Ʈ���� ��Ȯ�� ��ȯ �Լ�
	 */
	public double getAccuracy(){
		return this._accuracy;
	}	
	
	/**
	 * ������ �����ۼ�Ʈ������ �Էµ������� ���ָ� �з��� ��� �� ��ȯ �Լ�
	 * (��ȯ ���� Ŭ������ �ε�����)
	 */
	public double classifyInstance(Instance inst){
		double retVal = -1.0;
		
		try {
			this._mlp.classifyInstance(inst);
		} catch (Exception e) {
			System.out.println("����00003");
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	/**
	 * ������ �����ۼ�Ʈ�� ���� ��� �Լ�
	 */
	public void toModelSummary(){
		System.out.println(this._mlp.toString());
	}
	
}
