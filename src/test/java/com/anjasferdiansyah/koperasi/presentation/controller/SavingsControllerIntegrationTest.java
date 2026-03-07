package com.anjasferdiansyah.koperasi.presentation.controller;

import com.anjasferdiansyah.koperasi.domain.model.member.Member;
import com.anjasferdiansyah.koperasi.domain.model.savings.SavingsType;
import com.anjasferdiansyah.koperasi.domain.repository.MemberRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SavingsControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldOpenSavingsAccount() throws Exception {
        Member member = createAndSaveActiveMember();

        String body = objectMapper.writeValueAsString(Map.of(
                "memberId", member.getId(),
                "savingsType", SavingsType.SUKARELA.name()
        ));

        HttpResponse<String> response = postJson("/api/savings/accounts", body);

        assertEquals(201, response.statusCode());

        JsonNode json = objectMapper.readTree(response.body());
        assertTrue(json.path("success").asBoolean());
        assertEquals(member.getId().toString(), json.path("data").path("memberId").asText());
        assertEquals("SUKARELA", json.path("data").path("savingsType").asText());
        assertEquals("ACTIVE", json.path("data").path("status").asText());
        assertTrue(json.path("data").path("accountNo").asText().startsWith("SAV-SUK-"));
    }

    @Test
    void shouldReturnConflictWhenDuplicateSavingsTypeForSameMember() throws Exception {
        Member member = createAndSaveActiveMember();

        String body = objectMapper.writeValueAsString(Map.of(
                "memberId", member.getId(),
                "savingsType", SavingsType.POKOK.name()
        ));

        HttpResponse<String> first = postJson("/api/savings/accounts", body);
        assertEquals(201, first.statusCode());

        HttpResponse<String> second = postJson("/api/savings/accounts", body);
        assertEquals(409, second.statusCode());

        JsonNode json = objectMapper.readTree(second.body());
        assertEquals("SAVINGS_ACCOUNT_ALREADY_EXISTS", json.path("error").path("code").asText());
    }

    @Test
    void shouldDepositToSavingsAccount() throws Exception {
        Member member = createAndSaveActiveMember();
        UUID accountId = openSavingsAccount(member.getId(), SavingsType.WAJIB);

        String body = objectMapper.writeValueAsString(Map.of(
                "amount", 50_000,
                "externalReference", uniqueReference("EXT-WAJ"),
                "note", "Setoran wajib"
        ));

        HttpResponse<String> response = postJson("/api/savings/accounts/" + accountId + "/deposit", body);

        assertEquals(201, response.statusCode());
        JsonNode json = objectMapper.readTree(response.body());
        assertTrue(json.path("success").asBoolean());
        assertEquals("DEPOSIT", json.path("data").path("type").asText());
        assertEquals(0, BigDecimal.valueOf(50_000).compareTo(json.path("data").path("balanceAfter").decimalValue()));
        assertTrue(json.path("data").path("referenceNo").asText().startsWith("TRX-DEP-"));
        assertTrue(json.path("data").path("externalReference").asText().startsWith("EXT-WAJ-"));
    }

    @Test
    void shouldWithdrawFromSavingsAccount() throws Exception {
        Member member = createAndSaveActiveMember();
        UUID accountId = openSavingsAccount(member.getId(), SavingsType.SUKARELA);

        String depositBody = objectMapper.writeValueAsString(Map.of(
                "amount", 80_000,
                "externalReference", uniqueReference("EXT-DEP"),
                "note", "Setoran awal"
        ));
        HttpResponse<String> depositResponse = postJson("/api/savings/accounts/" + accountId + "/deposit", depositBody);
        assertEquals(201, depositResponse.statusCode());

        String withdrawBody = objectMapper.writeValueAsString(Map.of(
                "amount", 30_000,
                "externalReference", uniqueReference("EXT-WDR"),
                "note", "Tarik tunai"
        ));
        HttpResponse<String> withdrawResponse = postJson("/api/savings/accounts/" + accountId + "/withdraw", withdrawBody);

        assertEquals(201, withdrawResponse.statusCode());
        JsonNode json = objectMapper.readTree(withdrawResponse.body());
        assertTrue(json.path("success").asBoolean());
        assertEquals("WITHDRAWAL", json.path("data").path("type").asText());
        assertEquals(0, BigDecimal.valueOf(50_000).compareTo(json.path("data").path("balanceAfter").decimalValue()));
        assertTrue(json.path("data").path("referenceNo").asText().startsWith("TRX-WDR-"));
        assertTrue(json.path("data").path("externalReference").asText().startsWith("EXT-WDR-"));
    }

    @Test
    void shouldRejectWithdrawalWhenInsufficientBalance() throws Exception {
        Member member = createAndSaveActiveMember();
        UUID accountId = openSavingsAccount(member.getId(), SavingsType.SUKARELA);

        String body = objectMapper.writeValueAsString(Map.of(
                "amount", 10_000,
                "externalReference", uniqueReference("EXT-WDR"),
                "note", "Tarik lebih dulu"
        ));

        HttpResponse<String> response = postJson("/api/savings/accounts/" + accountId + "/withdraw", body);

        assertEquals(400, response.statusCode());
        JsonNode json = objectMapper.readTree(response.body());
        assertEquals("BAD_REQUEST", json.path("error").path("code").asText());
    }

    private HttpResponse<String> postJson(String path, String jsonBody)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private Member createAndSaveActiveMember() {
        String uniqueDigits = String.format("%012d", ThreadLocalRandom.current().nextLong(1_000_000_000_000L));
        String unique4 = uniqueDigits.substring(0, 4);
        String unique10 = uniqueDigits.substring(0, 10);

        Member member = Member.register(
                UUID.randomUUID(),
                "Integration User " + unique4,
                "integration+" + unique10 + "@mail.com",
                "Jakarta",
                "3173010101" + uniqueDigits.substring(0, 6),
                "6281234" + uniqueDigits.substring(0, 6),
                LocalDateTime.now(ZoneOffset.UTC)
        );
        member.approveKyc("integration-tester", LocalDateTime.now(ZoneOffset.UTC));
        return memberRepository.save(member);
    }

    private UUID openSavingsAccount(UUID memberId, SavingsType type) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "memberId", memberId,
                "savingsType", type.name()
        ));

        HttpResponse<String> response = postJson("/api/savings/accounts", body);
        assertEquals(201, response.statusCode());

        JsonNode json = objectMapper.readTree(response.body());
        return UUID.fromString(json.path("data").path("id").asText());
    }

    private String uniqueReference(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
