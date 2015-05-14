package com.microsoft.services.orc.jvm.impl;

import com.microsoft.services.orc.interfaces.Base64Encoder;
import com.microsoft.services.orc.impl.ByteArrayTypeAdapterBase;

public class ByteArrayTypeAdapterImpl extends ByteArrayTypeAdapterBase {
    @Override
    protected Base64Encoder getBase64Encoder() {
        return Base64EncoderImpl.getInstance();
    }
}
