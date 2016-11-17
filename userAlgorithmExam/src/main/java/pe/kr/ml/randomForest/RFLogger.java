package pe.kr.ml.randomForest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.google.common.io.Files;

public class RFLogger {
	
	/**
	 * 로그 파일 경로
	 */
	private static String logPath = "/userRF.log";
	
	/**
	 * 로그 출력 스트림
	 */
	private static PrintStream logOut = null;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("[yyyy-MM-dd, HH:mm:ss]"); 
	

	//	로그파일에 대한 출력스트림 열기
	static {
		try{
			FileSystem fs = FileSystem.get(new Configuration());
			Path tmpPath = new Path(logPath);
			if (!fs.exists(tmpPath)){
				fs.mkdirs(tmpPath.getParent());
			}else{
				fs.delete(tmpPath, true);
			}
			logOut = new PrintStream(fs.create(tmpPath));
		}catch(IOException ex){
			System.err.println("Fail to set up the log file.");
			ex.printStackTrace();
			System.exit(1);
		}
		
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
		if (logOut != null){
			logOut.printf(sb.toString(), args);
			logOut.flush();
		}
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
		if (logOut != null){
			logOut.println(sb.toString());
			logOut.flush();
		}
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
		if (logOut != null){
			logOut.print(sb.toString());
			logOut.flush();
		}
	}
	

}
