package org.ankus.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ankus.optimizer.GAMain;
import org.ankus.poolmgr.AlgorithmInfo;
import org.ankus.poolmgr.DataInfo;
import org.ankus.poolmgr.OptimizerConfigInfo;
import org.ankus.poolmgr.ManagerData;
import org.ankus.poolmgr.ParamInfo;
import org.ankus.poolmgr.PoolInfo;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;



/**
 * poolmgr 파라미터 정보 
 * <pre>
 * input : input.txt   - 입력데이터 파일
 * output : output.txt - 출력데이터 파일
 * Table : none  
 * </pre>
 *
 *poolmgr - add, list
 *
 * <pre>
 * <b>History:</b>
 *    작성자, 1.0, 2016.9.23 초기작성
 * </pre>
 *
 * @author shm
 * @version 1.0,
 * @
 */

public class App
{
	
	
	public static String datafile = "mgr.json";
	
	public static ObjectMapper mapper = new ObjectMapper();

	public static ManagerData load()
	{
		ManagerData data = null;

    	try {
			data = mapper.readValue(new File(datafile), ManagerData.class );
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
//    		System.out.printf("PASER ERROR\n");
//			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
//    		System.out.printf("mapping ERROR\n");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//    		System.out.printf("io ERROR\n");
//			e.printStackTrace();
		}

		return data;
	}
	
