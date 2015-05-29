package controllers.mobile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import business.Bid;
import business.User;
import controllers.BaseController;
import play.Logger;
import utils.JSONUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * <p>Project: com.shovesoft.sp2p</p>
 * <p>Title: ProductAction.java</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2014 Sunlights.cc</p>
 * <p>All Rights Reserved.</p>
 *
 * @author <a href="mailto:jiaming.wang@sunlights.cc">wangJiaMing</a>
 */
@With(H5Interceptor.class)
public class ProductAction extends BaseController {

    public static void productDetail(){
        if (params.get("bidId") == null) {
            MainContent.moneyMatters();
        }
        Long bidId = Long.valueOf(params.get("bidId"));
        Logger.info(">>bidId:" + bidId);
        Bid bid = new Bid();
        bid.id = bidId;
        if (bid.getId() == -1) {
            MainContent.moneyMatters();
        }

        Map jsonMap=new HashMap();
        if(null!=bid.repayment_res && bid.repayment_res.length()>44){
            try{
                String project=bid.repayment_res.split(";")[0];
                jsonMap.put("repayment_res_short",project.substring(0,44));//短的资金安全
            }catch (Exception e) {
                jsonMap.put("repayment_res_short",bid.repayment_res.substring(0,44));//短的资金安全
            }
        }else{
            jsonMap.put("repayment_res_short",bid.repayment_res);
        }

        if(null!=bid.description && bid.description.length()>44){
            jsonMap.put("project_introduction_short",bid.description.substring(0,44));
        }else{
            jsonMap.put("project_introduction_short",bid.description);
        }

        Logger.info(">>current bid status:" + bid.status);

        render(bid,jsonMap);
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
    	
    	String jsonBidInstance = null;
    	try {
			jsonBidInstance = JSONUtils.printObject(bid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	ProductAction.render(bid, map);
    }
    
    public static void bidSuccess () {
    	ProductAction.render();
    }
}
