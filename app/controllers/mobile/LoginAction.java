package controllers.mobile;

import business.User;
import com.google.gson.JsonObject;
import constants.Constants;
import controllers.BaseController;
import controllers.mobile.account.AccountAction;
import models.t_users;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.cache.Cache;
import play.libs.WS;
import play.mvc.Http;
import utils.ErrorInfo;
import utils.RegexUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    public static void login(String ...openid) {
        String openId= openid[0];
        String status= Http.Request.current().params.get("status");
        Logger.info("openId为："+openId+"status:"+status);
        flash.keep("url");
        render(openId,status);
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

        if (user.login(password, false, error) < 0) {
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


    /**
     * 跳转到注册页面
     */
    public static void register(String ...openid) {
        String openId= openid[0];
        render(openId);
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

        String authentication_id = registerToFp(error, mobile, password);

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

        registerGiveJinDou(error, mobile);

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

    private static String registerToFp(ErrorInfo error, String mobile, String password) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobilePhoneNo", mobile);
        params.put("passWord", password);
        params.put("channel", "1");

        String authentication_id = null;

        try {
            WS.HttpResponse httpResponse = WS.url(Constants.FP_REGISTER_URL).setParameters(params).post();
            Object value = parseFpResponse(httpResponse, error);
            if(value!= null && value instanceof JSONObject){
                authentication_id = ((JSONObject)value).getString("authenticationId");
            }
        }catch (Exception e){
            e.printStackTrace();
            Logger.error(e.getMessage());
            error.code = -1;
            error.msg = "注册成功,送金豆失败,请联系客服！";
        }
       
        return authentication_id;
    }

    /**
     * 注册送金豆
     */
    private static void registerGiveJinDou(ErrorInfo error,String name){
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobilePhoneNo", name);

        try {
            WS.HttpResponse httpResponse = WS.url(Constants.FP_REGISTER_GIVE_JINDOU).setParameters(params).post();
            parseFpResponse(httpResponse, error);
        }catch (Exception e){
            e.printStackTrace();
            Logger.error(e.getMessage());
            error.code = -1;
            error.msg = "注册失败";
        }

    }

    private static Object parseFpResponse(WS.HttpResponse httpResponse, ErrorInfo error) {
        Object value = null;
        Logger.info("fp response statusCode:" + httpResponse.getStatus());
        if (httpResponse.getStatus() == HttpStatus.SC_OK) {
            JsonObject jsonResult = httpResponse.getJson().getAsJsonObject();
            Logger.info("fp response result:" + jsonResult);

            Object message = jsonResult.get("message");
            if(message!= null && message instanceof JSONObject){
                String severity = ((JSONObject)message).getString("severity");
                if(!severity.equals("0")){
                    error.code = -1;
                    error.msg = ((JSONObject)message).getString("summary");
                }
            }
            value = jsonResult.get("value");

        }
        return value;
    }




}
