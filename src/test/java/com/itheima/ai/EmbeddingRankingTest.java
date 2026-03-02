package com.itheima.ai;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Qualifier; // 1. 导入这个注解

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@SpringBootTest
public class EmbeddingRankingTest {

    @Autowired
    @Qualifier("openAiEmbeddingModel") // 2. 指定使用 OpenAI (阿里云) 的模型
    private EmbeddingModel embeddingModel; // 注入 Spring AI 的模型

    /**
     * 测试主流程：一句话 vs 多句话，计算相似度并排序
     */
    @Test
    public void testEmbeddingSimilarityAndRanking() {
        // 1. 定义基准文本 A
        String textA = "我喜欢在春天的公园里散步，感受温暖阳光";

        // 2. 定义一组不同语义的文本 B (包含相似、部分相关、无关)
        List<String> textBList = List.of(
                "秋天去山里看红叶很不错",          // 季节/地点不同 (部分相关)
                "我讨厌下雨天，哪里都不想去",       // 情绪相反 (负相关)
                "春天去公园晒太阳很舒服",          // 语义非常相似 (正相关)
                "量子力学的纠缠态非常难以理解",     // 完全无关 (科技类)
                "周末在花园里走走心情很好",         // 语义相似 (同义替换)
                "今天的股票跌得很惨",              // 完全无关 (金融类)
                "公园里春暖花开，适合漫步"          // 语义极度相似 (近义词)
        );

        log.info("=== 基准文本 A: {} ===", textA);
        log.info("待比较文本数量: {}", textBList.size());

        // 3. 向量化文本 A
        float[] vectorA = getEmbeddingVector(textA);

        // 4. 循环向量化文本 B 并计算得分
        List<SimilarityResult> results = new ArrayList<>();

        for (String textB : textBList) {
            float[] vectorB = getEmbeddingVector(textB);

            // 计算两种指标
            double cosineSim = calculateCosineSimilarity(vectorA, vectorB);
            double euclideanDist = calculateEuclideanDistance(vectorA, vectorB);

            results.add(new SimilarityResult(textB, cosineSim, euclideanDist));
        }

        // 5. 【关键】排序
        // 规则：余弦相似度越高越好 (降序)；欧式距离越小越好 (升序)
        results.sort(Comparator.comparingDouble(SimilarityResult::getCosineSimilarity).reversed());

        // 6. 打印排序后的结果表格
        log.info("\n{:-^80}", " 相似度排行榜 (按余弦相似度降序) ");
        log.info(String.format("%-4s | %-35s | %-10s | %-10s", "排名", "文本内容", "余弦相似度", "欧式距离"));
        log.info("-".repeat(80));

        int rank = 1;
        for (SimilarityResult r : results) {
            // 截取过长文本以便显示
            String displayText = r.text.length() > 32 ? r.text.substring(0, 32) + "..." : r.text;
            log.info(String.format("%-4d | %-35s | %-10.6f | %-10.6f",
                    rank++,
                    displayText,
                    r.getCosineSimilarity(),
                    r.getEuclideanDistance()
            ));
        }
        log.info("-".repeat(80));
        log.info("结论：排名越靠前的句子，语义与 A 越接近。");
    }

    // ================= 辅助方法 =================

    /**
     * 调用模型获取向量
     */
    private float[] getEmbeddingVector(String text) {
        EmbeddingResponse response = embeddingModel.embedForResponse(List.of(text));
        return response.getResult().getOutput();
    }

    /**
     * 计算余弦相似度 (越接近 1 越相似)
     */
    private double calculateCosineSimilarity(float[] v1, float[] v2) {
        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (int i = 0; i < v1.length; i++) {
            dot += v1[i] * v2[i];
            norm1 += v1[i] * v1[i];
            norm2 += v2[i] * v2[i];
        }
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * 计算欧式距离 (越接近 0 越相似)
     */
    private double calculateEuclideanDistance(float[] v1, float[] v2) {
        double sum = 0.0;
        for (int i = 0; i < v1.length; i++) {
            float diff = v1[i] - v2[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    // 内部静态类用于存储结果
    static class SimilarityResult {
        String text;
        double cosineSimilarity;
        double euclideanDistance;

        public SimilarityResult(String text, double cosineSimilarity, double euclideanDistance) {
            this.text = text;
            this.cosineSimilarity = cosineSimilarity;
            this.euclideanDistance = euclideanDistance;
        }

        public double getCosineSimilarity() { return cosineSimilarity; }
        public double getEuclideanDistance() { return euclideanDistance; }
    }
}