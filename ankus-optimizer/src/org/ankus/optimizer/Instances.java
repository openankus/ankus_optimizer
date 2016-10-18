package org.ankus.optimizer;

import java.util.ArrayList;
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
	 * 생성자
	 * 
	 * @param dataFilePath	데이터 파일 경로
	 * @param delimiter		컬럼 구분자
	 * @param attributeIndexList 사용할 속성컬럼 index 목록
	 * @param classIndex	class index
	 */
	public Instances(String dataFilePath, String delimiter, List<Integer> attributeIndexList, Integer classIndex){
		this.dataFilePath = dataFilePath;
		this.delimiter = delimiter;
		this.classIndex = classIndex;
		this.attributeIndexList = new ArrayList<Integer>(attributeIndexList);
	}
	
	/**
	 * 생성자
	 */
	public Instances(Instances instances) {
		this.dataFilePath = instances.dataFilePath;
		this.delimiter = instances.delimiter;
		this.classIndex = instances.classIndex;
		this.attributeIndexList = new ArrayList<Integer>(instances.attributeIndexList);
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
	
	

}
