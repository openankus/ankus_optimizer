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
	private String poolName;
	private String fileName;
	private List<AlgorithmInfo> algorithms = new ArrayList<AlgorithmInfo>();
	
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
	
	
};
