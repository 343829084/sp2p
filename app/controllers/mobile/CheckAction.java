package controllers.mobile;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import business.User;
import utils.ErrorInfo;
import utils.RegexUtils;
import utils.SMSUtil;
import controllers.BaseController;

/**
 * <p>Project: com.shovesoft.sp2p</p>
 * <p>Title: CheckAction.java</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2014 Sunlights.cc</p>
 * <p>All Rights Reserved.</p>
 *
 * @author <a href="mailto:jiaming.wang@sunlights.cc">wangJiaMing</a>
 */
public class CheckAction extends BaseController {

    /**
     * 发送短信验证码
     */
    public static void sendVerifyCode() {
        JSONObject json = new JSONObject();
        String mobile = params.get("mobile");
        ErrorInfo error = new ErrorInfo();

        if(StringUtils.isBlank(mobile) ) {
            error.code = -1;
            error.msg = "手机号码不能为空";
            json.put("error",error);
            renderJSON(json);
        }

        if(!RegexUtils.isMobileNum(mobile)) {
            error.code = -1;
            error.msg = "请输入正确的手机号码";
            json.put("error",error);
            renderJSON(json);
        }
        
        if (User.isNameExist(mobile, error) != 0 ){
        	error.code = -1;
            error.msg = "用户名已经存在";
        	json.put("error",error);
            renderJSON(json);
        }
        
        SMSUtil.sendCode(mobile, error);

        json.put("error",error);
        renderJSON(json);
    }
}
