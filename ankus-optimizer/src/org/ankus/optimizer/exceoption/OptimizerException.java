package org.ankus.optimizer.exceoption;

public class OptimizerException extends Exception{
	
	public OptimizerException(String message) {
		super(message);
	}
	
	public OptimizerException(String message, Throwable ex) {
		super(message, ex);
	}

}
