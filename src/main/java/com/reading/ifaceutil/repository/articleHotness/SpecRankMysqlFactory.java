package com.reading.ifaceutil.repository.articleHotness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecRankMysqlFactory {
    @Autowired
    private SpecRankMysqlDefault specRankMysqlDefault;

    // 可以对榜单进行分类, 不同类的榜单可能对应不同的处理规则
    public ISpecRankMysql getInstance() {
        return specRankMysqlDefault;
    }
}