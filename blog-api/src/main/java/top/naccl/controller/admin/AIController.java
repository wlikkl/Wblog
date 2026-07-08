package top.naccl.controller.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import top.naccl.model.vo.Result;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/admin")
public class AIController {
	private static final String API_URL = "https://apihub.agnes-ai.com/v1/chat/completions";
	private static final String IMAGE_API_URL = "https://apihub.agnes-ai.com/v1/images/generations";
	private static final String API_KEY = "sk-1abnfGshjccgt9JJga2BCzH1WC71LgFppZuIXjBrC4CQeo0O";

	@Autowired
	private RestTemplate restTemplate;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@PostMapping("/ai/article")
	public Result generateArticle(@RequestBody Map<String, String> params) {
		String topic = params.get("topic");
		if (topic == null || topic.trim().isEmpty()) {
			return Result.error("请输入文章主题");
		}
		try {
			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("model", "agnes-2.0-flash");
			requestBody.put("stream", false);

			List<Map<String, String>> messages = new ArrayList<>();
			Map<String, String> systemMsg = new HashMap<>();
			systemMsg.put("role", "system");
			systemMsg.put("content", "你是一个技术博客作者，请用中文撰写高质量的技术文章。\n" +
				"返回格式：\n" +
				"1. 第一行是文章标题（以#开头）\n" +
				"2. 接下来是文章描述（1-2句话）\n" +
				"3. 然后是正文（Markdown格式，800-1500字）\n" +
				"4. 在正文适当位置插入 2-4 个图片占位符，格式：<!-- image: 详细的英文图片描述，用于AI生成配图 -->\n" +
				"   例如：<!-- image: A modern laptop showing code editor with Spring Boot logo on screen, professional tech illustration, clean style -->");
			messages.add(systemMsg);

			Map<String, String> userMsg = new HashMap<>();
			userMsg.put("role", "user");
			userMsg.put("content", "请写一篇关于「" + topic + "」的技术博客文章，包含标题、简介和正文，800-1500字左右。正文中需要 2-4 张配图，使用 <!-- image: 描述 --> 格式标记。");
			messages.add(userMsg);

			requestBody.put("messages", messages);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(API_KEY);

			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

			JsonNode root = objectMapper.readTree(response.getBody());
			String content = root.path("choices").get(0).path("message").path("content").asText();

			String title = "";
			String body = content;

			if (content.startsWith("#")) {
				int titleEnd = content.indexOf("\n");
				if (titleEnd > 0) {
					title = content.substring(1, titleEnd).trim();
					body = content.substring(titleEnd + 1).trim();
				}
			}

			// 处理图片占位符
			body = processImagePlaceholders(body);

			Map<String, Object> result = new HashMap<>();
			result.put("title", title);
			result.put("content", body);
			return Result.ok("生成成功", result);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("生成失败：" + e.getMessage());
		}
	}

	/**
	 * 将正文中的 <!-- image: 描述 --> 占位符替换为实际生成的图片
	 */
	private String processImagePlaceholders(String body) {
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("<!--\\s*image:\\s*(.+?)\\s*-->");
		java.util.regex.Matcher matcher = pattern.matcher(body);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String prompt = matcher.group(1).trim();
			String imageUrl = generateImageInternal(prompt);
			String replacement;
			if (imageUrl != null) {
				replacement = "![配图](" + imageUrl + ")";
			} else {
				// 生成失败时移除占位符
				replacement = "";
			}
			matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(replacement));
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	// AI 图片本地保存目录（Linux 服务器路径）
	private static final String AI_IMAGE_DIR = "/root/nblog/upload/ai/";

	/**
	 * 内部调用图片生成 API，下载图片保存到本地，返回本地访问路径，失败返回null
	 */
	private String generateImageInternal(String prompt) {
		try {
			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("model", "agnes-image-2.1-flash");
			requestBody.put("prompt", prompt);
			requestBody.put("n", 1);
			requestBody.put("size", "1024x1024");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(API_KEY);

			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(IMAGE_API_URL, entity, String.class);

			JsonNode root = objectMapper.readTree(response.getBody());
			String imageUrl = root.path("data").get(0).path("url").asText();
			if (imageUrl.isEmpty()) return null;

			// 下载图片保存到本地
			String fileName = "ai_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ".png";
			Path dir = Paths.get(AI_IMAGE_DIR);
			Files.createDirectories(dir);
			Path filePath = dir.resolve(fileName);

			try (InputStream in = new URL(imageUrl).openStream()) {
				Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
			}

			System.out.println("图片已保存到本地: " + filePath);
			return "/image/ai/" + fileName;
		} catch (Exception e) {
			System.err.println("图片生成失败: " + prompt + " - " + e.getMessage());
			return null;
		}
	}

	@PostMapping("/ai/image")
	public Result generateImage(@RequestBody Map<String, String> params) {
		String prompt = params.get("prompt");
		if (prompt == null || prompt.trim().isEmpty()) {
			return Result.error("请输入图片描述");
		}
		try {
			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("model", "agnes-image-2.1-flash");
			requestBody.put("prompt", prompt);
			requestBody.put("n", 1);
			requestBody.put("size", "1024x1024");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(API_KEY);

			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(IMAGE_API_URL, entity, String.class);

			JsonNode root = objectMapper.readTree(response.getBody());
			String imageUrl = root.path("data").get(0).path("url").asText();
			if (imageUrl.isEmpty()) {
				return Result.error("图片生成失败");
			}

			// 下载图片保存到本地
			String fileName = "ai_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ".png";
			Path dir = Paths.get(AI_IMAGE_DIR);
			Files.createDirectories(dir);
			Path filePath = dir.resolve(fileName);

			try (InputStream in = new URL(imageUrl).openStream()) {
				Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
			}

			String localUrl = "/image/ai/" + fileName;
			System.out.println("图片已保存到本地: " + filePath);
			return Result.ok("生成成功", localUrl);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("生成失败：" + e.getMessage());
		}
	}
}
