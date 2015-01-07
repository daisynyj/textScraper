package textScraper;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.htmlparser.*;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

public class htmlParser {
	
	private String numItems;
	private ArrayList<commodityInfo> commoditylist;
	Parser parser;
	
	public String getItemNumber(){
		return numItems;
	}
	
	public ArrayList<commodityInfo> getCommodityInfo(){
		return commoditylist;
	}
	
	// if the keywords is provided find the web page we need.
	public String buildURL(String keywords){
		String url = new String();
		url = "http://www.shopping.com/"+keywords+
				"/products?CLT=SCH&KW="+keywords;
		return url;
	}
	
	// if the keywords and page number are given find the url 
	public String buildURL(String keywords, int pageNumber){
		String url = new String();
		url = "http://www.shopping.com/"+keywords+
				"/products?CLT=SCH&KW="+keywords;
		if(pageNumber == 1){
			return url;
		}
		else{
			// TODO: parse the number of page
			try{
				parser = new Parser((HttpURLConnection)(new URL(url)).openConnection());
				// TODO: find the number of items
			}catch (Exception e){
					System.out.println("Cannot open website!");
					return "";
				}
			NodeFilter filter1 = new TagNameFilter("a");
			NodeFilter filter2 = new HasAttributeFilter("name","PLN");
			NodeFilter filter = new AndFilter(filter1,filter2);
			
			try{
				NodeList nodeList = parser.extractAllNodesThatMatch (filter);
				Node node = nodeList.elementAt(0);
				String returntext = node.getText();
				int start = returntext.indexOf("href=")+6;// start with "/"
				int end = returntext.indexOf("?KW="+keywords); // end before ""
				String sub = returntext.substring(start, end);
				//System.out.println(sub);
				int notDigit=0;
				for(int i=sub.length()-1;i>=0;i--){
					if(!Character.isDigit(sub.charAt(i))){
						notDigit = i;
						break;
					}
				}
				String subdir = sub.substring(0,notDigit+1);
				url ="http://www.shopping.com"+subdir+pageNumber+"?KW="+keywords;
				//System.out.println(url);
			}catch(Exception e){
				System.out.println("can not parse the information");
				return "";
			}
		}
		return url;
	}
	
	
	
	
	public void itemNumber (String itemTitle){
		//build the URL
		String url = buildURL(itemTitle);
		try{
			parser = new Parser((HttpURLConnection)(new URL(url)).openConnection());
			// TODO: find the number of items
		}catch (Exception e){
				System.out.println("Cannot open website!");
				return;
			}
		NodeFilter filter1 = new TagNameFilter("span");
		NodeFilter filter2 = new HasAttributeFilter("name");
		NodeFilter filter = new AndFilter(filter1,filter2);
		try{
			NodeList nodeList = parser.extractAllNodesThatMatch (filter);
			if(nodeList.size() != 1 ) 
				throw new Exception();
			Node node = nodeList.elementAt(0);
			String returnNumber = node.getText();
			// ArrayList<String> number = new ArrayList<String>();
			String number = new String();
			for(int i=0;i<returnNumber.length();i++){
				if(Character.isDigit(returnNumber.charAt(i))){
					char c =(char)returnNumber.charAt(i);
					number += c;
				}
			}
			numItems = number;
		}catch(Exception e){
			System.out.println("can not parse the information");
			return;
		}
		
	}
	
	
	
