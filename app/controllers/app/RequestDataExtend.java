package controllers.app;

import business.Bid;
import business.DealDetail;
import business.Payment;
import business.User;
import com.google.gson.JsonObject;
import constants.Constants;
import constants.SQLTempletes;
import constants.UserEvent;
import controllers.app.common.Message;
import controllers.app.common.MessageUtil;
import controllers.app.common.MsgCode;
import controllers.app.common.Severity;
import models.t_users;
import models.y_front_show_bids;
import models.y_subject_url;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.cache.Cache;
import play.db.jpa.JPA;
import play.libs.WS;
import play.mvc.Scope;
import utils.CacheManager;
import utils.CaptchaUtil;
import utils.ErrorInfo;

import javax.persistence.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by loki on 3/24/15.
 */
public class RequestDataExtend {

    static String infoMessage(Map<String, Object> jsonMap, MsgCode msgCode) {
        RequestData.messageUtil.setMessage(new Message(Severity.INFO, msgCode), jsonMap == null ? null : JSONObject.fromObject(jsonMap).toString());
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

    public static String getAuthToken(Map<String, String> params){
        ErrorInfo error = new ErrorInfo();
        String token = params.get("FPtoken");//TODO fp ?
        String name = params.get("mobile");
        if (StringUtils.isEmpty(name)) {
            MessageUtil.getInstance().setMessage(new Message(Severity.ERROR, MsgCode.ACCESS_FAIL));
            return MessageUtil.getInstance().toStr();
        }

        User user = new User();
        user.name = name;
        if (user.getId() == -1) {
            return errorMessage(null, MsgCode.AUTH_TOKEN_FAIL);
        }
        setAppCurrUser(user);

        DealDetail.userEvent(user.getId(), UserEvent.LOGIN, "登录成功", error);
        utils.Cache cache = CacheManager.getCacheInfo("online_user_" + user.getId() + "");

        if (null == cache) {
            cache = new utils.Cache();
            long timeout = 1800000;//单位毫秒
            CacheManager.putCacheInfo("online_user_" + user.getId(), cache, timeout);
        }

        t_users t_users = user.queryUser2ByUserId(user.getId(), error);

        String p2pRealNameFlag = t_users.id_number == null ? "0" : "1";
        String p2pAccountFlag = t_users.ips_acct_no == null ? "0" : "1";

        Map<String, String> valueMap = new HashMap<String, String>();
        valueMap.put("p2pRealNameFlag", p2pRealNameFlag);
        valueMap.put("p2pAccountFlag", p2pAccountFlag);

        if (error.code == 0) {//表示成功
            MessageUtil.getInstance().setMessage(new Message(Severity.INFO, MsgCode.AUTH_TOKEN_SUCC), valueMap);
        }else{
            MessageUtil.getInstance().setMessage(new Message(Severity.ERROR, MsgCode.AUTH_TOKEN_FAIL), valueMap);
        }

        return  MessageUtil.getInstance().toStr();
    }

    public static String removeAuthToken(){
        User user = User.currUser();

        Logger.info("当前退出authToken：" + Scope.Session.current().getAuthenticityToken());
        if (user != null) {
            Logger.info("当前退出user：userId_"+user.id);
            ErrorInfo error = new ErrorInfo();
            user.logout(error);
        }

        return infoMessage(null, MsgCode.AUTH_TOKEN_CLEAN_SUCC);
    }

    private static void setAppCurrUser(User user){
        if (Scope.Session.current() == null) {
            return;
        }

        String encryString = Scope.Session.current().getId();
        Scope.Session.current().getAuthenticityToken();
        //设置用户凭证
        Cache.set("front_" + encryString, user.getId(), Constants.CACHE_TIME_HOURS_12);
        //设置用户登录成功信息
        Cache.set("userId_"+user.getId(), user, Constants.CACHE_TIME_HOURS_12);

        Logger.info("当前登录人：userId_"+user.getId());
        Logger.info("当前创建authToken：" + Scope.Session.current().getAuthenticityToken());

    }

    private static void getAuthenticationInfo(String mobile, Map<String, String> params){
        Map<String,String> baseParams = new HashMap<String, String>();
        baseParams.put("mobile", mobile);
        WS.HttpResponse httpResponse = WS.url(Constants.FP_AUTHENTICATION).setParameters(baseParams).get();

        JsonObject jsonObject = null;
        if(httpResponse.getStatus().intValue() == HttpStatus.SC_OK){
            jsonObject = httpResponse.getJson().getAsJsonObject();
            String severity = jsonObject.get("message").getAsJsonObject().get("severity").getAsString();
            if ("0".equals(severity)) {
                String realName = jsonObject.get("value").getAsJsonObject().get("userName").getAsString();
                String idCard = jsonObject.get("value").getAsJsonObject().get("idCardNo").getAsString();
                String email = jsonObject.get("value").getAsJsonObject().get("email").getAsString();

                params.put("realName", realName);
                params.put("idCard", idCard);
                if (StringUtils.isNotEmpty(email)) {
                    params.put("email", email);
                }
            }
        }
    }

    /**
     * 完善用户资料接口
     * @param parameters
     * realName
     * idNo
     * email
     */
    public static void editUserInfo(Map<String, String> parameters) {
        User user = User.currUser();
        if (user == null) {
            MessageUtil.getInstance().setMessage(new Message(Severity.ERROR, MsgCode.CURRENT_USER_FAIL));
            return ;
        }

        if (user.id < 0) {
            MessageUtil.getInstance().setMessage(new Message(Severity.ERROR, MsgCode.SAVE_USER_INFO_FAIL), "该用户不存在");
            return ;
        }

        String certify = parameters.get("certify");//是否实名认证      0未  1已
        if ("1".equals(certify)) {
            try {
                getAuthenticationInfo(user.mobile, parameters);
            }catch (Exception e){
                e.printStackTrace();
                Logger.error("调用fp获取实名认证信息异常", e.getMessage());
                MessageUtil.getInstance().setMessage(new Message(Severity.ERROR, MsgCode.SAVE_USER_INFO_FAIL), "未获取到实名认证信息");
                return ;
            }
        }

        ErrorInfo error = new ErrorInfo();
        String realName = parameters.get("realName");
        String idNo = parameters.get("idNo");
        String email = parameters.get("email");

        User newUser = new User();
        newUser.id = user.id;
        newUser.realityName = realName;
        newUser.idNumber = idNo;
        if (email != null){//若传入参数则替换  否则默认数据库中的数据不作更新
            newUser.email = email;
        }
        newUser.appEditUser(user,error);

        if(error.code != 0){
            MessageUtil.getInstance().setMessage(new Message(Severity.ERROR, MsgCode.SAVE_USER_INFO_FAIL),  error.msg);
            return ;
        }

        MessageUtil.getInstance().setMessage(new Message(Severity.INFO, MsgCode.SAVE_USER_INFO_SUCC));
    }


    /**
     * 账户余额查询--金豆荚
     * @return
     */
    public static String queryForAccBalance() {
        User user = User.currUser();
        if (user == null) {
            MessageUtil.getInstance().setMessage(new Message(Severity.ERROR, MsgCode.CURRENT_USER_FAIL));
            return MessageUtil.getInstance().toStr();
        }

        String strJson = Payment.queryForAccBalanceFromIps(user);
        if (strJson == null) {
            MessageUtil.getInstance().setMessage(new Message(Severity.ERROR, MsgCode.QUERY_ACC_BALANCE_FAIL));
            return MessageUtil.getInstance().toStr();
        }

        JSONObject jsonObj = JSONObject.fromObject(strJson);

        String pErrCode = (String)jsonObj.get("pErrCode");
        if ("MG00000F".equals(pErrCode)) {//成功
            JSONObject obj = new JSONObject();
            obj.put("userName", user.name);
            obj.put("balance", user.balanceDetail.user_amount);//系统余额
            obj.put("pFreeze", user.balanceDetail.freeze);//系统冻结
            obj.put("pBalance", formatMoney((String)jsonObj.get("pBalance")));//托管余额
            obj.put("pLock", formatMoney((String)jsonObj.get("pLock")));//托管冻结
            obj.put("pAccBalance", formatMoney((String)jsonObj.get("pAccBalance")));//托管账户余额 （总额）

            MessageUtil.getInstance().setMessage(new Message(Severity.INFO, MsgCode.QUERY_ACC_BALANCE_SUCC), obj);
        }else{
            String pErrMsg = (String)jsonObj.get("pErrMsg");
            MessageUtil.getInstance().setMessage(new Message(Severity.ERROR, MsgCode.QUERY_ACC_BALANCE_FAIL, pErrMsg));
        }

        return MessageUtil.getInstance().toStr();
    }

    private static String formatMoney(String money){
        if (StringUtils.isEmpty(money)) {
            return "0.00";
        }

        String newMoney = money.replaceAll(",", "");

        return newMoney;
    }

    /**
     * 投标获取sign--金豆荚
     * @return
     */
    public static String invest(Map<String, String> parameters) {
        if (parameters.get("bidId") == null) {
            MessageUtil.getInstance().setMessage(new Message(Severity.ERROR, MsgCode.ACCESS_FAIL));
            return MessageUtil.getInstance().toStr();
        }
        Long bidId = Long.valueOf(parameters.get("bidId"));
        Bid bid = new Bid();
        bid.id = bidId;
        String sign = bid.getSign();
        String uuid = CaptchaUtil.getUUID(); // 防重复提交UUID

        JSONObject obj = new JSONObject();
        obj.put("uuid", uuid);
        obj.put("sign", sign);

        MessageUtil.getInstance().setMessage(new Message(Severity.INFO, MsgCode.SEARCH_INVEST_SUCC), obj);
        return MessageUtil.getInstance().toStr();
    }
}
