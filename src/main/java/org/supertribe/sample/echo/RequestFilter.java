/*
 *  Tomitribe Confidential
 *
 *  Copyright Tomitribe Corporation. 2017
 *
 *  The source code for this program is not published or otherwise divested
 *  of its trade secrets, irrespective of what has been deposited with the
 *  U.S. Copyright Office.
 */
package org.supertribe.sample.echo;

import org.tomitribe.util.IO;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;

@WebFilter(filterName = "EchoRequestFilter", urlPatterns = {"/*"})
public class RequestFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final StreamingOutput stream = echoRequest(servletRequest.getInputStream(), (HttpServletRequest) servletRequest);
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        stream.write(output);
        servletResponse.getOutputStream().write(output.toString("UTF-8").getBytes());

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setStatus(200);
    }

    @Override
    public void destroy() {

    }

    private StreamingOutput echoRequest(final InputStream body, HttpServletRequest httpServletRequest) {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream outputStream) throws IOException {
                final PrintStream out = new PrintStream(outputStream);

                final String query = httpServletRequest.getQueryString();

                out.printf("%s %s%n", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
                if (query != null) {
                    out.printf("[query: %s]%n", query);
                }

                final Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    final String element = headerNames.nextElement();
                    final String header = httpServletRequest.getHeader(element);
                    out.printf("%s : %s%n", element, header);
                }
                out.println();
                IO.copy(body, out);
            }
        };
    }
}
