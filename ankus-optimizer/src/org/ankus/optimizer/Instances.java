package org.ankus.optimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 데이터 집합
 *
 * @description
 * @version 0.1
 * @date  2016. 10. 17.
 * @author JungHo Kim
 *
 */
public class Instances {
	
	/**
	 * 데이터 파일의 경로
	 */
	private String dataFilePath;
	
	/**
	 * 컬럼 구분자
	 */
	private String delimiter = "\t";
	
	/**
	 * Class Index
	 */
	private Integer classIndex = -1;
	
	/**
	 * 속성 index 목록
	 */
	private List<Integer> attributeIndexList = new ArrayList<Integer>();
	
	/**
	 * 수치형 속성 index 목록
	 */
	List<Integer> numericIndexList = new ArrayList<Integer>();
	
	/**
	 * 기호형 속성 index 목록
	 */
	List<Integer> nominalIndexList = new ArrayList<Integer>();
	
	/**
	 * 클래스 레이블 목록
	 */
	List<String> classLabelList = new ArrayList<String>();
	
	/**
	 * 데이터의 전체 컬럼 개수(훈련 및 테스트용 이 아닌 속성들을 포함)
	 */
	Integer numAllAttr = 0;
	
	/**
	 * 생성자
	 * 
	 * @param dataFilePath	데이터 파일 경로
	 * @param delimiter		컬럼 구분자
	 * @param numAllAttr	데이터의 전체 컬럼 개수(훈련 및 테스트용 이 아닌 속성들을 포함)
	 * @param numericIndexList 수치형 속성컬럼 index 목록
	 * @param nominalIndexList 속성형 속성 컬럴 index 목록
	 * @param classIndex	class index
	 * @param classLabelList 클래스 레이블 목록
	 */
	public Instances(String dataFilePath, String delimiter, int numAllAttr, List<Integer> numericIndexList, List<Integer> nominalIndexList, Integer classIndex, List<String> classLabelList){
		this.dataFilePath = dataFilePath;
		this.delimiter = delimiter;
		this.numAllAttr = numAllAttr;
		this.classIndex = classIndex;
		this.numericIndexList = new ArrayList<Integer>(numericIndexList);
		this.nominalIndexList = new ArrayList<Integer>(nominalIndexList);
		this.attributeIndexList = new ArrayList<Integer>();
		attributeIndexList.addAll(numericIndexList);
		attributeIndexList.addAll(nominalIndexList);
		Collections.sort(attributeIndexList);
		this.classLabelList = new ArrayList<String>(classLabelList);
	}
	
	/**
	 * 생성자
	 */
	public Instances(Instances instances) {
		this.dataFilePath = instances.dataFilePath;
		this.delimiter = instances.delimiter;
		this.numAllAttr = instances.numAllAttr;
		this.classIndex = instances.classIndex;
		this.numericIndexList = new ArrayList<Integer>(instances.numericIndexList);
		this.nominalIndexList = new ArrayList<Integer>(instances.nominalIndexList);
		this.attributeIndexList = new ArrayList<Integer>();
		attributeIndexList.addAll(numericIndexList);
		attributeIndexList.addAll(nominalIndexList);
		Collections.sort(attributeIndexList);
		this.classLabelList = new ArrayList<String>(instances.classLabelList);
	}

	public String getDataFilePath() {
		return dataFilePath;
	}

	public Integer getClassIndex() {
		return classIndex;
	}

	public String getDelimiter() {
		return delimiter;
	}
	
	/**
	 * 입력속성 index 삭제
	 * 
	 * @param position 삭제할 
	 */
	public void deleteAttributeIndexAt(int position){
		
		Integer attrIdx = this.attributeIndexList.get(position);
		int idx=0;
		for (Integer tmpIdx : this.numericIndexList){
			if (tmpIdx.equals(attrIdx)){
				//	 수치형 속성일 경우, 해당 index 삭제
				this.numericIndexList.remove(tmpIdx);
				break;
			}
			idx++;
		}
		idx=0;
		for (Integer tmpIdx : this.nominalIndexList){
			if (tmpIdx.equals(attrIdx)){
				//	 기호형 속성일 경우, 해당 index 삭제
				this.nominalIndexList.remove(tmpIdx);
				break;
			}
			idx++;
		}
		
		this.attributeIndexList.remove(position);
	}
	
	/**
	 * 입력속성 index  개수 반환
	 * 
	 * @return 입력속성 index 개수
	 */
	public int getSizeOfAttributeIndexList(){
		return this.attributeIndexList.size();
	}
	
	/**
	 * 입력속성index 반환
	 * 
	 * @param position 입력속성index 목록에서의 위치
	 * @return
	 */
	public int getAttributeIndexAt(int position){
		return this.attributeIndexList.get(position);
	}

	public List<Integer> getNumericIndexList() {
		return numericIndexList;
	}

	public List<Integer> getNominalIndexList() {
		return nominalIndexList;
	}

	public List<String> getClassLabelList() {
		return classLabelList;
	}

	public Integer getNumAllAttr() {
		return numAllAttr;
	}
	
	

}
