package com.ark.reactivemp4analyser.service;

import com.ark.reactivemp4analyser.model.BoxInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.List;

import static org.mockito.Mockito.*;

public class MP4AnalyzerServiceTest {

    private MP4AnalyzerService mp4AnalyzerService;
    private VirtualTimeScheduler virtualTimeScheduler;
    private InputStream mockInputStream;

    @BeforeEach
    void setUp() {
        mp4AnalyzerService = new MP4AnalyzerService();
        virtualTimeScheduler = VirtualTimeScheduler.getOrSet();
        mockInputStream = mock(InputStream.class);
    }

    @Test
    void testParseMp4Boxes() throws IOException {
        String testUrl = "https://demo.castlabs.com/tmp/text0.mp4";
        List<BoxInfo> expectedBoxInfos = createExpectedBoxInfos(); // Create your expected box infos here

        // Mock the blocking operation to open an InputStream
        when(mockInputStream.read(any(byte[].class))).thenReturn(4).thenReturn(4).thenReturn(-1); // Adjust this based on your test case
        when(mockInputStream.skip(anyLong())).thenReturn(0L);

        PublisherProbe<List<BoxInfo>> probe = PublisherProbe.of(Mono.fromCallable(() -> {
            // Mock the blocking call inside the fromCallable
            try (InputStream ignored = URI.create(testUrl).toURL().openStream()) {
                return expectedBoxInfos;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));

        // Use VirtualTimeScheduler to control the timing of the test
        Mono<List<BoxInfo>> resultMono = mp4AnalyzerService.parseMp4Boxes(testUrl)
                .elapsed()
                .map(tuple -> {
                    virtualTimeScheduler.advanceTimeBy(Duration.ofDays(tuple.getT1()));
                    return tuple.getT2();
                })
                .flatMap(boxInfos -> probe.mono());

        StepVerifier.withVirtualTime(() -> resultMono)
                .expectSubscription()
                .expectNext(expectedBoxInfos)
                .verifyComplete();

        probe.assertWasSubscribed();
        probe.assertWasRequested();
    }

    private List<BoxInfo> createExpectedBoxInfos() {
        // Create and return the expected BoxInfo list for your test case
        return List.of(
                new BoxInfo(32, "ftyp"),
                new BoxInfo(101871, "moov")
                // Add more BoxInfo objects as needed
        );
    }
}
