package com.medeat.util;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class PillNameCsvLoader {

    private static final Logger log = LoggerFactory.getLogger(PillNameCsvLoader.class);

    private final Map<Long, String> itemSeqToNameMap = new HashMap<>();

    @PostConstruct
    public void load() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("pill/pill_name.csv")) {
            if (is == null) {
                log.warn("pill_name.csv file was not found in classpath.");
                return;
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                boolean first = true;

                while ((line = br.readLine()) != null) {
                    if (first) {
                        first = false;
                        continue;
                    }

                    String[] cols = line.split(",", -1);
                    if (cols.length < 5) {
                        continue;
                    }

                    String name = cols[3].trim();
                    String seqStr = cols[4].trim();

                    if (name.isEmpty() || seqStr.isEmpty()) {
                        continue;
                    }

                    try {
                        itemSeqToNameMap.put(Long.parseLong(seqStr), name);
                    } catch (NumberFormatException ignore) {
                        log.debug("Skipping invalid pill item sequence: {}", seqStr);
                    }
                }
            }

            log.info("Loaded {} pill names from CSV.", itemSeqToNameMap.size());
        } catch (Exception e) {
            log.error("Failed to load pill_name.csv.", e);
        }
    }

    public String getName(String itemSeq) {
        if (itemSeq == null) {
            return null;
        }

        try {
            return itemSeqToNameMap.get(Long.parseLong(itemSeq));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
