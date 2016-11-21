####### Build 및 실행환경 설정 ###########

* JAR Build
 > maven 설치 및 maven 명령어에 대한 PATH 설정
 > /ankus-optimizer로 이동
 > build 명령어 실행 (명령어 : mvn package)
  - build JAR 파일경로 : /ankus-optimizer/target/ankus-optimizer-0.5.jar

* 실행 환경 구축
 > HADOOP 2.6 설치 및 명령어에 대한 PATH 설정





####### 최적화 실행명령어  ###########

---------- Pool 관리 기능 -----------------
* pool 추가
 hadoop jar ankus-optimizer-0.5.jar poolmgr add [pool name] [JAR path]
 > Example (pool명: ankusPool, 라이브러리파일: ankus-core2.jar)
 hadoop jar ankus-optimizer-0.5.jar poolmgr add ankusPool ankus-core2-onycom-alpha1-1.1.0.jar

* pool 삭제
 hadoop jar ankus-optimizer-0.5.jar poolmgr delete [pool name]
 > Example (pool명: ankusPool)
 hadoop jar ankus-optimizer-0.5.jar poolmgr delete ankusPool

* pool 목록
 hadoop jar ankus-optimizer-0.5.jar poolmgr list


---------- 알고리즘 관리 기능 -----------------

* 알고리즘 추가
 hadoop jar ankus-optimizer-0.5.jar algomgr add [pool name] [algorithm name]
 > Example (pool명: ankusPool, 알고리즘명: C45)
 hadoop jar ankus-optimizer-0.5.jar algomgr add ankusPool C45
 
* 알고리즘 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delete [pool name] [algorithm name]
 > Example (pool명: ankusPool, 알고리즘명: C45)
 hadoop jar ankus-optimizer-0.5.jar algomgr delete ankusPool C45
 
* 알고리즘 목록
 hadoop jar ankus-optimizer-0.5.jar algomgr list ankusPool
 
* 알고리즘 훈련 MapReduce 드라이버  클래스명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setTrainClassName [algorithm name] [MapReduce driver class name]
 > Example (알고리즘명: C45, MapReduce 드라이버 클래스명: C45)
 hadoop jar ankus-optimizer-0.5.jar algomgr setTrainClassName C45 C45
 
* 알고리즘 분류 MapReduce 드라이버 클래스명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyClassName [algorithm name] [MapReduce driver class name]
 > Example (알고리즘명: C45, MapReduce 드라이버 클래스명: C45)
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyClassName C45 C45

* 알고리즘 파라메터 형식 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParamFormat [algorithm name] [parameter format option. (Available Values: ankus, mahout, user)]
 > Example (알고리즘명: C45, MapReduce 드라이버 클래스명: C45)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParamFormat C45 ankus
 
* 알고리즘 훈련 입력 데이터 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam [algorithm name] input [parameter name]
 > Example (알고리즘명: C45, 파라메터 유형: input, 파라메터명: -input)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 input -input

* 알고리즘 분류결과 입력 데이터 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam [algorithm name] inputTest [parameter name]
 > Example (알고리즘명: C45, 파라메터 유형: inputTest, 파라메터명: -input)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 inputTest -input

* 알고리즘 구분자 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam [algorithm name] delimiter [parameter name]
 > Example (알고리즘명: C45, 파라메터 유형: delimiter, 파라메터명: -delimiter)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 delimiter -delimiter

* 알고리즘 출력 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam [algorithm name] output [parameter name]
 > Example (알고리즘명: C45, 파라메터 유형: output, 파라메터명: -output)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 output -output

* 알고리즘 모델생성 용 속성 인덱스 목록 파라메터명 설정 
 (해당 알고리즘이 속성의 데이터자료형을 구분짓지 않는 것일 경우 사용되며, 해당 파라메터 명을 정의시 수치형 및 기호형 속성 인덱스 목록 파라메터명 설정은 무시 됨)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam [algorithm name] indexList [parameter name]
 > Example (알고리즘명: C45, 파라메터 유형: indexList, 파라메터명: -indexList)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 indexList -indexList

* 알고리즘 모델생성 용 수치형 속성 인덱스 목록 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam [algorithm name] numericIndexList [parameter name]
 > Example (알고리즘명: C45, 파라메터 유형: numericIndexList, 파라메터명: -numericIndexList)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 numericIndexList -numericIndexList

* 알고리즘 모델생성 용 수치형 속성 인덱스 목록 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam [algorithm name] nominalIndexList [parameter name]
 > Example (알고리즘명: C45, 파라메터 유형: nominalIndexList, 파라메터명: -nominalIndexList)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 nominalIndexList -nominalIndexList

