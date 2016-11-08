package org.ankus.poolmgr;

/**
 * 파라미터 정보
 *
 * @description
 * @version 0.1
 * @date  2016. 10. 24.
 * @author Homoon Song
 *
 */
public class ParamInfo implements Cloneable{	

	/**
	 * 훈련 데이터 입력파일 기본 파라메터 유형
	 */
	public static final String PARAM_TYPE_INPUT = "input";
	
	/**
	 * 테스트 데이터 입력파일 기본 파라메터 유형
	 */
	public static final String PARAM_TYPE_INPUT_TEST = "inputTest";
	
	/**
	 * 구분자 기본 파라메터 유형
	 */
	public static final String PARAM_TYPE_DELIMITER = "delimiter";
	
	/**
	 * 출력파일 기본 파라메터 유형
	 */
	public static final String PARAM_TYPE_OUTPUT = "output";
	
	/**
	 * 클래스 인덱스 기본 파라메터 유형
	 */
	public static final String PARAM_TYPE_CLASS_INDEX = "classIndex";
	
	/**
	 * 속성 인덱스 목록 기본 파라메터 유형
	 */
	public static final String PARAM_TYPE_INDEX_LIST = "indexList";
	
	/**
	 * 수치형 속성 인덱스 목록 기본 파라메터 유형
	 */
	public static final String PARAM_TYPE_NUMERIC_INDEX_LIST = "numericIndexList";
	
	/**
	 * 기호형 속성 인덱스 목록 기본 파라메터 유형
	 */
	public static final String PARAM_TYPE_NOMINAL_INDEX_LIST = "nominalIndexList";
	
	/**
	 * 훈련된 모델경로 기본 파라메터 유형
	 */
	public static final String PARAM_TYPE_MODEL_PATH = "modelPath";
	
	/**
	 *  알고리즘 최적화 관련 파라메터 유형
	 */
	public static final String PARAM_TYPE_OPTIMIZE = "optimize";
	

	/**
	 *  알고리즘 최적화와 관련없는 기타 학습 파라메터 유형
	 */
	public static final String PARAM_TYPE_TRAIN_ETC = "trainEtc";
	
	/**
	 *  알고리즘 최적화와 관련없는 기타 학습 파라메터 유형
	 */
	public static final String PARAM_TYPE_CLASSIFY_ETC = "classifyEtc";
	
	/**
	 * 정수 자료형
	 */
	public static final String DATA_TYPE_INT = "int";
	
	/**
	 * 실수 자료형
	 */
	public static final String DATA_TYPE_FLOAT = "float";
	
//	/**
//	 * 기호 자료형
//	 */
//	public static final String DATA_TYPE_NOMINAL = "nominal";
	
	
	
	/**
	 * 파라메터 이름
	 */
	private String paramName;
	
	/**
	 * 파라메터 기본값
	 */
	private String paramValue;
	
	/**
	 * 타입
	 */
	private String dataType; 
	/**
	 * 자료형
	 */
	private String paramType;
	/**
	 * 최소값(수치 자료형 관련)
	 */
	private String min; 
	/**
	 * 최대값(수치 자료형 관련)
	 */
	private String max; 
//	/**
//	 * 허용 기호값 목록(기호 자료형 관련)
//	 */
//	private String values; // 
	
	public ParamInfo() {}
	
	public ParamInfo(ParamInfo param){
		this.paramName = param.getParamName();
		this.dataType = param.getDataType(); 
		this.paramType = param.getParamType();
		this.min = param.getMin(); 
		this.max = param.getMax(); 
//		this.values = param.getValues(); 
	}
	
//	public ParamInfo(String paramName, String defaultValue, String min, String max) {
//		super();
//		this.paramName = paramName;
//		this.defaultValue = defaultValue;
//		this.dataType = "numeric";
//		this.min = min;
//		this.max = max;
//		this.values = values;
//	}
//	public ParamInfo(String paramName, String defaultValue, String values) {
//		super();
//		this.paramName = paramName;
//		this.defaultValue = defaultValue;
//		this.dataType = "nominal";
//		this.values = values;
//	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

//	public String getValues() {
//		return values;
//	}
//
//	public void setValues(String values) {
//		this.values = values;
//	}
	
	
};