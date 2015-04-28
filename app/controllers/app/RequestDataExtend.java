package controllers.app;

import business.Bid;
import constants.SQLTempletes;
import controllers.app.common.Message;
import controllers.app.common.MsgCode;
import controllers.app.common.Severity;
import models.v_front_all_bids;
import models.y_front_show_bids;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import play.db.jpa.JPA;
import utils.ErrorInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Created by loki on 3/24/15.
 */
public class RequestDataExtend {

    static String infoMessage(Map<String, Object> jsonMap, MsgCode msgCode) {
        RequestData.messageUtil.setMessage(new Message(Severity.INFO, msgCode), JSONObject.fromObject(jsonMap).toString());
        return RequestData.messageUtil.toStr();
    }

    static void extendBid(Map<String, Object> jsonMap, Bid bid) {
        jsonMap.put("company_info", bid.company_info);//相关企业信息
        jsonMap.put("repayment_res", bid.repayment_res);//还款来源
        jsonMap.put("risk_control", bid.risk_control);//风险措施
        jsonMap.put("about_risk", bid.about_risk);//风险提示
        jsonMap.put("feeType", "免手续费");//手续费
        jsonMap.put("minTenderedSum", bid.minAllowInvestAmount);//起购金额
        //PageBean<v_invest_records> pageBean = Invest.queryBidInvestRecords(1, 10, bidId,error);预留
        jsonMap.put("bid_record_total", "已投标人数： " + bid.investCount + "人");//投标记录人数
        jsonMap.put("is_new", 0);//0表示新手
        jsonMap.put("repayment_tips", "本项目只支持提前还款,到期后本金和收益自动归还到余额帐户");//还款提示
        jsonMap.put("hasInvestedAmount", bid.hasInvestedAmount);//已投金额
        jsonMap.put("remainderAmount", bid.amount - bid.hasInvestedAmount);//剩余金额
        jsonMap.put("time", bid.time + "");//发布世间
        jsonMap.put("realInvestExpireTime", bid.realInvestExpireTime + "");//实际满标时间

        jsonMap.put("sell_time", bid.time + "");//开售时间
        jsonMap.put("qixi_date", bid.time + "");//起息日
        jsonMap.put("repayall_date", bid.time + "");//还本结息日
        jsonMap.put("moneyback_time", bid.time + "");//预计资金到账时间
    }

    /**
     * 项目详情
     *
     * @param parameters
     * @return
     */
    public static String projectDetail(Map<String, String> parameters) {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        String borrowIdStr = parameters.get("borrowId");
        if (StringUtils.isBlank(borrowIdStr)) {
            jsonMap.put("error", -3);
            jsonMap.put("msg", "借款id有误");
            RequestData.messageUtil.setMessage(new Message(Severity.ERROR, MsgCode.LOAN_BID_DETAIL_QUERY_ID_FAIL), JSONObject.fromObject(jsonMap).toString());
            return RequestData.messageUtil.toStr();
        }
        long bidId = Long.parseLong(borrowIdStr);

        Bid bid = buildBid(bidId);
        jsonMap.put("error", -1);
        jsonMap.put("msg", "查询成功");
        jsonMap.put("project_introduction", bid.project_introduction);
        jsonMap.put("company_info", bid.company_info);
        jsonMap.put("borrowId", bid.id);

        return infoMessage(jsonMap, MsgCode.LOAN_BID_PROJECT_DETAIL_QUERY_SUCC);
    }

    /**
     * 资金安全
     *
     * @param parameters
     * @return
     */
    public static String fundSecurity(Map<String, String> parameters) {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        String borrowIdStr = parameters.get("borrowId");
        if (StringUtils.isBlank(borrowIdStr)) {
            jsonMap.put("error", -3);
            jsonMap.put("msg", "借款id有误");
            RequestData.messageUtil.setMessage(new Message(Severity.ERROR, MsgCode.LOAN_BID_DETAIL_QUERY_ID_FAIL), JSONObject.fromObject(jsonMap).toString());
            return RequestData.messageUtil.toStr();
        }
        long bidId = Long.parseLong(borrowIdStr);

        Bid bid = buildBid(bidId);
        jsonMap.put("error", -1);
        jsonMap.put("msg", "查询成功");
        jsonMap.put("repayment_res", bid.repayment_res + "/r/n" + bid.risk_control);
        jsonMap.put("about_risk", bid.about_risk);
        jsonMap.put("borrowId", bid.id);

        return infoMessage(jsonMap, MsgCode.LOAN_BID_FUND_SECURITY_QUERY_SUCC);
    }
    
    
    /**
     * 首页展示接口p2p产品接口
     *
     * @param parameters
     * @return
     */
    public static String showP2PProductOnHome(Map<String, String> parameters) {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        ErrorInfo error = new ErrorInfo();
        List<y_front_show_bids> bidList = new ArrayList<y_front_show_bids>();
		StringBuffer sql = new StringBuffer("");
		sql.append(SQLTempletes.SELECT);
		sql.append(SQLTempletes.V_FRONT_HOMEPAGE_SHOW_BID);
		try{
			Query query = JPA.em().createNativeQuery(sql.toString(),y_front_show_bids.class);
			bidList = query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			error.msg = "系统异常，给您带来的不便敬请谅解！";
			error.code = -1;
		}
		if(error.code < 0){
			jsonMap.put("error", "-4");
			jsonMap.put("msg",error.msg);
			return RequestDataExtend.errorMessage(jsonMap, MsgCode.LOAN_BID_SHOW_QUERY_FAIL);
		}		
        jsonMap.put("error", -1);
        jsonMap.put("msg", "查询成功");
        jsonMap.put("list",bidList);
        return infoMessage(jsonMap, MsgCode.LOAN_BID_SHOW_QUERY_SUCC);
    }

    static Bid buildBid(long bidId) {
        Bid bid = new Bid();
        bid.id = bidId;
        return bid;
    }

    static String errorMessage(Map<String, Object> jsonMap, MsgCode msgCode) {
        RequestData.messageUtil.setMessage(new Message(Severity.ERROR, msgCode), JSONObject.fromObject(jsonMap).toString());
        return  RequestData.messageUtil.toStr();
    }
}
