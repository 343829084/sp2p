package business;

import constants.Constants;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import play.Logger;
import utils.MD5Util;

import javax.net.ssl.*;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
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
    public static final String MCH_ID = Constants.MCH_ID;      //商户号
    public static final String WXAPPID =Constants.WECHAT_APPID;     //公众账号appid
    public static final String NICK_NAME = Constants.REDPACKET_APPLY_NAME;   //提供方名称
    public static final String SEND_NAME = Constants.SEND_NAME;   //商户名称
    public static final int MIN_VALUE = 100;       //红包最小金额 单位:分
    public static final int MAX_VALUE = 200;       //红包最大金额 单位:分
    public static final int TOTAL_NUM = 1;         //红包发放人数
    public static  String CLIENT_IP;   //调用接口的机器IP
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
    public static synchronized  RedPacketBill getAmount(String openid,String billNo,RedPacket redPacket){
        //该用户获取的随机红包金额
        int amount = (int) Math.round(Math.random()*(redPacket.getMaxValue()-redPacket.getMinValue())+redPacket.getMinValue());
        //如果此次随机金额比商户红包余额还要大,则返回商户红包余额
        if(amount > redPacket.getBalance()){
            amount =  redPacket.getBalance();
        }
        RedPacketBill redPacketBill = new RedPacketBill();
        redPacketBill.setAddTime(new Date());
        redPacketBill.setAmount(amount);
        redPacketBill.setOpenid(openid);
        redPacketBill.setResult(RedPacketParam.LOCK);
        redPacketBill.setBillNo(billNo);
        //先锁定用户领取的金额,防止领取金额超过预算金额
      //  service.save(hongbao);
        return redPacketBill;
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
     * @param amount
     * @return
     */
    public static SortedMap<String, String> createMap(String billNo,RedPacket redPacket, String openid, int amount) throws UnknownHostException {
        CLIENT_IP=getLocalIp();
        SortedMap<String, String> params = new TreeMap<String, String>();
        params.put("wxappid", WXAPPID);
        params.put("nonce_str", createNonceStr());
        params.put("mch_billno", billNo);
        params.put("mch_id", MCH_ID);
        params.put("nick_name", NICK_NAME);
        params.put("send_name", SEND_NAME);
        params.put("re_openid", openid);
        params.put("total_amount",amount+ "");
        params.put("min_value",amount + "");
        params.put("max_value",  amount + "");
        params.put("total_num", 1 + "");
        params.put("wishing", redPacket.getWishing());
        params.put("client_ip", CLIENT_IP);
        params.put("act_name", redPacket.getActName());
        params.put("remark", redPacket.getRemark());
        params.put("logo_imgurl", redPacket.getLogo_imgurl());
        params.put("share_content ", redPacket.getContent());
        params.put("share_url", redPacket.getShare_url());
        params.put("share_imgurl", redPacket.getShare_imgurl());
        Logger.info("发送红包参数"+params.toString());
        return params;
    }

    private static String getLocalIp() throws UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();
        String ip = addr.getHostAddress();//获得本机IP
        return ip;
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
     * @returnMCH_ID
     */
    public static String post(String requestXML) throws Exception {
        Logger.info("执行发送红包开始");
        KeyStore keyStore  = KeyStore.getInstance("PKCS12");
        FileInputStream inputStream = new FileInputStream("F:/404/apiclient_cert.p12");
        try {
            keyStore.load(inputStream, MCH_ID.toCharArray());
        }finally {
            inputStream.close();
            }
            // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, MCH_ID.toCharArray())
                .build();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[] { "TLSv1" },
                null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();
        String result = "";
       try {
           HttpPost postMethod = new HttpPost("https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack");
           StringEntity reqEntity = new StringEntity(requestXML, "utf-8"); //如果此处编码不对，可能导致客户端签名跟微信的签名不一致
           reqEntity.setContentType("application/x-www-form-urlencoded");
           postMethod.setEntity(reqEntity);

           CloseableHttpResponse response = httpClient.execute(postMethod);
           Logger.info("执行发送红包完成");
           try {
               HttpEntity entity = response.getEntity();
               if (entity != null) {
                   BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(),"UTF-8"));
                   String text;
                   while ((text = bufferedReader.readLine()) != null) {
                       result +=text;
                   }
               }
               EntityUtils.consume(entity);
           } finally {
               response.close();
           }


       }catch(Exception e){

        } finally {
           httpClient.close();

        }

        return null;}
}
