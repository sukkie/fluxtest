package com.cos.fluxtest;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MyFilter implements Filter {

    private EventNotify eventNotify;

    public MyFilter(EventNotify eventNotify) {
        this.eventNotify = eventNotify;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chai응) throws IOException, ServletException {
        System.out.println("필터 실행됨");

        HttpServletResponse res = (HttpServletResponse) response;
        res.setContentType("text/event-stream;charset=UTF-8");
//        res.setContentType("text/html;charset=UTF-8");
//        res.setContentType("text/plain;charset=UTF-8"); // 브라우저가 마임타입이 text/plain이면 버퍼를 비워도 바로 표시되지 않고 한번에 표시.
        PrintWriter out = res.getWriter();

        // 1. Reactive Stream 라이브러리를 쓰면 표준을 지켜서 사용 할 수 있음
        for (int i = 0; i < 5; i++) {
            out.write("응답" + i + "\n");
            out.flush(); // 버퍼를 비운다
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 2. SSE프로토콜, Emitter 라이브러리를 쓰면 편하게 쓸 수 이씀
        while (true) {
            try {
                if (eventNotify.getChange()) {
                    int lastIndex = eventNotify.getEvents().size() - 1;
                    out.write("응답" + eventNotify.getEvents().get(lastIndex) + "\n");
                    out.flush();
                    eventNotify.setChange(false);
                }
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 3. WebFlux -> Reactive Streams 이 친구가 적용된 stream을 배운다(비동기 단일 쓰레드 동작)
        // 4. Servlet MVC -> Reactive Streams 이 친구가 적용된 stream을 배운다(멀티 쓰레드 동작)
    }
}
