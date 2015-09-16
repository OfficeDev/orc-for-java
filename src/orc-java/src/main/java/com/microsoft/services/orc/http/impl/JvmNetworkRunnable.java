package com.microsoft.services.orc.http.impl;

import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.services.orc.http.NetworkRunnable;
import com.microsoft.services.orc.http.Request;
import com.microsoft.services.orc.http.Response;

import org.apache.commons.httpclient.util.URIUtil;
import org.apache.http.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.message.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.BitSet;
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
        CloseableHttpClient client = null;
        try {

            client = HttpClients.createDefault();

            String rawUrl = "https://www.google.com/api/v1.0/users%2Ftest@example.onmicrosoft.com/Events/?$filter=Start%20gt%202015-01-01T00:00:00.000+02:00&$select=ID,Body";
            String scapedUrl = URIUtil.encodeQuery(rawUrl);

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
