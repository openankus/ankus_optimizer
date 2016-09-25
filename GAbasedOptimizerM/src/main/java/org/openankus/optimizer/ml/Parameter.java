package org.openankus.optimizer.ml;

public class Parameter {
	
	private String 	_name;
	private float	_value;
	private float	_minVal;
	private float	_maxVal;
	
	public Parameter(String name, float minVal, float maxVal){
		this._name = name;
		this._minVal = minVal;
		this._maxVal = maxVal;
	}
	
	public void decoding(float decimal,int binaryStrSize){
		this._value = ((decimal*(this._maxVal-this._minVal))/((float)Math.pow(2.0, (double)binaryStrSize)-1.0f))+(float)this._minVal;
	}
	
	public float getValue(){
		return this._value;
	}

	public String getName() {
		return this._name;
	}
	
}
