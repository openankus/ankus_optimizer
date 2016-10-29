package org.ankus.poolmgr;

import java.util.ArrayList;
import java.util.List;

/**
 * 알고리즘 학습 및 테스트에 사용될 데이터 파일 정보
 *
 * @description
 * @version 0.1
 * @date  2016. 10. 25.
 * @author JungHo Kim
 *
 */
public class DataInfo {
	
	/**
	 * 데이터 경로
	 */
	private String path;
	
	/**
	 * 컬럼 구분자
	 */
	private String delimiter;
	
	private List<String> indexList = new ArrayList<String>();
	
	/**
	 * 클래스 컬럼 인덱스
	 */
	private int classIndex;
	
	/**
	 * 클래스 레이블 목록
	 */
	private List<String> classLabelList = new ArrayList<String>();
	
	public DataInfo() {
	}
	
	public DataInfo(String path, String delimiter, List<String> indexList, int classIndex, List<String> classLabelList) {
		this.path = path;
		this.delimiter = delimiter;
		setIndexList(indexList);
		this.classIndex = classIndex;
		setClassLabelList(classLabelList);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	
	public List<String> getIndexList() {
		return indexList;
	}

	public void setIndexList(List<String> indexList) {
		this.indexList = new ArrayList<String>(indexList);
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

	
	
}
