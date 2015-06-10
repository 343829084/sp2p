package controllers.mobile;

import business.User;
import constants.Constants;
import controllers.BaseController;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.mvc.Http;
import utils.ErrorInfo;
import utils.ParseClientUtil;
import utils.WebChartUtil;

/**
 * Created by libaozhong on 2015/5/27.
 */
public class QuickRegister extends BaseController {
    public static void quickRegister(){
       String mobile= params.get("mobile");
        String fpHots= Constants.FP_HOST;
            params.put("status", Constants.WEIXINSTATUS.QUICKREGISTERSUCCESS);
            params.put("mobile", mobile);
            if (ParseClientUtil.isWeiXin()) {
                WeChatAction.weChatGate();
            }

        render(fpHots);
    }

    public static void qrredirect(){

        String fpHots= Constants.FP_HOST;
        render(fpHots);


    }
    public static void webChartBind(String mobile,String openId, ErrorInfo error){
        if(StringUtils.isNotEmpty(openId)){//bindweixin
            User user=new User();
            user.name=mobile;
            user.bindingSocialToFp(WebChartUtil.WECHAT, openId, error);
        }

        JSONObject json = new JSONObject();
        json.put("error", error);
        renderTemplate("/mobile/registerSuccess");
    }

    public static void registerSuccess(String ...openid){

        Http.Request reuqets = Http.Request.current();
        String errorCode = reuqets.params.get("errorcode");
        String fpHots= Constants.FP_HOST;
        render(fpHots,errorCode);

    }
}
