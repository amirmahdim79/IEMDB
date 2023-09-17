package IEMDB.CA5;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CorsFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Origin", "*");
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Credentials", "true");
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Headers", "*");
        ((HttpServletResponse) response).setHeader("Access-Control-Expose-Headers", "*");
        if (((HttpServletRequest) request).getMethod().equals("OPTIONS")) {

            ((HttpServletResponse) response).setHeader("Access-Control-Allow-Methods", "*");
            ((HttpServletResponse) response).setHeader("Access-Control-Max-Age", "1800");
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_ACCEPTED);
        } else
            chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}