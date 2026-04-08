package com.medeat.mediscan.embedding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmbeddingIndex {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingIndex.class);
    private static final String EMBEDDING_PATH = "pill/final_embedding.json";

    private final List<EmbeddingItem> items = new ArrayList<>();

    @PostConstruct
    public void init() {
        load();
    }

    private void load() {
        try (InputStream is = new ClassPathResource(EMBEDDING_PATH).getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);
            JsonNode itemNodes = root.get("items");

            if (itemNodes == null || !itemNodes.isArray()) {
                log.warn("Embedding resource does not contain an 'items' array. path={}", EMBEDDING_PATH);
                return;
            }

            for (JsonNode node : itemNodes) {
                Long itemSeq = node.get("itemSeq").asLong();
                int dim = node.get("dim").asInt();

                float[] vec = new float[dim];
                for (int i = 0; i < dim; i++) {
                    vec[i] = (float) node.get("embedding").get(i).asDouble();
                }

                items.add(new EmbeddingItem(itemSeq, dim, vec));
            }

            log.info("Loaded {} embedding items.", items.size());
        } catch (Exception e) {
            log.warn("Failed to load embedding resource. MediScan similarity search will be unavailable. path={}",
                    EMBEDDING_PATH, e);
        }
    }

    public List<EmbeddingItem> searchTopN(float[] query, int topN) {
        if (items.isEmpty()) {
            return List.of();
        }

        return items.stream()
                .map(item -> {
                    double score = SimilarityUtils.cosine(query, item.getEmbedding());
                    item.setScore(score);
                    return item;
                })
                .sorted(Comparator.comparingDouble(EmbeddingItem::getScore).reversed())
                .limit(topN)
                .collect(Collectors.toList());
    }
}
