package com.example.chillStream.config;

import com.example.chillStream.service.VisitorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class VisitorInterceptor implements HandlerInterceptor {

    private final VisitorService visitorService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();

        // watchBoard 페이지에서만 카운트
        if (!requestURI.equals("/home")) {
            return true;
        }

        // 세션을 통한 중복 방문 체크
        HttpSession session = request.getSession();
        String lastVisitTime = (String) session.getAttribute("lastVisitTime");
        String currentTime = String.valueOf(System.currentTimeMillis());

        // 세션에 방문 시간이 없거나 마지막 방문으로부터 30분이 지났을 경우에만 카운트
        if (lastVisitTime == null ||
                (System.currentTimeMillis() - Long.parseLong(lastVisitTime)) > 30 * 60 * 1000) {
            visitorService.incrementVisitorCount();
            session.setAttribute("lastVisitTime", currentTime);
        }

        return true;
    }
}