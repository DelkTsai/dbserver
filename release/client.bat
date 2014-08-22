
@set JAVA_HOME="C:\Program Files\Java\jdk1.7.0_15"  
  
   
set APP_MAINCLASS="org.enilu.socket.v3.client.Main"  
 
 
set CLASSPATH=.;lib/*
 
set JAVA_OPTS="-Duser.timezone=GMT+08 -Xms6144m -Xmx6144m -Xmn1536m -XX:PermSize=288m -XX:MaxPermSize=288m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:MaxTenuringThreshold=15 -Xloggc:/opt/gc.log  -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/opt/dump/"  
 
java %JAVA_OPTS% -classpath %CLASSPATH% %APP_MAINCLASS%
      
      