* 알고리즘 클래스인덱스 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam [algorithm name] classIndex [parameter name]
 > Example (알고리즘명: C45, 파라메터 유형: classIndex, 파라메터명: -classIndex)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 classIndex -classIndex

* 알고리즘 학습모델 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam [algorithm name] modelPath [parameter name]
 > Example (알고리즘명: C45, 파라메터 유형: modelPath, 파라메터명: -modelPath)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 modelPath -modelPath
 
* 알고리즘 최적화 파라메터 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam [algorithm name] optimize [parmeter name] [data type: (Available value : int, float)] [min value] [max value]
 
 > Example (알고리즘명: C45, 파라메터 유형: optimize, 파라메터명: -minLeafData, 자료형: 정수형, 최소값: 2, 최대값: 20)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 optimize -minLeafData int 2 20

 > Example (알고리즘명: C45, 파라메터 유형: optimize, 파라메터명: -minLeafData, 자료형: 실수형, 최소값: 0.5, 최대값: 1.0)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 optimize -purity float 0.5 1.0

* 알고리즘 학습 관련 기타 파라메터 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam [algorithm name] trainEtc [parameter name] [parameter value to be skippable]
 > Example (알고리즘명: C45, 파라메터 유형: trainEtc, 파라메터명: -isValidation, 파라메터값: false)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 trainEtc -isValidation false

* 알고리즘 분류결과 출력 관련 기타 파라메터 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam [algorithm name] classifyEtc [parameter name] [parameter value to be skippable]
 > Example (알고리즘명: C45, 파라메터 유형: classifyEtc, 파라메터명: -isValidation, 파라메터값: true)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 classifyEtc -isValidation true
 
 
* 알고리즘 파라메터명 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delParam [algorithm name] [parameter name]
 > Example (알고리즘명: C45, 파라메터명: -test)
 hadoop jar ankus-optimizer-0.5.jar algomgr delParam C45 -test
 
 
* 알고리즘이 학습모델을 출력하는 절대경로 설정(최적화 프로그램이 분류결과를 평가하기 위한 정보로서, 알고리즘 구현이 특정경로로만 출력하는 경우를 위한 설정 ) 
 hadoop jar ankus-optimizer-0.5.jar algomgr setModelAbsPath [algorithm name] [absolute path to a model file]
 > Example (알고리즘명: C45, 모델 출력 경로: /data/iris.csv)
 hadoop jar ankus-optimizer-0.5.jar algomgr setModelAbsPath C45 /data/iris.csv

* 알고리즘 학습모델 출력경로(절대경로) 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delModelAbsPath [algorithm name]
 > Example (알고리즘명: C45)
 hadoop jar ankus-optimizer-0.5.jar algomgr delModelAbsPath C45
 
* 알고리즘 학습모델 출력경로(population 훈련 출력디렉토리 기준 상대경로) 설정(최적화 프로그램이 분류결과를 평가하기 위한 정보로서, 알고리즘 구현이 설정된 출력경로로 특정 파일명으로 출력하는 경우를 위한 설정 ) 
 hadoop jar ankus-optimizer-0.5.jar algomgr setModelRelPath [algorithm name] [relative path to a model file based on the output directory path]
 > Example (알고리즘명: C45, 모델 출력 경로: /C45_rule.txt)
 hadoop jar ankus-optimizer-0.5.jar algomgr setModelRelPath C45 /C45_rule.txt

* 알고리즘 학습모델 출력경로(population 훈련 출력디렉토리 기준 상대경로) 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delModelRelPath [algorithm name]
 > Example (알고리즘명: C45)
 hadoop jar ankus-optimizer-0.5.jar algomgr delModelRelPath C45

* 알고리즘 분류결과 출력경로(population 분류결과 출력디렉토리 기준 상대경로) 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyOutputRelPath [algorithm name] [relative path to a classifying result based on the output directory path]
 > Example (알고리즘명: C45, 분류결과 출력경로: /classifying_result/total_result.csv)
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyOutputRelPath C45 /classifying_result/total_result.csv

* 알고리즘 분류결과 출력경로(population 분류결과 출력디렉토리 기준 상대경로) 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delClassifyOutputRelPath [algorithm name]
 > Example (알고리즘명: C45)
 hadoop jar ankus-optimizer-0.5.jar algomgr delClassifyOutputRelPath C45

