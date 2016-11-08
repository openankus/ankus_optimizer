package org.ankus.optimizer;

/**
 * 분석알고리즘 환경변수 설정정보
 *
 * @description
 * @version 0.1
 * @date  2016. 10. 17.
 * @author JungHo Kim
 *
 */
public abstract class Parameter{
	
	/**
	 * 환경변수 이름
	 */
	protected String 	name;
	
	/**
	 * 생성자
	 * 
	 * @param name 환경변수 이름
	 */
	protected Parameter(String name){
		this.name = name;
	}
	

	/**
	 * 환경변수 이름
	 * 
	 * @return
	 */
	protected String getName() {
		return this.name;
	}
	
	/**
	 * 환경변수의 값을 문자열 형태로 반환
	 * 
	 * @return 환경변수값을 문자열
	 */
	abstract String getValueString();
	
	/**
	 * 해당 파라메터의 자료형

	 * @return 자료형 문자열
	 */
	abstract String getDataType();
}
