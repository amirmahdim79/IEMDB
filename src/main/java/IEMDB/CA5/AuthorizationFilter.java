package IEMDB.CA5;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

public class AuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("Initiated !!");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String res = Tools.authenticate(request.getHeader("Authorization"));
        System.out.println(request.getHeaderNames());
        System.out.println(request.getHeader("Authorization"));

        Enumeration<String> headerNames = request.getHeaderNames();

        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                System.out.println("Header: " + request.getHeader(headerNames.nextElement()));
            }
        }
        if(res.equals("false"))
            response.sendError(401,"Access Denied");
        else{
            request.setAttribute("userEmail",res);
            filterChain.doFilter(request, response);
        }

    }

    @Override
    public void destroy() {
    }
}