package com.dev.simple.handler;

import com.dev.simple.constant.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.RequestLine;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.common.WsResponse;
import org.tio.websocket.common.WsSessionContext;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.util.Objects;

/**
 * @author xiaotian.huang
 */
public class ShowcaseWsMsgHandler implements IWsMsgHandler {
    private  static Logger log = LoggerFactory.getLogger((ShowcaseWsMsgHandler.class));

    public static ShowcaseWsMsgHandler me = new ShowcaseWsMsgHandler();

    public ShowcaseWsMsgHandler() {
    }

    /**
     * 握手时走这个方法，业务可以在这里获取cookie，request参数等
     */
    @Override
    public HttpResponse handshake(HttpRequest request, HttpResponse response, ChannelContext channelContext) throws Exception {
        String clientIp = request.getClientIp();
        log.info("收到来自{}的ws握手包\r\n{}",clientIp,request.toString());
        return response;
    }

    /**
     * 字节消息（binaryType = arraybuffer) 过来后会走这个方法
     */
    @Override
    public Object onBytes(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
        return null;
    }

    /**
     * 当客户端close flag时，会走这个方法
     */
    @Override
    public Object onClose(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
        Aio.remove(channelContext,"receive close flag");
        return null;
    }

    /**
     * 字符消息（binaryType = blob）过来后会走这个方法
     */
    @Override
    public Object onText(WsRequest wsRequest, String text, ChannelContext channelContext) throws Exception {
        WsSessionContext wsSessionContext = (WsSessionContext) channelContext.getAttribute();
        // 获取webSocket握手包
        HttpRequest httpRequest = wsSessionContext.getHandshakeRequestPacket();

        if (log.isDebugEnabled()) {
            log.debug("握手包：{}",httpRequest);
        }

        log.info("收到ws消息：{}",text);

        if (Objects.equals("心跳内容",text)) {
            return null;
        }

        String msg = channelContext.getClientNode().toString() + "说：" + text;

        // 用tio-websocket, 服务器发送到客户端的Packet是WsResponse
//        WsResponse wsResponse = WsResponse.fromText(msg, showcaseServerConfig.CHARSET);
        // 群发
//        Aio.sendToGroup(channelContext.getGroupContext(), Const.GROUP_ID,wsResponse);

        // 返回值是要发给客户端的内容，一般都是返回null
        return null;
    }


}
