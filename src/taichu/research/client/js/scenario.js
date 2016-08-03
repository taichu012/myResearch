<script type="text/javascript" src="oocommon.js"></script>


/* --------------------------------------*/
/* PART-A: Definition scenario    [BEGIN]*/
/* --------------------------------------*/

var toolbar=new Object();

	toolbar['gps']={name:'gps',key:'k_gps', buttons:new Object(){
		gps_funcA:new Object(){name:'name',key:'key',icons:itoolbar_icon['gps'].icons.gps_funcA}, 
		gps_funcB:new Object(){name:'name',key:'key',icons:itoolbar_icon['gps'].icons.gps_funcB}, 
		gps_funcC:new Object(){name:'name',key:'key',icons:itoolbar_icon['gps'].icons.gps_funcC},
		gps_funcD:new Object(){name:'name',key:'key',icons:itoolbar_icon['gps'].icons.gps_funcD}
		}};
		

function scenarioType(name,preCondition){
	this.name = name;
	this.preCondition = preCondition;
}

//定义空状态，仅用于定义和框架判断，无实际意义；
var SNO_NULL= new scenarioType('Scenario_Null',null); 
//定义空状态，无约束条件；
var SNO_EMPTY= new scenarioType('Scenario_Empty',SNO_NULL); 
//定义任何场景；
var SNO_ANY= new scenarioType('Scenario_Any',SNO_NULL); 
//定义独立场景，和其他场景无关，无前置场景要求(或任何场景都可作为前置场景，即SNO_ANY）
var SNO_INDPT= new scenarioType('Scenario_Independent',SNO_ANY);
//定义排他性场景，必须独自进入和退出，必须退出任何场景（前置场景为空即SNO_EMPTY）
var SNO_EXCL = new scenarioType('Scenario_Exclusive',SNO_EMPTY);
//定义举例：场景A,B的互相排斥，但和其他场景无关；
var SNO_NOT_A = new scenarioType('Scenario_NOT_A',SNO_ANY);
var SNO_NOT_B = new scenarioType('Scenario_NOT_B',SNO_ANY);
var SNO_A = new scenarioType('Scenario_A',SNO_NOT_B);
var SNO_B = new scenarioType('Scenario_A',SNO_NOT_A);

	
		
function scenario(name,type,pkey){
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







/* --------------------------------------*/
/* [END][END][END][END][END][END][END]   */
/* --------------------------------------*/