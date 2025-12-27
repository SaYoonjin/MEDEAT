package com.medeat.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VectorParser {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 지원 포맷
     * 1) "[0.1, 0.2, ...]" (순수 배열)
     * 2) {"embedding":[...]} / {"vector":[...]} / {"feat":[...]}
     * 3) {"itemSeq":..., "dim":..., "embedding":[...]} 같은 레코드 전체
     */
    public static double[] parse(String json) {
        if (json == null) return null;
        String s = json.trim();
        if (s.isEmpty()) return null;

        try {
            JsonNode root = MAPPER.readTree(s);

            JsonNode arr = null;

            // (1) root 자체가 배열이면 OK
            if (root.isArray()) {
                arr = root;
            }

            // (2) root가 객체면 embedding/vector/feat/features 중 하나를 찾기
            if (arr == null && root.isObject()) {
                String[] keys = {"embedding", "vector", "emb", "feat", "features"};
                for (String k : keys) {
                    JsonNode node = root.get(k);
                    if (node != null && node.isArray()) {
                        arr = node;
                        break;
                    }
                }
            }

            if (arr == null || !arr.isArray() || arr.size() == 0) return null;

            double[] out = new double[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                JsonNode v = arr.get(i);
                if (v == null || !v.isNumber()) return null;
                out[i] = v.asDouble();
            }
            return out;

        } catch (Exception e) {
            return null;
        }
    }
}
