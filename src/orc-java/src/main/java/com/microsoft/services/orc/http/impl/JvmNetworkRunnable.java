package com.microsoft.services.orc.http.impl;

import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.services.orc.http.NetworkRunnable;
import com.microsoft.services.orc.http.Request;
import com.microsoft.services.orc.http.Response;
import com.squareup.okhttp.HttpUrl;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.EntityEnclosingRequestWrapper;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JvmNetworkRunnable extends NetworkRunnable {
    /**
     * Initializes the network runnable
     *
     * @param request The request to execute
     * @param future  Future for the operation
     */
    public JvmNetworkRunnable(Request request, SettableFuture<Response> future) {
        super(request, future);
    }

    @Override
    public void run() {

        //based on 2.6 http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html
        ConnectionKeepAliveStrategy keepAliveStrategy = new ConnectionKeepAliveStrategy() {

            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {

                HeaderElementIterator it = new BasicHeaderElementIterator(
                        response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase("timeout")) {
                        try {
                            return Long.parseLong(value) * 1000;
                        } catch (NumberFormatException ignore) {
                        }
                    }
                }
                return 30 * 1000;
            }
        };


        CloseableHttpClient client = null;
        try {

            HttpClients.custom().setKeepAliveStrategy(keepAliveStrategy);
            client = HttpClients.createDefault();
            String scapedUrl = HttpUrl.parse(mRequest.getUrl().toString()).toString();

            BasicHttpEntityEnclosingRequest realRequest = new BasicHttpEntityEnclosingRequest(mRequest.getVerb().toString(), scapedUrl);
            EntityEnclosingRequestWrapper wrapper = new EntityEnclosingRequestWrapper(realRequest);

            Map<String, String> headers = mRequest.getHeaders();
            for (String key : headers.keySet()) {
                wrapper.addHeader(key, headers.get(key));
            }

            if (mRequest.getContent() != null) {
                ByteArrayEntity entity = new ByteArrayEntity(mRequest.getContent());
                wrapper.setEntity(entity);
            } else if (mRequest.getStreamedContent() != null) {
                InputStream stream = mRequest.getStreamedContent();
                InputStreamEntity entity = new InputStreamEntity(stream, mRequest.getStreamedContentSize());
                wrapper.setEntity(entity);
            }

            HttpResponse realResponse = client.execute(wrapper);
            int status = realResponse.getStatusLine().getStatusCode();

            Map<String, List<String>> responseHeaders = new HashMap<String, List<String>>();
            for (Header header : realResponse.getAllHeaders()) {
                List<String> headerValues = new ArrayList<String>();
                for (HeaderElement element : header.getElements()) {
                    headerValues.add(element.getValue());
                }
                responseHeaders.put(header.getName(), headerValues);
            }

            HttpEntity entity = realResponse.getEntity();
            InputStream stream = null;

            if (entity != null) {
                stream = entity.getContent();
            }

            if (stream != null) {
                Response response = new ResponseImpl(
                        stream,
                        status,
                        responseHeaders,
                        client);

                mFuture.set(response);
            } else {
                client.close();
                mFuture.set(new EmptyResponse(status, responseHeaders));
            }

        } catch (Throwable t) {
            if (client != null) {
                try {
                    client.close();
                } catch (Throwable ignore) {
                }
            }

            mFuture.setException(t);
        }
    }
}
