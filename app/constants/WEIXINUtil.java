package constants;

import play.mvc.Http;

/**
 * Created by libaozhong on 2015/6/8.
 */
public class WEIXINUtil {
    public static String authCode="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx320badb1a6f6b806&redirect_uri=http%3A%2F%2Fp2pv2.sunlights.me%2Fmobile%2Fweixin%2FopenId&response_type=code&scope=snsapi_base&state=5#wechat_redirect";
    public static boolean isWeiXin(){

        Object agent = Http.Request.current().headers.get("user-agent");
        String agentUrl=agent.toString().toLowerCase();
        if(agentUrl.indexOf("mobile") > 0){
            if(agentUrl.indexOf("micromessenger") > 0){
                return true;
            }
        }
        return false;
    }
}
