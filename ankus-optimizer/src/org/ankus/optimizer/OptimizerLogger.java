package org.ankus.optimizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OptimizerLogger {
	
	/**
	 * 로그 파일 경로
	 */
	private static String logPath;
	
	/**
	 * 로그 출력 스트림
	 */
	private static PrintStream logOut = null;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("[yyyy-MM-dd, hh:mm:ss a]"); 
	

	//	로그파일에 대한 출력스트림 열기
	static {
		logPath = System.getenv("OPTMGR_OUTFLOG");
		if (logPath != null && !"".equals(logPath.trim())){
			//	로그 파일 환경변수 설정이 있을 경우, 로그파일 출력
			File logFile = new File(logPath);
			if (!logFile.getParentFile().exists()){
				boolean flag = logFile.getParentFile().mkdirs();
				if (!flag){
					System.out.println("Can not create the log file.(OPTMGR_OUTFLOG="+logPath+")");
					System.exit(1);
				}
			}
			try{
				if (!logFile.exists()){
					logFile.createNewFile();
				}
				logOut = new PrintStream(new FileOutputStream(logFile, true));
			}catch(IOException ex){
				System.err.println("Fail to set up the log file.");
				ex.printStackTrace();
				System.exit(1);
			}
		}
		System.out.println("로그파일경로: "+logPath);
	}
	
	/**
	 * 형식에 맞춘 문자열 출력
	 * 
	 * @param format	형식
	 * @param args	문자열에 대입될 값
	 */
	public static void printf(String format, Object... args){
		StringBuffer sb = new StringBuffer();
		sb.append(sdf.format(Calendar.getInstance().getTime()));
		sb.append(" ");
		sb.append(format);
		System.out.printf(sb.toString(), args);
		if (logOut != null)	logOut.printf(sb.toString(), args);
	}
	
	/**
	 * 문자열 출력후, 줄 바꿈 출력
	 * 
	 * @param message 출력할 문자열
	 */
	public static void println(String message){
		StringBuffer sb = new StringBuffer();
		sb.append(sdf.format(Calendar.getInstance().getTime()));
		sb.append(" ");
		sb.append(message);
		System.out.println(sb.toString());
		if (logOut != null)	logOut.println(sb.toString());
	}
	
	/**
	 * 문자열 출력
	 * 
	 * @param message 출력할 문자열
	 */
	public static void print(String message){
		StringBuffer sb = new StringBuffer();
		sb.append(sdf.format(Calendar.getInstance().getTime()));
		sb.append(" ");
		sb.append(message);
		System.out.print(sb.toString());
		if (logOut != null)	logOut.print(sb.toString());
	}
	

}
