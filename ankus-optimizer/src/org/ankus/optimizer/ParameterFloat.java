package org.ankus.optimizer;

/**
 * 실수형 분석알고리즘 환경변수
 *
 * @description
 * @version 0.1
 * @date  2016. 10. 18.
 * @author JungHo Kim
 *
 */
public class ParameterFloat extends Parameter{
	
	private float	value;
	private float	minVal;
	private float	maxVal;
	
	public ParameterFloat(String name, float minVal, float maxVal){
		super(name);
		this.minVal = minVal;
		this.maxVal = maxVal;
	}
	
	public ParameterFloat(ParameterFloat parameterFloat){
		super(parameterFloat.getName());
		this.minVal = parameterFloat.getMinVal();
		this.maxVal = parameterFloat.getMaxVal();
		this.value = parameterFloat.value;
	}
	
	public void decoding(float decimal,int binaryStrSize){
		this.value = ((decimal*(this.maxVal-this.minVal))/((float)Math.pow(2.0, (double)binaryStrSize)-1.0f))+(float)this.minVal;
	}
	
	@Override
	String getValueString() {
		return String.valueOf(this.value);
	}

	public float getMinVal() {
		return minVal;
	}

	public float getMaxVal() {
		return maxVal;
	}
	
	@Override
	String getDataType() {
		return "float";
	}

}
