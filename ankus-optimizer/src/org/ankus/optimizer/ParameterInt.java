package org.ankus.optimizer;

/**
 * 
 * 정수형 분석알고리즘 환경변수
 *
 * @description
 * @version 0.1
 * @date  2016. 10. 18.
 * @author JungHo Kim
 *
 */
public class ParameterInt extends Parameter{
	
	private int	value;
	private int	minVal;
	private int	maxVal;
	
	public ParameterInt(String name, int minVal, int maxVal){
		super(name);
		this.minVal = minVal;
		this.maxVal = maxVal;
	}
	
	public ParameterInt(ParameterInt parameterInt){
		super(parameterInt.getName());
		this.minVal = parameterInt.getMinVal();
		this.maxVal = parameterInt.getMaxVal();
		this.value = parameterInt.value;
	}
	
	public void decoding(int decimal,int binaryStrSize){
		this.value = ((decimal*(this.maxVal-this.minVal))/((int)Math.pow(2.0, binaryStrSize)-1))+(int)this.minVal;
	}
	

	@Override
	String getValueString() {
		return String.valueOf(this.value);
	}

	public int getMinVal() {
		return minVal;
	}

	public int getMaxVal() {
		return maxVal;
	}
	
	@Override
	String getDataType() {
		return "int";
	}
	
	

}
