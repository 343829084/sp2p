package controllers.mobile;

import constants.Constants;
import controllers.BaseController;

/**
 * <p>Project: com.shovesoft.sp2p</p>
 * <p>Title: LoginController.java</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2014 Sunlights.cc</p>
 * <p>All Rights Reserved.</p>
 *
 * @author <a href="mailto:jiaming.wang@sunlights.cc">wangJiaMing</a>
 */

public class LoginAction extends BaseController {

        /*
         * 跳转到登录页面
         */
        public static void login() {
            String loginOrRegister = Constants.LOGIN_AREAL_FLAG;

            render(loginOrRegister);
        }
}
