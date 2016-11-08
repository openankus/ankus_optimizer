package org.ankus.optimizer.util;

/**
 * Constants
 * @desc
 *      Collected constants of general utility
 *
 * @description
 * @version 0.1
 * @date  2016. 10. 18.
 * @author JungHo Kim
 *
 */
public class Constants {

    public static final String UTF8 = "UTF-8";
    
    /**
     * 유전자알고리즘 관련 각 출력결과 표현을 위한 구분자
     */
    public static final String DELIMITER_GA = "\t";

    /**
     * 환경변수 별 구분자
     */
    public static final String DELIMITER_GA_PARAM = "&&";

    /**
     * 자료값 별 구분자 (예> index 등)
     */
    public static final String DELIMITER_GA_VALUE = ",";
    

    /**
     * 유전자알고리즘 관련 설정정보 및 소요시간 출력파일 이름
     */
    public static final String FILE_NAME_GA_INFO = "gaInfo";

    /**
     * 세대별 최상위 개체정보 목록을 담는 파일 이름
     */
    public static final String FILE_NAME_GENERATION_TOP_LIST = "generationTopList";

    /**
     * 생성된 모델의 개체정보 목록을 담는 파일 이름
     */
    public static final String FILE_NAME_POP_INFO_LIST = "popInfoList";

    /**
     * 생성된 모델의 개체정보 목록을 담는 파일 이름
     */
    public static final String FILE_NAME_ORDERED_POP_INFO_LIST = "orderedPopInfoList";
    
}
