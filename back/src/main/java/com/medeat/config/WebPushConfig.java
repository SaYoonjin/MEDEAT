package com.medeat.config;

import org.springframework.context.annotation.Bean;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Configuration;
import nl.martijndwars.webpush.PushService;

@Configuration
public class WebPushConfig {
	
	static {
        // BC Provider 등록
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Bean
    public PushService pushService() throws Exception {

        String publicKey = "BDDDSRuFrr4hKJr9qtwwDTdC4CU8sgikrB_fv-KaoIxSbhedLziblvfhITNygkS2lp8rt7_lHEBlsPkiZ4l7UHU";
        String privateKey = "uuV8Oz43qteSv_Pj8-DK6kqXGxAG8m15KoKmbdEfZmk";

        PushService service = new PushService();
        service.setPublicKey(publicKey);
        service.setPrivateKey(privateKey);
        service.setSubject("mailto:admin@me.com");

        return service;
    }
}
