package com.reading.ifaceutil.model.cache;

import lombok.Data;

@Data
public class ArticleHeatCacheKey {
    // 榜单多的时候可以考虑 heat:{typeId}
    public static final String HEAT_KEY_PREFIX = "heat_zset:";
    // 榜单上的元素前缀
    public static final String ARTICLE_KEY_PREFIX = "heat_kv:";
    // 默认过期时间
    public static final int TIMEOUT = 24 * 60 * 60;
    public static CacheKey RcGetHeatKey() {
        return new CacheKey(HEAT_KEY_PREFIX + "0", TIMEOUT);
    }

    public static CacheKey RcGetArticleKey(Long articleId) {
        return new CacheKey(ARTICLE_KEY_PREFIX + articleId, TIMEOUT);
    }
}
