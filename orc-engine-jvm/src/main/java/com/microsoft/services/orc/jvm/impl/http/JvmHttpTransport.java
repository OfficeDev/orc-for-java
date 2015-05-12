package com.microsoft.services.orc.jvm.impl.http;

import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.services.orc.impl.http.BaseHttpTransport;
import com.microsoft.services.orc.impl.http.NetworkRunnable;
import com.microsoft.services.orc.interfaces.Request;
import com.microsoft.services.orc.interfaces.Response;

public class JvmHttpTransport extends BaseHttpTransport {
    @Override
    protected NetworkRunnable createNetworkRunnable(Request request, SettableFuture<Response> future) {
        return new JvmNetworkRunnable(request, future);
    }
}
