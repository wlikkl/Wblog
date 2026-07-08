package top.naccl.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import top.naccl.service.ChatService;

import java.util.*;

@Service
public class ChatServiceImpl implements ChatService {
	@Value("${chat.api.key}")
	private String apiKey;

	@Value("${chat.api.url}")
	private String apiUrl;

	@Value("${chat.api.model}")
	private String model;

	@Autowired
	private RestTemplate restTemplate;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String chat(String message) {
		try {
			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("model", model);
			requestBody.put("stream", false);

			List<Map<String, String>> messages = new ArrayList<>();
			Map<String, String> systemMsg = new HashMap<>();
			systemMsg.put("role", "system");
			systemMsg.put("content", "你是一个友好的博客助手，帮助访客解答问题。请用中文回复，简洁明了。");
			messages.add(systemMsg);

			Map<String, String> userMsg = new HashMap<>();
			userMsg.put("role", "user");
			userMsg.put("content", message);
			messages.add(userMsg);

			requestBody.put("messages", messages);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(apiKey);

			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

			ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

			JsonNode root = objectMapper.readTree(response.getBody());
			return root.path("choices").get(0).path("message").path("content").asText();
		} catch (Exception e) {
			e.printStackTrace();
			return "抱歉，我暂时无法回复，请稍后再试。";
		}
	}
}
