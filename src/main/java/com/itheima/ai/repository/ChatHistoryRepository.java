package com.itheima.ai.repository;

import java.util.List;

/**
 * 用来维护保存会话侧边栏
 * @author WU,Rowan
 * @date 2026/2/27
 */
public interface ChatHistoryRepository {
    /**
     * 保存会话记录
     * @param type 业务类型，如：chat、service、pdf
     * @param chatId 会话ID
     */
    void save(String type, String chatId);

    /**
     * 获取会话ID列表
     * @param type 业务类型，如：chat、service、pdf
     * @return 会话ID列表
     */
    List<String> getChatIds(String type);
}
