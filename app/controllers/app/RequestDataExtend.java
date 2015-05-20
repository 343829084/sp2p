package controllers.app;

import business.Bid;
import constants.SQLTempletes;
import controllers.app.common.Message;
import controllers.app.common.MsgCode;
import controllers.app.common.Severity;
import models.t_user_audit_items;
import models.v_front_all_bids;
import models.y_front_show_bids;
import models.y_subject_url;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.db.jpa.JPA;
import utils.ErrorInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
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
	
        jsonMap.put("repayment_res", bid.repayment_res);//还款来源  资金安全
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
        
        jsonMap.put("risk_control", bid.risk_control);//风险措施
        jsonMap.put("about_risk", bid.about_risk);//风险提示
        jsonMap.put("feeType", "免手续费");//手续费
        jsonMap.put("minTenderedSum", bid.minInvestAmount);//起购金额
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
        
        if(bid.isAgency==true){
        	jsonMap.put("bidType", 1);//0表示个人标,1表示机构标
        }else{
        	jsonMap.put("bidType", 0);
        }
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
        if(true==bid.isAgency){
        	jsonMap.put("project_introduction", bid.description);
        	jsonMap.put("company_info", bid.company_info);
        	jsonMap.put("borrowId", bid.id);
        	jsonMap.put("isAgency", bid.isAgency);
        	jsonMap.put("bidType", 1);//0表示个人标,1表示机构标
        }else{
        	jsonMap.put("project_introduction", bid.description);
        	jsonMap.put("borrowId", bid.id);
        	jsonMap.put("isAgency", bid.isAgency);
        	jsonMap.put("bidType", 0);//0表示个人标,1表示机构标
        	jsonMap.put("personInfo", bid.project_introduction);//项目简述字段替换成personInfo
        	jsonMap.put("realityName",bid.user.realityName.substring(0,1)+"**");
        	jsonMap.put("sex", bid.user.sex);
        	jsonMap.put("idNumber", bid.user.idNumber.substring(0,4)+"***");
        	jsonMap.put("cityName", bid.user.provinceName+bid.user.cityName);
        	jsonMap.put("educationName", bid.user.educationName);
        	jsonMap.put("maritalName", bid.user.maritalName);
        	jsonMap.put("houseName", bid.user.houseName);
        	jsonMap.put("carName", bid.user.carName);
        	
        }
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
        List<y_subject_url> userAuditList = new ArrayList<y_subject_url>();
        if (StringUtils.isBlank(borrowIdStr)) {
            jsonMap.put("error", -3);
            jsonMap.put("msg", "借款id有误");
            RequestData.messageUtil.setMessage(new Message(Severity.ERROR, MsgCode.LOAN_BID_DETAIL_QUERY_ID_FAIL), JSONObject.fromObject(jsonMap).toString());
            return RequestData.messageUtil.toStr();
        }
        long bidId = Long.parseLong(borrowIdStr);
        Bid bid = buildBid(bidId);
        
		String sql="select	a.id,b.name ,a.image_file_name from t_user_audit_items a ,t_dict_audit_items b  where a.mark=b.mark and a.status=2 and a.user_id=?";
		try{
			Query query = JPA.em().createNativeQuery(sql,y_subject_url.class);
            query.setParameter(1, bid.userId);
            userAuditList=query.getResultList();
		}catch (Exception e) { 
			e.printStackTrace();
		}
		jsonMap.put("error", -1);
		jsonMap.put("userAuditList", userAuditList);
        jsonMap.put("msg", "查询成功");
        jsonMap.put("repayment_res", bid.repayment_res);
        //jsonMap.put("about_risk", bid.about_risk); jsonMap.put("repayment_res", bid.repayment_res+ "/r/n" + bid.risk_control);
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
    /**
     * 收益方式接口
     * @param parameters
     * @return
     */
    public static String returnMode(Map<String, String> parameters){
    	Map<String, Object> jsonMap = new HashMap<String, Object>();
    	Map<String, Object> timeNode1 = new HashMap<String, Object>();//开售时间
    	Map<String, Object> timeNode2 = new HashMap<String, Object>();//起息时间
    	Map<String, Object> timeNode3 = new HashMap<String, Object>();//还本结息时间
    	DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
    	List timeNodeList=new LinkedList();
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
        jsonMap.put("feeType", "无");//手续费
        jsonMap.put("repayment_tips", "本项目只支持提前还款,到期后本金和收益自动归还到余额帐户");//还款提示
		jsonMap.put("paymentType", bid.repayment.id);
		jsonMap.put("paymentMode", bid.repayment.name);//还款方式
		timeNode1.put("nodeTime", dft.format(bid.time));//发布时间  开售时间//备注：添加预计时间字段后需要增加completeInd状态逻辑
		timeNode1.put("nodeName", "开售时间");
		timeNode1.put("completeInd", 1);//1表示有效，0表示无效
		timeNodeList.add(timeNode1);
		if(null!=bid.realInvestExpireTime){
			timeNode2.put("nodeTime", dft.format(bid.realInvestExpireTime));//实际满标时间
			timeNode2.put("nodeName", "起息日");
			timeNode2.put("completeInd", 1);
			timeNodeList.add(timeNode2);
		}else{
			timeNode2.put("nodeTime", dft.format(bid.investExpireTime));//预计满标时间 =起息日
			timeNode2.put("nodeName", "起息日");
			timeNode2.put("completeInd", 0);
			timeNodeList.add(timeNode2);
		}
		if(null!=bid.recentRepayTime){
			timeNode3.put("nodeTime", dft.format(bid.recentRepayTime));//还本结息日    //还款日   recentRepayTime 	period//借款期限      periodUnit //-1 年 0月  1日 
			timeNode3.put("nodeName", "还本结息日");
			if(5==bid.status){
				timeNode3.put("completeInd", 1);
			}else{
				timeNode3.put("completeInd", 0);
			}
			timeNodeList.add(timeNode3);
		}else{
			
			Date begindate=bid.investExpireTime;
			Calendar date = Calendar.getInstance();
			date.setTime(begindate);
			if(-1==bid.periodUnit){
				date.add(date.YEAR,bid.period);
				timeNode3.put("nodeTime", dft.format(date.getTime()));
			}else if(0==bid.periodUnit){
				date.add(date.MONTH,bid.period);
				timeNode3.put("nodeTime", dft.format(date.getTime()));
			}else if(1==bid.periodUnit){
				date.add(date.DAY_OF_YEAR,bid.period);
				timeNode3.put("nodeTime", dft.format(date.getTime()));
			}else{
				date.add(date.DAY_OF_YEAR,bid.period);
				timeNode3.put("nodeTime", dft.format(date.getTime()));
			}
			timeNode3.put("nodeName", "还本结息日");
			timeNode3.put("completeInd", 0);
			timeNodeList.add(timeNode3);
		}
		jsonMap.put("timeNodeList", timeNodeList);
    	return infoMessage(jsonMap, MsgCode.LOAN_BID_RETURN_MODE_SUCC);
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
