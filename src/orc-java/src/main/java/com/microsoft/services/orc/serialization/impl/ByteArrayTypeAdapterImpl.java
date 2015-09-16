package com.microsoft.services.orc.serialization.impl;

import com.microsoft.services.orc.http.Base64Encoder;
import com.microsoft.services.orc.http.impl.Base64EncoderImpl;
import com.microsoft.services.orc.serialization.ByteArrayTypeAdapterBase;

public class ByteArrayTypeAdapterImpl extends ByteArrayTypeAdapterBase {
    @Override
    protected Base64Encoder getBase64Encoder() {
        return Base64EncoderImpl.getInstance();
    }
}
