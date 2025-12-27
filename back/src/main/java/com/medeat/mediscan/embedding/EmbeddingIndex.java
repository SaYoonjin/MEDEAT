package com.medeat.mediscan.embedding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class EmbeddingIndex {

    private static final String EMBEDDING_PATH = "pill/final_embedding.json";

    private final List<EmbeddingItem> items = new ArrayList<>();

    @PostConstruct
    public void init() {
        load();
    }

    private void load() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = new ClassPathResource(EMBEDDING_PATH).getInputStream();
            JsonNode root = mapper.readTree(is);

            for (JsonNode node : root.get("items")) {
                Long itemSeq = node.get("itemSeq").asLong();
                int dim = node.get("dim").asInt();

                float[] vec = new float[dim];
                for (int i = 0; i < dim; i++) {
                    vec[i] = (float) node.get("embedding").get(i).asDouble();
                }

                items.add(new EmbeddingItem(itemSeq, dim, vec));
            }

            System.out.println("[MediScan] embedding loaded = " + items.size());

        } catch (Exception e) {
            throw new RuntimeException("Failed to load final_embedding.json", e);
        }
    }

    public List<EmbeddingItem> searchTopN(float[] query, int topN) {
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
