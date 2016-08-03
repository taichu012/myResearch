/* CONST definition   */
//TODO

/* JS Object定义举例 */
function Person(passwd) {
  this.username = new Array(); //字段后续用push初始化
  this.password = passwd; //字段new的时候通过参数初始化
  this.printPW2 = printPW2; //在class外部定义方法
  //在class内部定义方法
  if(typeof Person.printPW == "undefined") {
  	//此块代码应该只在第一次调用的时候执行，只invoke一次
  	alert("object.js| Person.printPW is invoked"); 
  	Person.prototype.printPW = function() { 
  	//这个方法定义在原型中，会被每一个对象所共同拥有
  	alert("object.js| ("+this.username + ") password is (" + this.password+")."); } 
  	Person.printPW = true;
 	};
 };
 
 /* 在class外面（后面）定义class的方法 */
 if(typeof Person.printPW2 == "undefined") {
 	//此块代码应该只在第一次调用的时候执行，只invoke一次
  	alert("object.js| Person.printPW2 is invoked"); 
 	function printPW2(x){
        //console.log(this.username + x + this.password);
        alert("object.js| "+this.username + x + this.password);
 	};
 	Person.printPW2=true;
};

/* test Object的新建，赋值，方法调用 */
var p1 = new Person("pw123");
var p2 = new Person("pw456"); 
p1.username.push("张三");
p1.username.push("zhangsan");
p2.username.push("李四");
p2.username.push("lisi");

p1.printPW2("的密码是");
p1.printPW();

p2.printPW2("'pw =");
p2.printPW(); 



