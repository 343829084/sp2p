package controllers.mobile;

import java.util.HashMap;
import java.util.Map;

import business.Bid;
import business.User;
import controllers.BaseController;
import play.Logger;

/**
 * <p>Project: com.shovesoft.sp2p</p>
 * <p>Title: ProductAction.java</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2014 Sunlights.cc</p>
 * <p>All Rights Reserved.</p>
 *
 * @author <a href="mailto:jiaming.wang@sunlights.cc">wangJiaMing</a>
 */
public class ProductAction extends BaseController {

    public static void productDetail(){
        if (params.get("bidId") == null) {
            MainContent.moneyMatters();
        }
        Long bidId = Long.valueOf(params.get("bidId"));
        Logger.info(">>bidId:" + bidId);
        Bid bid = new Bid();
        bid.id = bidId;

        Logger.info(">>current bid status:" + bid.status);

        render(bid);
    }
    
    
    public static void productBid(){
    	Bid bid = new Bid();
    	bid.setId(Integer.parseInt(params.get("bidId")));
    	if (bid.id == -1 ) {
    		MainContent.moneyMatters();
    	}
    	double availavleInvestedAmount = bid.amount - bid.hasInvestedAmount;
    	Map map = new HashMap();
    	map.put("availavleInvestedAmount", availavleInvestedAmount);
    	
    	map.put("currentUser", User.currUser());
    	
    	ProductAction.render(bid, map);
    }
}
