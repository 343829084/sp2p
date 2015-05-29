package controllers.mobile.account;

import business.IpsDetail;
import business.Payment;
import business.User;
import com.google.gson.Gson;
import com.shove.Convert;
import com.shove.security.Encrypt;
import constants.Constants;
import constants.IPSConstants;
import controllers.BaseController;
import controllers.interceptor.H5Interceptor;
import controllers.mobile.LoginAction;
import controllers.mobile.MainContent;
import controllers.mobile.ProductAction;
import net.sf.json.JSONObject;
import play.Logger;
import play.cache.Cache;
import play.mvc.With;
import utils.Converter;
import utils.ErrorInfo;
import utils.ParseClientUtil;

import java.util.Map;

/**
 * <p>Project: com.shovesoft.sp2p</p>
 * <p>Title: InvestAction.java</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2014 Sunlights.cc</p>
 * <p>All Rights Reserved.</p>
 *
 * @author <a href="mailto:jiaming.wang@sunlights.cc">wangJiaMing</a>
 */

@With(H5Interceptor.class)
public class InvestAction extends BaseController {

    public static void confirmInvest(){
        User user = User.currUser();
        if (user == null) {
            LoginAction.login();
        }

        if(User.currUser().ipsAcctNo == null){//未开户
            AccountAction.createAcct();
        }

        if (params.get("bidId") == null) {
            MainContent.moneyMatters();
        }

        ErrorInfo error = new ErrorInfo();
        Map<String, String> args = controllers.front.invest.InvestAction.buildConfirmInvestParams(error, ParseClientUtil.H5);
        if (error.code < 0) {
            Logger.info(">>确认投标失败：" + error.msg);
            flash.error(error.msg);
            ProductAction.productBid(params.get("bidId"));
        }
        Logger.info(">>确认投标成功");
        renderTemplate("front/account/PaymentAction/registerCreditor.html", args);
    }


    /**
     * 登记债权人回调（异步）
     */
    public static void registerCreditorCBSys() {
        Logger.info("----------- 登记债权人回调（异步）:----------");
        ErrorInfo error = new ErrorInfo();

        Payment pay = new Payment();
        pay.pMerCode = params.get("pMerCode");
        pay.pErrCode = params.get("pErrCode");
        pay.pErrMsg = params.get("pErrMsg");
        pay.p3DesXmlPara = params.get("p3DesXmlPara");
        pay.pSign =  params.get("pSign");

        pay.print();

        JSONObject resultJson = pay.registerCreditorCB(error);

        String pPostUrl = IPSConstants.IPSH5Url.UNFREEZE_INVEST_AMOUNT;
        if(resultJson == null) {
            resultJson = new JSONObject();
            pPostUrl = IPSConstants.IPSH5Url.REGISTER_CREDITOR;
        }

        resultJson.put("code", error.code);
        resultJson.put("msg", error.msg);
        resultJson.put("pPostUrl", pPostUrl);
        resultJson.put("oldMerBillNo", pay.jsonPara.getString("pMerBillNo"));

        Logger.info("----------登记债权人(异步)-------------:"+resultJson.toString());
        Logger.info("----------登记债权人(异步) end-------------");
        renderText(resultJson.toString());
    }

    /**
     * 投标成功
     */
    public static void registerCreditorCB(){

        String result = params.get("result");

        Logger.info("投标回调信息 start >>：" + result);

        ErrorInfo error = new ErrorInfo();
        Logger.info("----------登记债权人(ws处理业务逻辑后post返回) start-------------:");
        result = Encrypt.decrypt3DES(result, Constants.ENCRYPTION_KEY);

        JSONObject json = (JSONObject)Converter.xmlToObj(result);
        Logger.info("----------登记债权人(ws处理业务逻辑后post返回) result-------------:"+result);
        String pMerBillNo = json.getString("oldMerBillNo");
        String info =IpsDetail.getIpsInfo(Long.parseLong(pMerBillNo), error);

        Gson gson = new Gson();
        Map<String, String> map = gson.fromJson(info, Map.class);
        long bidId = Convert.strToLong(map.get("bidId") + "", -1);

        if (error.code < 0 && error.code != Constants.ALREADY_RUN) {
            flash.error(error.msg);
            ProductAction.productBid(bidId + "");
        }
        Logger.info("投标回调信息 end >>：");

        MainContent.property();//TODO
    }

    /**
     * 投标失败情况(解冻投资金额)
     */
    public static void unfreezeInvestAmountCB(String result) {

        Logger.info("----------登记债权人(ws处理业务逻辑后post返回) start-------------:");
        result = Encrypt.decrypt3DES(result, Constants.ENCRYPTION_KEY);

        JSONObject json = (JSONObject)Converter.xmlToObj(result);
        Logger.info("----------登记债权人(ws处理业务逻辑后post返回) result-------------:"+result);
        ErrorInfo error = new ErrorInfo();

        Payment pay = new Payment();
        pay.pMerCode = json.getString("pMerCode");
        pay.pErrCode = json.getString("pErrCode");
        pay.pErrMsg = json.getString("pErrMsg");
        pay.p3DesXmlPara = json.getString("p3DesXmlPara");
        pay.pSign = json.getString("pSign");

        String pMerBillNo = json.getString("oldMerBillNo");

        pay.print();
        pay.unfreezeInvestAmountCB(error);

        Map<String, Object> map = (Map<String, Object>) Cache.get(pMerBillNo);
        long bidId = Convert.strToLong(map.get("bidId") + "", -1);

        if (error.code < 0 && error.code != Constants.ALREADY_RUN) {
            flash.error(error.msg);
            ProductAction.productBid(bidId+"");
        }

        MainContent.property();//TODO

    }


}
