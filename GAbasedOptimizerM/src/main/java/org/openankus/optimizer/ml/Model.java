package org.openankus.optimizer.ml;

import weka.core.Instance;
import weka.core.Instances;

public class Model {
	private Instances _Data = null;			// �Էµ�����
	private Algorithm _algorithm = null;	// �������� �����K �˰��� ��ü

	/**
	 * ������
	 * @param data	�Էµ�����
	 */
	public Model(Instances data){
		this._Data = data;
	}
	
	/**
	 * ������
	 * @param data	�Էµ�����
	 * @param a		�������� ������ �˰��� �������̽�
	 */
	public Model(Instances data, Algorithm a){
		this._Data = new Instances (data);
		this._algorithm = a;
	}
	
	/**
	 * �Էµ����ͷκ��� ���õ� �˰������� ������ ȯ�溯�� �� �Ӽ����� ���θ� ������
	 * �������� �����ϰ� ���ϴ� �Լ� 
	 * @param a		�˰��� �������̽�
	 * @param data	�Էµ�����
	 * @param parameters	�Ӽ����� ���� �� �˰����� ȯ�溯�� ���� ��
	 */
	public void methodwData(Algorithm a, Instances data, Parameter[] parameters){
		this._algorithm = a;
		this._algorithm.method(data, parameters);
	}
	
	/**
	 * ������ �������� ��Ȯ�� ��ȯ �Լ�
	 * @return	�������� ��Ȯ��
	 */
	public double getAccuracy(){
		return this._algorithm.getAccuracy();
	}
	
	/**
	 * �Էµ����� ��ȣ�� �Լ�
	 * @return	�Էµ�����
	 */
	public Instances getInstance(){
		return this._Data;
	}
	
	/**
	 * �Է·������ �з��� Ŭ���� ���̵� ��ȯ
	 * @param inst	�Է·��ڵ�
	 * @return	�з��� Ŭ���� ���̵�
	 */
	public double classifyInstance(Instance inst){
		return this._algorithm.classifyInstance(inst);
	}

	/**
	 * �� ���� �Լ�
	 * @return	����� �� 
	 */
	public Model copy() {
		return new Model(this._Data, this._algorithm);
	}

	/**
	 * ������ ������ ��� �Լ�
	 */
	public void toSummaryString() {
		this._algorithm.toModelSummary();
	}
	
	
}
