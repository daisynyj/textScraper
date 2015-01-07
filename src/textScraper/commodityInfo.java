package textScraper;

public class commodityInfo {
	private String title;
	private String Price;
	private String shipPrice;
	private String vendor;
	
	public commodityInfo(){
		title = null;
		Price = null;
		shipPrice = null;
		vendor = null;
	}
	
	public commodityInfo(String t, String p, String sh, String v){
		title = t;
		Price = p;
		shipPrice = sh;
		vendor = v;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getPrice(){
		return Price;
	}
	
	public String getShipping(){
		return shipPrice;
	}
	
	public String getVendor(){
		return vendor;
	}
	
	public void setTitle( String t){
		title = t;
	}
	
	public void setPrice( String p){
		Price = p;
	}
	
	public void setShipping( String sh){
		shipPrice = sh;
	}
	
	public void setVendor( String v){
		vendor = v;
	}
}
