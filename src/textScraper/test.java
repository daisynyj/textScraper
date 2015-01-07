package textScraper;

import java.util.ArrayList;

public class test {

	public static void main(String[] args){
		htmlParser hp = new htmlParser();
		//hp.itemInfo("book", 2);
		if (args.length > 0) {
			if(args.length == 1){
				hp.itemNumber(args[0]);
				String numItem = hp.getItemNumber();
				System.out.println(numItem);
			}
			if(args.length == 2){
				Integer pagenumber = Integer.parseInt(args[1]);
				hp.itemInfo(args[0], pagenumber);
				ArrayList<commodityInfo> ci = hp.getCommodityInfo();
				for(int i=0;i<ci.size();i++){
					System.out.println("Title:"+" "+ci.get(i).getTitle());
					System.out.println("Price:"+" "+ci.get(i).getPrice());
					System.out.println("Shipping information:"+" "+ci.get(i).getShipping());
					System.out.println("Vendor"+" "+ci.get(i).getVendor());
					System.out.println();
				}
			}
		}
		System.exit(0);
	}
	
	
}
