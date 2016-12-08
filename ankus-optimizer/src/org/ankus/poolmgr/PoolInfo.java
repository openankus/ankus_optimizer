package org.ankus.poolmgr;

import java.util.ArrayList;
import java.util.List;
/**
 * pool 정보
 *
 * @description
 * @version 0.1
 * @date  2016. 10. 24.
 * @author Homoon Song
 * @modified Jungho Kim
 *
 */
public class PoolInfo {
	
	/**
	 * Pool 이름
	 */
	private String poolName;
	
	/**
	 * JAR 파일 경로
	 */
	private String fileName;
	
	/**
	 * Pool 소속 알고리즘 목록
	 */
	private List<AlgorithmInfo> algorithms = new ArrayList<AlgorithmInfo>();
	
	/**
	 * 생성일시
	 */
	private String createDateTime = null;

	/**
	 * 갱신일시
	 */
	private String updateDateTime = null;
	
	
	public PoolInfo()
	{
		
	}
	
	public PoolInfo(String poolName, String fileName)
	{
		this.poolName = poolName;
		this.fileName = fileName;
	}

	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<AlgorithmInfo> getAlgorithms() {
		return algorithms;
	}

	public void setAlgorithms(List<AlgorithmInfo> algorithms) {
		this.algorithms = new ArrayList<AlgorithmInfo>(algorithms);
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
