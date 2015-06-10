package controllers.mobile;

import business.User;
import com.google.gson.JsonObject;
import constants.Constants;
import controllers.BaseController;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.cache.Cache;
import play.libs.WS;
import utils.ErrorInfo;
import utils.ParseClientUtil;
import utils.RegexUtils;
import utils.WebChartUtil;

import java.io.IOException;
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
    public static void login() {

        User user = User.currUser();
        if (user != null) {
            MainContent.property();
        }

        Map<String,String> map=new HashMap<String,String>();
        map.put("status","1");
        if (ParseClientUtil.isWeiXin()) {
            weChatGate(map);
        }

        String openId = params.get("openId");

        Logger.info("openId为："+openId);
        flash.keep("url");

        JSONObject paramsJson = new JSONObject();
        paramsJson.put("openId", openId);
        paramsJson.put("status", Constants.WEIXINSTATUS.LOGIN);

        render(paramsJson);
    }

    /**
     * 进入微信统一入口
     * @throws IOException
     *
     * @param map
     */
    private static void weChatGate(Map<String, String> map) {
        Logger.info("进入");
        String status =  map.get("status");
        String mobile =map.get("mobile");
        Logger.info("WeChatAction.weChatGate.status:"+status+"mobile:"+mobile);
        String url = WebChartUtil.buildWeChatGateUrl(status, mobile);
        Logger.info("url：" + url);
        redirect(url);
    }
    public static void doLogin() {
        ErrorInfo error = new ErrorInfo();

        String name = params.get("name");
        String password = params.get("password");
        String openId = params.get("openId");
        flash.put("name", name);
        flash.put("password", password);
        flash.put("openId", openId);
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
            if(StringUtils.isNotEmpty(openId)){//bindweixin
                user.bindingSocialToFp(WebChartUtil.WECHAT, openId, error);
            }

            String url = flash.get("url");
            if (StringUtils.isNotBlank(url)) {
                redirect(url);
            }else {
                MainContent.moneyMatters();
            }
        } else {
            flash.keep("url");
            login();
        }

    }

    /**
     * 跳转到注册页面
     */
    public static void register() {
        if (ParseClientUtil.isWeiXin()) {
            Map<String,String> map=new HashMap<String,String>();
            map.put("status","2");
            weChatGate(map);
        }
        render();
    }

    public static void doRegister() {
        JSONObject json = new JSONObject();
        ErrorInfo error = new ErrorInfo();
            json.put("error", error);
        String mobile = params.get("name");//the user name is mobile
        String password = params.get("password");
        String verifyCode = params.get("verifyCode");
        String recommendUserName = params.get("recommended");
        String openId = params.get("openId");
        String queryName = params.get("queryName");

        registerValidation(error, mobile, password, verifyCode);

        if (error.code < 0) {
            json.put("error", error);
            renderJSON(json);
        }


         String authentication_id = User.registerToFp(error, mobile, password);

        if (error.code < 0 && error.code!=-2) {
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
          if(error.code!=-2) {
              user.register(error);

                if (error.code < 0) {
                    json.put("error", error);
                    renderJSON(json);
                }
              registerGiveJinDou(error, mobile);
          }
        if(!StringUtils.isNotEmpty(queryName)){
            if(StringUtils.isNotEmpty(openId)){//bindweixin
            ErrorInfo error2=new ErrorInfo();
            user.bindingSocialToFp(WebChartUtil.WECHAT, openId, error2);
        }
        }
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
