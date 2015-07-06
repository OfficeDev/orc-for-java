package com.microsoft.services.orc.resolvers;

import com.microsoft.services.orc.core.*;
import com.microsoft.services.orc.http.*;
import com.microsoft.services.orc.http.impl.*;
import com.microsoft.services.orc.log.impl.LoggerImpl;
import com.microsoft.services.orc.serialization.JsonSerializer;
import com.microsoft.services.orc.serialization.impl.GsonSerializer;

public class JavaDependencyResolver implements DependencyResolver {

    private LoggerImpl logger;
    private String token;

    public JavaDependencyResolver(String token) {
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
