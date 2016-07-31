//***********************************************
//TODO：
//***********************************************

//【完成/8小时】（10）实现对磁盘目录（windows）的浏览(JTREE含有目录和文件两者）。 
//【取消      】（15）添加svn支持。
//【完成/4小时】（20）实现文件属性的罗列，并将JTREE只显示目录，文件在右侧TABLE显示。
//【完成/8小时】（21）用jfreechart实现图表，现实文件及容量饼图，为以后显示做准备。
//【进行中    】（22）考虑用现成的JNA框架替代繁琐的JNI实现功能
//【暂停中    】（25）用JNI实现JAVA调用VC的DLL来获取一个windows平台的本地文件，且判断OS=linux的时候不操作！
//【计划中    】（30）右侧放入三个TAB，分别是：table，view，text。PLAN
//【计划中    】（40）预先针对SIZE,DATE等标签做树形数据结构，并全局扫描后在右面TABLE显示。PLAN
//【计划中    】（50）在右面的TEXT可以按照一定“文本规则”来修改标签树结构，并刷新右面的TREE来显示。PLAN
//【计划中    】（60）为标签树结构建模，为标签判断规则建模，为数据（文件，或pic文件）建模，为标签树应用到数据上建模，4张DB表。PLAN
//【计划中    】（70）考虑可视化数据呈现。PLAN
//【计划中    】（80）对PIC文件的人脸识别（JNI调用CPP）。PLAN
//【计划中    】（90）对PIC文件的场景或景物识别（JNI调用CPP）。PLAN
//【计划中    】（100）高清视频的播放，编解码等媒体算法（JNI调用CPP）。PLAN
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
//BUG1001【FIXED】: 根目录会添加两边，比如C:\temp会出现两次，请仔细测试，可能是跟目录没有判断的特殊性导致。
//BUG1002: 






 