* 알고리즘 파마메터값목록 구분자 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParamValueDelimiter [algorithm name] [parameter delimiter]
 > Example (알고리즘명: C45, 구분자: , )
 hadoop jar ankus-optimizer-0.5.jar algomgr setParamValueDelimiter C45 ,
 
 
* 알고리즘 파마메터값목록 구분자 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delParamValueDelimiter [algorithm name]
 > Example (알고리즘명: C45)
 hadoop jar ankus-optimizer-0.5.jar algomgr delParamValueDelimiter C45

* 알고리즘 분류결과 출력모드 설정방법
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyOutputMode [algorithm name] [mode to output classifying results. (available value: labelWithInput, indexWithoutInput)]

 > Example (알고리즘명: C45, 분류결과 출력방식: 입력 데이터 및 분류 클래스 레이블 출력)
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyOutputMode C45 labelWithInput

 > Example (알고리즘명: C45, 분류결과 출력방식: 입력 데이터 없이, 분류 클래스 레이블의 index만 출력)
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyOutputMode C45 indexWithoutInput







 
---------- 최적화 관리 기능 -----------------

* 알고리즘 평가 멀티스레드 실행 여부 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr setMultiThreadEval [whether GA optimization runs out in multi-threading mode. (available value: true, false)]
 > Example (최적화 기능수행을 다중 스레드로 실행)
 hadoop jar ankus-optimizer-0.5.jar optmgr setMultiThreadEval true
 
* 예측분석할 입력데이터 파일 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr infSet [data file path] [data delimiter]
 > Example (입력데이터파일 경로: /data/iris.csv, 데이터 구분자: ,)
 hadoop jar ankus-optimizer-0.5.jar optmgr infSet /data/iris.csv ,

* 예측분석할 입력데이터 파일 정보를 보여줌
 hadoop jar ankus-optimizer-0.5.jar optmgr dataList

* 예측분석할 입력데이터 수치형 속성 인덱스목록 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr infNumericIndexList [numerical attribute index list to make a model]
 > Example (현재 설정된 입력데이터에서 모델생성에 사용할 수치형 속성 index 목록: 0,1,2,3)
 hadoop jar ankus-optimizer-0.5.jar optmgr infNumericIndexList 0,1,2,3

* 예측분석할 입력데이터 기호형 속성 인덱스목록 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr infNumericIndexList [nominal attribute index list to make a model]
 > Example (현재 설정된 입력데이터에서 모델생성에 사용할 기호형 속성 index 목록: 4)
 hadoop jar ankus-optimizer-0.5.jar optmgr infNominalIndexList 4
 
* 예측분석할 입력데이터 클래스 정보 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr infClass 4 [class label list]
 > Example (클래스 레이블 목록: Iris-setosa,Iris-versicolor,Iris-virginica)
 hadoop jar ankus-optimizer-0.5.jar optmgr infClass 4 Iris-setosa,Iris-versicolor,Iris-virginica


* 예측분석할 라이브러리와 알고리즘 선택
 hadoop jar ankus-optimizer-0.5.jar optmgr algSet ankusPool [algorithm name]
 > Example (최적화 수행 대상 알고리즘: C45)
 hadoop jar ankus-optimizer-0.5.jar optmgr algSet ankusPool C45
 
* 예측분석할 알고리즘의 환경번수 초기값 설정방법 선택
 hadoop jar ankus-optimizer-0.5.jar optmgr algIniPara [mode to initialize optimizing parameters. (available value: user, random)]
 
 > Example (최적화 환경변수 설졍에 사용자 정의 변수 적용)
 hadoop jar ankus-optimizer-0.5.jar optmgr algIniPara user

 > Example (최적화 환경변수 설졍을 임의방법 변수 적용)
 hadoop jar ankus-optimizer-0.5.jar optmgr algIniPara random
 
* 예측분석할 알고리즘의 환경변수 설정방법이 "사용자 선택인 경우, 선택된 알고리즘 환경변수 값 설정 기능
 hadoop jar ankus-optimizer-0.5.jar optmgr algParaSet [algorithm parameter name to be fixed] [parameter value]
 > Example (최적화에 사용되는 알고리즘 파라메터명: -minLeafData, 파라메터값: 5)
 hadoop jar ankus-optimizer-0.5.jar optmgr algParaSet -minLeafData 5
 
* 최적화 환경변수설정(돌연변이율)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet mp [ratio]
 > Example (돌연변이율: 0.75)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet mp 0.75
 
* 최적화 환경변수설정(교배율)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet cp [ratio]
 > Example (교배율: 0.5)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet cp 0.5

