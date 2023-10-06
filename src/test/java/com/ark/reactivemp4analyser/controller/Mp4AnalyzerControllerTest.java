package com.ark.reactivemp4analyser.controller;

import com.ark.reactivemp4analyser.model.BoxInfo;
import com.ark.reactivemp4analyser.service.MP4AnalyzerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

@SpringBootTest
@AutoConfigureWebTestClient
public class Mp4AnalyzerControllerTest {

    @MockBean
    private MP4AnalyzerService analyzerService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(new Mp4AnalyzerController(analyzerService)).build();
    }

    @Test
    void testAnalyzeMp4File() {
        String testUrl = "https://demo.castlabs.com/tmp/text0.mp4";
        List<BoxInfo> expectedBoxInfos = createExpectedBoxInfos();

        // Mock the service method to return the expected data
        Mockito.when(analyzerService.parseMp4Boxes(testUrl)).thenReturn(Mono.just(expectedBoxInfos));

        webTestClient.get()
                .uri("/analyze?url={url}", testUrl)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(BoxInfo.class);
    }

    private List<BoxInfo> createExpectedBoxInfos() {

        BoxInfo b1 = new BoxInfo(181, "moof");
        BoxInfo b2 = new BoxInfo(17908, "mdat");
        BoxInfo b3 = new BoxInfo(16, "mfhd");
        BoxInfo b4 = new BoxInfo(157, "traf");
        BoxInfo b5 = new BoxInfo(24, "tfhd");
        BoxInfo b6 = new BoxInfo(20, "trun");
        BoxInfo b7 = new BoxInfo(44, "uuid");
        BoxInfo b8 = new BoxInfo(61, "uuid");

        b1.setSubBoxes(List.of(b3, b4));
        b4.setSubBoxes(List.of(b5, b6, b7, b8));

        return List.of(b1, b2);
    }
}
