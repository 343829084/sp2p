package business;

import constants.Constants;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import utils.MD5Util;

import javax.net.ssl.*;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import utils.MyX509TrustManager;

/**
 * Created by libaozhong on 2015/6/11.
 */
public class RedPacketParam {
    public static final String MCH_ID = "XX";      //商户号
    public static final String WXAPPID = "XX";     //公众账号appid
    public static final String NICK_NAME = "XX";   //提供方名称
    public static final String SEND_NAME = "XX";   //商户名称
    public static final int MIN_VALUE = 100;       //红包最小金额 单位:分
    public static final int MAX_VALUE = 200;       //红包最大金额 单位:分
    public static final int TOTAL_NUM = 1;         //红包发放人数
    public static final String WISHING = "XX";     //红包祝福语
    public static final String CLIENT_IP = "XX";   //调用接口的机器IP
    public static final String ACT_NAME = "XX";    //活动名称
    public static final String REMARK = "XX";      //备注
    public static final String KEY = "XX";         //秘钥

    public static final int FAIL = 0;              //领取失败
    public static final int SUCCESS = 1;           //领取成功
    public static final int LOCK = 2;              //已在余额表中锁定该用户的余额,防止领取的红包金额大于预算

    /**
     * 对请求参数名ASCII码从小到大排序后签名
     *
     * @param params
     */
    public static void sign(SortedMap<String, String> params) {
        Set<Map.Entry<String, String>> entrys = params.entrySet();
        Iterator<Map.Entry<String, String>> it = entrys.iterator();
        StringBuffer result = new StringBuffer();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            result.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("&");
        }
        result.append("key=").append(KEY);
        params.put("sign", MD5Util.getMD5String(result.toString()));
    }

    /**
     * 生成提交给微信服务器的xml格式参数
     *
     * @param params
     * @return
     */
    public static String getRequestXml(SortedMap<String, String> params) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = params.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if ("nick_name".equalsIgnoreCase(k) || "send_name".equalsIgnoreCase(k) || "wishing".equalsIgnoreCase(k) || "act_name".equalsIgnoreCase(k) || "remark".equalsIgnoreCase(k) || "sign".equalsIgnoreCase(k)) {
                sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
            } else {
                sb.append("<" + k + ">" + v + "</" + k + ">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 创建map
     *
     * @param billNo
     * @param openid
     * @param userId
     * @param amount
     * @return
     */
    public static SortedMap<String, String> createMap(String billNo, String openid, String userId, int amount) {
        SortedMap<String, String> params = new TreeMap<String, String>();
        params.put("wxappid", WXAPPID);
        params.put("nonce_str", createNonceStr());
        params.put("mch_billno", billNo);
        params.put("mch_id", MCH_ID);
        params.put("nick_name", NICK_NAME);
        params.put("send_name", SEND_NAME);
        params.put("re_openid", openid);
        params.put("total_amount", amount + "");
        params.put("min_value", amount + "");
        params.put("max_value", amount + "");
        params.put("total_num", TOTAL_NUM + "");
        params.put("wishing", WISHING);
        params.put("client_ip", CLIENT_IP);
        params.put("act_name", ACT_NAME);
        params.put("remark", REMARK);
        return params;
    }

    /**
     * 生成随机字符串
     *
     * @return
     */
    public static String createNonceStr() {
        return UUID.randomUUID().toString().toUpperCase().replace("-", "");
    }

    /**
     * 生成商户订单号
     *
     * @param userId 该用户的userID
     * @return
     */
    public static String createBillNo(String userId) {
        //组成： mch_id+yyyymmdd+10位一天内不能重复的数字
        //10位一天内不能重复的数字实现方法如下:
        //因为每个用户绑定了userId,他们的userId不同,加上随机生成的(10-length(userId))可保证这10位数字不一样
        Date dt = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyymmdd");
        String nowTime = df.format(dt);
        int length = 10 - userId.length();
        return MCH_ID + nowTime + userId + getRandomNum(length);
    }

    /**
     * 生成特定位数的随机数字
     *
     * @param length
     * @return
     */
    private static String getRandomNum(int length) {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            val += String.valueOf(random.nextInt(10));
        }
        return val;
    }

    /**
     * post提交到微信服务器
     *
     * @param requestXML
     * @param instream
     * @return
     */
    public static String post(String requestXML, InputStream instream) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try {
            keyStore.load(instream, MCH_ID.toCharArray());
        } finally {
            instream.close();
        }
//        try{
//            SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(null, new TrustStrategy() {
//                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {//信任所有
//                    return true;
//                }
//            }).build();
//            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
//            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
//            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + Constants.APPID + "&secret=" + Constants.APPSECRET;
//            HttpGet get = new HttpGet(url);
//           HttpResponse response = httpclient.execute(get);
//            HttpEntity entity = response.getEntity();
//            if (null != entity) {
//                String responseContent = EntityUtils.toString(entity, "UTF-8");
//                JSONObject demoJson = new JSONObject(responseContent);
//                System.out.print(demoJson.getString("access_token"));
//                //EntityUtils.consume(entity);
//            }
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (KeyStoreException e) {
//            e.printStackTrace();     }
//              catch (IOException e) {
//            e.printStackTrace();


//        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, MCH_ID.toCharArray()).build();
//        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
//                sslcontext,
//                new String[] { "TLSv1" },
//                null,
//                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
//        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
//        String result = "";
//        try {
//            HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack");
//            StringEntity  reqEntity  = new StringEntity(requestXML,"utf-8"); //如果此处编码不对，可能导致客户端签名跟微信的签名不一致
//            reqEntity.setContentType("application/x-www-form-urlencoded");
//            httpPost.setEntity(reqEntity);
//            CloseableHttpResponse response = httpclient.execute(httpPost);
//            try {
//                HttpEntity entity = response.getEntity();
//                if (entity != null) {
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(),"UTF-8"));
//                    String text;
//                    while ((text = bufferedReader.readLine()) != null) {
//                        result +=text;
//                    }
//                }
//                EntityUtils.consume(entity);
//            } finally {
//                response.close();
//            }
//        } finally {
//            httpclient.close();
//        }

        return null;
    }
}
