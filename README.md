dbserver 文档数据库
=============================================
	仿mangodb的小程序，包括socket通信，文件读写等功能 
 
 主要使用到的技术包括：
-----------------
	1，	socket通信，socket通信使用java NIO来实现
	2，	BSON，服务器端与客户端的的通信使用BSON来封装数据。
	3，	java线程池，自己依照java线程池框架Exector实现了简单的线程池功能，用来在服务器端处理客户端的并发请求。
	4，	实现了简单的通信协议：
		4.1，	所有的消息包括消息头和消息体两部分：
		4.2，	消息头协议为：
					[int32 sequence,int32 messageLen, int32 opCode]
		4.3，	客户端的发送指令协议为：
					消息头+消息体，其中消息体为：bson bytes
		4.4，	服务器端返回的消息协议体为：
		 			消息头+消息体，其中消息体为：[int32 returnCode,int32 numReturn,bson bytes] 
		…….
 	
 
文档下载
--------
--
交流、反馈和建议
---------------
		QQ：82552623@qq.com


