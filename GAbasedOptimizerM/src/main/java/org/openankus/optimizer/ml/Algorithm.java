package org.openankus.optimizer.ml;

import weka.core.Instance;
import weka.core.Instances;

public interface Algorithm {
	
	/**
	 * ������ �����ϰ� ���Ͽ� ��Ȯ���� �����ϴ� �Լ�
	 * @param data		�Էµ�����(���� ������ �� ���Ǵ� ������)
	 * @param parameters	������ �˰����� ȯ�溯�� ��
	 */
	public abstract void method(Instances data, Parameter[] parameters);
	
	/**
	 * �������� �з� ��Ȯ�� ��ȯ �Լ�
	 * @return	�з� ��Ȯ��
	 */
	public abstract double getAccuracy();

	/**
	 * �Էµ������� ����(Ŭ����)�� �����ϴ� �Լ�
	 * @param inst	�Էµ�����
	 * @return	������ ����(Ŭ����)�� �ε���
	 */
	public abstract double classifyInstance(Instance inst);

	/**
	 * ������ ���� ��� �Լ�
	 */
	public abstract void toModelSummary();
	
}