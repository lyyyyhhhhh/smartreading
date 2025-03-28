package com.reading.ifaceutil.mq.processor;

import java.util.Map;

public interface Processor {
    void Distribute(Map<String, Object> message);
}
