package controllers.interceptor;

import business.User;
import controllers.BaseController;
import controllers.mobile.LoginAction;
import play.Logger;
import play.mvc.Before;

public class H5Interceptor extends BaseController {

    @Before(only = {"mobile.MainContent.property",
            "mobile.MainContent.me",
            "mobile.TradeController.tradeList",
            "mobile.TradeController.tradeHistory",
            "mobile.TradeController.remainMoney",
            "mobile.MeAction.changePassWord",
            "mobile.MeAction.accountSafe"})
    public static void checkLogin() {
        Logger.debug("[checkLogin]" + request.url);
        User user = User.currUser();
        if (user == null) {
            flash.put("url", request.url);
            LoginAction.login();
        }
    }
}