	// find the titles
	public void getTitles(String itemTitle,int pageNumber){
		String url = buildURL(itemTitle,pageNumber);
		try{
			parser = new Parser((HttpURLConnection)(new URL(url)).openConnection());
			//System.out.println(url);
		}catch (Exception e){
				System.out.println("Cannot open website!");
				return;
			}
		// find the names
		NodeFilter[]  filters = new NodeFilter[2];   
		filters[0] = new TagNameFilter("span");
		filters[1] = new HasAttributeFilter("qltarget");
		NodeFilter filter = new AndFilter(filters);
		try{
			NodeList nodeList = parser.extractAllNodesThatMatch (filter);
			//System.out.println(nodeList.size());
			for(int i =0;i<nodeList.size();i++){
				//commodityInfo ci = new commodityInfo();
				Node node = nodeList.elementAt(i);
				String returntext = node.getText();
				int start = returntext.indexOf("qltarget=")+11;
				int end = returntext.indexOf("/quicklook");
				String sub = returntext.substring(start, end);
				String[] ss =sub.split("/");
				String rawtitle = ss[0];
				String[] arr = rawtitle.split("-|_");
				String title = new String();
				for(int j=0;j<arr.length;j++){
					title +=arr[j];
					title +=" ";
				}
				//System.out.println(title);
				commoditylist.get(i).setTitle(title);
				//commoditylist.add(ci);
			}	
		}catch(Exception e){
			System.out.println("can not parse the information1");
			return;
		}
	}
	
	
	
	
	public void getInfo(String itemTitle, int pageNumber,int count){
		String url = buildURL(itemTitle,pageNumber);
		try{
			parser = new Parser((HttpURLConnection)(new URL(url)).openConnection());
		}catch (Exception e){
				System.out.println("Cannot open website!");
				return;
			}
		NodeFilter filter = new HasAttributeFilter("class","centerInnerPanel");
		try{
			NodeList nodelist = parser.extractAllNodesThatMatch (filter);
		   Node node = nodelist.elementAt(0);
		   String html = node.getChildren().toHtml();
		   parser = Parser.createParser(html, parser.getEncoding());
			NodeList nodes = new NodeList(); 
			for(int i=1;i<=count;i++){
				try{
					parser = new Parser((HttpURLConnection)(new URL(url)).openConnection());
				}catch (Exception e){
						System.out.println("Cannot open website!");
						return;
				}
				NodeFilter filterss = new AndFilter(new TagNameFilter("div"),new HasAttributeFilter("id","quickLookItem-"+i));
				if(nodes!=null) nodes.removeAll();
				nodes= parser.extractAllNodesThatMatch(filterss);
				//System.out.println(nodes.size());
				String info = nodes.elementAt(0).getChildren().asString();
				int start=info.indexOf("&#36;")+5;
				int ind = start;
				while(info.charAt(ind)!='\n'){
					ind++;
				}
				String price = info.substring(start,ind);
				//System.out.println(info);
				//System.out.println(i);
				commoditylist.get(i-1).setPrice(price);
				//System.out.println(price);
				start = ind;
				while(!Character.isLetterOrDigit(info.charAt(ind))){
					start++;
					ind++;
				}
				while(info.charAt(ind)!='\n'){
					ind++;
				}
				String vendor = info.substring(start, ind);
				commoditylist.get(i-1).setVendor(vendor);
				//System.out.println(vendor);
				String shipping ;
				if(info.indexOf("Free Shipping")!= -1){
					shipping = "Free Shipping";
				}
				else{
					if(info.indexOf("No Shipping Info")!= -1){
					shipping = "No Shipping Info";
					}
					else{
						start = info.indexOf("&#36;", ind)+5;
						int end = info.indexOf("shipping",start)-1;
						shipping = info.substring(start, end);
					}
				}
				//System.out.println(shipping);
				commoditylist.get(i-1).setShipping(shipping);
			}
					//System.out.println(count);
				}catch(Exception e){
					System.out.println("can not parse the information3");
					return;
				}
	}
	
	public void itemInfo (String itemTitle, int pageNumber){
		commoditylist = new ArrayList<commodityInfo>();
		String url = buildURL(itemTitle,pageNumber);
		try{
			parser = new Parser((HttpURLConnection)(new URL(url)).openConnection());
			//System.out.println(url);
		}catch (Exception e){
				System.out.println("Cannot open website!");
				return;
			}
		// the number of items on this page ==========================================
				NodeFilter[]  filters = new NodeFilter[2];   
				filters[0] = new TagNameFilter("div");
				filters[1] = new HasAttributeFilter("id");
				NodeFilter filter = new AndFilter(filters);
				try{
					NodeList nodeList = parser.extractAllNodesThatMatch (filter);
					//System.out.println(nodeList.size());
					int count=0;
					for(int i =0;i<nodeList.size();i++){
						Node node = nodeList.elementAt(i);
						String returntext = node.getText();
						if(returntext.indexOf("quickLookItem")!=-1) {
							commodityInfo ci = new commodityInfo();
							commoditylist.add(ci);
							count++;
						}
					}
					//System.out.println(count);
					getTitles(itemTitle,pageNumber);
					getInfo(itemTitle,pageNumber,count);
				}catch(Exception e){
					System.out.println("can not parse the information4");
					return;
				}
	}
}
