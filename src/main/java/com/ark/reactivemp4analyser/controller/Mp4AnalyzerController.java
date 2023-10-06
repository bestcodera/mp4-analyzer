package com.ark.reactivemp4analyser.controller;

import com.ark.reactivemp4analyser.model.BoxInfo;
import com.ark.reactivemp4analyser.service.MP4AnalyzerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class Mp4AnalyzerController {

    private final MP4AnalyzerService analyzerService;

    @GetMapping("/analyze")
    public Mono<ResponseEntity<List<BoxInfo>>> analyzeMp4File(@RequestParam("url") String url) {
        return analyzerService.parseMp4Boxes(url)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

}