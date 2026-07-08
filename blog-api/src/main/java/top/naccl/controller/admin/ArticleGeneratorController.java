package top.naccl.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import top.naccl.model.vo.Result;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 自动生成文章配置管理
 */
@RestController
@RequestMapping("/admin")
public class ArticleGeneratorController {

    private static final String CONFIG_FILE = System.getProperty("user.home") + "/article-generator-config.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取配置
     */
    @GetMapping("/article-generator/config")
    public Result getConfig() {
        try {
            File file = new File(CONFIG_FILE);
            if (!file.exists()) {
                // 返回默认配置
                return Result.ok("获取成功", getDefaultConfig());
            }
            Map<String, Object> config = objectMapper.readValue(file, Map.class);
            return Result.ok("获取成功", config);
        } catch (IOException e) {
            return Result.ok("获取成功", getDefaultConfig());
        }
    }

    /**
     * 保存配置
     */
    @PostMapping("/article-generator/config")
    public Result saveConfig(@RequestBody Map<String, Object> config) {
        try {
            File file = new File(CONFIG_FILE);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, config);
            return Result.ok("配置保存成功");
        } catch (IOException e) {
            return Result.error("配置保存失败: " + e.getMessage());
        }
    }

    /**
     * 获取默认配置
     */
    private Map<String, Object> getDefaultConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("enabled", true);
        config.put("frequency", "daily");
        config.put("cronExpression", "0 8 * * *");
        config.put("generateTime", "08:00");
        config.put("topics", Arrays.asList("erp", "ai", "manufacturing"));
        config.put("customTopics", new ArrayList<>());
        config.put("generateCoverImage", true);
        config.put("generateBodyImages", true);
        config.put("bodyImageCount", 3);
        config.put("articleWordCount", 2000);
        config.put("category", "AI技术");
        config.put("tags", Arrays.asList("AI", "技术博客"));
        config.put("autoPublish", true);
        return config;
    }
}