	public static boolean save(ManagerData data)
	{
		try {
			mapper.writeValue(new File(datafile), data);
			return true;
			
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static PoolInfo findpool(ManagerData data, String poolname)
	{
		if(data==null || data.pools==null) return null;
		
		for(int i =0; i<data.pools.size(); i++) if(data.pools.get(i).getPoolName().equals(poolname)) return data.pools.get(i);

		return null;
	}

	public static boolean deletepool(ManagerData data, String poolname)
	{
		if(data==null || data.pools==null) return false;
		
		for(int i =0; i<data.pools.size(); i++) if(data.pools.get(i).getPoolName().equals(poolname)) {
			
			data.pools.remove(i);
			return true;
		}

		return false;
	}

	public static AlgorithmInfo findalgo(ManagerData data, String algoname)
	{
		if(data==null || data.pools==null) return null;
		
		for(int i=0; i<data.pools.size(); i++) {
			
			for(int j=0; j<data.pools.get(i).getAlgorithms().size(); j++)
				if(data.pools.get(i).getAlgorithms().get(j).getAlgorithmName().equals(algoname)) return data.pools.get(i).getAlgorithms().get(j);
		}

		return null;
	}

	public static boolean deletealgo(ManagerData data, String poolname, String algoname)
	{
		if(data==null || data.pools==null) return false;
		
		for(int i=0; i<data.pools.size(); i++) {
			
			if(!data.pools.get(i).getPoolName().equals(poolname)) continue;
			if(data.pools.get(i).getAlgorithms()==null) continue;
			for(int j=0; j<data.pools.get(i).getAlgorithms().size(); j++)
				if(data.pools.get(i).getAlgorithms().get(j).getAlgorithmName().equals(algoname)) {
					data.pools.get(i).getAlgorithms().remove(j);
					return true;
				}
		}

		return false;
	}
	
	
    public static void main( String[] args ) throws Exception
    {
    	if(args.length<2) {
//    		System.out.printf("poolmgr add <pool명> <파일명>\n");
    		System.out.printf("poolmgr add <pool명>\n");
    		System.out.printf("poolmgr delete <pool명>\n");
    		System.out.printf("poolmgr list\n");
    		System.out.printf("algomgr add <pool명> <algorithm명> <shell 명렁어>\n");
    		System.out.printf("algomgr delete <pool명> <algorithm명>\n");
//    		System.out.printf("algomgr delete <pool명> <algorithm명>\n");
    		System.out.printf("algomgr list <pool명>\n");
    		
    		
    		//	김정호 추가
    		System.out.printf("algomgr setCommand <pool명> <algorithm명> <shell 명령어>\n");
    		
    		
    		
    		System.out.printf("optmgr add <algorithm명> <parameter명> numeric <초기값> <min> <max>\n");
    		System.out.printf("optmgr add <algorithm명> <parameter명> nominal <초기값> <values>\n");
//    		System.out.printf("optmgr add <algorithm명> <parameter명> <type(numeric|nominal)> <초기값> (<values>|(<min> <max>))\n");
    		System.out.printf("optmgr delete <algorithm명> <parameter명>\n");
    		System.out.printf("optmgr list <algorithm명>\n");
    		
    		return;
    	}
    	String mgr = args[0];
    	String cmd = args[1];
    	
    	ManagerData info = load();
    	
    	if(info==null) info = new ManagerData();
    	
    	// pool mgr
    	if(mgr.equalsIgnoreCase("poolmgr") && cmd.equalsIgnoreCase("add") && args.length==4)
    	{
    		String poolname = args[2];
    		String poolfile = args[3];
    		
    		if(!(new File(poolfile)).exists()) {
    			System.out.printf("file not found!\n");
    			return;
    		}
//    		if(info==null) info = new MgrData();
    		
    		if(info.pools == null) info.pools = new ArrayList<PoolInfo>();
    		
    		for (PoolInfo poolInfo : info.pools){
    			if (poolInfo.getPoolName().equals(poolname)){
        			System.out.printf("이미 동일한 이름의 pool이 존재합니다.!\n");
    				System.exit(1);
    			}
    		}
    		
    		PoolInfo pi = new PoolInfo(poolname, poolfile);
    		info.pools.add(pi);
    		
    		if(save(info)) System.out.printf("Add pool %s %s\n", poolname, poolfile);
    		else System.out.printf("Add Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("poolmgr") && cmd.equalsIgnoreCase("list"))
    	{
			System.out.printf("Pool List\n========================\n");

			if(info==null) {
				System.out.printf("none\n");
				return;
			}

			for(int i = 0; i<info.pools.size(); i++)
			{
				System.out.printf("pool%d. poolname=[%s], poolfile=[%s], algorithmcnt=[%d]\n", i+1, info.pools.get(i).getPoolName(), info.pools.get(i).getFileName(), info.pools.get(i).getAlgorithms().size());
			}
    	}
    	else if(mgr.equalsIgnoreCase("poolmgr") && cmd.equalsIgnoreCase("delete") && args.length==3)
    	{
    		String poolname = args[2];
			for(int i = 0; i<info.pools.size(); i++)
			{
				if(info.pools.get(i).getPoolName().equals(poolname))
				{
					System.out.printf("poolname=[%s] deleted\n", info.pools.get(i).getPoolName());
					info.pools.remove(i);
					if(save(info))
					{
//						System.out.printf("poolname=[%s] poolfile=[%s] deleted\n", info.pools.get(i).poolName, info.pools.get(i).fileName);
						return;
					}
				}
			}
			System.out.printf("pool not found!!\n");			
    	}
    	// algo mgr
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("add") && args.length==6)
    	{
    		String poolname = args[2];
    		String algorithmname = args[3];
    		String trainClassName = args[4];
    		String classifyClassName = args[4];
    		
    		PoolInfo pinfo = findpool(info, poolname);
    		if(pinfo==null) {
    			System.out.printf("poolname not found!\n");
    			return;
    		}
    		
    		AlgorithmInfo ainfo = findalgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo!=null)
    		{
    			System.out.printf("알고리즘이 이미 등록됨.\n");
    			return;
    		}
    		
    		AlgorithmInfo ai = new AlgorithmInfo(algorithmname, trainClassName, classifyClassName);
    		pinfo.getAlgorithms().add(ai);
    		
    		if(save(info)) System.out.printf("Add algorithm %s %s %s %s\n", poolname, algorithmname, trainClassName, classifyClassName);
    		else System.out.printf("Add Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") 
    			&& cmd.equalsIgnoreCase("setParam") 
    			&& args.length==5
    			&& (ParamInfo.PARAM_TYPE_INPUT.equals(args[3]) 
    					|| ParamInfo.PARAM_TYPE_DELIMITER.equals(args[3])
    					|| ParamInfo.PARAM_TYPE_OUTPUT.equals(args[3]) 
    					|| ParamInfo.PARAM_TYPE_INDEX_LIST.equals(args[3]) 
    					|| ParamInfo.PARAM_TYPE_NUMERIC_INDEX_LIST.equals(args[3]) 
    					|| ParamInfo.PARAM_TYPE_NOMINAL_INDEX_LIST.equals(args[3]) 
    					|| ParamInfo.PARAM_TYPE_CLASS_INDEX.equals(args[3])
    					|| ParamInfo.PARAM_TYPE_MODEL_PATH.equals(args[3])) 
    			)
    	{
    		String algorithmname = args[2];
    		String paramType = args[3];
    		String paramName = args[4];
    		
    		AlgorithmInfo ainfo = findalgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("알고리즘이 존재하지 않습니다.\n");
    			return;
    		}
    		
    		List<ParamInfo> params = ainfo.getParams();
    		boolean hasParam = false;
    		for (ParamInfo paramInfo : ainfo.getParams()){
    			if (paramName.equals(paramInfo.getParamName())){
    				paramInfo.setParamName(paramName);
    				paramInfo.setParamType(paramType);
    				hasParam = true;
    				break;
    			}
    		}
    		
    		if (!hasParam){
    			ParamInfo paramInfo = new ParamInfo();
    			paramInfo.setParamName(paramName);
    			paramInfo.setParamType(paramType);
    			ainfo.getParams().add(paramInfo);
    		}
    		
    		if(save(info)) System.out.printf("Set algorithm parameter %s %s %s\n", algorithmname, paramType, paramName);
    		else System.out.printf("Add Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") 
    			&& cmd.equalsIgnoreCase("setParam") 
    			&& args.length == 8
    			&& (ParamInfo.PARAM_TYPE_OPTIMIZE.equals(args[3])) 
    			)
    	{
    		String algorithmname = args[2];
    		String paramType = args[3];
    		String paramName = args[4];
    		String dataType = args[5];
    		
    		AlgorithmInfo ainfo = findalgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("알고리즘이 존재하지 않습니다.\n");
    			return;
    		}
    		
    		if (!ParamInfo.DATA_TYPE_INT.equals(dataType) && !ParamInfo.DATA_TYPE_FLOAT.equals(dataType)){
    			System.out.printf("자료형이 존재하지 않습니다.\n");
    			return;
    		}
    		
			String min = args[6];
			String max = args[7];
    		if (ParamInfo.DATA_TYPE_INT.equals(dataType)){
    			try{
    				Integer.parseInt(min);
    				Integer.parseInt(max);
    			}catch(NumberFormatException ex){
    				System.out.println("최대값 및 최소값을 자료형에 맞게 입력해주세요.");
    				return;
    				
    			}
    		}else if (ParamInfo.DATA_TYPE_FLOAT.equals(dataType)){
    			try{
    				Float.parseFloat(min);
    				Float.parseFloat(max);
    			}catch(NumberFormatException ex){
    				System.out.println("최대값 및 최소값을 자료형에 맞게 입력해주세요.");
    				return;
    				
    			}
    		}
    		
    		
    		
    		List<ParamInfo> params = ainfo.getParams();
    		boolean hasParam = false;
    		for (ParamInfo paramInfo : ainfo.getParams()){
    			if (paramName.equals(paramInfo.getParamName())){
    				paramInfo.setParamName(paramName);
    				paramInfo.setParamType(paramType);
    				paramInfo.setDataType(dataType);
    				paramInfo.setMax(max);
    				paramInfo.setMin(min);
    				paramInfo.setParamValue(null);
    				hasParam = true;
    				break;
    			}
    		}
    		
    		if (!hasParam){
    			ParamInfo paramInfo = new ParamInfo();
    			paramInfo.setParamName(paramName);
				paramInfo.setParamType(paramType);
				paramInfo.setDataType(dataType);
				paramInfo.setMax(max);
				paramInfo.setMin(min);
				paramInfo.setParamValue(null);
    			ainfo.getParams().add(paramInfo);
    		}
    		
    		if(save(info)) System.out.printf("Set algorithm parameter %s %s %s %s %s %s\n", algorithmname, paramType, paramName, dataType, min, max);
    		else System.out.printf("Set Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") 
    			&& cmd.equalsIgnoreCase("setParam") 
    			&& args.length >= 5
    			&& (ParamInfo.PARAM_TYPE_TRAIN_ETC.equals(args[3]) ||  ParamInfo.PARAM_TYPE_CLASSIFY_ETC.equals(args[3])) 
    			)
    	{
    		String algorithmname = args[2];
    		String paramType = args[3];
    		String paramName = args[4];
    		String paramValue = null;
    		if (args.length >= 6){
        		paramValue = args[5];
    		}
    		
    		AlgorithmInfo ainfo = findalgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("알고리즘이 존재하지 않습니다.\n");
    			return;
    		}
    		
    		List<ParamInfo> params = ainfo.getParams();
    		boolean hasParam = false;
    		for (ParamInfo paramInfo : ainfo.getParams()){
    			if (paramName.equals(paramInfo.getParamName())
    					&& paramType.equals(paramInfo.getParamType())){
    				paramInfo.setParamName(paramName);
    				paramInfo.setParamType(paramType);
    				paramInfo.setParamValue(paramValue);
    				hasParam = true;
    				break;
    			}
    		}
    		
    		if (!hasParam){
    			ParamInfo paramInfo = new ParamInfo();
    			paramInfo.setParamName(paramName);
    			paramInfo.setParamType(paramType);
				paramInfo.setParamValue(paramValue);
    			ainfo.getParams().add(paramInfo);
    		}
    		
    		if(save(info)) System.out.printf("Set algorithm parameter %s %s %s %s\n", algorithmname, paramType, paramName, paramValue);
    		else System.out.printf("Add Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("delParam") && args.length==4)
    	{
    		String algorithmname = args[2];
    		String paramName = args[3];
    		
    		AlgorithmInfo ainfo = findalgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("알고리즘이 존재하지 않습니다.\n");
    			return;
    		}

    		List<ParamInfo> params = ainfo.getParams();
    		boolean hasParam = false;
    		int idx=0;
    		for (ParamInfo paramInfo : ainfo.getParams()){
    			if (paramName.equals(paramInfo.getParamName())){
    				ainfo.getParams().remove(idx);
    				break;
    			}
    			idx++;
    		}
    		
    		if(save(info)) System.out.printf("Delete algorithm parameter %s %s\n", algorithmname, paramName);
    		else System.out.printf("Add Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("setClassifyOutputRelPath") && args.length==4)
    	{
    		String algorithmname = args[2];
    		String classifyOutputRelPath = args[3];
    		
    		AlgorithmInfo ainfo = findalgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("알고리즘이 존재하지 않습니다.\n");
    			return;
    		}

    		ainfo.setClassifyOutputRelPath(classifyOutputRelPath);
    		
    		if(save(info)) System.out.printf("Add algorithm classification output relative path %s %s\n", algorithmname, classifyOutputRelPath);
    		else System.out.printf("Add Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("delClassifyOutputRelPath") && args.length==3)
    	{
    		String algorithmname = args[2];
    		
    		AlgorithmInfo ainfo = findalgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("알고리즘이 존재하지 않습니다.\n");
    			return;
    		}

    		ainfo.setClassifyOutputRelPath(null);
    		
    		if(save(info)) System.out.printf("Delete algorithm classification output relative path %s\n", algorithmname);
    		else System.out.printf("Delete Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("setModelAbsPath") && args.length==4)
    	{
    		String algorithmname = args[2];
    		String modelPath = args[3];
    		
    		AlgorithmInfo ainfo = findalgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("알고리즘이 존재하지 않습니다.\n");
    			return;
    		}
    		
    		ainfo.setModelAbsPath(modelPath);
    		
    		if(save(info)) System.out.printf("Add algorithm model absolute path %s %s\n", algorithmname, modelPath);
    		else System.out.printf("Add Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("delModelAbsPath") && args.length==3)
    	{
    		String algorithmname = args[2];
    		
    		AlgorithmInfo ainfo = findalgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("알고리즘이 존재하지 않습니다.\n");
    			return;
    		}

    		ainfo.setModelAbsPath(null);
    		
    		if(save(info)) System.out.printf("Delete algorithm model absolute path %s\n", algorithmname);
    		else System.out.printf("Delete Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("setModelRelPath") && args.length==4)
    	{
    		String algorithmname = args[2];
    		String modelPath = args[3];
    		
    		AlgorithmInfo ainfo = findalgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("알고리즘이 존재하지 않습니다.\n");
    			return;
    		}
    		
    		ainfo.setModelRelPath(modelPath);
    		
    		if(save(info)) System.out.printf("Add algorithm model relative path %s %s\n", algorithmname, modelPath);
    		else System.out.printf("Add Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("delModelRelPath") && args.length==3)
    	{
    		String algorithmname = args[2];
    		
    		AlgorithmInfo ainfo = findalgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("알고리즘이 존재하지 않습니다.\n");
    			return;
    		}

    		ainfo.setModelRelPath(null);
    		
    		if(save(info)) System.out.printf("Delete algorithm model relative path %s\n", algorithmname);
    		else System.out.printf("Delete Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("setParamValueDelimiter") && args.length==4)
    	{
    		String algorithmname = args[2];
    		String paramValueDelimiter = args[3];
    		
    		AlgorithmInfo ainfo = findalgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("알고리즘이 존재하지 않습니다.\n");
    			return;
    		}

			ainfo.setParamValueDelimiter(paramValueDelimiter);
    		
    		if(save(info)) System.out.printf("Add algorithm parameter value delimiter %s %s\n", algorithmname, paramValueDelimiter);
    		else System.out.printf("Add Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("delParamValueDelimiter") && args.length==3)
    	{
    		String algorithmname = args[2];
    		
    		AlgorithmInfo ainfo = findalgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("알고리즘이 존재하지 않습니다.\n");
    			return;
    		}

			ainfo.setParamValueDelimiter(null);
    		
    		if(save(info)) System.out.printf("Delete algorithm parameter value delimiter %s \n", algorithmname);
    		else System.out.printf("Add Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("list") && args.length==3)
    	{
    		String poolname = args[2];
			System.out.printf("%s pool algorithm List\n========================\n", poolname);

    		PoolInfo pinfo = findpool(info, poolname);
    		if(pinfo==null) {
    			System.out.printf("pool not found!\n");
    			return;
    		}

    		List<AlgorithmInfo> algorithms = pinfo.getAlgorithms();
			for(int i = 0; i<algorithms.size(); i++)
			{
				System.out.printf("algorithm%d. algorithmname=[%s], trainClassName=[%s], classifyClassName=[%s], paramcnt=[%d]\n", 
						i+1, algorithms.get(i).getAlgorithmName(), algorithms.get(i).getTrainClassName(), algorithms.get(i).getClassifyClassName(), 
						algorithms.get(i).getParams()==null?0:algorithms.get(i).getParams().size());
			}
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("delete") && args.length==4)
    	{
    		String poolname = args[2];
    		String algorithmname = args[3];
    		if(deletealgo(info, poolname, algorithmname)) {
    			if(save(info))
    			{
					System.out.printf("algorithm명=[%s] deleted\n", algorithmname);
    				return;
    			}
    		}
    			
			System.out.printf("[%s] algorithm not found!!\n", algorithmname);			
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("setClassifyOutputMode") && args.length==4)
    	{
    		String algorithmname = args[2];
    		String mode = args[3];
    		
    		AlgorithmInfo ainfo = findalgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("알고리즘이 존재하지 않습니다.\n");
    			return;
    		}
    		
    		
    		if ( ! (AlgorithmInfo.CO_MODE_LABEL_WITH_INPUT.equals(mode)
    				|| AlgorithmInfo.CO_MODE_INDEX_WITHOUT_INPUT.equals(mode))){
    			System.out.printf("Fail : Wrong classify result mode!!! available mode: {%s, %s}\n", 
    					AlgorithmInfo.CO_MODE_LABEL_WITH_INPUT, 
    					AlgorithmInfo.CO_MODE_INDEX_WITHOUT_INPUT);
    			return;
    		}
    		
			ainfo.setClassifyOutputMode(mode);
    		if (save(info)){
    			System.out.printf("Set algorithm classify output mode %s %s\n", algorithmname, mode);
    		}else System.out.println("Fail ....");
    	}
    	// opt mgr
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("infSet") && args.length==7)
    	{
    		String path = args[2];
    		String delimiter = args[3];
    		String indexListString = args[4];
    		ArrayList<String> indexList = new ArrayList<String>();
    		for (String label : indexListString.split(",")){
    			indexList.add(label);
    		}
    		int classIndex =  Integer.parseInt(args[5]);
    		String classLabelListString =  args[6];
    		ArrayList<String> classLabelList = new ArrayList<String>();
    		for (String label : classLabelListString.split(",")){
    			classLabelList.add(label);
    		}
    		DataInfo dataInfo = new DataInfo(path, delimiter, indexList, classIndex, classLabelList);
    		
    		OptimizerConfigInfo configInfo = info.getOptimizerConfigInfo();
    		configInfo.setInput(dataInfo);
    		
    		if(save(info)) System.out.printf("Set optimizer input data path=%s, delimiter=%s indexList=[%s] classIndex=%s, classLabelList=[%s]\n", path, delimiter, indexListString, classIndex, classLabelListString);
    		else System.out.printf("Add Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("algSet") && args.length==4)
    	{
    		String poolName = args[2];
    		String algorithmName = args[3];
    		
    		PoolInfo pinfo = findpool(info, poolName);
    		if(pinfo==null) {
    			System.out.printf("pool not found!\n");
    			return;
    		}
    		
    		
    		AlgorithmInfo algorithmInfo = findalgo(info, algorithmName);
    		
    		if (algorithmInfo == null){
    			System.out.println("알고리즘이 존재하지 않습니다.");
    			return;
    		}
    		
    		OptimizerConfigInfo configInfo = info.getOptimizerConfigInfo();
    		configInfo.setPoolName(poolName);
    		configInfo.setAlgorithmName(algorithmName);
    		
    		if(save(info)) System.out.printf("Set optimizer algorithm %s %s\n", poolName,algorithmName);
    		else System.out.printf("Add Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("genSet") && args.length==4)
    	{
    		String paramName = args[2];
    		String paramValue = args[3];
    		
    		OptimizerConfigInfo configInfo = info.getOptimizerConfigInfo();
    		if ("mp".equals(paramName)){
    			configInfo.setMutationProbabilty(Float.parseFloat(paramValue));;
    		}else if ("cp".equals(paramName)){
    			configInfo.setCrossProbabilty(Float.parseFloat(paramValue));
    		}else if ("ps".equals(paramName)){
    			configInfo.setPopSize(Integer.parseInt(paramValue));
    		}else if ("rs".equals(paramName)){
    			configInfo.setRandomSeed(Integer.parseInt(paramValue));
    		}else if ("tc_maxg".equals(paramName)){
    			configInfo.setMaxGeneration(Integer.parseInt(paramValue));
    		}else if ("tc_minf".equals(paramName)){
    			configInfo.setMinFitness(Float.parseFloat(paramValue));
    		}else if ("bs".equals(paramName)){
    			configInfo.setBinaryStringSize(Integer.parseInt(paramValue));
    		}
    		
    		if(save(info)) System.out.printf("Set optimizer parameter[%s]=%s\n", paramName, paramValue);
    		else System.out.printf("Add Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("genSetList") && args.length==2)
    	{
    		OptimizerConfigInfo configInfo = info.getOptimizerConfigInfo();
			System.out.printf("Optimizer Parameter List\n========================\n");
			System.out.println("mp : "+ configInfo.getMutationProbabilty());
			System.out.println("cp : "+ configInfo.getCrossProbabilty());
			System.out.println("ps : "+ configInfo.getPopSize());
			System.out.println("rs : "+ configInfo.getRandomSeed());
			System.out.println("tc_maxg : "+ configInfo.getMaxGeneration());
			System.out.println("tc_minf : "+ configInfo.getMinFitness());
			
    	}
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("algIniPara") && args.length==3)
    	{
    		String paramValue = args[2];
    		
    		OptimizerConfigInfo configInfo = info.getOptimizerConfigInfo();
    		if ("user".equals(paramValue)){
    			configInfo.setUserAlgorithmParamMode(true);
    		}else if ("random".equals(paramValue)){
    			configInfo.setUserAlgorithmParamMode(false);
    		}else{
    			System.out.printf("설정 실패: user 혹은 random으로 설정할 수 있습니다..\n");
    			return;
    		}
    		    		
    		if(save(info)) System.out.printf("Set algorithm paramter setting mode = %s\n", paramValue);
    		else System.out.printf("Set Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("algParaSet") && args.length==5)
    	{
    		String algorithmName = args[2];
    		String paramName = args[3];
    		String paramValue = args[4];
    		
    		AlgorithmInfo ainfo = findalgo(info, algorithmName); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("알고리즘이 존재하지 않습니다.\n");
    			return;
    		}
    		
    		ParamInfo paramInfo = null;
    		for (ParamInfo tmp : ainfo.getParams()){
    			if (paramName.equals(tmp.getParamName())){
    				paramInfo = tmp;
    				break;
    			}
    		}
    		if (paramInfo == null){
    			System.out.printf("알고리즘에 관련 파라메터가 정의되어있지 않습니다.\n");
    			return;
    		}
    		
    		List<ParamInfo> userAlgorithmParms =
    				info.getOptimizerConfigInfo().getUserAlgorithmParams();
    		boolean hasParam = false;
    		for (ParamInfo tmp : userAlgorithmParms){
    			if (paramName.equals(tmp.getParamName())){
    				tmp.setMax(null);
    				tmp.setMin(null);
    				tmp.setParamValue(paramValue);
    				hasParam = true;
    				break;
    			}
    		}
    		if (!hasParam){
				ParamInfo paramInfo2 = new ParamInfo();
				paramInfo2.setParamName(paramName);
				paramInfo2.setParamType(paramInfo.getParamType());
				paramInfo2.setDataType(paramInfo.getDataType());
				paramInfo2.setParamValue(paramValue);
				paramInfo2.setMax(null);
				paramInfo2.setMin(null);
				userAlgorithmParms.add(paramInfo2);
    		}

    		if(save(info)) System.out.printf("Add user algorithm parameter values parameter[%s]=%s\n", paramName, paramValue);
    		else System.out.printf("Add Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("algParaDel") && args.length==4)
    	{
    		String algorithmName = args[2];
    		String paramName = args[3];
    		
    		AlgorithmInfo ainfo = findalgo(info, algorithmName); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("알고리즘이 존재하지 않습니다.\n");
    			return;
    		}
    		
    		List<ParamInfo> userAlgorithmParms =
    				info.getOptimizerConfigInfo().getUserAlgorithmParams();
    		int idx = 0;
    		for (ParamInfo tmp : userAlgorithmParms){
    			if (paramName.equals(tmp.getParamName())){
    				userAlgorithmParms.remove(idx);
    				break;
    			}
    			idx++;
    		}

    		if(save(info)) System.out.printf("Delete user algorithm parameter values parameter[%s]\n", paramName);
    		else System.out.printf("Add Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("algParaList") && args.length==3)
    	{
    		String algorithmname = args[2];
			System.out.printf("%s algorithm param List\n========================\n", algorithmname);

    		AlgorithmInfo ainfo = findalgo(info, algorithmname);
    		if(ainfo==null) {
    			System.out.printf("algorithmName not found!\n");
    			return;
    		}
    		
    		//	사용자 파라메터 입력모드를 위한 대체 파라메터 map 설정
    		Map<String, ParamInfo> algParamMap = new HashMap<String, ParamInfo>();
    		if (info.getOptimizerConfigInfo().isUserAlgorithmParamMode()){
        		for (ParamInfo paramInfo : info.getOptimizerConfigInfo().getUserAlgorithmParams()){
        			algParamMap.put(paramInfo.getParamName(), paramInfo);
        		}
    		}

    		List<ParamInfo> params = ainfo.getParams();
    		int idx=0;
    		for (ParamInfo paramInfo : params){
    			
    			if (algParamMap.containsKey(paramInfo.getParamName()))
    				paramInfo = algParamMap.get(paramInfo.getParamName());
    			
    			if (ParamInfo.PARAM_TYPE_INPUT.equals(paramInfo.getParamType()) 
    					|| ParamInfo.PARAM_TYPE_DELIMITER.equals(paramInfo.getParamType())
    					|| ParamInfo.PARAM_TYPE_OUTPUT.equals(paramInfo.getParamType()) 
    					|| ParamInfo.PARAM_TYPE_INDEX_LIST.equals(paramInfo.getParamType()) 
    					|| ParamInfo.PARAM_TYPE_NUMERIC_INDEX_LIST.equals(paramInfo.getParamType()) 
    					|| ParamInfo.PARAM_TYPE_NOMINAL_INDEX_LIST.equals(paramInfo.getParamType()) 
    					|| ParamInfo.PARAM_TYPE_CLASS_INDEX.equals(paramInfo.getParamType())
    					|| ParamInfo.PARAM_TYPE_MODEL_PATH.equals(paramInfo.getParamType())
    					){
    				System.out.printf("param-%d. paramType=[%s], paramName=[%s], paramValue=%s\n"
    						,idx, paramInfo.getParamType(), paramInfo.getParamName(), paramInfo.getParamValue());
    			}else if (ParamInfo.PARAM_TYPE_OPTIMIZE.equals(paramInfo.getParamType())){
    				if (paramInfo.getParamValue() != null){
        				System.out.printf("param-%d. paramType=[%s], paramName=[%s], dataType=[%s], paramValue=%s\n"
        						,idx, paramInfo.getParamType(), paramInfo.getParamName(), paramInfo.getDataType(), paramInfo.getParamValue());
    				}else{
        				System.out.printf("param-%d. paramType=[%s], paramName=[%s], dataType=[%s], min=%s, max=%s\n"
        						,idx, paramInfo.getParamType(), paramInfo.getParamName(), paramInfo.getDataType(), paramInfo.getMin(), paramInfo.getMax());
    				}
    			}else if (ParamInfo.PARAM_TYPE_TRAIN_ETC.equals(paramInfo.getParamType())
    					|| ParamInfo.PARAM_TYPE_CLASSIFY_ETC.equals(paramInfo.getParamType())){
    				System.out.printf("param-%d. paramType=[%s], paramName=[%s], paramValue=%s\n"
    						,idx, paramInfo.getParamType(), paramInfo.getParamName(), paramInfo.getParamValue());
    			}
    			idx++;
    			
    		}
    		
    	}
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("run") && args.length==2)
    	{
    		System.out.println("Optimizer Start!!!!");
    		
    		GAMain.optimize(info);
    		
    		
    		System.out.println("Optimizer end!!!!");
    	}
//    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("add") && args.length==6) // nominal
//    	{
//    		String algorithmname = args[2];
//    		String paramname = args[3];
//    		String type =  args[4];
//    		String defaultvalue =  args[5];
//    		String values =  args[6];
//    		
//    		AlgorithmInfo ainfo = findalgo(info, algorithmname);
//    		if(ainfo==null) {
//    			System.out.printf("algorithmname not found!\n");
//    			return;
//    		}
//    		
//    		if(ainfo.params ==null) ainfo.params = new ArrayList<ParamInfo>();  
//    		
//    		ParamInfo pi = new ParamInfo(paramname, type, defaultvalue, values);
//    		ainfo.params.add(pi);
//    		
//    		if(save(info)) System.out.printf("Add parameter[%s] name=%s, type=%s default=%s, values=[%s]\n", algorithmname, paramname, type, defaultvalue, values);
//    		else System.out.printf("Add Fail..\n");
//    	}
//    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("add") && args.length==7) // numeric
//    	{
//    		String algorithmname = args[2];
//    		String paramname = args[3];
//    		String type =  args[4];
//    		String defaultvalue =  args[5];
//    		String min =  args[6];
//    		String max =  args[7];
//    		
//    		AlgorithmInfo ainfo = findalgo(info, algorithmname);
//    		if(ainfo==null) {
//    			System.out.printf("algorithmname not found!\n");
//    			return;
//    		}
//    		
//    		if(ainfo.params ==null) ainfo.params = new ArrayList<ParamInfo>();  
//    		
//    		ParamInfo pi = new ParamInfo(paramname, type, defaultvalue, min, max);
//    		ainfo.params.add(pi);
//    		
//    		if(save(info)) System.out.printf("Add parameter[%s] name=%s, type=%s default=%s, min=%s, max=%s\n", algorithmname, paramname, type, defaultvalue, min, max);
//    		else System.out.printf("Add Fail..\n");
//    	}
//    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("list") && args.length==3)
//    	{
//    		String algorithmname = args[2];
//			System.out.printf("%s algorithm param List\n========================\n", algorithmname);
//
//    		AlgorithmInfo ainfo = findalgo(info, algorithmname);
//    		if(ainfo==null) {
//    			System.out.printf("algorithmname not found!\n");
//    			return;
//    		}
//
//			if(ainfo.params!=null) for(int i = 0; i<ainfo.params.size(); i++)
//			{
//				String attrs =  String.format("min=%s, max=%s", ainfo.params.get(i).min, ainfo.params.get(i).max);
//				if(ainfo.params.get(i).type.equalsIgnoreCase("nominal")) attrs =  String.format("values=[%s]", ainfo.params.get(i).values);
//				System.out.printf("param%d. paramname=[%s], type=[%s], default=%s, %s\n", i+1, ainfo.params.get(i).paramName, ainfo.params.get(i).type, ainfo.params.get(i).defaultValue, attrs);
//			}
//    	}
//    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("delete") && args.length==4)
//    	{
//    		String algorithmname = args[2];
//    		String paramname = args[3];
//    		
//    		AlgorithmInfo ainfo = findalgo(info, algorithmname);
//    		if(ainfo==null) {
//    			System.out.printf("algorithmname not found!\n");
//    			return;
//    		}
//    		
//    		if(ainfo.params!=null) for(int i = 0; i<ainfo.params.size(); i++)
//    		{
//    			if (ainfo.params.get(i).paramName.equals(paramname))
//    			{
//    				ainfo.params.remove(i);
//        			if(save(info))
//        			{
//    					System.out.printf("algorithm명=[%s], param명=[%s] deleted\n", algorithmname, paramname);
//        				return;
//        			}
//    				
//    			}
//    			
//    		}
//    			
//			System.out.printf("[%s] paramname not found!!\n", paramname);			
//    	}
    	else
			System.out.printf("파라미터가 올바르지 않습니다. 확인바랍니다.!!\n");
    		
    	
    }
}
