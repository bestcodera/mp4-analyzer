package com.ark.reactivemp4analyser.service;

import com.ark.reactivemp4analyser.model.BoxInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MP4AnalyzerService {

    public Mono<List<BoxInfo>> parseMp4Boxes(String url) {
        return Mono.fromCallable(() -> {
                    try (InputStream inputStream = URI.create(url).toURL().openStream()) {
                        List<BoxInfo> boxInfos = new ArrayList<>();
                        analyzeMp4Boxes(inputStream, boxInfos, -1L);
                        return boxInfos;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .subscribeOn(Schedulers.boundedElastic()); // Use a boundedElastic scheduler to offload blocking work
    }

    private void analyzeMp4Boxes(InputStream inputStream, List<BoxInfo> boxInfos, long parentSize) throws IOException {
        byte[] sizeBytes = new byte[4];
        byte[] typeBytes = new byte[4];
        long counter = 0L;

        while (counter != (parentSize - 8) && inputStream.read(sizeBytes) == 4 && inputStream.read(typeBytes) == 4) {

            long size = new BigInteger(sizeBytes).longValue();
            String type = new String(typeBytes);

            counter += size;

            BoxInfo boxInfo = new BoxInfo(size, type);
            boxInfos.add(boxInfo);

            if (type.equalsIgnoreCase("MOOF") || type.equalsIgnoreCase("TRAF")) {
                analyzeMp4Boxes(inputStream, boxInfo.getSubBoxes(), size);
            } else {
                // Skip payload if any
                if (size > 8) {
                    inputStream.skip(size - 8);
                }
            }
        }
    }
}
