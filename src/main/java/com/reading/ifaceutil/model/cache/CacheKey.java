package com.reading.ifaceutil.model.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheKey {
    public String redisKey;
    public int timeout ;
}
