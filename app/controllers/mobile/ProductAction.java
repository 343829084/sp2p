package controllers.mobile;

import business.Bid;
import business.User;
import controllers.BaseController;
import controllers.interceptor.H5Interceptor;
import net.sf.json.JSONObject;
import play.Logger;
import play.mvc.Scope;
import play.mvc.With;
import utils.CaptchaUtil;

import java.util.Date;

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

        JSONObject jsonMap = new JSONObject();
        if(bid.repayment_res != null && bid.repayment_res.length() > 44){
            try{
                String project = bid.repayment_res.split(";")[0];
                jsonMap.put("repayment_res_short", project.substring(0,44) + "...");//短的资金安全
            }catch (Exception e) {
                jsonMap.put("repayment_res_short", bid.repayment_res.substring(0,44) + "...");//短的资金安全
            }
        }else{
            jsonMap.put("repayment_res_short", bid.repayment_res);
        }

        if(bid.description != null && bid.description.length() > 44){
            jsonMap.put("project_introduction_short", bid.description.substring(0,44) + "...");
        }else{
            jsonMap.put("project_introduction_short", bid.description);
        }

        boolean bidCanBuyFlag = false;//是否可以购买
        if (bid.status == 2) {//筹款中
            Long balanceTime = (bid.investExpireTime.getTime() - new Date().getTime()) / 1000;
            jsonMap.put("balanceTime", balanceTime);//倒计时时间

            double canInvestAmount = bid.amount - bid.hasInvestedAmount;
            if (canInvestAmount > 0) {
                bidCanBuyFlag = true;
            }
        }
        jsonMap.put("bidCanBuyFlag", bidCanBuyFlag);

        Logger.info(">>current bid status:" + bid.status);

        render(bid, jsonMap);
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
        map.put("userId", "front_"+Scope.Session.current().getId());

        ProductAction.render(bid, map);
    }


}
