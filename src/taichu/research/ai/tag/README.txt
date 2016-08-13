//***********************************************
//TODO：
//***********************************************

//【完成/8小时】（  10）【COMMON  】实现对磁盘目录（windows）的浏览(JTREE含有目录和文件两者）。 
//【取消      】（  15）【COMMON  】添加svn支持。
//【完成/4小时】（  20）【COMMON  】实现文件属性的罗列，并将JTREE只显示目录，文件在右侧TABLE显示。
//【完成/8小时】（  21）【COMMON  】用jfreechart实现图表，现实文件及容量饼图，为以后显示做准备。
//【暂停/2小时】（  22）【JNA和JNI】考虑用现成的JNA框架实现（低性能要求，本地代码无需回调JAVA）的简单需求，比如创建，修改，访问，改动模式，这四个时间对linux和win兼容性！
//【计划中    】（  25）【JNA和JNI】用JNI实现JAVA调用LINUX的XVID编码库来编码YUV原始视频数据为AVI，并能播放！
//【完成/1小时】（  30）【COMMON  】右侧放入三个TAB，分别是：table，view，text。
//【进行中|3PH】（  33）【多维标签】不管左树TREE，而将递归遍历整个ROOT，并将结果全部罗列在右面，按照日期排序，并放入DB。
//【进行中    】（  35）【多维标签】预先针对DATE标签归类为树形数据结构，并全局扫描ROOT后在左面TREE显示。
//【计划中    】（  40）【多维标签】预先针对SIZE等标签做树形数据结构，并全局扫描后在右面TABLE显示。PLAN
//【计划中    】（  50）【多维标签】在右面的TEXT可以按照一定“文本规则”来修改标签树结构，并刷新右面的TREE来显示。PLAN
//【计划中    】（  60）【多维标签】为标签树结构建模，为标签判断规则建模，为数据（文件，或pic文件）建模，为标签树应用到数据上建模，4张DB表。PLAN
//【完成/1小时】（  70）【COMMON  】考虑可视化数据呈现。用饼图显示容量size
//【计划中    】（  80）【智能分析】对PIC文件的人脸识别（JNI调用CPP）。PLAN
//【计划中    】（  90）【智能分析】对PIC文件的场景或景物识别（JNI调用CPP）。PLAN
//【计划中    】（ 100）【高清视频】高清视频的播放，编解码等媒体算法（JNI调用CPP，或JNI研究）。
//【计划中    】（ 110）【网络工具】TLV研究和带性能数据统计和chart显示的udp，tcp网络包探测程序（需TCP/IP协议了解，JAVA无直接调用ICMP协议的LIB，用到JNA/JNI）。
//【计划中    】（ 120）【自由逻辑】人脑存储和思维模型程序仿真研究。
//
//
//
//
//
//









//***********************************************
//COMMENTS：
//***********************************************

// COMMENTS GOES HERE!

/*
 * 
 */
 
 /*
 //(1)解决eclipse中文字很小( http://coolheaded.cn/?p=14 )
 
 //(2)http://docs.oracle.com/javase/7/docs/api/javax/swing/tree/TreeModel.html
 
 //（3）通过OS的exec来获取windows平台的文件创建时间（http://whln007.blog.163.com/blog/static/20988292007102653050347/）。
 
 // （4）因为JAVA跨平台，所以没有现成文件创建时间函数，所以利用JNI来调用DLL（http://pet.iteye.com/blog/188313）
 
 //(5)java chart lib! (http://www.jfree.org/jfreechart/samples.html)
 
 /* JNA link 内容
 ------------------------------------------------
http://ig2net.info/archives/852.html
http://jna.java.net/(最新：https://github.com/twall/jna）
	https://github.com/twall/jna/blob/master/www/GettingStarted.md
	http://twall.github.com/jna/3.5.1/javadoc/

【非常好】http://xbgd.iteye.com/blog/1044864
【好API 】http://twall.github.com/jna/3.5.0/javadoc/com/sun/jna/Structure.html#getFieldOrder()
【好例子】http://www.redhat.com/archives/libvir-list/2012-October/msg00593.html
http://blog.163.com/ljf_gzhu/blog/static/13155344020123299148838/
http://www.iteye.com/blogs/tag/jna
http://download.csdn.net/detail/huangdou0204/4759880
http://blog.csdn.net/RyanLuX/article/details/4193636
http://www.blogjava.net/vwpolo/archive/2010/05/06/jna.html
 */
 
 
 
 
 
 
 
 
 
 
 
 
 */

//*********************************************** 
//BUG LIST：
//***********************************************
//BUG1001【FIXED】: 根目录会添加两遍，比如C:\temp会出现两次，请仔细测试，可能是跟目录没有判断的特殊性导致。
//BUG1002【OPEN 】: 因为swing仅支持一个JTREE，如果中途设置为null并重新建立对象不可用！修改后反复使用一个JTREE，
                    但是有时候node添加慢，被鼠标打断后node就不对了！需要等到都添加node完毕！不健壮！看怎么改动？
                    【解决】速度慢，等待一下，不能鼠标乱动，暂时就不控制鼠标了！






 
