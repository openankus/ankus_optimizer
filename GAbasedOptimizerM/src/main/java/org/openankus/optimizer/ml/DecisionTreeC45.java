package org.openankus.optimizer.ml;

import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

public class DecisionTreeC45 implements Algorithm {
	
	public J48 _decisionTree = null; 	// �ǻ����Ʈ�� ��ü
	public double _accuracy = -1; 		// ���� ��Ȯ��
	
	@Override
	/**
	 * �ǻ����Ʈ��,C45 �� ���� �� �� �Լ�
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
			System.out.println("�н� �� ����......");
		}
		
		System.out.println(this._decisionTree.toString());
		
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
	 * ������ �ǻ����Ʈ���� ��Ȯ�� ��ȯ �Լ�
	 */
	public double getAccuracy(){
		return this._accuracy;
	}
	
	/**
	 * ������ �ǻ����Ʈ���� �Էµ������� ���ָ� �з��� ��� �� ��ȯ �Լ�
	 * (��ȯ ���� Ŭ������ �ε�����)
	 */
	public double classifyInstance(Instance inst){
		
		double retVal = -1.0f;
		
		try {
			retVal= this._decisionTree.classifyInstance(inst);
		} catch (Exception e) {
			System.out.println("���� 00002");
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	/**
	 * ������ �ǻ����Ʈ�� ���� ��� �Լ�
	 */
	public void toModelSummary(){
		System.out.println(this._decisionTree.toString());
	}
}
