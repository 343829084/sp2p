package controllers.mobile;

import constants.Constants;
import controllers.BaseController;
import play.Logger;
import play.mvc.Http;

/**
 * Created by libaozhong on 2015/5/27.
 */
public class QuickRegister extends BaseController {
    public static void quickRegister(){
        params.put("status", Constants.WEIXINSTATUS.MOBILEHADREGISTER);

        String fpHots= Constants.FP_HOST;

        Http.Request reuqets = Http.Request.current();
        Logger.info(reuqets.params.toString());
        String code=reuqets.params.get("code");
        Logger.info(code);
        render(code,fpHots);
    }

    public static void qrredirect(){

        String fpHots= Constants.FP_HOST;
        render(fpHots);


    }
    public static void registerSuccess(String ...openid){
        params.put("status", Constants.WEIXINSTATUS.QUICKREGISTERSUCCESS);

        Http.Request reuqets = Http.Request.current();
        String fpHots= Constants.FP_HOST;
        String openId="";
        String status="";

        if(null!=openid){
            openId=openid[0];
            status=openid[1];
            Logger.info("openid:"+openId+"status"+status);
        }
        render(fpHots,openId,status);

    }
}
