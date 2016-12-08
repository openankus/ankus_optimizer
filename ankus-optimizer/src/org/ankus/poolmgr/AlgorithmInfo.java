package org.ankus.poolmgr;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 알고리즘 정보 
 *
 * @description
 * @version 0.1
 * @date  2016. 10. 24.
 * @author  Homoon Song
 *
 */
public class AlgorithmInfo {

	/**
	 * 파라메터 형식 - ankus
	 */
	public static final String PARAM_FORMAT_ANKUS = "ankus";
	
	/**
	 * 파라메터 형식 - mahout random forest
	 */
	public static final String PARAM_FORMAT_MAHOUT_RF = "mahoutRF";
	
	/**
	 * 파라메터 형식 - 사용자 구현
	 */
	public static final String PARAM_FORMAT_USER = "user";

	/**
	 * 분류결과 출력파일 모드 - 입력데이터와 함께 클래스 레이블로 출력 
	 */
	public static final String CO_MODE_LABEL_WITH_INPUT = "labelWithInput";

	/**
	 * 분류결과 출력파일 모드 - 입력데이터 없이 클래스 인덱스만 출력 
	 */
	public static final String CO_MODE_INDEX_WITHOUT_INPUT = "indexWithoutInput";
	
	/**
	 * 알고리즘 이름
	 */
	private String algorithmName;
	
	/**
	 * 모델학습 클래스 이름
	 */
	private String trainClassName;
	
	/**
	 * 분류결과출력 클래스 이름
	 */
	private String classifyClassName;
	
	/**
	 * 파라메터 형식
	 */
	private String paramFormat;
	
	
	/**
	 * 파라메터 정보
	 */
	private List<ParamInfo> params = new ArrayList<ParamInfo>();
	
	/**
	 * 분류출력결과의 상대경로(population 출력 디렉터리 기준)
	 */
	private String classifyOutputRelPath;
	
	/**
	 * 파라메터값 목록 구분자
	 */
	private String paramValueDelimiter;
	
	/**
	 * 훈련된 모델 출력파일의 절대경로
	 */
	private String modelAbsPath;
	
	/**
	 * 훈련된 모델 출력파일의 상대경로(population 출력 디렉터리 기준)
	 */
	private String modelRelPath;
	
	/**
	 * 분류결과 출력 모드
	 */
	private String classifyOutputMode = CO_MODE_LABEL_WITH_INPUT;
	
	/**
	 * 생성일시
	 */
	private String createDateTime = null;

	/**
	 * 갱신일시
	 */
	private String updateDateTime = null;
	
	
	public AlgorithmInfo() {}
	
	public AlgorithmInfo(String algorithmName)
	{
		this.algorithmName = algorithmName;
	}


	public String getAlgorithmName() {
		return algorithmName;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}


	public List<ParamInfo> getParams() {
		return params;
	}

	public void setParams(List<ParamInfo> params) {
		this.params = new ArrayList<ParamInfo>(params);
	}

	
	public String getClassifyOutputRelPath() {
		return classifyOutputRelPath;
	}

	public void setClassifyOutputRelPath(String classifyOutputRelPath) {
		this.classifyOutputRelPath = classifyOutputRelPath;
	}


	public String getParamValueDelimiter() {
		return paramValueDelimiter;
	}

	public void setParamValueDelimiter(String paramValueDelimiter) {
		this.paramValueDelimiter = paramValueDelimiter;
	}

	public String getClassifyOutputMode() {
		return classifyOutputMode;
	}

	public void setClassifyOutputMode(String classifyResultMode) {
		this.classifyOutputMode = classifyResultMode;
	}

	public String getTrainClassName() {
		return trainClassName;
	}

	public void setTrainClassName(String trainClassName) {
		this.trainClassName = trainClassName;
	}

	public String getClassifyClassName() {
		return classifyClassName;
	}

	public void setClassifyClassName(String classifyClassName) {
		this.classifyClassName = classifyClassName;
	}

	public String getModelAbsPath() {
		return modelAbsPath;
	}

	public void setModelAbsPath(String modelAbsPath) {
		this.modelAbsPath = modelAbsPath;
	}

	public String getModelRelPath() {
		return modelRelPath;
	}

	public void setModelRelPath(String modelRelPath) {
		this.modelRelPath = modelRelPath;
	}

	public String getParamFormat() {
		return paramFormat;
	}

	public void setParamFormat(String paramFormat) {
		this.paramFormat = paramFormat;
	}

	public String getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(String createDateTime) {
		this.createDateTime = createDateTime;
	}

	public String getUpdateDateTime() {
		return updateDateTime;
	}

	public void setUpdateDateTime(String updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

	
	
	
	
	
	
};
