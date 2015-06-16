package controllers.supervisor.activity;

import business.PageVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.BaseController;
import controllers.supervisor.activity.service.RedPacketService;
import controllers.supervisor.activity.vo.RedPacketVo;
import play.Logger;

import java.util.List;
import java.util.Map;

/**
 * Created by Yuan on 2015/6/16.
 */
public class RedPacketController extends BaseController {

    private static RedPacketService redPacketService = new RedPacketService();

    public static void index() {
        PageVo pageVo = new PageVo();
        List<RedPacketVo> redPacketVos = redPacketService.findRedPacketVos(pageVo);
        render(redPacketVos);
    }

    public static void detail(Long id) {
        RedPacketVo redPacketVo = redPacketService.findRedPacketDetailById(id);
        render(redPacketVo);
    }

    public static void save() {
        Map<String, String> map = params.allSimple();
        map.remove("body");
        map.remove("authenticityToken");
        Long id = "".equals(map.get("id")) ? 0 : Long.valueOf(map.get("id"));
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            RedPacketVo redPacketVo = objectMapper.convertValue(map, RedPacketVo.class);
            redPacketService.save(redPacketVo);
            index();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e.getMessage(), e);
            detail(id);
        }

    }

    public static void findRedPacket() {
        RedPacketVo redPacketVo = redPacketService.findRedPacketVoBy(1L, "111111");
        renderJSON(redPacketVo);
    }


}
