package utils;

import business.ReceiveXmlEntity;
import play.Logger;

/**
 * Created by libaozhong on 2015/6/16.
 */
public class WechatProcess {
    public String processWechatMag(String xml){
        /** 解析xml数据 */
        ReceiveXmlEntity xmlEntity = new ReceiveXmlProcess().getMsgEntity(xml);

        /** 以文本消息为例，调用图灵机器人api接口，获取回复内容 */
        String result = "超链接:<a href='http:p2p.sunlights.me/mobile/weixin/getOpenId>跳转</a>";
        Logger.info(xmlEntity.getMsgType());
        if("text".endsWith(xmlEntity.getMsgType())){
            Logger.info(xmlEntity.getContent());
            if(xmlEntity.getContent().equalsIgnoreCase("我要红包"));
            Logger.info("xmlEntity.getContent()");
        //    result = new TulingApiProcess().getTulingResult(xmlEntity.getContent());
        }

        /** 此时，如果用户输入的是“你好”，在经过上面的过程之后，result为“你也好”类似的内容
         *  因为最终回复给微信的也是xml格式的数据，所有需要将其封装为文本类型返回消息
         * */
        result = new FormatXmlProcess().formatXmlAnswer(xmlEntity.getFromUserName(), xmlEntity.getToUserName(), result);

        return result;
    }
}
