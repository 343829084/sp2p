package controllers.mobile.account;

import business.User;
import controllers.BaseController;
import controllers.interceptor.H5Interceptor;
import play.Logger;
import play.mvc.With;

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
    public static void rechargeCB(){
        Logger.info("充值回调信息 start >>：");
        User user = User.currUser();
        Logger.info("user:" + user);
        String pErrCode = params.get("pErrCode");
        if (pErrCode != null && !"MG00000F".equals(pErrCode)) {
            String pErrMsg = params.get("pErrMsg");
            Logger.info("充值失败：" + pErrMsg);
            flash.error(pErrMsg);
//            render();//TODO
        }
        Logger.info("充值回调信息 end >>：");

//        render();//TODO
    }

    public static void recharge(){


        render();
    }
}