* 최적화 환경변수설정(세대 별 개체수)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet ps [integer value which bigger than 0]
 > Example (세대 별 개체수: 1)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet ps 1
 
* 최적화 환경변수설정(난수 seed)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet rs [seed]
 > Example (최적화 난수방생기의 seed: 123)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet rs 123

* 최적화 환경변수설정(최대 세대수)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet tc_maxg [integer value which bigger than -1]
 > Example (최대 세대수: 1)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet tc_maxg 1

* 최적화 환경변수설정(최소적합도)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet tc_minf [ratio]
 > Example (최소 적합도: 0.5)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet tc_minf 0.5

* 예측 최적화 분석을 위한 유전자알고리즘 환경변수 설정 내용 확인 기능
 hadoop jar ankus-optimizer-0.5.jar optmgr genSetList

* 예측분석할 알고리즘의 환경변수 설정 내용 확인 기능
 hadoop jar ankus-optimizer-0.5.jar optmgr algParaList
 
* 최적화 실행
 hadoop jar ankus-optimizer-0.5.jar optmgr run











####### ankus C4.5를 위한 최적화 실행명령어 ###########

---------- Pool 관리 기능 -----------------
* pool 추가(pool명: ankusPool, 라이브러리파일: ankus-core2.jar)
 hadoop jar ankus-optimizer-0.5.jar poolmgr add ankusPool ankus-core2-onycom-alpha1-1.1.0.jar

* pool 삭제(pool명: ankusPool)
 hadoop jar ankus-optimizer-0.5.jar poolmgr delete ankusPool

* pool 목록
 hadoop jar ankus-optimizer-0.5.jar poolmgr list


 
---------- 알고리즘 관리 기능 -----------------

* 알고리즘 추가
 hadoop jar ankus-optimizer-0.5.jar algomgr add ankusPool C45
 
 
* 알고리즘 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delete ankusPool C45
 
* 알고리즘 목록
 hadoop jar ankus-optimizer-0.5.jar algomgr list ankusPool
 
* 알고리즘 훈련 클래스명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setTrainClassName C45 C45
 
* 알고리즘 분류 클래스명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyClassName C45 C45

* 알고리즘 파라메터 형식 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParamFormat C45 ankus
 
* 알고리즘 훈련 데이터 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 input -input

* 알고리즘 테스트 데이터 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 inputTest -input

* 알고리즘 구분자 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 delimiter -delimiter

* 알고리즘 출력 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 output -output

* 알고리즘 모델생성 용 수치형 속성 인덱스목록 파라메터명 설정 
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 numericIndexList -numericIndexList

* 알고리즘 모델생성 용 기호형 속성 인덱스목록 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 nominalIndexList -nominalIndexList


* 알고리즘 클래스인덱스 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 classIndex -classIndex

* 알고리즘 학습모델 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 modelPath -modelPath
 
* 알고리즘 최적화 파라메터 설정(최소노드수)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 optimize -minLeafData int 2 20

* 알고리즘 학습 관련 기타 파라메터 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 trainEtc -isValidation false

* 알고리즘 분류결과 출력 관련 기타 파라메터 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam C45 classifyEtc -isValidation true
 
 
* 알고리즘 파라메터명 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delParam C45 -test
 
 
* 알고리즘 학습모델 출력경로(population 훈련 출력디렉토리 기준 상대경로) 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setModelRelPath C45 /C45_rule.txt

* 알고리즘 학습모델 출력경로(population 훈련 출력디렉토리 기준 상대경로) 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delModelRelPath C45

* 알고리즘 분류결과 출력경로(population 분류결과 출력디렉토리 기준 상대경로) 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyOutputRelPath C45 /classifying_result/total_result.csv

* 알고리즘 분류결과 출력경로(population 분류결과 출력디렉토리 기준 상대경로) 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delClassifyOutputRelPath C45

* 알고리즘 파마메터값목록 구분자 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParamValueDelimiter C45 ,
 
 
* 알고리즘 파마메터값목록 구분자 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delParamValueDelimiter C45

* 알고리즘 분류결과 출력모드 설정방법
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyOutputMode C45 labelWithInput

* 알고리즘 평가 멀티스레드 실행 여부 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr setMultiThreadEval true
 
* 예측분석할 입력데이터 파일 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr infSet /data/iris.csv ,

* 예측분석할 입력데이터 파일 정보를 보여줌
 hadoop jar ankus-optimizer-0.5.jar optmgr dataList

* 예측분석할 입력데이터 수치형 속성 인덱스목록 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr infNumericIndexList 0,1,2,3

* 예측분석할 입력데이터 기호형 속성 인덱스목록 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr infNominalIndexList 4
 
