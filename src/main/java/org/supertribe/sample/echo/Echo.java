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
import org.tomitribe.util.Join;

import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.MediaType.WILDCARD;

@Path("test")
@Startup
@Singleton(description = "restful service to test connectivity.")
@Lock(LockType.WRITE)
public class Echo {

    @Context
    private UriInfo uriInfo;

    @Context
    private HttpHeaders httpHeaders;

    @Context
    private Request request;

    @Context
    private SecurityContext security;

    @Context
    private Providers providers;

    @Context
    private HttpServletRequest httpServletRequest;

    /**
     * session context for async methods to detect shutdown.
     */
    @Resource
    private SessionContext sessionContext;


    @POST
    @Path("echo")
    @Consumes(WILDCARD)
    @Produces(TEXT_PLAIN)
    public StreamingOutput echoPost(final InputStream body) {
        return echoRequest(body);
    }

    @POST
    @Path("echo/{test}")
    @Consumes(WILDCARD)
    @Produces(TEXT_PLAIN)
    public StreamingOutput echoPostWild(final @PathParam("test") String test, final InputStream body) {
        return echoRequest(body);
    }

    @OPTIONS
    @Path("echo")
    @Consumes(WILDCARD)
    @Produces(TEXT_PLAIN)
    public StreamingOutput echoOptions(final InputStream body) {
        return echoRequest(body);
    }

    @PUT
    @Path("echo")
    @Consumes(WILDCARD)
    @Produces(TEXT_PLAIN)
    public StreamingOutput echoPut(final InputStream body) {
        return echoRequest(body);
    }

    @DELETE
    @Path("echo")
    @Consumes(WILDCARD)
    @Produces(TEXT_PLAIN)
    public StreamingOutput echoDelete(final InputStream body) {
        return echoRequest(body);
    }

    @GET
    @Path("echo")
    @Consumes(WILDCARD)
    @Produces(TEXT_PLAIN)
    public StreamingOutput echoGet(final InputStream body) {
        return echoRequest(body);
    }

    @GET
    @Path("echo/{test}")
    @Consumes(WILDCARD)
    @Produces(TEXT_PLAIN)
    public StreamingOutput echoGetWild(final @PathParam("test") String test, final InputStream body) {
        return echoRequest(body);
    }

    @HEAD
    @Path("echo")
    @Consumes(WILDCARD)
    @Produces(TEXT_PLAIN)
    public StreamingOutput echoHead(final InputStream body) {
        return echoRequest(body);
    }

    private StreamingOutput echoRequest(final InputStream body) {
        return new StreamingOutput() {

            @Override
            public void write(OutputStream outputStream) throws IOException {
                final PrintStream out = new PrintStream(outputStream);

                final String query = httpServletRequest.getQueryString();

                out.printf("%s %s%n", request.getMethod(), uriInfo.getAbsolutePath());
                if (query != null) {
                    out.printf("[query: %s]%n", query);
                }

                final MultivaluedMap<String, String> map = httpHeaders.getRequestHeaders();
                for (String key : map.keySet()) {
                    final List<String> values = map.get(key);
                    out.printf("%s : %s%n", key, Join.join(", ", values));
                }

                out.println();

                IO.copy(body, out);
            }
        };
    }
}
