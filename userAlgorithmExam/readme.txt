
####### 사용자 알고리즘 예제의 실행명령어 (User Algorithm Example - RandomForest) ###########

* 훈련 실행의 예
 (파라메터 설정)
 > 수치형 속성 index 목록 : 0,1,2,3
 > 기호형 속성 index 목록 : 4
 > 클래스 속성 index : 4
 > 모델출력 경로(HDFS상의 절대경로): /output/userRF/train
 > 입력데이터형식 파일 경로 (HDFS상의 절대경로): /data/iris.arff
 > 트리 노드 생성 시, 최소 데이터 개수: 3
 > 트리 pruning에 사용되는 confidence factor : 0.3
 > 입력데이터 파일의 경로 (HDFS상의 절대경로): /data/iris.csv
 > 입력데이터 파일의 데이터값 delimiter : ,
 > 입력데이터 파일의 분할 개수(= tree 생성개수) : 3
  
 (실행명령어)
hadoop jar userAlgorithm-0.0.1-SNAPSHOT.jar rf -numericIndexList 0,1,2,3 -nominalIndexList 4 -classIndex 4 -output /output/userRF/train -input /data/iris.csv -dataFormat /data/iris.arff -minLeafData 3 -confidenceFactor 0.3 -delimiter , -numPartition 3

* 분류결과
 (파라메터 설정)
 > 수치형 속성 index 목록 : 0,1,2,3
 > 기호형 속성 index 목록 : 4
 > 클래스 속성 index : 4
 > 모델 경로(HDFS상의 절대경로): /output/userRF/train
 > 입력데이터형식 파일 경로 (HDFS상의 절대경로): /data/iris.arff
 > 입력데이터 파일의 경로 (HDFS상의 절대경로): /data/iris.csv
 > 입력데이터 파일의 데이터값 delimiter : ,
 > 입력데이터 파일의 분할 개수(= 데이터 분류에 사용되는 Map의 개수) : 3

 (실행명령어)
 hadoop jar userAlgorithm-0.0.1-SNAPSHOT.jar rf -numericIndexList 0,1,2,3 -nominalIndexList 4 -classIndex 4 -output /output/userRF/classify_result -input /data/iris.csv -dataFormat /data/iris.arff -delimiter , -modelPath /output/userRF/train -numPartition 3


 
 
 
 
 
####### ankus-optimizer 등록을 위한 실행명령어(user Algorithm Example - RandomForest) ###########
 
---------- Pool 관리 기능 -----------------
* pool 추가(pool명: userPool, 라이브러리파일: userAlgorithm-0.0.1-SNAPSHOT.jar)
 hadoop jar ankus-optimizer-0.5.jar poolmgr add userPool userAlgorithmExam-0.0.1-SNAPSHOT.jar

* pool 삭제(pool명: userPool)
 hadoop jar ankus-optimizer-0.5.jar poolmgr delete userPool

* pool 목록
 hadoop jar ankus-optimizer-0.5.jar poolmgr list


 
---------- 알고리즘 관리 기능 -----------------

* 알고리즘 추가
 hadoop jar ankus-optimizer-0.5.jar algomgr add userPool rf
 
 
* 알고리즘 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delete userPool rf
 
* 알고리즘 목록
 hadoop jar ankus-optimizer-0.5.jar algomgr list userPool
 
* 알고리즘 훈련 클래스명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setTrainClassName rf rf
 
* 알고리즘 분류 클래스명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyClassName rf rf

* 알고리즘 파라메터 형식 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParamFormat rf user
 
* 알고리즘 훈련 데이터 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam rf input -input

* 알고리즘 테스트 데이터 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam rf inputTest -input

* 알고리즘 구분자 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam rf delimiter -delimiter

* 알고리즘 출력 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam rf output -output

* 알고리즘 수치형 속성 인덱스목록 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam rf numericIndexList -numericIndexList
 
* 알고리즘 수치형 속성 인덱스목록 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam rf nominalIndexList -nominalIndexList


* 알고리즘 클래스인덱스 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam rf classIndex -classIndex

* 알고리즘 학습모델 파라메터명 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam rf modelPath -modelPath
 
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
* 알고리즘 최적화 파라메터 설정(최소노드수, confindence factor)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam rf optimize -minLeafData int 2 20

 hadoop jar ankus-optimizer-0.5.jar algomgr setParam rf optimize -confidenceFactor float 0.3 0.8

* 알고리즘 학습 관련 기타 파라메터 설정 (데이터 속성정보 파일 설정, partition 개수만큼 tree 생성)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam rf trainEtc -dataFormat /data/iris.arff

 hadoop jar ankus-optimizer-0.5.jar algomgr setParam rf trainEtc -numPartition 3

* 알고리즘 분류결과 출력 관련 기타 파라메터 설정 (데이터 속성정보 파일 설정, partition 개수만큼 Map 분산 처리)
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam rf classifyEtc -dataFormat /data/iris.arff
 
 hadoop jar ankus-optimizer-0.5.jar algomgr setParam rf classifyEtc -numPartition 3
 
 
* 알고리즘 파라메터명 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delParam rf -test

 
 
* 알고리즘 학습모델 출력경로(population 훈련 출력디렉토리 기준 상대경로) 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setModelRelPath rf ./

* 알고리즘 학습모델 출력경로(population 훈련 출력디렉토리 기준 상대경로) 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delModelRelPath rf

* 알고리즘 분류결과 출력경로(population 분류결과 출력디렉토리 기준 상대경로) 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyOutputRelPath rf /part-r-00000

* 알고리즘 분류결과 출력경로(population 분류결과 출력디렉토리 기준 상대경로) 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delClassifyOutputRelPath rf

* 알고리즘 파마메터값목록 구분자 설정
 hadoop jar ankus-optimizer-0.5.jar algomgr setParamValueDelimiter rf ,
 
 
* 알고리즘 파마메터값목록 구분자 삭제
 hadoop jar ankus-optimizer-0.5.jar algomgr delParamValueDelimiter rf

* 알고리즘 분류결과 출력모드 설정방법
 java -jar ankus-optimizer-0.5.jar algomgr setClassifyOutputMode rf labelWithInput
 hadoop jar ankus-optimizer-0.5.jar algomgr setClassifyOutputMode rf labelWithInput
 
 
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
 hadoop jar ankus-optimizer-0.5.jar optmgr algSet userPool rf
 
* 예측분석할 알고리즘의 환경번수 초기값 설정방법 선택(사용자 정의 추가: user, 사용자 정의를 사용하지 않음: random)
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

 
 