* 예측분석할 입력데이터 클래스 정보 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr infClass 4 Iris-setosa,Iris-versicolor,Iris-virginica


* 예측분석할 라이브러리와 알고리즘 선택
 hadoop jar ankus-optimizer-0.5.jar optmgr algSet ankusPool C45
 
* 예측분석할 알고리즘의 환경번수 초기값 설정방법 선택
 hadoop jar ankus-optimizer-0.5.jar optmgr algIniPara user

 hadoop jar ankus-optimizer-0.5.jar optmgr algIniPara random
 
* 최적화 환경변수설정(돌연변이율)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet mp 0.75
 
* 최적화 환경변수설정(교배율)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet cp 0.5

* 최적화 환경변수설정(세대 별 개체수)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet ps 1
 
* 최적화 환경변수설정(난수 seed)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet rs 123

* 최적화 환경변수설정(최대 세대수)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet tc_maxg 1

* 최적화 환경변수설정(최소적합도)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet tc_minf 0.5

* 예측 최적화 분석을 위한 유전자알고리즘 환경변수 설정 내용 확인 기능
 hadoop jar ankus-optimizer-0.5.jar optmgr genSetList
 
* 예측분석할 알고리즘의 환경변수 설정방법이 "사용자 선택인 경우, 선택된 알고리즘 환경변수 값 설정 기능
 hadoop jar ankus-optimizer-0.5.jar optmgr algParaSet -minLeafData 5

* 예측분석할 알고리즘의 환경변수 설정 내용 확인 기능
 hadoop jar ankus-optimizer-0.5.jar optmgr algParaList
 
* 최적화 실행
 hadoop jar ankus-optimizer-0.5.jar optmgr run

 
 





####### ankus kNN을 위한 최적화 실행명령어 예제  ###########

---------- Pool 관리 기능 -----------------
* pool 추가(pool명: ankusPool, 라이브러리파일: ankus-core2.jar)
 hadoop jar ankus-optimizer-0.5.jar poolmgr add ankusPool ankus-core2-onycom-alpha1-1.1.0.jar

* pool 삭제(pool명: ankusPool)
 hadoop jar ankus-optimizer-0.5.jar poolmgr delete ankusPool

* pool 목록
 hadoop jar ankus-optimizer-0.5.jar poolmgr list


 
---------- 알고리즘 관리 기능 -----------------

* 알고리즘 추가
 hadoop jar ankus-optimizer-0.5.jar algomgr add ankusPool kNN

 
 
* 알고리즘 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delete ankusPool kNN
 
* 알고리즘 목록
 hadoop jar ankus-optimizer-0.5.jar algomgr list ankusPool

 
* 알고리즘 훈련 클래스명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setTrainClassName kNN kNN
 
* 알고리즘 분류 클래스명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyClassName kNN kNN

* 알고리즘 파라메터 형식 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParamFormat kNN ankus

 
* 알고리즘 훈련 데이터 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam kNN input -input

* 알고리즘 테스트 데이터 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam kNN inputTest -input
 
* 알고리즘 구분자 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam kNN delimiter -delimiter

* 알고리즘 출력 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam kNN output -output

* 알고리즘 모델생성 용 속성 인덱스목록 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam kNN indexList -indexList

* 알고리즘 클래스인덱스 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam kNN classIndex -classIndex

* 알고리즘 학습모델 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam kNN modelPath -modelPath
 
* 알고리즘 최적화 파라메터 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam kNN optimize -k int 1 20

* 알고리즘 학습 관련 기타 파라메터 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam kNN trainEtc -isValidation false

* 알고리즘 분류결과 출력 관련 기타 파라메터 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam kNN classifyEtc -isValidation true
 
 
* 알고리즘 파라메터명 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delParam kNN -test
 
* 알고리즘 학습모델 출력경로(population 훈련 출력디렉토리 기준 상대경로) 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setModelRelPath kNN ../../../trainData.txt

* 알고리즘 학습모델 출력경로(population 훈련 출력디렉토리 기준 상대경로) 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delModelRelPath kNN

* 알고리즘 분류결과 출력경로(population 분류결과 출력디렉토리 기준 상대경로) 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyOutputRelPath kNN /classifying_result/part-r-00000

* 알고리즘 분류결과 출력경로(population 분류결과 출력디렉토리 기준 상대경로) 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delClassifyOutputRelPath kNN

* 알고리즘 파마메터값목록 구분자 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParamValueDelimiter kNN ,
 
 
* 알고리즘 파마메터값목록 구분자 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delParamValueDelimiter kNN

