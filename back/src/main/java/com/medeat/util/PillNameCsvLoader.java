package com.medeat.util;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class PillNameCsvLoader {

    private final Map<Long, String> itemSeqToNameMap = new HashMap<>();

    @PostConstruct
    public void load() {
        try {
            InputStream is = getClass()
                    .getClassLoader()
                    .getResourceAsStream("pill/pill_name.csv");

            if (is == null) {
                System.out.println("❌ pill_name.csv 파일을 찾을 수 없습니다.");
                return;
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8)
            );

            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }

                String[] cols = line.split(",", -1);
                if (cols.length < 5) continue;

                // ⭐ 네가 강조한 바로 그 부분
                String name = cols[3].trim();     // 품목명
                String seqStr = cols[4].trim();  // 품목기준코드

                if (name.isEmpty() || seqStr.isEmpty()) continue;

                try {
                    Long itemSeq = Long.parseLong(seqStr);
                    itemSeqToNameMap.put(itemSeq, name);
                } catch (NumberFormatException ignore) {}
            }

            System.out.println("✅ CSV 약품명 로딩 완료: " + itemSeqToNameMap.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getName(String itemSeq) {
        if (itemSeq == null) return null;

        try {
            Long key = Long.parseLong(itemSeq);
            return itemSeqToNameMap.get(key);
        } catch (NumberFormatException e) {
            return null;
        }
    }


}
