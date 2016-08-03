/* 公共js */

/* just for test */
alert("common.js| goes here.");

/* 通过URL来加载JS */
/* Usage: include("http://lib.sinaapp.com/js/jquery/1.9.1/jquery-1.9.1.min.js"); */
 if(typeof include == "undefined") {
  	alert("common.js| include is invoked"); 
	function include(jsurl) {
		if (jsurl == null || typeof(jsurl) != 'string') 
			alert("common.js| JSURL("+jsurl+") is NULL or NOT A STRING!");
			return;     
			var script = document.createElement('script');
			script.type = 'text/javascript';
			script.charset = 'utf-8';
			script.src = jsurl;     
			/*script.setAttribute("src",jsurl);*/     
			document.head.appendChild(script); 
		} 
		include=true;
 }

/* jQuery中加载JS的方法
$.getScript('/path/to/imported/script.js', function()
{
    // script is now loaded and executed.
    // put your dependent JS here.
});
*/





/*
OO框架代码
来源：from:http://www.111cn.net/wy/js-ajax/45533.htm
或搜索关键字‘js实现class和interface’
*/

/* ---------框架代码----------- */
//框架代码：interface接口定义（作为js中的一个普通类）
//Constructor 
var Interface = function(name, methods) { 
　　if(arguments.length != 2) { 
　　　　throw new Error("Interface constructor called with " 
　　　　　　　　　　　　　+ arguments.length + "arguments, but expected exactly 2."); 
　　} 
　　this.name = name; 
　　this.methods = []; 
　　for(var i = 0, len = methods.length; i < len; i++) { 
　　　　if(typeof methods[i] !== 'string') { 
　　　　　　throw new Error("Interface constructor expects method names to be " 
　　　　　　　　　　　　　　+ "passed in as a string."); 
　　　　} 
　　　　this.methods.push(methods[i]); 
　　} 
}; 

//框架代码：定义interface类的验证功能func
// Static class method. 
Interface.ensureImplements = function(object) { 
　　if(arguments.length < 2) { 
　　　　throw new Error("Function Interface.ensureImplements called with " 
　　　　　　　　　　　　　　+arguments.length + "arguments, but expected at least 2."); 
　　} 

for (var i = 1, len = arguments.length; i < len; i++) {
                //inter_face为接口，一定要实现Interface类
                //书中使用interface，因是JavaScript中保留字，所以暂替换为inter_face
                 var inter_face = arguments[i];
                 if (inter_face.constructor !== Interface) {
                         throw new Error("Function Interface.ensureImplementsexpects arguments " +
                         "two and above to be instances of Interface.");
                 }
                 for (var j = 0, methodsLen = inter_face.methods.length; j < methodsLen; j++) {
                       //对象中是否含有接口中定义的方法
                        var method = inter_face.methods[j];
                         if (!object[method] || typeof object[method] !== 'function') {
                                 throw new Error("Function Interface.ensureImplements: object " +
                                 "does not implements the " +
                                 inter_face.name +
                                 "interface.Method " +
                                 method +
                                 "was not found.");
                         }
                 }
         }
 }
 
 
/* ---------框架使用举例----------- */ 
 
//使用举例：演示定义2个接口及其约定子函数func 
var Composite = new Interface('Composite', ['add', 'remove', 'getChild']); 
var FormItem = new Interface('FormItem', ['save']); 

//使用举例：实现不同interface接口的类应该在内部按约定实现相关method
var CompositeForm = function(id, method, action) { 
　　//实现class Composite的三个func（add,remove,getChild);
　　// ....
　　//实现class FormItem的1个func（save）
　　//  
}; 

//使用举例：在某方法中用“ensureImplements”来验证class对interface约定的方法的实现情况 
function addForm(formInstance) { 
	//用下面的方法来做interface的验证;
　　ensureImplements(formInstance, Composite, FormItem); 
　　// This function will throw an error if a required method is not implemented. 
　　//... 
} 



