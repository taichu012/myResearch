package taichu.research.network.netty4.test.VehiclePassingRecordCollector.entity;

public class VehiclePassingRecord {

	
	public String getvLicenseType() {
		return vLicenseType;
	}
	public void setvLicenseType(String vLicenseType) {
		this.vLicenseType = vLicenseType;
	}
	public String getvLicenseNbr() {
		return vLicenseNbr;
	}
	public void setvLicenseNbr(String vLicenseNbr) {
		this.vLicenseNbr = vLicenseNbr;
	}
	public String getvPic1Path() {
		return vPic1Path;
	}
	public void setvPic1Path(String vPic1Path) {
		this.vPic1Path = vPic1Path;
	}
	public String getvPic2Path() {
		return vPic2Path;
	}
	public void setvPic2Path(String vPic2Path) {
		this.vPic2Path = vPic2Path;
	}
	public String getvPic3Path() {
		return vPic3Path;
	}
	public void setvPic3Path(String vPic3Path) {
		this.vPic3Path = vPic3Path;
	}
	public String getvLicensepPicPath() {
		return vLicensepPicPath;
	}
	public void setvLicensepPicPath(String vLicensepPicPath) {
		this.vLicensepPicPath = vLicensepPicPath;
	}
	public String getvLogoPicPath() {
		return vLogoPicPath;
	}
	public void setvLogoPicPath(String vLogoPicPath) {
		this.vLogoPicPath = vLogoPicPath;
	}
	public String getvLogoWords() {
		return vLogoWords;
	}
	public void setvLogoWords(String vLogoWords) {
		this.vLogoWords = vLogoWords;
	}
	public String getvLicenseColor() {
		return vLicenseColor;
	}
	public void setvLicenseColor(String vLicenseColor) {
		this.vLicenseColor = vLicenseColor;
	}
	public String getvColor() {
		return vColor;
	}
	public void setvColor(String vColor) {
		this.vColor = vColor;
	}
	public String getvBrand() {
		return vBrand;
	}
	public void setvBrand(String vBrand) {
		this.vBrand = vBrand;
	}
	public String getvModel() {
		return vModel;
	}
	public void setvModel(String vModel) {
		this.vModel = vModel;
	}
	public String getvManufacturer() {
		return vManufacturer;
	}
	public void setvManufacturer(String vManufacturer) {
		this.vManufacturer = vManufacturer;
	}
	public String getvProductionYear() {
		return vProductionYear;
	}
	public void setvProductionYear(String vProductionYear) {
		this.vProductionYear = vProductionYear;
	}
	public String getVpCollectDate() {
		return vpCollectDate;
	}
	public void setVpCollectDate(String vpCollectDate) {
		this.vpCollectDate = vpCollectDate;
	}
	public String getVpCollectAddrID() {
		return vpCollectAddrID;
	}
	public void setVpCollectAddrID(String vpCollectAddrID) {
		this.vpCollectAddrID = vpCollectAddrID;
	}
	public String getVpCollectAddr() {
		return vpCollectAddr;
	}
	public void setVpCollectAddr(String vpCollectAddr) {
		this.vpCollectAddr = vpCollectAddr;
	}
	public String getVpCollectAgent() {
		return vpCollectAgent;
	}
	public void setVpCollectAgent(String vpCollectAgent) {
		this.vpCollectAgent = vpCollectAgent;
	}
	public String getVpDirection() {
		return vpDirection;
	}
	public void setVpDirection(String vpDirection) {
		this.vpDirection = vpDirection;
	}
	public String getVpCollectDataSource() {
		return vpCollectDataSource;
	}
	public void setVpCollectDataSource(String vpCollectDataSource) {
		this.vpCollectDataSource = vpCollectDataSource;
	}
	public String getVpDeviceCode() {
		return vpDeviceCode;
	}
	public void setVpDeviceCode(String vpDeviceCode) {
		this.vpDeviceCode = vpDeviceCode;
	}
	public String getVpLaneNbr() {
		return vpLaneNbr;
	}
	public void setVpLaneNbr(String vpLaneNbr) {
		this.vpLaneNbr = vpLaneNbr;
	}
	public String getVpvSpeed() {
		return vpvSpeed;
	}
	public void setVpvSpeed(String vpvSpeed) {
		this.vpvSpeed = vpvSpeed;
	}
	public String getAiFlag() {
		return aiFlag;
	}
	public void setAiFlag(String aiFlag) {
		this.aiFlag = aiFlag;
	}
	public String getIsIllegal() {
		return isIllegal;
	}
	public void setIsIllegal(String isIllegal) {
		this.isIllegal = isIllegal;
	}
	public String getAiPicPath() {
		return aiPicPath;
	}
	public void setAiPicPath(String aiPicPath) {
		this.aiPicPath = aiPicPath;
	}
	public String getAiColor() {
		return aiColor;
	}
	public void setAiColor(String aiColor) {
		this.aiColor = aiColor;
	}
	
	
	//TODO: Detail length of every fields NEED definition later!
	// vehicle info
	private String vLicenseType = null; // is '车牌类型'; //VARCHAR2（2）
	private String vLicenseNbr = null; // is '车牌号码'; //VARCHAR2（16）
	private String vPic1Path = null; // is '图片路径1'; //VARCHAR2（400）not null
	private String vPic2Path = null; // is '图片路径2'; //VARCHAR2（400）
	private String vPic3Path = null; // is '图片路径3'; //VARCHAR2（400）
	private String vLicensepPicPath = null; // is '车牌图片'; //VARCHAR2（400）
	private String vLogoPicPath = null; // is '车标图片'; //VARCHAR2（400）
	private String vLogoWords = null; // is '车标文字'; //VARCHAR2（32）
	private String vLicenseColor = null; // is '车牌颜色'; //VARCHAR2（8）
	private String vColor = null; // is '车身颜色'; //VARCHAR2（8）
	private String vBrand = null; // is '车辆品牌'; //NUMBER（3）
	private String vModel = null; // is '车辆型号'; //NUMBER（7）
	private String vManufacturer = null; // is '生产厂家'; //NUMBER（4）
//	private String vProductionDate = null; // is '生产年份'; //VARCHAR2（50）
	private String vProductionYear = null; // is '生产年份'; //VARCHAR2（50）

	// vehicle Passing & kakou info
	private String vpCollectDate = null; // is '采集时间'; ， DATE not null, 
	private String vpCollectAddrID = null; // is '卡口地点编号'; //VARCHAR2（12） //应该就是卡口ID
	private String vpCollectAddr = null; // is '采集地点名称'; //VARCHAR2（200）
	private String vpCollectAgent = null; // is '采集部门编号'; //VARCHAR2（12）not												
	private String vpDirection = null; // is '方向'; //NUMBER（1）
	private String vpCollectDataSource = null; // is '数据来源'; //VARCHAR2（2）
	private String vpDeviceCode = null; // is '设备编号'; //VARCHAR2（18）
	private String vpLaneNbr = null; // is '车道编号';//VARCHAR2（2）
	private String vpvSpeed = null; // is '车辆速度'; //NUMBER（3）not null

	// vehicle manager info =null; //
	private String aiFlag = null; // is '年检、保险、绿色环保标志'; //CHAR(1) //ai=ann inspection
	private String isIllegal =null; // s '是否违法'; //CHAR(1) //is illegal
	private String aiPicPath = null; // is '年检、保险、绿色环保标志图片'; //VARCHAR2（400）
	private String aiColor = null; // is '年检颜色';// VARCHAR2(16)
}
