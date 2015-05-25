package controllers.interceptor;

import business.User;
import controllers.BaseController;
import controllers.mobile.LoginAction;
import play.Logger;
import play.mvc.Before;

public class H5Interceptor extends BaseController {

    @Before(only = {"mobile.MainContent.property",
            "mobile.MainContent.me"})
    public static void checkLogin() {
        Logger.info("[checkLogin]");
        User user = User.currUser();
        if (user == null) {
            LoginAction.login();
        }
    }
}
