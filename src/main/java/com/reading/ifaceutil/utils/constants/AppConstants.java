package com.reading.ifaceutil.utils.constants;

import java.util.Map;
import java.util.Set;

public class AppConstants {
    // 任务状态机
    public static final String PENDING = "PENDING";
    public static final String COMPLETED = "COMPLETED";
    public static final String CLAIMED = "CLAIMED";

    // 任务名称
    public static final String SIGN_IN = "SIGN_IN";
    public static final String COMMENT = "COMMENT";
    public static final String READING = "READING";
    public static final Map<String, String> taskToShow = Map.of(
            SIGN_IN, "签到",
            COMMENT, "评论",
            READING, "阅读时长"
    );

    public static final Map<String, String> taskTypeToProcessorMap = Map.of(
            "SIGN_IN", "signInTaskProcessor",
            "COMMENT", "commentTaskProcessor",
            "READING", "readingTaskProcessor"
    );


    // 日志表关联的数据库表名
    public static final String USER_TASK_PROGRESS = "user_task_progress";
    public static final String USER_PURCHASED_ARTICLES = "user_purchased_articles";

    public static final Set<String> ALLOWED_TABLES = Set.of("user_task_progress", "user_purchased_articles", "article_heat");


    // 文章热度表名
    public static final String ARTICLE_HEAT = "article_heat";

    // 积分兑换热度比例
    public static final int POINTS_TO_HEAT = 5;

    // 增加文章热度的互动事件
    public static final Set<String> HEAT_INTERACTIONS = Set.of(COMMENT, READING);

    public static final Map<String, Double> INTERACTIONS_TO_HEAT = Map.of(
            "COMMENT", 2.0,
            "READING", 1.0
    );

}
