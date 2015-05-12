package com.microsoft.services.orc.jvm.impl;

import com.microsoft.services.orc.Constants;
import com.microsoft.services.orc.impl.OrcURLImpl;
import com.microsoft.services.orc.jvm.impl.http.JvmHttpTransport;
import com.microsoft.services.orc.impl.http.RequestImpl;
import com.microsoft.services.orc.interfaces.*;

public class JvmDependencyResolver implements DependencyResolver {

    private LoggerImpl logger;
    private String token;

    public JvmDependencyResolver(String token) {
        this.logger = new LoggerImpl();
        this.token = token;
    }

    @Override
    public HttpTransport getHttpTransport() {
        return new JvmHttpTransport();
    }

    @Override
    public LoggerImpl getLogger() {
        return this.logger;
    }

    @Override
    public JsonSerializer getJsonSerializer() {
        return new GsonSerializer();
    }

    @Override
    public OrcURL createODataURL() {
        return new OrcURLImpl();
    }

    @Override
    public Request createRequest() {
        return new RequestImpl();
    }

    @Override
    public String getPlatformUserAgent(String productName) {

        String osName = System.getProperty("os.name");
        return String.format(
                "%s/1.0 (lang=%s; os=%s; version=%s)",
                productName, "Java", osName, Constants.SDK_VERSION);
    }

    @Override
    public Credentials getCredentials() {
        return new Credentials() {
            @Override
            public void prepareRequest(Request request) {
                request.addHeader("Authorization", "Bearer " + token);
            }
        };
    }
}
