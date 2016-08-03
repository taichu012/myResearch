/*
Object： 
新建：var obj = new Object(); 或 var obj = {}; 
增加：obj[key] = value; (key为string) 
删除：delete obj[key]; 
遍历：for ( var key in obj ) obj[key]; 
* 
*    
1、使用new操作符后跟Object构造函数 如： 
 var person = new Object();  可以写成 var person = {}; 
 person.name = "kitty"; 
 person.age = 25; 
2、使用“对象字面量”表示法，如： 
 var person = { 
      name : "kitty", 
      age:25 
 }; 
3、工厂模式 方法： 
 var createPerson = function(name,age,job){
        var person = new Object();
        person.name = name;
        person.age = age;
        person.job = job;
        person.sayName = function(){
           alert(person.name);
        }
        return person;
     }
     var person1 = createPerson("zh","62","Doctor");
     person1.sayName(); 
4、构造函数模式  方法 
function Person(name,age,job){
     this.name = name;
     this.age = age;
     this.job = job;
     this.sayName = function(){
        alert(this.name);
     };
   };  
   var person1 = new Person("zhou",23,"test");
   person1.sayName(); 

*/

<script type="text/javascript" src="oocommon.js"></script>


/* --------------------------------------*/
/* PART-A: Definition Toolbars    [BEGIN]*/
/* --------------------------------------*/

/* Step-1: definition UI/ICON */

var toolbar_icon=new Object();
	toolbar_icon['gps']={icons:new Object(){
		iconIdx:'01',gps_funcA:new Object(){normal:'01',grey:'02',
		alart:'03',angle_0:'a_0',angle_15:'a_15',angle_345:'a_345',h:'64',w:'64'},
		
		iconIdx:'02',gps_funcB:new Object(){normal:'01',grey:'02',
		alart:'03',angle_0:'a_0',angle_15:'a_15',angle_345:'a_345',h:'64',w:'64'},
		
		iconIdx:'03',gps_funcC:new Object(){normal:'01',grey:'02',
		alart:'03',angle_0:'a_0',angle_15:'a_15',angle_345:'a_345',h:'64',w:'64'},
		
		iconIdx:'04',gps_funcD:new Object(){normal:'01',grey:'02',
		alart:'03',angle_0:'a_0',angle_15:'a_15',angle_345:'a_345',h:'64',w:'64'},
	}};
	
	/* todo:等待定义kakou的icon；并考虑结构的优化和取消重复的定义 */
	
/* Step-2: definition prototype */
var toolbar=new Object();

	toolbar['gps']={name:'gps',key:'k_gps', buttons:new Object(){
		gps_funcA:new Object(){name:'name',key:'key',icons:itoolbar_icon['gps'].icons.gps_funcA}, 
		gps_funcB:new Object(){name:'name',key:'key',icons:itoolbar_icon['gps'].icons.gps_funcB}, 
		gps_funcC:new Object(){name:'name',key:'key',icons:itoolbar_icon['gps'].icons.gps_funcC},
		gps_funcD:new Object(){name:'name',key:'key',icons:itoolbar_icon['gps'].icons.gps_funcD}
		}};

	toolbar['kakou']={name:'kakou',key:'k_kakou', buttons:new Object(){
		kakou_funcA:new Object(){name:'name',key:'key'}, 
		kakou_funcB:new Object(){name:'name',key:'key'}, 
		kakou_funcC:new Object(){name:'name',key:'key'},
		kakou_funcD:new Object(){name:'name',key:'key'},
		kakou_funcE:new Object(){name:'name',key:'key'},
		kakou_funcF:new Object(){name:'name',key:'key'},
		kakou_funcG:new Object(){name:'name',key:'key'}
		}};


	/* please append next toolbar */
	




/* --------------------------------------*/
/* [END][END][END][END][END][END][END]   */
/* --------------------------------------*/

	
	

/* --------------------------------------*/
/* PART-B: Definition Menus              */
/* --------------------------------------*/

/* B-1: definition prototype */

function Menu(name,key,pkey){
     this.name = name;
     this.key = key;
     this.pKey = pKey;
     this.setEnable = function(){
        /* TODO: 级联设定自己和所辖子menu都enable */
     };
   };  
   
   /* TODO:将菜单key单独做成一个数据结构，然后用他来初始化menu结构，
    * 因为menu结构可能会有name，灰掉与否，起效与否，隐藏与否，对应icon，权限等陪伴属性，
    * 但是menu的结构是固定的，需要先于menu来定义，然后用他定义陪伴属性的真正的menu；
    * 考虑！！！即便达成以上，这个menu是否还是不能用for来生成？因为数据或属性都是不同的！
    */
    
   	var menu_funcA= new Menu('A','K_A',null);
	menu['funcA.1']={name:'A_1',key:'k_A_1',pKey:menu['funcA'].key};
	menu['funcA.2']={name:'A_2',key:'k_A_2',pKey:menu['funcA'].key};
   
   var person1 = new Person("zhou",23,"test");
   person1.sayName(); 


var menu=new Object();

	menu['funcA']={name:'A',key:'k_A','pKey':null};
	menu['funcA.1']={name:'A_1',key:'k_A_1',pKey:menu['funcA'].key};
	menu['funcA.2']={name:'A_2',key:'k_A_2',pKey:menu['funcA'].key};
	/* please append next menu */


/* --------------------------------------*/
/* [END][END][END][END][END][END][END]   */
/* --------------------------------------*/

