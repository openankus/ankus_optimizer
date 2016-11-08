package org.ankus.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.ankus.optimizer.GAMain;
import org.ankus.poolmgr.AlgorithmInfo;
import org.ankus.poolmgr.OptimizerConfigInfo;
import org.ankus.poolmgr.ManagerData;
import org.ankus.poolmgr.ParamInfo;
import org.ankus.poolmgr.PoolInfo;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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
	
	/**
	 * 관리자 데이터 파일의 경로
	 */
	public static String datafile = "mgr.json";
	
	/**
	 * 관리자 데이터를 파일 저장하기 위한 mapper 객체
	 */
	public static ObjectMapper mapper = new ObjectMapper();

	/**
	 * 관리자 데이터 로드
	 * 
	 * @return 관리자 데이터 객체
	 */
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
	
	/**
	 * 관리자 데이터 객체를 파일로 저장
	 * 
	 * @param data 관리자 데이터 객체
	 * @return
	 */
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
	
	/**
	 * 관리자 데이터 객체로 부터 Pool 정보를 검색
	 * 
	 * @param data	관리자 데이터 객체
	 * @param poolname 검색하려는 pool 명
	 * @return
	 */
	public static PoolInfo findPool(ManagerData data, String poolname)
	{
		if(data==null || data.pools==null) return null;
		
		for(int i =0; i<data.pools.size(); i++) if(data.pools.get(i).getPoolName().equals(poolname)) return data.pools.get(i);

		return null;
	}

	/**
	 * 관리자 데이터 객체로부터 Pool 정보 삭제
	 * 
	 * @param data 관리자 데이터 객체
	 * @param poolname pool 명
	 * @return
	 */
	public static boolean deletePool(ManagerData data, String poolname)
	{
		if(data==null || data.pools==null) return false;
		
		for(int i =0; i<data.pools.size(); i++) if(data.pools.get(i).getPoolName().equals(poolname)) {
			
			data.pools.remove(i);
			return true;
		}

		return false;
	}

	/**
	 * 관리자 데이터 객체로부터 알고리즘 검색
	 * 
	 * @param data	관리자 데이터 객체
	 * @param algoname 알고리즘 명
	 * @return
	 */
	public static AlgorithmInfo findAlgo(ManagerData data, String algoname)
	{
		if(data==null || data.pools==null) return null;
		
		for(int i=0; i<data.pools.size(); i++) {
			
			for(int j=0; j<data.pools.get(i).getAlgorithms().size(); j++)
				if(data.pools.get(i).getAlgorithms().get(j).getAlgorithmName().equals(algoname)) return data.pools.get(i).getAlgorithms().get(j);
		}

		return null;
	}

	/**
	 * 관리자 데이터 객체로부터 알고리즘 삭제
	 * 
	 * @param data	관리자 데이터 객체
	 * @param poolname 삭제하려는 알고리즘이 포함되는 pool 명
	 * @param algoname 삭제라혀는 알고리즘의 이름
	 * @return
	 */
	public static boolean deleteAlgo(ManagerData data, String poolname, String algoname)
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
        			System.out.printf("A pool with the same name exists!\n");
    				System.exit(1);
    			}
    		}
    		
    		PoolInfo pi = new PoolInfo(poolname, poolfile);
    		info.pools.add(pi);
    		
    		if(save(info)) System.out.printf("Add pool poolName=[%s], jarFile=[%s]\n", poolname, poolfile);
    		else System.out.printf("Fail to add...\n");
    	}
    	else if(mgr.equalsIgnoreCase("poolmgr") && cmd.equalsIgnoreCase("list"))
    	{
			System.out.printf("<Pool List>========================\n");

			if(info==null) {
				System.out.printf("none...\n");
				return;
			}

			for(int i = 0; i<info.pools.size(); i++)
			{
				System.out.printf("pool%d. poolName=[%s], poolFile=[%s], algorithmCount=[%d]\n", i+1, info.pools.get(i).getPoolName(), info.pools.get(i).getFileName(), info.pools.get(i).getAlgorithms().size());
			}
    	}
    	else if(mgr.equalsIgnoreCase("poolmgr") && cmd.equalsIgnoreCase("delete") && args.length==3)
    	{
    		String poolname = args[2];
			for(int i = 0; i<info.pools.size(); i++)
			{
				if(info.pools.get(i).getPoolName().equals(poolname))
				{
					info.pools.remove(i);
					if(save(info))
					{
						System.out.printf("poolName=[%s] has been deleted\n", info.pools.get(i).getPoolName());
						return;
					}else{
						System.out.println("Fail to delete...");
					}
				}
			}
			System.out.printf("pool not found!!\n");			
    	}
    	// algo mgr
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("add") && args.length==4)
    	{
    		String poolname = args[2];
    		String algorithmname = args[3];
    		
    		PoolInfo pinfo = findPool(info, poolname);
    		if(pinfo==null) {
    			System.out.printf("poolName(="+poolname+") does not exit!\n");
    			return;
    		}
    		
    		AlgorithmInfo ainfo = findAlgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo!=null)
    		{
    			System.out.printf("The algorihm name(="+algorithmname+") already exists.\n");
    			return;
    		}
    		
    		AlgorithmInfo ai = new AlgorithmInfo(algorithmname);
    		pinfo.getAlgorithms().add(ai);
    		
    		if(save(info)) System.out.printf("Add algorithm %s %s\n", poolname, algorithmname);
    		else System.out.printf("Fail to add...\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("setParamFormat") && args.length==4)
    	{
    		String algorithmName = args[2];
    		String paramFormat = args[3];
    		
    		AlgorithmInfo ainfo = findAlgo(info, algorithmName); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("The algorithm(="+algorithmName+") does not exist.\n");
    			return;
    		}
    		
    		ainfo.setParamFormat(paramFormat);
    		
    		if(save(info)) System.out.printf("Set algorithm parameter format (algorithm=[%s], paramFormat=[%s]) \n", algorithmName, paramFormat);
    		else System.out.printf("Fail to set ...\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("setTrainClassName") && args.length==4)
    	{
    		String algorithmName = args[2];
    		String className = args[3];
    		
    		AlgorithmInfo ainfo = findAlgo(info, algorithmName); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("The algorithm(="+algorithmName+") does not exist.\n");
    			return;
    		}
    		
    		ainfo.setTrainClassName(className);
    		
    		if(save(info)) System.out.printf("Set algorithm train class name format (algorithm=[%s], className=[%s]) \n", algorithmName, className);
    		else System.out.printf("Fail to set ...\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("setClassifyClassName") && args.length==4)
    	{
    		String algorithmName = args[2];
    		String className = args[3];
    		
    		AlgorithmInfo ainfo = findAlgo(info, algorithmName); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("The algorithm(="+algorithmName+") does not exist.\n");
    			return;
    		}
    		
    		ainfo.setClassifyClassName(className);
    		
    		if(save(info)) System.out.printf("Set algorithm classification class name format (algorithm=[%s], className=[%s]) \n", algorithmName, className);
    		else System.out.printf("Fail to set...\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") 
    			&& cmd.equalsIgnoreCase("setParam") 
    			&& args.length==5
    			&& (ParamInfo.PARAM_TYPE_INPUT.equals(args[3]) 
    					|| ParamInfo.PARAM_TYPE_INPUT_TEST.equals(args[3])
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
    		
    		AlgorithmInfo ainfo = findAlgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("The algorithm(="+algorithmname+") does not exist.\n");
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
    		
    		if(save(info)) System.out.printf("Set algorithm parameter (algorithm=[%s], classType=[%s], paramName=[%s])\n", algorithmname, paramType, paramName);
    		else System.out.printf("Fail to set...\n");
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
    		
    		AlgorithmInfo ainfo = findAlgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("The algorithm(="+algorithmname+") does not exist.\n");
    			return;
    		}
    		
    		if (!ParamInfo.DATA_TYPE_INT.equals(dataType) && !ParamInfo.DATA_TYPE_FLOAT.equals(dataType)){
    			System.out.printf("The data type(= "+dataType+") dones not exist.\n");
    			return;
    		}
    		
			String min = args[6];
			String max = args[7];
    		if (ParamInfo.DATA_TYPE_INT.equals(dataType)){
    			try{
    				Integer.parseInt(min);
    				Integer.parseInt(max);
    			}catch(NumberFormatException ex){
    				System.out.println("Please input correct min and max values in the data type format.");
    				return;
    				
    			}
    		}else if (ParamInfo.DATA_TYPE_FLOAT.equals(dataType)){
    			try{
    				Float.parseFloat(min);
    				Float.parseFloat(max);
    			}catch(NumberFormatException ex){
    				System.out.println("Please input correct min and max values in the data type format.");
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
    		
    		if(save(info)) System.out.printf("Set algorithm parameter (algorithm=[%s], paramType=[%s], paramName=[%s], dataType=[%s], min=[%s], max=[%s])\n", algorithmname, paramType, paramName, dataType, min, max);
    		else System.out.printf("Fail to set...\n");
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
    		
    		AlgorithmInfo ainfo = findAlgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("The algorithm(="+algorithmname+") does not exist.\n");
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
    		
    		if(save(info)) System.out.printf("Set algorithm parameter (algorithm=[%s], paramType=[%s], paramName=[%s], paramValue=[%s])\n", algorithmname, paramType, paramName, paramValue);
    		else System.out.printf("Fail to set...\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("delParam") && args.length==4)
    	{
    		String algorithmname = args[2];
    		String paramName = args[3];
    		
    		AlgorithmInfo ainfo = findAlgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("The algorithm(="+algorithmname+") does not exist.\n");
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
    		
    		if(save(info)) System.out.printf("Delete algorithm parameter (algorithm=[%s], paramName=[%s])\n", algorithmname, paramName);
    		else System.out.printf("Fail to delete...\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("setClassifyOutputRelPath") && args.length==4)
    	{
    		String algorithmname = args[2];
    		String classifyOutputRelPath = args[3];
    		
    		AlgorithmInfo ainfo = findAlgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("The algorithm(="+algorithmname+") does not exist.\n");
    			return;
    		}

    		ainfo.setClassifyOutputRelPath(classifyOutputRelPath);
    		
    		if(save(info)) System.out.printf("Set algorithm classification output relative path (algorithm=[%s], classifyOutputRelPath=[%s])\n", algorithmname, classifyOutputRelPath);
    		else System.out.printf("Fail to set...\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("delClassifyOutputRelPath") && args.length==3)
    	{
    		String algorithmname = args[2];
    		
    		AlgorithmInfo ainfo = findAlgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("The algorithm(="+algorithmname+") does not exist.\n");
    			return;
    		}

    		ainfo.setClassifyOutputRelPath(null);
    		
    		if(save(info)) System.out.printf("Delete algorithm classification output relative path (algorithm=[%s])\n", algorithmname);
    		else System.out.printf("Fail to delete...\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("setModelAbsPath") && args.length==4)
    	{
    		String algorithmname = args[2];
    		String modelPath = args[3];
    		
    		AlgorithmInfo ainfo = findAlgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("The algorithm(="+algorithmname+") does not exist.\n");
    			return;
    		}
    		
    		ainfo.setModelAbsPath(modelPath);
    		
    		if(save(info)) System.out.printf("Set algorithm model absolute path (algorithm=[%s], modelAbsPath=[%s])\n", algorithmname, modelPath);
    		else System.out.printf("Fail to set...\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("delModelAbsPath") && args.length==3)
    	{
    		String algorithmname = args[2];
    		
    		AlgorithmInfo ainfo = findAlgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("The algorithm(="+algorithmname+") does not exist.\n");
    			return;
    		}

    		ainfo.setModelAbsPath(null);
    		
    		if(save(info)) System.out.printf("Delete algorithm model absolute path (algorithm=[%s])\n", algorithmname);
    		else System.out.printf("Delete Fail..\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("setModelRelPath") && args.length==4)
    	{
    		String algorithmname = args[2];
    		String modelPath = args[3];
    		
    		AlgorithmInfo ainfo = findAlgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("The algorithm(="+algorithmname+") does not exist.\n");
    			return;
    		}
    		
    		ainfo.setModelRelPath(modelPath);
    		
    		if(save(info)) System.out.printf("Set algorithm model relative path (algorithm=[%s], modelRelPath=[%s])\n", algorithmname, modelPath);
    		else System.out.printf("Fail to set...\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("delModelRelPath") && args.length==3)
    	{
    		String algorithmname = args[2];
    		
    		AlgorithmInfo ainfo = findAlgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("The algorithm(="+algorithmname+") does not exist.\n");
    			return;
    		}

    		ainfo.setModelRelPath(null);
    		
    		if(save(info)) System.out.printf("Delete algorithm model relative path (algorithm=[%s])\n", algorithmname);
    		else System.out.printf("Fail to delete...\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("setParamValueDelimiter") && args.length==4)
    	{
    		String algorithmname = args[2];
    		String paramValueDelimiter = args[3];
    		
    		AlgorithmInfo ainfo = findAlgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("The algorithm(="+algorithmname+") does not exist.\n");
    			return;
    		}

			ainfo.setParamValueDelimiter(paramValueDelimiter);
    		
    		if(save(info)) System.out.printf("Set algorithm parameter value delimiter (algorithm=[%s], paramValueDelimiter=[%s])\n", algorithmname, paramValueDelimiter);
    		else System.out.printf("Fail to est...\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("delParamValueDelimiter") && args.length==3)
    	{
    		String algorithmname = args[2];
    		
    		AlgorithmInfo ainfo = findAlgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("The algorithm(="+algorithmname+") does not exist.\n");
    			return;
    		}

			ainfo.setParamValueDelimiter(null);
    		
    		if(save(info)) System.out.printf("Delete algorithm parameter value delimiter (algorithm=[%s]) \n", algorithmname);
    		else System.out.printf("Fail to delete...\n");
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("list") && args.length==3)
    	{
    		String poolname = args[2];
			System.out.printf("<Pool(= %s) algorithm List>\n========================\n", poolname);

    		PoolInfo pinfo = findPool(info, poolname);
    		if(pinfo==null) {
    			System.out.printf("The pool (= "+poolname+") does not exist!\n");
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
    		if(deleteAlgo(info, poolname, algorithmname)) {
    			if(save(info))
    			{
					System.out.printf("algorithmName=[%s] deleted\n", algorithmname);
    				return;
    			}
    		}
    			
			System.out.printf("The algorithm(= %s) does not exist!\n", algorithmname);			
    	}
    	else if(mgr.equalsIgnoreCase("algomgr") && cmd.equalsIgnoreCase("setClassifyOutputMode") && args.length==4)
    	{
    		String algorithmname = args[2];
    		String mode = args[3];
    		
    		AlgorithmInfo ainfo = findAlgo(info, algorithmname); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("The algorithm(= "+algorithmname+") does not exist.\n");
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
    			System.out.printf("Set algorithm classify output mode (algorithm=[%s], classifyOutputMode=[%s])\n", algorithmname, mode);
    		}else System.out.println("Fail to set ....");
    	}
    	// opt mgr
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("dataList") && args.length == 2)
    	{
    		
    		OptimizerConfigInfo configInfo = info.getOptimizerConfigInfo();
    		String dataFile = configInfo.getDataFilePath();
    		if (dataFile == null || "".equals(dataFile.trim())){
    			System.out.println("Please set an input data file.");
    			System.exit(1);
    		}

    		FileSystem fs = FileSystem.get(new Configuration());
    		Path dataFilePath = new Path(dataFile);
    		
    		if (!fs.exists(dataFilePath)){
    			System.out.println("The input data file does not exist on HDFS.\n(Path = "+dataFile+")");
    			System.exit(1);
    		}
    		
    		long sizeFileKb = fs.getFileStatus(dataFilePath).getLen()/1024L;
    		
    		FSDataInputStream is = fs.open(dataFilePath);
    		InputStreamReader ir = new InputStreamReader(is, "UTF-8");
    		BufferedReader br = new BufferedReader(ir);
    		int cntRow = 0;
    		StringBuffer sb = new StringBuffer();
    		String rl = null;
    		int cntAttr = 0;
    		while ((rl = br.readLine()) != null && cntRow < 10){
    			sb.append(rl);
    			sb.append("\n");

    			if (cntRow == 0){
    	    		StringTokenizer tokenizer = new StringTokenizer(rl, configInfo.getDelimiter());
    	    		while (tokenizer.hasMoreTokens()){
    	    			tokenizer.nextToken();
    	    			cntAttr++;
    	    		}
    			}
    			cntRow++;
    		}
    		
    		System.out.println("=========================================================");
    		System.out.println("Data file path = "+dataFile);
    		System.out.println("File size(KB) = "+sizeFileKb);
    		System.out.println("Number of Attributes = "+cntAttr);
    		System.out.println("------(First 10 records, after below)--------------------");
    		System.out.println(sb.toString());
    		System.out.println("=========================================================");
    	}
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("setMultiThreadEval") && args.length==3)
    	{
    		String multiThreadEval = args[2];
    		
    		boolean flag = false;
    		if ("true".equals(multiThreadEval)){
    			flag = true;
    		}else if ("false".equals(multiThreadEval)){
    			flag = false;
    		}else{
    			System.out.println("Fail wrong parameter value");
    			return;
    		}
    		
    		OptimizerConfigInfo configInfo = info.getOptimizerConfigInfo();
    		configInfo.setMultiThreadEval(flag);
    		
    		if(save(info)) System.out.printf("Set optimizer multi thread evaluation %s\n", multiThreadEval);
    		else System.out.printf("Fail to set...\n");
    	}
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("infSet") && args.length==4)
    	{
    		String dataFilePath = args[2];
    		String delimiter = args[3];
    		
    		OptimizerConfigInfo configInfo = info.getOptimizerConfigInfo();
    		configInfo.setDataFilePath(dataFilePath);
    		configInfo.setDelimiter(delimiter);
    		
    		if(save(info)) System.out.printf("Set optimizer input data data file path=%s, delimiter=%s\n", dataFilePath, delimiter);
    		else System.out.printf("Fail to set...\n");
    	}
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("infNumericIndexList") && args.length==3)
    	{
    		String indexListString = args[2];
    		ArrayList<String> indexList = new ArrayList<String>();
    		for (String label : indexListString.split(",")){
    			indexList.add(label);
    		}
    		
    		OptimizerConfigInfo configInfo = info.getOptimizerConfigInfo();
    		configInfo.setNumericIndexList(indexList);
    		
    		if(save(info)) System.out.printf("Set optimizer numericIndexList=[%s]\n", indexListString);
    		else System.out.printf("Fail to set...\n");
    	}
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("infNominalIndexList") && args.length==3)
    	{
    		String indexListString = args[2];
    		ArrayList<String> indexList = new ArrayList<String>();
    		for (String label : indexListString.split(",")){
    			indexList.add(label);
    		}
    		
    		OptimizerConfigInfo configInfo = info.getOptimizerConfigInfo();
    		configInfo.setNominalIndexList(indexList);
    		
    		if(save(info)) System.out.printf("Set optimizer nominalIndexList=[%s]\n", indexListString);
    		else System.out.printf("Fail to set...\n");
    	}
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("infClass") && args.length==4)
    	{
    		int classIndex =  Integer.parseInt(args[2]);
    		String classLabelListString =  args[3];
    		ArrayList<String> classLabelList = new ArrayList<String>();
    		for (String label : classLabelListString.split(",")){
    			classLabelList.add(label);
    		}
    		
    		OptimizerConfigInfo configInfo = info.getOptimizerConfigInfo();
    		configInfo.setClassIndex(classIndex);
    		configInfo.setClassLabelList(classLabelList);
    		
    		if(save(info)) System.out.printf("Set optimizer classIndex=%s, classLabelList=[%s]\n", classIndex, classLabelListString);
    		else System.out.printf("Fail to set...\n");
    	}
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("algSet") && args.length==4)
    	{
    		String poolName = args[2];
    		String algorithmName = args[3];
    		
    		PoolInfo pinfo = findPool(info, poolName);
    		if(pinfo==null) {
    			System.out.printf("The pool(= "+poolName+") does not exist!\n");
    			return;
    		}
    		
    		
    		AlgorithmInfo algorithmInfo = findAlgo(info, algorithmName);
    		
    		if (algorithmInfo == null){
    			System.out.println("The alogirhm(= "+algorithmName+") does not exist.");
    			return;
    		}
    		
    		if (poolName.equals(info.getOptimizerConfigInfo().getPoolName()) 
    				&& algorithmName.equals(info.getOptimizerConfigInfo().getAlgorithmName())){
    			System.out.println("The algorithm(= "+algorithmName+") already has been set.");
    			return;
    		}
    		
    		OptimizerConfigInfo configInfo = info.getOptimizerConfigInfo();
    		configInfo.setPoolName(poolName);
    		configInfo.setAlgorithmName(algorithmName);
    		configInfo.getUserAlgorithmParams().clear();
    		
    		if(save(info)){
    			System.out.printf("Set optimizer algorithm %s %s\n", poolName,algorithmName);
    			
    			System.out.printf("<Current algorithm( = "+algorithmName+") parameter settings>--------------------------\n");
        		
        		//	사용자 파라메터 입력모드를 위한 대체 파라메터 map 설정
        		Map<String, ParamInfo> algParamMap = new HashMap<String, ParamInfo>();
        		if (info.getOptimizerConfigInfo().isUserAlgorithmParamMode()){
            		for (ParamInfo tmpParam : info.getOptimizerConfigInfo().getUserAlgorithmParams()){
            			algParamMap.put(tmpParam.getParamName(), tmpParam);
            		}
        		}

        		List<ParamInfo> params = algorithmInfo.getParams();
        		int idxParam=0;
        		for (ParamInfo tmpParam : params){
        			
        			if (algParamMap.containsKey(tmpParam.getParamName()))
        				tmpParam = algParamMap.get(tmpParam.getParamName());
        			
        			if (ParamInfo.PARAM_TYPE_INPUT.equals(tmpParam.getParamType()) 
        					|| ParamInfo.PARAM_TYPE_INPUT_TEST.equals(tmpParam.getParamType())
        					|| ParamInfo.PARAM_TYPE_DELIMITER.equals(tmpParam.getParamType())
        					|| ParamInfo.PARAM_TYPE_OUTPUT.equals(tmpParam.getParamType()) 
        					|| ParamInfo.PARAM_TYPE_INDEX_LIST.equals(tmpParam.getParamType()) 
        					|| ParamInfo.PARAM_TYPE_NUMERIC_INDEX_LIST.equals(tmpParam.getParamType()) 
        					|| ParamInfo.PARAM_TYPE_NOMINAL_INDEX_LIST.equals(tmpParam.getParamType()) 
        					|| ParamInfo.PARAM_TYPE_CLASS_INDEX.equals(tmpParam.getParamType())
        					|| ParamInfo.PARAM_TYPE_MODEL_PATH.equals(tmpParam.getParamType())
        					){
        				System.out.printf("param-%d. paramType=[%s], paramName=[%s]\n"
        						,idxParam, tmpParam.getParamType(), tmpParam.getParamName());
        			}else if (ParamInfo.PARAM_TYPE_OPTIMIZE.equals(tmpParam.getParamType())){
        				if (tmpParam.getParamValue() != null){
            				System.out.printf("param-%d. paramType=[%s], paramName=[%s], dataType=[%s], paramValue=%s\n"
            						,idxParam, tmpParam.getParamType(), tmpParam.getParamName(), tmpParam.getDataType(), tmpParam.getParamValue());
        				}else{
            				System.out.printf("param-%d. paramType=[%s], paramName=[%s], dataType=[%s], min=%s, max=%s\n"
            						,idxParam, tmpParam.getParamType(), tmpParam.getParamName(), tmpParam.getDataType(), tmpParam.getMin(), tmpParam.getMax());
        				}
        			}else if (ParamInfo.PARAM_TYPE_TRAIN_ETC.equals(tmpParam.getParamType())
        					|| ParamInfo.PARAM_TYPE_CLASSIFY_ETC.equals(tmpParam.getParamType())){
        				System.out.printf("param-%d. paramType=[%s], paramName=[%s], paramValue=%s\n"
        						,idxParam, tmpParam.getParamType(), tmpParam.getParamName(), tmpParam.getParamValue());
        			}
        			idxParam++;
        			
        		}
        		
    			System.out.printf("--------------------------------------------------------------------------\n");    			
    		}
    		else System.out.printf("Fail to set...\n");
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
    		else System.out.printf("Fail to set...\n");
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
    			System.out.printf("Fail to set!!!: The available parameter values are only 'user' and 'random'.\n");
    			return;
    		}
    		    		
    		if(save(info)) System.out.printf("Set algorithm paramter setting mode = %s\n", paramValue);
    		else System.out.printf("Fail to set..\n");
    	}
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("algParaSet") && args.length==4)
    	{
    		String algorithmName = info.getOptimizerConfigInfo().getAlgorithmName();
    		String paramName = args[2];
    		String paramValue = args[3];
    		
    		AlgorithmInfo ainfo = findAlgo(info, algorithmName); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("The algorithm(="+algorithmName+") does not exist.\n");
    			return;
    		}
    		
    		ParamInfo paramInfo = null;
    		for (ParamInfo tmp : ainfo.getParams()){
    			if (paramName.equals(tmp.getParamName()) && ParamInfo.PARAM_TYPE_OPTIMIZE.equals(tmp.getParamType())){
    				paramInfo = tmp;
    				break;
    			}
    		}
    		if (paramInfo == null){
    			System.out.printf("Fail to set!!!: the parameter(= "+paramName+") is not for optimization.\n");
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

    		if(save(info)){
    			System.out.printf("Set user algorithm parameter values parameter[%s]=%s\n", paramName, paramValue);

    			System.out.printf("<Current algorithm( = "+algorithmName+") parameter settings>--------------------------\n");
    		
        		//	사용자 파라메터 입력모드를 위한 대체 파라메터 map 설정
        		Map<String, ParamInfo> algParamMap = new HashMap<String, ParamInfo>();
        		if (info.getOptimizerConfigInfo().isUserAlgorithmParamMode()){
            		for (ParamInfo tmpParam : info.getOptimizerConfigInfo().getUserAlgorithmParams()){
            			algParamMap.put(tmpParam.getParamName(), tmpParam);
            		}
        		}

        		List<ParamInfo> params = ainfo.getParams();
        		int idxParam=0;
        		for (ParamInfo tmpParam : params){
        			
        			if (algParamMap.containsKey(tmpParam.getParamName()))
        				tmpParam = algParamMap.get(tmpParam.getParamName());
        			
        			if (ParamInfo.PARAM_TYPE_INPUT.equals(tmpParam.getParamType()) 
        					|| ParamInfo.PARAM_TYPE_INPUT_TEST.equals(tmpParam.getParamType())
        					|| ParamInfo.PARAM_TYPE_DELIMITER.equals(tmpParam.getParamType())
        					|| ParamInfo.PARAM_TYPE_OUTPUT.equals(tmpParam.getParamType()) 
        					|| ParamInfo.PARAM_TYPE_INDEX_LIST.equals(tmpParam.getParamType()) 
        					|| ParamInfo.PARAM_TYPE_NUMERIC_INDEX_LIST.equals(tmpParam.getParamType()) 
        					|| ParamInfo.PARAM_TYPE_NOMINAL_INDEX_LIST.equals(tmpParam.getParamType()) 
        					|| ParamInfo.PARAM_TYPE_CLASS_INDEX.equals(tmpParam.getParamType())
        					|| ParamInfo.PARAM_TYPE_MODEL_PATH.equals(tmpParam.getParamType())
        					){
        				System.out.printf("param-%d. paramType=[%s], paramName=[%s]\n"
        						,idxParam, tmpParam.getParamType(), tmpParam.getParamName());
        			}else if (ParamInfo.PARAM_TYPE_OPTIMIZE.equals(tmpParam.getParamType())){
        				if (tmpParam.getParamValue() != null){
            				System.out.printf("param-%d. paramType=[%s], paramName=[%s], dataType=[%s], paramValue=%s\n"
            						,idxParam, tmpParam.getParamType(), tmpParam.getParamName(), tmpParam.getDataType(), tmpParam.getParamValue());
        				}else{
            				System.out.printf("param-%d. paramType=[%s], paramName=[%s], dataType=[%s], min=%s, max=%s\n"
            						,idxParam, tmpParam.getParamType(), tmpParam.getParamName(), tmpParam.getDataType(), tmpParam.getMin(), tmpParam.getMax());
        				}
        			}else if (ParamInfo.PARAM_TYPE_TRAIN_ETC.equals(tmpParam.getParamType())
        					|| ParamInfo.PARAM_TYPE_CLASSIFY_ETC.equals(tmpParam.getParamType())){
        				System.out.printf("param-%d. paramType=[%s], paramName=[%s], paramValue=%s\n"
        						,idxParam, tmpParam.getParamType(), tmpParam.getParamName(), tmpParam.getParamValue());
        			}
        			idxParam++;
        			
        		}
        		
    			System.out.printf("--------------------------------------------------------------------------\n");
    		}
    		else System.out.printf("Fail to set...\n");
    	}
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("algParaDel") && args.length==3)
    	{
    		String algorithmName = info.getOptimizerConfigInfo().getAlgorithmName();
    		String paramName = args[2];
    		
    		AlgorithmInfo ainfo = findAlgo(info, algorithmName); // 알고리즘명 중복 허용안함.
    		
    		if(ainfo==null)
    		{
    			System.out.printf("The algorithm(="+algorithmName+") does not exist.\n");
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

    		if(save(info)){
    			System.out.printf("Delete user algorithm parameter values parameter[%s]\n", paramName);

    		
    			System.out.printf("<Current algorithm( = "+algorithmName+") parameter settings>--------------------------\n");
        		
        		//	사용자 파라메터 입력모드를 위한 대체 파라메터 map 설정
        		Map<String, ParamInfo> algParamMap = new HashMap<String, ParamInfo>();
        		if (info.getOptimizerConfigInfo().isUserAlgorithmParamMode()){
            		for (ParamInfo tmpParam : info.getOptimizerConfigInfo().getUserAlgorithmParams()){
            			algParamMap.put(tmpParam.getParamName(), tmpParam);
            		}
        		}

        		List<ParamInfo> params = ainfo.getParams();
        		int idxParam=0;
        		for (ParamInfo tmpParam : params){
        			
        			if (algParamMap.containsKey(tmpParam.getParamName()))
        				tmpParam = algParamMap.get(tmpParam.getParamName());
        			
        			if (ParamInfo.PARAM_TYPE_INPUT.equals(tmpParam.getParamType()) 
        					|| ParamInfo.PARAM_TYPE_INPUT_TEST.equals(tmpParam.getParamType())
        					|| ParamInfo.PARAM_TYPE_DELIMITER.equals(tmpParam.getParamType())
        					|| ParamInfo.PARAM_TYPE_OUTPUT.equals(tmpParam.getParamType()) 
        					|| ParamInfo.PARAM_TYPE_INDEX_LIST.equals(tmpParam.getParamType()) 
        					|| ParamInfo.PARAM_TYPE_NUMERIC_INDEX_LIST.equals(tmpParam.getParamType()) 
        					|| ParamInfo.PARAM_TYPE_NOMINAL_INDEX_LIST.equals(tmpParam.getParamType()) 
        					|| ParamInfo.PARAM_TYPE_CLASS_INDEX.equals(tmpParam.getParamType())
        					|| ParamInfo.PARAM_TYPE_MODEL_PATH.equals(tmpParam.getParamType())
        					){
        				System.out.printf("param-%d. paramType=[%s], paramName=[%s]\n"
        						,idxParam, tmpParam.getParamType(), tmpParam.getParamName());
        			}else if (ParamInfo.PARAM_TYPE_OPTIMIZE.equals(tmpParam.getParamType())){
        				if (tmpParam.getParamValue() != null){
            				System.out.printf("param-%d. paramType=[%s], paramName=[%s], dataType=[%s], paramValue=%s\n"
            						,idxParam, tmpParam.getParamType(), tmpParam.getParamName(), tmpParam.getDataType(), tmpParam.getParamValue());
        				}else{
            				System.out.printf("param-%d. paramType=[%s], paramName=[%s], dataType=[%s], min=%s, max=%s\n"
            						,idxParam, tmpParam.getParamType(), tmpParam.getParamName(), tmpParam.getDataType(), tmpParam.getMin(), tmpParam.getMax());
        				}
        			}else if (ParamInfo.PARAM_TYPE_TRAIN_ETC.equals(tmpParam.getParamType())
        					|| ParamInfo.PARAM_TYPE_CLASSIFY_ETC.equals(tmpParam.getParamType())){
        				System.out.printf("param-%d. paramType=[%s], paramName=[%s], paramValue=%s\n"
        						,idxParam, tmpParam.getParamType(), tmpParam.getParamName(), tmpParam.getParamValue());
        			}
        			idxParam++;
        			
        		}
        		
    			System.out.printf("--------------------------------------------------------------------------\n");
    		}
    		else System.out.printf("Fail to delete...\n");
    	}
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("algParaList") && args.length==2)
    	{
    		String algorithmName = info.getOptimizerConfigInfo().getAlgorithmName();

    		AlgorithmInfo ainfo = findAlgo(info, algorithmName);
    		if(ainfo==null) {
    			System.out.printf("The algorithm(= "+algorithmName+") does not exist!\n");
    			return;
    		}
    		
			System.out.printf("<Current algorithm( = "+algorithmName+") parameter settings>--------------------------\n");
    		
    		//	사용자 파라메터 입력모드를 위한 대체 파라메터 map 설정
    		Map<String, ParamInfo> algParamMap = new HashMap<String, ParamInfo>();
    		if (info.getOptimizerConfigInfo().isUserAlgorithmParamMode()){
        		for (ParamInfo tmpParam : info.getOptimizerConfigInfo().getUserAlgorithmParams()){
        			algParamMap.put(tmpParam.getParamName(), tmpParam);
        		}
    		}

    		List<ParamInfo> params = ainfo.getParams();
    		int idxParam=0;
    		for (ParamInfo tmpParam : params){
    			
    			if (algParamMap.containsKey(tmpParam.getParamName()))
    				tmpParam = algParamMap.get(tmpParam.getParamName());
    			
    			if (ParamInfo.PARAM_TYPE_INPUT.equals(tmpParam.getParamType()) 
    					|| ParamInfo.PARAM_TYPE_INPUT_TEST.equals(tmpParam.getParamType())
    					|| ParamInfo.PARAM_TYPE_DELIMITER.equals(tmpParam.getParamType())
    					|| ParamInfo.PARAM_TYPE_OUTPUT.equals(tmpParam.getParamType()) 
    					|| ParamInfo.PARAM_TYPE_INDEX_LIST.equals(tmpParam.getParamType()) 
    					|| ParamInfo.PARAM_TYPE_NUMERIC_INDEX_LIST.equals(tmpParam.getParamType()) 
    					|| ParamInfo.PARAM_TYPE_NOMINAL_INDEX_LIST.equals(tmpParam.getParamType()) 
    					|| ParamInfo.PARAM_TYPE_CLASS_INDEX.equals(tmpParam.getParamType())
    					|| ParamInfo.PARAM_TYPE_MODEL_PATH.equals(tmpParam.getParamType())
    					){
    				System.out.printf("param-%d. paramType=[%s], paramName=[%s]\n"
    						,idxParam, tmpParam.getParamType(), tmpParam.getParamName());
    			}else if (ParamInfo.PARAM_TYPE_OPTIMIZE.equals(tmpParam.getParamType())){
    				if (tmpParam.getParamValue() != null){
        				System.out.printf("param-%d. paramType=[%s], paramName=[%s], dataType=[%s], paramValue=%s\n"
        						,idxParam, tmpParam.getParamType(), tmpParam.getParamName(), tmpParam.getDataType(), tmpParam.getParamValue());
    				}else{
        				System.out.printf("param-%d. paramType=[%s], paramName=[%s], dataType=[%s], min=%s, max=%s\n"
        						,idxParam, tmpParam.getParamType(), tmpParam.getParamName(), tmpParam.getDataType(), tmpParam.getMin(), tmpParam.getMax());
    				}
    			}else if (ParamInfo.PARAM_TYPE_TRAIN_ETC.equals(tmpParam.getParamType())
    					|| ParamInfo.PARAM_TYPE_CLASSIFY_ETC.equals(tmpParam.getParamType())){
    				System.out.printf("param-%d. paramType=[%s], paramName=[%s], paramValue=%s\n"
    						,idxParam, tmpParam.getParamType(), tmpParam.getParamName(), tmpParam.getParamValue());
    			}
    			idxParam++;
    			
    		}
    		
			System.out.printf("--------------------------------------------------------------------------\n");
    		
    	}
    	else if(mgr.equalsIgnoreCase("optmgr") && cmd.equalsIgnoreCase("run") && args.length==2)
    	{
    		
    		GAMain.optimize(info);
    		
    		
    	}
    	else{
			System.out.printf("Wrong parameters!!! Please refer to the below\n");
    		
			//	pool 관련 
    		System.out.printf("\t- poolmgr add <pool name> <jar path>\n");
//    		System.out.printf("\t- poolmgr delete <pool name>\n");
    		System.out.printf("\t- poolmgr list\n");
    		
    		// algorithm 관련
    		System.out.printf("\t- algomgr add <pool name> <algorithm name>\n");
//    		System.out.printf("\t- algomgr delete <pool name> <algorithm name>\n");
    		System.out.printf("\t- algomgr list <pool name>\n");
    		System.out.printf("\t- algomgr setParamFormat <algorithm name> <param format: ankus | mahoutRF>\n");
    		System.out.printf("\t- algomgr setTrainClassName <algorithm name> <class name>\n");
    		System.out.printf("\t- algomgr setClassifyClassName <algorithm name> <class name>\n");
    		System.out.printf("\t- algomgr setParam <algorithm name> input <parameter name>\n");
    		System.out.printf("\t- algomgr setParam <algorithm name> inputTest <parameter name>\n");
    		System.out.printf("\t- algomgr setParam <algorithm name> delimiter <parameter name>\n");
    		System.out.printf("\t- algomgr setParam <algorithm name> output <parameter name>\n");
    		System.out.printf("\t- algomgr setParam <algorithm name> indexList <parameter name>\n");
    		System.out.printf("\t- algomgr setParam <algorithm name> numericIndexList <parameter name>\n");
    		System.out.printf("\t- algomgr setParam <algorithm name> nominalIndexList <parameter name>\n");
    		System.out.printf("\t- algomgr setParam <algorithm name> classIndex <parameter name>\n");
    		System.out.printf("\t- algomgr setParam <algorithm name> modelPath <parameter name>\n");
    		System.out.printf("\t- algomgr setParam <algorithm name> optimize <parameter name> <data type> <min value> <max value>\n");
    		System.out.printf("\t- algomgr setParam <algorithm name> trainEtc <parameter name> [<parameter value>] | '']\n");
    		System.out.printf("\t- algomgr setParam <algorithm name> classifyEtc <parameter name> [<parameter value>] | '']\n");
    		System.out.printf("\t- algomgr delParam <algorithm name> <parameter name>\n");
    		System.out.printf("\t- algomgr setClassifyOutputRelPath <algorithm name> <relative path on each popolulation output directory>\n");
//    		System.out.printf("\t- algomgr delClassifyOutputRelPath <algorithm name>\n");
    		System.out.printf("\t- algomgr setParamValueDelimiter <algorithm name> <delimiter>\n");
//    		System.out.printf("\t- algomgr delParamValueDelimiter <algorithm name>\n");
    		System.out.printf("\t- algomgr setClassifyOutputMode <algorithm name> <mode : lableWithInput | indexWithoutInput>\n");
    		System.out.printf("\t- algomgr setModelAbsPath <algorithm name> <aboslute path to a trained model>\n");
    		System.out.printf("\t- algomgr delModelAbsPath <algorithm name>\n");
    		System.out.printf("\t- algomgr setModelRelPath <algorithm name> <relative path to a trained model on each popoluation output directory>\n");
    		System.out.printf("\t- algomgr delModelRelPath <algorithm name>\n");
    		
    		
    		//	optimizer 관련
    		System.out.printf("\t- optmgr infSet <input data file path> <delimiter>\n");
    		System.out.printf("\t- optmgr dataList \n");
    		System.out.printf("\t- optmgr infNumericIndexList <index list delimitted with comma(,)>\n");
    		System.out.printf("\t- optmgr infNominalIndexList <index list delimitted with comma(,)>\n");
    		System.out.printf("\t- optmgr infClass <class index> <class label list delimitted with comma(,)>\n");
    		System.out.printf("\t- optmgr setMultiThreadEval [true | false]\n");
    		System.out.printf("\t- optmgr algSet <algorithm name>\n");
    		System.out.printf("\t- optmgr algIniPara [random | user]\n");
    		System.out.printf("\t- optmgr algParaSet <parameter name> [<parameter value> | '']\n");
    		System.out.printf("\t- optmgr algParaDel <parameter name>\n");
    		System.out.printf("\t- optmgr algParaList \n");
    		System.out.printf("\t- optmgr genSet <parameter name : mp | cp | ps | rs | tc_maxg | tc_minf | bs> <parameter value> \n");
    		System.out.printf("\t- optmgr genSetList \n");
    		System.out.printf("\t- optmgr optmgr run \n");
    		
    		return;
    	}
    		
    	
    }
}
