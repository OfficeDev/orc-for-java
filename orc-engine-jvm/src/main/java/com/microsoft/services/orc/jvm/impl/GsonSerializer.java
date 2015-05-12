package com.microsoft.services.orc.jvm.impl;

import com.microsoft.services.orc.impl.ByteArrayTypeAdapterBase;
import com.microsoft.services.orc.impl.GsonSerializerBase;

public class GsonSerializer extends GsonSerializerBase {
    @Override
    protected ByteArrayTypeAdapterBase getByteArrayTypeAdapter() {
        return new ByteArrayTypeAdapterImpl();
    }
}
