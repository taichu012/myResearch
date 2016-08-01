
<html>
   <head>
     <title>第一个Html文档</title>
   </head>
   
   //五.动态原型方式
   //构造函数中通过标志量让所有对象共享一个方法，而每个对象拥有自己的属性。

   
   <body>
     欢迎访问<a href="http://www.jb51.net">脚本之家</a>!
   </body>
   
<script type="text/javascript"> 

function Person(passwd) {
  this.username = new Array();;
  this.password = passwd;
  if(typeof Person.flag == "undefined") {
  //此块代码应该只在第一次调用的时候执行 alert("invoked"); 
  Person.prototype.getInfo = function() { 
  //这个方法定义在原型中，会被每一个对象所共同拥有
  alert(this.username + ", " + this.password); } 
  Person.flag = true;
  //第一次定义完之后，之后的对象就不需要再进来这块代码了 
 };} 
 
var p = new Person("123");
var p2 = new Person("456"); 
p.username.push("zhangsan");
p.username.push("end");
p2.username.push("lisi");
p2.username.push("end");
p.getInfo();
p2.getInfo(); 



//llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll
//在构造函数里定义数据属性和方法函数，前缀this
function people(){
        this.name = "Jeapedu",
        this.qq = 2603707788,
        this.info = info
};
function info(x){
        console.log(this.name + x + this.qq);
        alert(this.name+x+thus.qq);
};
var j = new people();
j.info("->");

</script>

</html> 


