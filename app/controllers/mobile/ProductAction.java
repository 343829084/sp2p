package controllers.mobile;

import business.Bid;
import business.User;
import controllers.BaseController;
import controllers.interceptor.H5Interceptor;
import net.sf.json.JSONObject;
import play.Logger;
import play.mvc.With;
import utils.CaptchaUtil;

import java.util.Calendar;
import java.util.Date;
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

        int  period =  bid.period;
        int unit=bid.periodUnit;
        unit= unit==-1?unit*=-365:unit==0?unit*=30:unit;
        unit*=period;
        Calendar ca=Calendar.getInstance();
        ca.setTime(bid.time);
        ca.add(Calendar.DAY_OF_YEAR, unit);
        Date lastTime=ca.getTime();
        render(bid,jsonMap,lastTime);
    }


    public static void productBid(String bidId){
        Logger.info("current bid :" + bidId);
        if (bidId == null) {
            MainContent.moneyMatters();
        }
        Long newBidId = Long.valueOf(bidId);
        Bid bid = new Bid();
        bid.id = newBidId;

        if (bid.getId() == -1) {
            MainContent.moneyMatters();
        }

        String sign = bid.getSign();
        String uuid = CaptchaUtil.getUUID(); // 防重复提交UUID

        JSONObject map = new JSONObject();
        double availavleInvestedAmount = bid.amount - bid.hasInvestedAmount;
        map.put("availavleInvestedAmount", availavleInvestedAmount);
        map.put("currentUser", User.currUser());
        map.put("uuid", uuid);
        map.put("sign", sign);

        ProductAction.render(bid, map);
    }


}
