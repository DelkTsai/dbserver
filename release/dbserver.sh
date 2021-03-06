#!/bin/bash  
  
# JDK所在路径  
JAVA_HOME="/usr/java/jdk1.7.0_65"  
  
# 需要启动的Java主程序（main方法类）  
APP_SERVER_MAINCLASS="org.enilu.socket.v3.server.Main"  
APP_SHUTDOWNSERVER_MAINCLASS="org.enilu.socket.v3.server.Shutdown"
APP_NAME="dbserver"
# 拼凑完整的classpath参数，包括指定lib目录下所有的jar  
CLASSPATH="."  
for i in lib/*.jar; do  
    CLASSPATH="$CLASSPATH":"$i"  
done  
  
# java虚拟机启动参数  
# =======================================================================  
# -XX:+<option> 开启option参数  
# -XX:-<option> 关闭option参数  
# -XX:<option>=<value> 将option参数的值设置为value  
# =======================================================================  
# -Duser.timezone=GMT+08 | 东八区  
# -Xms6144m -Xmx6144m | 堆内存设置为6G，并禁止扩展  
# -Xmn1536m | 堆内存(6G)中的1.5G分配给新生代，新生代太大会影响gc停顿时间，并且netty tcp长连接保存在老年代里  
# -XX:PermSize=288m -XX:MaxPermSize=288m | 永久代固定为288m  
# 剩下的就是老年代了 (堆内存size = 新生代size + 老年代size + 永久代size)  
# -XX:UseConcMarkSweepGC | 使用ParNew + CMS + Serial Old的收集器组合进行内存回收。如果CMS出现Concurrent Mode Failure，则Serial Old作为后备  
# -XX:+CMSClassUnloadingEnabled | 配合UseConcMarkSweepGC选项在jdk1.6中可实现对永久代(PermGen)的GC  
# -XX:MaxTenuringThreshold | 晋升到老年代的对象年龄，每个对象在坚持过一次Minor GC后年龄就+1  
# -Xloggc:/opt/gc.log  
# -XX:+PrintGCDetails | 打印GC详细信息  
# -XX:+PrintGCTimeStamps | 打印GC停顿时间  
# -XX:+HeapDumpOnOutOfMemoryError | 在发生内存溢出异常时生成堆转储快照  
# -XX:HeapDumpPath=/opt/dump/ | 生成堆转储快照的目录  
JAVA_OPTS="-Duser.timezone=GMT+08 -Xms6144m -Xmx6144m -Xmn1536m -XX:PermSize=288m -XX:MaxPermSize=288m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:MaxTenuringThreshold=15 -Xloggc:/opt/gc.log  -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/opt/dump/"  
  
# 初始化psid变量（全局）  
psid=0  
  
###################################  
# (函数)判断程序是否已启动  
#  
# 说明：  
# 使用JDK自带的JPS命令及grep命令组合，准确查找pid  
# jps 加 l 参数，表示显示java的完整包路径  
# 使用awk，分割出pid ($1部分)，及Java程序名称($2部分)  
###################################  
checkpid() {  
    javaps=`$JAVA_HOME/bin/jps -l | grep $APP_SERVER_MAINCLASS`  
    if [ -n "$javaps" ]; then  
        psid=`echo $javaps | awk '{print $1}'`  
    else  
        psid=0  
    fi  
} 

###################################  
# (服务器端)启动程序  
#  
# 说明：  
# 1.首先调用checkpid函数，刷新$psid全局变量  
# 2.如果程序已经启动（$psid不等于0），则提示程序已启动  
# 3.如果程序没有被启动，则执行启动命令行  
###################################  
startServer() {  
    checkpid  
    if [ $psid -ne 0 ]; then          
        echo "warn: $APP_NAME already started! (pid=$psid)"  
    else  
	  java $JAVA_OPTS -classpath $CLASSPATH "$APP_SERVER_MAINCLASS"
    fi  
}  


###################################  
# (服务器端)关闭程序  
#  
# 说明：  
# 1.调用关闭程序关服务  
###################################  
shutdownServer() {       
	  java $JAVA_OPTS -classpath $CLASSPATH "$APP_SHUTDOWNSERVER_MAINCLASS"  
}
 
 
###################################  
# (服务器端)检查程序  
#  
# 说明：  
# 1.调用chkkpid检查进程是否存在  
###################################  
chkStatus() {  
    checkpid  
    if [ $psid -ne 0 ]; then          
        echo "^_^：$APP_NAME is running (pid=$psid)"  
    else  
	  	  echo "囧：$APP_NAME is not running"  
    fi  
} 
###################################  
#读取脚本的第一个参数($1)，进行判断  
###################################  
case $1 in  
start)   
    startServer  
    ;;
shutdown)  
    shutdownServer  
    ;; 
restart)  
    shutdownServer  
    startserver  
    ;;  
status)  
    chkStatus  
    ;;  
*)  
esac  
exit 0  
