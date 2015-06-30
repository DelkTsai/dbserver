#dbserver 文档数据库

* 仿mangodb的javase应用，包括socket通信，文件读写等功能 
 
#主要技术：

* socket通信，socket通信使用java NIO来实现
* BSON，服务器端与客户端的的通信使用BSON来封装数据
* java线程池，依照java线程池框架Exector实现了简单的线程池功能
* 实现了简单的通信协议：
	* 所有的消息包括消息头和消息体两部分：
	* 消息头协议为：
		* [int32 sequence,int32 messageLen, int32 opCode]
	* 客户端的发送指令协议为：
		* 消息头+消息体，其中消息体为：bson bytes
	* 服务器端返回的消息协议体为：
		* 消息头+消息体，其中消息体为：[int32 returnCode,int32 numReturn,bson bytes] 
 	
#快速体验：
	
* release目录是程序发布运行目录，
	* 其中：lib存放程序运行时依赖的相关jar和程序本身的jar包
	* 程序自身的jar包由源代码打包
* 程序运行命令(linux)：
	* ./start.sh or start.bat 启动服务器端
	* ./client.sh or client.bat启动客户端
	* 在客户端控制台输入connect ip port 连接服务端
	* 连接成功后，分别使用insert/delete/update/query {key:value,key:value...}
		* 对数据库进行增删改查,例如查询id为1的数据：query {id:1}
	* ./shutdown.sh or shutdown.bat停止服务端
		 
#文档下载
 
#交流、反馈和建议

* QQ：82552623,1070235836,[在这里留言]http://blog.csdn.net/mooyinn


