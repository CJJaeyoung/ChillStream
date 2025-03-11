package com.example.chillStream.exception;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

// WebSocketExceptionHandler 클래스는 Spring WebSocket에서 WebSocket 연결 중 발생하는 예외를 처리하기 위해 만든 커스텀 핸들러
// 이 클래스는 TextWebSocketHandler를 상속받아 WebSocket 메시지 핸들링 기능을 확장하거나 수정할 수 있다.

public class WebSocketExceptionHandler extends TextWebSocketHandler {

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
       
        super.handleTransportError(session, exception);
    }
}
