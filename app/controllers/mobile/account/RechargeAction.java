package controllers.mobile.account;

import business.Payment;
import com.shove.security.Encrypt;
import constants.Constants;
import constants.IPSConstants;
import controllers.BaseController;
import controllers.app.common.MsgCode;
import controllers.interceptor.H5Interceptor;
import net.sf.json.JSONObject;
import play.Logger;
import play.mvc.With;
import utils.Converter;
import utils.ErrorInfo;
import utils.ParseClientUtil;

import java.util.Map;

/**
 * <p>Project: com.shovesoft.sp2p</p>
 * <p>Title: RechargeAction.java</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2014 Sunlights.cc</p>
 * <p>All Rights Reserved.</p>
 *
 * @author <a href="mailto:jiaming.wang@sunlights.cc">wangJiaMing</a>
 */

@With(H5Interceptor.class)
public class RechargeAction extends BaseController {

    public static void recharge(){

        render();
    }

    public static void rechargeConfirm(){
        ErrorInfo errorInfo = new ErrorInfo();
        String bankCode = params.get("bankCode");//非必填
        double money = 0;

        try {
            if (params.get("money") == null) {//必填
                errorInfo.code = -1;
                errorInfo.msg = MsgCode.RECHARGE_ERROR.getMessage();
                flash.error(errorInfo.msg);
                recharge();
            }
            money = Double.valueOf(params.get("money"));
        }catch(Exception e){
            e.printStackTrace();
            errorInfo.code = -1;
            errorInfo.msg = MsgCode.RECHARGE_ERROR.getMessage();
            flash.error(errorInfo.msg);
            recharge();
        }

        if (money <= 0) {
            errorInfo.code = -1;
            errorInfo.msg = MsgCode.RECHARGE_ERROR.getMessage();
            flash.error(errorInfo.msg);
            recharge();
        }

        Map<String, String> args = Payment.doDpTrade(money, bankCode, errorInfo, ParseClientUtil.H5);

        render("@front.account.PaymentAction.doDpTrade", args);
    }


    /**
     * 充值回调（异步）
     */
    public static void rechargeCBSys() {
        Logger.info("-----------充值回调（异步）:----------");
        ErrorInfo error = new ErrorInfo();

        Payment pay = new Payment();
        pay.pMerCode = params.get("pMerCode");
        pay.pErrCode = params.get("pErrCode");
        pay.pErrMsg = params.get("pErrMsg");
        pay.p3DesXmlPara = params.get("p3DesXmlPara");
        pay.pSign =  params.get("pSign");

        pay.print();
        pay.doDpTradeCB(error);

        JSONObject resultJson = new JSONObject();

        resultJson.put("code", error.code);
        resultJson.put("msg", error.msg);
        resultJson.put("pMemo1", pay.jsonPara.getString("pMemo1"));
        resultJson.put("pPostUrl", IPSConstants.IPSH5Url.DO_DP_TRADE);

        Logger.info("----------充值异步(ws处理业务逻辑)-------------:"+resultJson.toString());
        Logger.info("---------充值异步(ws处理业务逻辑) end-------------");
        renderText(resultJson.toString());
    }

    /**
     * 充值回调（ws处理业务逻辑后post返回）
     */
    public static void rechargeCB(){
        Logger.info("----------登记债权人(ws处理业务逻辑后post返回) start-------------:");
        String result = params.get("result");

        result = Encrypt.decrypt3DES(result, Constants.ENCRYPTION_KEY);
        ErrorInfo error = new ErrorInfo();

        JSONObject json = (JSONObject) Converter.xmlToObj(result);
        Logger.info("----------登记债权人(ws处理业务逻辑后post返回) result-------------:"+result);

        error.code = json.getInt("code");
        error.msg = json.getString("msg");

        if (error.code < 0 && error.code != Constants.ALREADY_RUN) {
            flash.error(error.msg);
            recharge();
        }
        renderTemplate("mobile/account/RechargeAction/rechargeSuccess.html");
    }

}
