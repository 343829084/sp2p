项目搭建步骤：

1,拉项目下来：
	git@gitlab.sunlights.me:sunlights/sp2ponline-dev-v2.git
	git@gitlab.sunlights.me:sunlights/p2pspay-dev-v2.git

2，环境变量：
	jdk1.6  play-1.2.7

3，导入所有jar包：
 	导入play-1.2.7\framework\lib下所有jar包
	lib下所有jar包
	play-1.2.7\framework\play-1.2.7.jar包

4，配置运行参数
	eclipse中：	
		配置java application
		
		sp2ponline-dev-v2项目配置
		name:sp2ponline-dev-v2
		project:sp2ponline-dev-v2
		main class:play.server.Server
		arguments: -Xms512m -Xmx512m -XX:PermSize=512m -XX:MaxPermSize=512m -Xms512m -Xmx512m -XX:PermSize=126m -XX:MaxPermSize=126m -Xdebug -Dplay.debug=yes -Dplay.id= -Dapplication.path="${project_loc:sp2ponline-dev-v2}" -Djava.endorsed.dirs="C:\Program Files\play-1.2.7\framework/endorsed" -javaagent:"C:\Program Files\play-1.2.7\framework/play-1.2.7.jar"
		
		p2pspay-dev-v2项目配置：
		name:p2pspay-dev-v2
		project:p2pspay-dev-v2
		main class:play.server.Server
		arguments:	-Xdebug  -Dplay.id= -Dapplication.path="${project_loc:p2pspay-dev-v2}" -Djava.endorsed.dirs="C:\Program Files\play-1.2.7/framework/endorsed" -javaagent:"C:\Program Files\play-1.2.7/framework/play-1.2.7.jar"
	
	IDEA中	：
		 Edit configurations
         add Application
            Main Class: play.server.Server
            VM options: -Xms512m -Xmx512m -XX:PermSize=512m -XX:MaxPermSize=512m -Xms512m -Xmx512m -XX:PermSize=126m -XX:MaxPermSize=126m -Xdebug -Dplay.debug=yes -Dplay.id= -Dapplication.path="E:\p2pworkspace\sp2ponline" -Djava.endorsed.dirs="C:\play\play-1.2.7\framework/endorsed" -javaagent:"C:\play\play-1.2.7\framework/play-1.2.7.jar"
		Terminal
        play idealize重新生成本地配置文件
        若出现版本问题  VerifyError...XXX   使用 play clean 删除tmp文件
        
        
		
		
		