* 알고리즘 분류결과 출력모드 설정방법
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyOutputMode kNN labelWithInput
 
 
---------- 최적화 관리 기능 -----------------

* 알고리즘 평가 멀티스레드 실행 여부 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr setMultiThreadEval true

 hadoop jar ankus-optimizer-0.5.jar optmgr setMultiThreadEval false
 
* 예측분석할 입력데이터 파일 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr infSet /data/iris.csv ,

* 예측분석할 입력데이터 파일 정보를 보여줌
 hadoop jar ankus-optimizer-0.5.jar optmgr dataList

 
* 예측분석할 입력데이터 수치형 속성 인덱스목록 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr infNumericIndexList 0,1,2,3

* 예측분석할 입력데이터 기호형 속성 인덱스목록 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr infNominalIndexList 4
 
* 예측분석할 입력데이터 클래스 정보 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr infClass 4 Iris-setosa,Iris-versicolor,Iris-virginica

 
* 예측분석할 라이브러리와 알고리즘 선택
 hadoop jar ankus-optimizer-0.5.jar optmgr algSet ankusPool kNN
 
* 예측분석할 알고리즘의 환경번수 초기값 설정방법 선택
 hadoop jar ankus-optimizer-0.5.jar optmgr algIniPara user

 hadoop jar ankus-optimizer-0.5.jar optmgr algIniPara random
 
* 최적화 환경변수설정(돌연변이율)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet mp 0.75
 
* 최적화 환경변수설정(교배율)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet cp 0.5

* 최적화 환경변수설정(세대 별 개체수)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet ps 1
 
* 최적화 환경변수설정(난수 seed)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet rs 123

* 최적화 환경변수설정(최대 세대수)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet tc_maxg 1

* 최적화 환경변수설정(최소적합도)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet tc_minf 0.5

* 예측 최적화 분석을 위한 유전자알고리즘 환경변수 설정 내용 확인 기능
 hadoop jar ankus-optimizer-0.5.jar optmgr genSetList
 
* 예측분석할 알고리즘의 환경변수 설정방법이 "사용자 선택인 경우, 선택된 알고리즘 환경변수 값 설정 기능
 hadoop jar ankus-optimizer-0.5.jar optmgr algParaSet -k 10

* 예측분석할 알고리즘의 환경변수 설정 내용 확인 기능
 hadoop jar ankus-optimizer-0.5.jar optmgr algParaList
 
* 최적화 실행
 hadoop jar ankus-optimizer-0.5.jar optmgr run






####### ankus MLP(Multi Layer Perception)의 최적화 실행명령어 예제  ###########


---------- Pool 관리 기능 -----------------
* pool 추가(pool명: ankusPool, 라이브러리파일: ankus-core2.jar)
 hadoop jar ankus-optimizer-0.5.jar poolmgr add ankusPool ankus-core2-onycom-alpha1-1.1.0.jar

* pool 삭제(pool명: ankusPool)
 hadoop jar ankus-optimizer-0.5.jar poolmgr delete ankusPool

* pool 목록
 hadoop jar ankus-optimizer-0.5.jar poolmgr list


 
---------- 알고리즘 관리 기능 -----------------

* 알고리즘 추가
 hadoop jar ankus-optimizer-0.5.jar algomgr add ankusPool mlp
 
 
* 알고리즘 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delete ankusPool mlp
 
* 알고리즘 목록
 hadoop jar ankus-optimizer-0.5.jar algomgr list ankusPool

* 알고리즘 훈련 클래스명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setTrainClassName mlp MultilayerPerceptron
 
* 알고리즘 분류 클래스명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyClassName mlp MultilayerPerceptron

* 알고리즘 파라메터 형식 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParamFormat mlp ankus
 
* 알고리즘 훈련 데이터 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mlp input -input

* 알고리즘 테스트 데이터 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mlp inputTest -testPath

* 알고리즘 구분자 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mlp delimiter -delimiter

* 알고리즘 출력 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mlp output -output
 
* 알고리즘 수치형 속성 인덱스목록 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mlp numericIndexList -numericIndexList
 
* 알고리즘 수치형 속성 인덱스목록 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mlp nominalIndexList -nominalIndexList

* 알고리즘 클래스인덱스 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mlp classIndex -classIndex

* 알고리즘 학습모델 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mlp modelPath -modelPath
 
* 알고리즘 최적화 파라메터 설정(은닉노드수, 학습률)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mlp optimize -numHiddenNodes int 3 10
 
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mlp optimize -learningRate float 0.1 0.8

