package controllers.mobile;

import business.User;
import constants.Constants;
import controllers.BaseController;
import controllers.app.common.MsgCode;
import controllers.mobile.account.AccountAction;
import models.t_users;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import play.cache.Cache;
import utils.ErrorInfo;
import utils.JSONUtils;
import utils.RegexUtils;

import java.util.Date;

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
        flash.keep("url");
        render();
    }

    public static void doLogin() {
        ErrorInfo error = new ErrorInfo();

        String name = params.get("name");
        String password = params.get("password");
        flash.put("name", name);
        flash.put("password", password);

        boolean validate = true;

        if (StringUtils.isBlank(name)) {
            error.code = -1;
            error.msg = "请输入用户名";
            flash.error(error.msg);
            validate = false;
        }
        if (StringUtils.isBlank(password)) {
            error.code = -1;
            error.msg = "请输入密码";
            flash.error(error.msg);
            validate = false;
        }

        User user = new User();
        user.name = name;

        if (user.id < 0) {
            error.code = -1;
            error.msg = "该用户名不存在";
            flash.error(error.msg);
            validate = false;
        }

        if (user.loginFromH5(password, error) < 0) {
            flash.error(error.msg);
            validate = false;
        }

        if (validate) {
            String url = flash.get("url");
            if (StringUtils.isNotBlank(url)) {
                redirect(url);
            }else {
                t_users t_users = user.queryUser2ByUserId(user.getId(), error);
                if (t_users.ips_acct_no == null) {//未开户
                    AccountAction.createAcct();
                }else{
                    MainContent.moneyMatters();
                }
            }
        } else {
            flash.keep("url");
            login();
        }

    }

    public static void loginBySocial(){
        ErrorInfo error = new ErrorInfo();

        String name = params.get("mobilePhoneNo");
        String socialType = params.get("socialType");

        if (StringUtils.isBlank(name)) {
            error.code = -1;
            error.msg = "手机号不能为空";
            renderJSON(JSONUtils.toJSONString(error, MsgCode.SOCIAL_LOGIN_FAIL));
        }
        if (StringUtils.isBlank(socialType)) {
            error.code = -1;
            error.msg = "社交类型不能为空";
            renderJSON(JSONUtils.toJSONString(error, MsgCode.SOCIAL_LOGIN_FAIL));
        }

        User user = new User();
        user.name = name;

        if (user.id < 0) {
            error.code = -1;
            error.msg = "该用户名不存在";
            renderJSON(JSONUtils.toJSONString(error, MsgCode.SOCIAL_LOGIN_FAIL));
        }

        user.loginBySocial(socialType, error);

        if (error.code < 0) {
            renderJSON(JSONUtils.toJSONString(error, MsgCode.SOCIAL_LOGIN_FAIL));
        }

        renderJSON(JSONUtils.toJSONString(error, MsgCode.SOCIAL_LOGIN_SUCC));
    }

    public static void bindingSocial(){
        ErrorInfo error = new ErrorInfo();

        String name = params.get("mobilePhoneNo");
        String socialType = params.get("socialType");
        String socialNo = params.get("socialNo");

        if (StringUtils.isBlank(name)) {
            error.code = -1;
            error.msg = "手机号不能为空";
            renderJSON(JSONUtils.toJSONString(error, MsgCode.SOCIAL_BINDING_FAIL));
        }
        if (StringUtils.isBlank(socialType)) {
            error.code = -1;
            error.msg = "社交类型不能为空";
            renderJSON(JSONUtils.toJSONString(error, MsgCode.SOCIAL_BINDING_FAIL));
        }
        if (StringUtils.isBlank(socialNo)) {
            error.code = -1;
            error.msg = "社交号不能为空";
            renderJSON(JSONUtils.toJSONString(error, MsgCode.SOCIAL_BINDING_FAIL));
        }

        User user = new User();
        user.name = name;

        if (user.id < 0) {
            error.code = -1;
            error.msg = "该用户名不存在";
            renderJSON(JSONUtils.toJSONString(error, MsgCode.SOCIAL_BINDING_FAIL));
        }
        user.bindingSocial(socialType, socialNo, error);

        if (error.code < 0) {
            renderJSON(JSONUtils.toJSONString(error, MsgCode.SOCIAL_BINDING_FAIL));
        }

        renderJSON(JSONUtils.toJSONString(error, MsgCode.SOCIAL_BINDING_SUCC));
    }


    /**
     * 跳转到注册页面
     */
    public static void register() {
        render();
    }

    public static void doRegister() {
        JSONObject json = new JSONObject();
        ErrorInfo error = new ErrorInfo();

        String mobile = params.get("name");//the user name is mobile
        String password = params.get("password");
        String verifyCode = params.get("verifyCode");
        String recommendUserName = params.get("recommended");

        registerValidation(error, mobile, password, verifyCode);

        if (error.code < 0) {
            json.put("error", error);
            renderJSON(json);
        }

        String authentication_id = User.registerToFp(error, mobile, password);

        if (error.code < 0) {
            json.put("error", error);
            renderJSON(json);
        }

        User user = new User();
        user.time = new Date();
        user.name = mobile;
        user.password = password;
        user.mobile = mobile;
        user.isMobileVerified = true;
        user.authentication_id = authentication_id;
        user.recommendUserName = recommendUserName;

        user.register(error);

        if (error.code < 0) {
            json.put("error", error);
            renderJSON(json);
        }

        User.registerGiveJinDou(error, mobile);

        json.put("error", error);
        renderJSON(json);
    }

    private static void registerValidation(ErrorInfo error, String mobile, String password, String verifyCode) {
        if (StringUtils.isBlank(mobile)) {
            error.code = -1;
            error.msg = "请填写手机号";
            return;
        }
        if (StringUtils.isBlank(password)) {
            error.code = -1;
            error.msg = "请输入密码";
            return;
        }
        if (StringUtils.isBlank(verifyCode)) {
            error.code = -1;
            error.msg = "请输入验证码";
            return;
        }
        if (!RegexUtils.isMobileNum(mobile)) {
            error.code = -1;
            error.msg = "请填写正确的手机号码";
            return;
        }
        if (!RegexUtils.isValidPassword(password)) {
            error.code = -1;
            error.msg = "请填写符合要求的密码";
            return;
        }

        String cacheVerifyCode = Cache.get(mobile) + "";
        if (Constants.CHECK_CODE && !verifyCode.equals(cacheVerifyCode)) {
            error.code = -1;
            error.msg = "验证码输入有误";
            return;
        }

        User.isNameExist(mobile, error);
    }





}
