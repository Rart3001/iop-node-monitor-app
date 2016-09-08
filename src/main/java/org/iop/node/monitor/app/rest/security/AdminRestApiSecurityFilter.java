/*
 * @#SecurityFilter  - 2016
 * Copyright bitDubai.com., All rights reserved.
 * You may not modify, use, reproduce or distribute this software.
 */
package org.iop.node.monitor.app.rest.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.TextCodec;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The Class <code>SecurityFilter</code> implements
 * <p/>
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 21/06/16.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class AdminRestApiSecurityFilter implements Filter {

    /**
     * Represent the logger instance
     */
    private final Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(AdminRestApiSecurityFilter.class));

    /**
     * (non-javadoc)
     * @see Filter#init(FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.info("init");
    }

    /**
     * (non-javadoc)
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        LOG.info("doFilter");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {

            LOG.info("Authorization = " + httpRequest.getHeader("Authorization"));

            final String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader == null || !authHeader.contains("Bearer ")) {
                LOG.error("Missing or invalid Authorization header.");
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header.");
                return;
            }

            String token = authHeader.substring("Bearer ".length(), authHeader.length());
            LOG.info("token = " + token);

            final Claims claims = Jwts.parser().setSigningKey(TextCodec.BASE64.encode(JWTManager.getKey())).parseClaimsJws(token).getBody();
            if (claims.getSubject().equals("fermat")){
                chain.doFilter(request, response);
            }

        } catch (final SignatureException e) {
            e.printStackTrace();
            LOG.error( "Invalid token: "+e.getMessage());
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: "+e.getMessage());
        } catch (final Exception e) {
            e.printStackTrace();
            LOG.error( "Error in token: "+e.getMessage());
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: "+e.getMessage());
        }
    }

    /**
     * (non-javadoc)
     * @see Filter#destroy()
     */
    @Override
    public void destroy() {
        LOG.info("destroy");
    }
}
