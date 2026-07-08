package top.naccl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.naccl.model.vo.Result;
import top.naccl.service.ChatService;

import java.util.Map;

@RestController
public class ChatController {
	@Autowired
	ChatService chatService;

	@PostMapping("/chat")
	public Result chat(@RequestBody Map<String, String> params) {
		String message = params.get("message");
		if (message == null || message.trim().isEmpty()) {
			return Result.error("消息不能为空");
		}
		String reply = chatService.chat(message);
		return Result.ok("success", reply);
	}
}