* 알고리즘 학습 관련 기타 파라메터 설정(랜덤시드, 모멘텀, 종료조건)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mlp trainEtc -seed 6

 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mlp trainEtc -momentum 0.2

 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mlp trainEtc -endCondition 0

 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mlp trainEtc -maxEpoch 1

 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mlp trainEtc -subMaxEpoch 1
 
* 알고리즘 파라메터명 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delParam mlp -test
 
 
* 알고리즘 학습모델 출력경로(population 훈련 출력디렉토리 기준 상대경로) 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setModelRelPath mlp /net.out

* 알고리즘 학습모델 출력경로(population 훈련 출력디렉토리 기준 상대경로) 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delModelRelPath mlp

* 알고리즘 분류결과 출력경로(population 분류결과 출력디렉토리 기준 상대경로) 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyOutputRelPath mlp /result/part-r-00000

* 알고리즘 분류결과 출력경로(population 분류결과 출력디렉토리 기준 상대경로) 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delClassifyOutputRelPath mlp

* 알고리즘 파마메터값목록 구분자 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParamValueDelimiter mlp ,
 
 
* 알고리즘 파마메터값목록 구분자 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delParamValueDelimiter mlp

* 알고리즘 분류결과 출력모드 설정방법
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyOutputMode mlp labelWithInput
 
 
 
 
 
---------- 최적화 관리 기능 -----------------

* 알고리즘 평가 멀티스레드 실행 여부 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr setMultiThreadEval true
 
* 예측분석할 입력데이터 파일 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr infSet /data/iris.csv ,

* 예측분석할 입력데이터 파일 정보를 보여줌
 hadoop jar ankus-optimizer-0.5.jar optmgr dataList

* 예측분석할 입력데이터 수치형 속성 인덱스목록 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr infNumericIndexList 0,1,2,3

* 예측분석할 입력데이터 기호형 속성 인덱스목록 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr infNominalIndexList 4
 
* 예측분석할 입력데이터 클래스 정보 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr infClass 4 Iris-setosa,Iris-versicolor,Iris-virginica
 
* 예측분석할 라이브러리와 알고리즘 선택
 hadoop jar ankus-optimizer-0.5.jar optmgr algSet ankusPool mlp
 
* 예측분석할 알고리즘의 환경번수 초기값 설정방법 선택
 hadoop jar ankus-optimizer-0.5.jar optmgr algIniPara user

 hadoop jar ankus-optimizer-0.5.jar optmgr algIniPara random

 
* 최적화 환경변수설정(돌연변이율)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet mp 0.75
 
* 최적화 환경변수설정(교배율)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet cp 0.5

* 최적화 환경변수설정(세대 별 개체수)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet ps 1
 
* 최적화 환경변수설정(난수 seed)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet rs 123

* 최적화 환경변수설정(최대 세대수)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet tc_maxg 1

* 최적화 환경변수설정(최소적합도)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet tc_minf 0.5

* 예측 최적화 분석을 위한 유전자알고리즘 환경변수 설정 내용 확인 기능
 hadoop jar ankus-optimizer-0.5.jar optmgr genSetList

* 예측분석할 알고리즘의 환경변수 설정방법이 "사용자 선택인 경우, 선택된 알고리즘 환경변수 값 설정 기능
 hadoop jar ankus-optimizer-0.5.jar optmgr algParaSet -numHiddenNodes 5

* 예측분석할 알고리즘의 환경변수 설정 내용 확인 기능
 hadoop jar ankus-optimizer-0.5.jar optmgr algParaList
 
* 최적화 실행
 hadoop jar ankus-optimizer-0.5.jar optmgr run
 
  
 

 
 
 
 
 
 
 

####### mahout Random Forest의 최적화 실행명령어 예제 ###########

---------- Pool 관리 기능 -----------------
* pool 추가(pool명: mahoutPool, 라이브러리파일: ankus-core2.jar)
 hadoop jar ankus-optimizer-0.5.jar poolmgr add mahoutPool ~/mahout/mahout-examples-0.10.1-job.jar

* pool 삭제(pool명: mahoutPool)
 hadoop jar ankus-optimizer-0.5.jar poolmgr delete mahoutPool

* pool 목록
 hadoop jar ankus-optimizer-0.5.jar poolmgr list


 
---------- 알고리즘 관리 기능 -----------------

* 알고리즘 추가
 hadoop jar ankus-optimizer-0.5.jar algomgr add mahoutPool mahoutRF
 
* 알고리즘 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delete mahoutPool mahoutRF
 
