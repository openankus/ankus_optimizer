package org.ankus.poolmgr;

import java.util.ArrayList;

/**
 *	Manager Data 
 *
 * @description
 * @version 0.1
 * @date  2016. 10. 24.
 * @author Homoon Song
 *
 */
public class ManagerData {
	
	/**
	 * Pool 목록
	 */
	public ArrayList<PoolInfo> pools;
	
	/**
	 * 유전자 알고리즘 설정 정보
	 */
	private OptimizerConfigInfo optimizerConfigInfo = new OptimizerConfigInfo();
	
	public ManagerData() {}

	public OptimizerConfigInfo getOptimizerConfigInfo() {
		return optimizerConfigInfo;
	}
	
	
}
