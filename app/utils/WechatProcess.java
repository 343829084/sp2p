package utils;

import business.LinkMessage;
import business.ReceiveXmlEntity;
import play.Logger;

import java.util.Random;

/**
 * Created by libaozhong on 2015/6/16.
 */
public class WechatProcess {
    public String processWechatMag(String xml){
        Logger.info("WechatProcess.processWechatMag");
        /** 解析xml数据 */
        ReceiveXmlEntity xmlEntity = new ReceiveXmlProcess().getMsgEntity(xml);

        /** 以文本消息为例，调用图灵机器人api接口，获取回复内容 */
        String result = "";
        Logger.info(xmlEntity.getMsgType());
        if("text".endsWith(xmlEntity.getMsgType())){
            Logger.info(xmlEntity.getContent());
            Logger.info("xmlEntity.getContent()");
            if(xmlEntity.getContent().equals("我要红包")){
                Logger.info("发红包");
                LinkMessage link=new LinkMessage();
                link.setContent("点击领取红包");
                link.setTitle("加薪猫送红包");
                link.setLink("http:p2p.sunlights.me/mobile/weixin/sendRedpact?redPacketId=1");
                link.setMsgId(new Random(5234567890123456l).toString());
                result = new FormatXmlProcess().formatLinkXmlAnswer(xmlEntity.getFromUserName(), xmlEntity.getToUserName(),
                        link.getTitle(),link.getContent(),link.getLink(),link.getMsgId());
            };


        //    result = new TulingApiProcess().getTulingResult(xmlEntity.getContent());
        }else{
            result = new FormatXmlProcess().formatXmlAnswer(xmlEntity.getFromUserName(), xmlEntity.getToUserName(), result);
        }

        /** 此时，如果用户输入的是“你好”，在经过上面的过程之后，result为“你也好”类似的内容
         *  因为最终回复给微信的也是xml格式的数据，所有需要将其封装为文本类型返回消息
         * */

        return result;
    }
}
