package org.ankus.poolmgr;

import java.util.ArrayList;
import java.util.List;

/**
 * 유전자알고리즘 최적화 설정 정보
 *
 * @description
 * @version 0.1
 * @date  2016. 10. 25.
 * @author JungHo Kim
 *
 */
public class OptimizerConfigInfo {
	
	/**
	 * 데이터 경로
	 */
	private String dataFilePath;
	
	/**
	 * 컬럼 구분자
	 */
	private String delimiter;
	
	/**
	 * 수치형 속성 인덱스 목록(해당 값 설정시, 속성 인덱스목록은 무시...)
	 */
	private List<String> numericIndexList = new ArrayList<String>();
	
	/**
	 * 기호형 속성 인덱스 목록(해당 값 설정시, 속성 인덱스목록은 무시...)
	 */
	private List<String> nominalIndexList = new ArrayList<String>();
	
	/**
	 * 클래스 컬럼 인덱스
	 */
	private int classIndex;
	
	/**
	 * 클래스 레이블 목록
	 */
	private List<String> classLabelList = new ArrayList<String>();
	
	/**
	 * 분류알고리즘 Pool
	 */
	private String poolName;
	
	/**
	 * 분류알고리즘
	 */
	private String algorithmName;
	
	/**
	 * 난수발생기 seed
	 */
	private int randomSeed = 1;
	
	/**
	 * 세대 별 개체생성 개수
	 */
	private int popSize = 10;

	/**
	 * 교배확률
	 */
	private float crossProbabilty = 0.9f;

	/**
	 * 돌연변이율
	 */
	private float mutationProbabilty = 0.5f;
	
	/**
	 * 최대 세대수
	 */
	private int maxGeneration =  1;
	
	/**
	 * 최소 적합도
	 */
	private float minFitness = 0.75f;
	
	/**
	 * 이진 문자열 크기
	 */
	private int binaryStringSize = 3;
	
	
	/**
	 * 알고리즘 환경변수에 대한 사용자 설정 활용 여부
	 */
	private boolean userAlgorithmParamMode = false;
	
	/**
	 * 사용자가 설정한 알고리즘 파라메터 값 목록
	 */
	private List<ParamInfo> userAlgorithmParams = new ArrayList<ParamInfo>();
	
	/**
	 * 멀티스레드 알고리즘 평가 여부
	 */
	private boolean multiThreadEval = true;
	
	
	public String getDataFilePath() {
		return dataFilePath;
	}

	public void setDataFilePath(String dataFilePath) {
		this.dataFilePath = dataFilePath;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	
	public int getClassIndex() {
		return classIndex;
	}

	public void setClassIndex(int classIndex) {
		this.classIndex = classIndex;
	}

	public List<String> getClassLabelList() {
		return classLabelList;
	}

	public void setClassLabelList(List<String> classLabelList) {
		this.classLabelList = new ArrayList<String>(classLabelList);
	}

	public List<String> getNumericIndexList() {
		return numericIndexList;
	}

	public void setNumericIndexList(List<String> numericIndexList) {
		this.numericIndexList = numericIndexList;
	}

	public List<String> getNominalIndexList() {
		return nominalIndexList;
	}

	public void setNominalIndexList(List<String> nominalIndexList) {
		this.nominalIndexList = nominalIndexList;
	}


	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	public int getRandomSeed() {
		return randomSeed;
	}

	public void setRandomSeed(int randomSeed) {
		this.randomSeed = randomSeed;
	}

	public int getPopSize() {
		return popSize;
	}

	public void setPopSize(int popSize) {
		this.popSize = popSize;
	}

	public float getCrossProbabilty() {
		return crossProbabilty;
	}

	public void setCrossProbabilty(float crossProbabilty) {
		this.crossProbabilty = crossProbabilty;
	}

	public float getMutationProbabilty() {
		return mutationProbabilty;
	}

	public void setMutationProbabilty(float mutationProbabilty) {
		this.mutationProbabilty = mutationProbabilty;
	}

	public int getMaxGeneration() {
		return maxGeneration;
	}

	public void setMaxGeneration(int maxGeneration) {
		this.maxGeneration = maxGeneration;
	}

	public float getMinFitness() {
		return minFitness;
	}

	public void setMinFitness(float minFitness) {
		this.minFitness = minFitness;
	}

	public int getBinaryStringSize() {
		return binaryStringSize;
	}

	public void setBinaryStringSize(int binaryStringSize) {
		this.binaryStringSize = binaryStringSize;
	}

	public boolean isUserAlgorithmParamMode() {
		return userAlgorithmParamMode;
	}

	public void setUserAlgorithmParamMode(boolean userAlgorithmParamMode) {
		this.userAlgorithmParamMode = userAlgorithmParamMode;
	}

	public List<ParamInfo> getUserAlgorithmParams() {
		return userAlgorithmParams;
	}

	public void setUserAlgorithmParams(List<ParamInfo> userAlgorithmParams) {
		this.userAlgorithmParams = new ArrayList<ParamInfo>(userAlgorithmParams);
	}

	public boolean isMultiThreadEval() {
		return multiThreadEval;
	}

	public void setMultiThreadEval(boolean multiThreadEval) {
		this.multiThreadEval = multiThreadEval;
	}
	
	
}