* 알고리즘 목록
 hadoop jar ankus-optimizer-0.5.jar algomgr list mahoutPool
 
* 알고리즘 훈련 클래스명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setTrainClassName mahoutRF org.apache.mahout.classifier.df.mapreduce.BuildForest
 
* 알고리즘 분류 클래스명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyClassName mahoutRF org.apache.mahout.classifier.df.mapreduce.TestForest

* 알고리즘 파라메터 형식 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParamFormat mahoutRF mahoutRF

 
* 알고리즘 훈련 데이터 파라메터 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mahoutRF input -d

* 알고리즘 테스트 데이터 파라메터 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mahoutRF inputTest -i
 
* 알고리즘 출력 파라메터 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mahoutRF output -o

* 알고리즘 학습모델 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mahoutRF modelPath -m

* 알고리즘 최적화 파라메터 설정
 // builds minimum 1 ~ maximum 20 trees
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mahoutRF optimize -t int 1 20
 

* 알고리즘 학습 관련 기타 파라메터 설정
 // indicates to Hadoop the max. size of each partition, in this case 1/10 of the size of the dataset
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mahoutRF trainEtc -Dmapred.max.split.size=1874231
 
 // partial implementation
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam mahoutRF trainEtc -p

 
* 알고리즘 파라메터명 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delParam mahoutRF -test

 
 
* 알고리즘 학습모델 출력경로(population 훈련 출력디렉토리 기준 상대경로) 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setModelRelPath mahoutRF /forest.seq

* 알고리즘 학습모델 출력경로(population 훈련 출력디렉토리 기준 상대경로) 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delModelRelPath mahoutRF

 
* 알고리즘 분류결과 출력경로(population 분류결과 출력디렉토리 기준 상대경로) 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyOutputRelPath mahoutRF /classifying_result/part-r-00000

* 알고리즘 분류결과 출력경로(population 분류결과 출력디렉토리 기준 상대경로) 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delClassifyOutputRelPath mahoutRF

* 알고리즘 파마메터값목록 구분자 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParamValueDelimiter mahoutRF ,
 
 
* 알고리즘 파마메터값목록 구분자 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delParamValueDelimiter mahoutRF

* 알고리즘 분류결과 출력모드 설정방법
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyOutputMode mahoutRF indexWithoutInput
 
 
---------- 최적화 관리 기능 -----------------

* 알고리즘 평가 멀티스레드 실행 여부 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr setMultiThreadEval false
 
 
* 예측분석할 입력데이터 선택 기능
 hadoop jar ankus-optimizer-0.5.jar optmgr infSet /data/iris.csv ,


* 예측분석할 입력데이터 파일 정보를 보여줌
 hadoop jar ankus-optimizer-0.5.jar optmgr dataList

* 예측분석할 입력데이터 수치형 속성 인덱스목록 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr infNumericIndexList 0,1,2,3

* 예측분석할 입력데이터 기호형 속성 인덱스목록 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr infNominalIndexList 4
 
* 예측분석할 입력데이터 클래스 정보 설정
 hadoop jar ankus-optimizer-0.5.jar optmgr infClass 4 Iris-setosa,Iris-versicolor,Iris-virginica

 
* 예측분석할 라이브러리와 알고리즘 선택
 hadoop jar ankus-optimizer-0.5.jar optmgr algSet mahoutPool mahoutRF
 
* 예측분석할 알고리즘의 환경번수 초기값 설정방법 선택
 hadoop jar ankus-optimizer-0.5.jar optmgr algIniPara random
 
* 최적화 환경변수설정(돌연변이율)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet mp 0.5
 
* 최적화 환경변수설정(교배율)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet cp 0.3

* 최적화 환경변수설정(세대 별 개체수)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet ps 1
 
* 최적화 환경변수설정(난수 seed)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet rs 123

* 최적화 환경변수설정(최대 세대수)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet tc_maxg 1

* 최적화 환경변수설정(최소적합도)
 hadoop jar ankus-optimizer-0.5.jar optmgr genSet tc_minf 0.5

* 예측 최적화 분석을 위한 유전자알고리즘 환경변수 설정 내용 확인 기능
 hadoop jar ankus-optimizer-0.5.jar optmgr genSetList
 
* 예측분석할 알고리즘의 환경변수 설정 내용 확인 기능
 hadoop jar ankus-optimizer-0.5.jar optmgr algParaList
 
* 최적화 실행
 hadoop jar /mnt/hgfs/shared_for_vm/ankus-optimizer-0.5.jar optmgr run

 
  
 
 
 
 
 
 