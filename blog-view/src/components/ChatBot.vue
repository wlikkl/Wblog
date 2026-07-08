<template>
	<div class="chatbot-container">
		<div v-if="visible" class="chatbot-window">
			<div class="chatbot-header">
				<span>AI 助手</span>
				<button class="close-btn" @click="visible = false">×</button>
			</div>
			<div class="chatbot-body" ref="body">
				<div v-for="(msg, i) in messages" :key="i" :class="['msg', msg.role]">
					<div class="bubble">{{ msg.content }}</div>
				</div>
				<div v-if="loading" class="msg assistant">
					<div class="bubble typing">...</div>
				</div>
			</div>
			<div class="chatbot-footer">
				<input v-model="input" @keyup.enter="send" placeholder="输入消息..." :disabled="loading" />
				<button @click="send" :disabled="loading || !input.trim()">发送</button>
			</div>
		</div>
		<button class="chatbot-toggle" @click="visible = !visible">
			{{ visible ? '×' : '💬' }}
		</button>
	</div>
</template>

<script>
import {sendChatMessage} from '@/api/chat'

export default {
	name: 'ChatBot',
	data() {
		return {
			visible: false,
			input: '',
			loading: false,
			messages: [
				{role: 'assistant', content: '你好！我是博客 AI 助手，有什么可以帮助你的吗？'}
			]
		}
	},
	methods: {
		async send() {
			const text = this.input.trim()
			if (!text || this.loading) return
			this.messages.push({role: 'user', content: text})
			this.input = ''
			this.loading = true
			try {
				const res = await sendChatMessage(text)
				this.messages.push({role: 'assistant', content: res.data})
			} catch {
				this.messages.push({role: 'assistant', content: '抱歉，我暂时无法回复。'})
			} finally {
				this.loading = false
				this.$nextTick(() => {
					this.$refs.body.scrollTop = this.$refs.body.scrollHeight
				})
			}
		}
	}
}
</script>

<style scoped>
.chatbot-container {
	position: fixed;
	bottom: 20px;
	right: 20px;
	z-index: 9999;
	font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}
.chatbot-window {
	position: absolute;
	bottom: 60px;
	right: 0;
	width: 360px;
	height: 500px;
	background: rgba(20,16,28,0.95);
	border: 1px solid rgba(167,139,250,0.15);
	border-radius: 14px;
	box-shadow: 0 8px 32px rgba(0,0,0,0.5);
	display: flex;
	flex-direction: column;
	overflow: hidden;
	backdrop-filter: blur(20px);
}
.chatbot-header {
	background: linear-gradient(135deg, #7c3aed, #a78bfa);
	color: #fff;
	padding: 12px 16px;
	display: flex;
	justify-content: space-between;
	align-items: center;
	font-size: 15px;
	font-weight: 600;
}
.close-btn {
	background: none;
	border: none;
	color: #fff;
	font-size: 22px;
	cursor: pointer;
	opacity: 0.8;
	transition: opacity 0.2s;
}
.close-btn:hover { opacity: 1; }
.chatbot-body {
	flex: 1;
	padding: 12px;
	overflow-y: auto;
	background: rgba(14,10,20,0.6);
}
.msg { margin-bottom: 12px; display: flex; }
.msg.user { justify-content: flex-end; }
.bubble {
	max-width: 80%;
	padding: 10px 14px;
	border-radius: 12px;
	font-size: 14px;
	line-height: 1.5;
	word-break: break-word;
}
.assistant .bubble {
	background: rgba(255,255,255,0.06);
	color: var(--text-primary);
	border: 1px solid rgba(255,255,255,0.08);
}
.user .bubble {
	background: linear-gradient(135deg, #7c3aed, #a78bfa);
	color: #fff;
}
.typing { min-width: 40px; text-align: center; }
.chatbot-footer {
	display: flex;
	padding: 10px;
	border-top: 1px solid rgba(255,255,255,0.08);
	background: rgba(14,10,20,0.8);
	gap: 8px;
}
.chatbot-footer input {
	flex: 1;
	border: 1px solid rgba(255,255,255,0.1);
	border-radius: 8px;
	padding: 8px 12px;
	font-size: 14px;
	outline: none;
	background: rgba(255,255,255,0.04);
	color: var(--text-primary);
}
.chatbot-footer input::placeholder { color: rgba(255,255,255,0.3); }
.chatbot-footer input:focus {
	border-color: var(--accent);
	background: rgba(255,255,255,0.06);
}
.chatbot-footer button {
	background: linear-gradient(135deg, #7c3aed, #a78bfa);
	color: #fff;
	border: none;
	border-radius: 8px;
	padding: 8px 16px;
	font-size: 14px;
	cursor: pointer;
	transition: all 0.2s;
}
.chatbot-footer button:hover:not(:disabled) {
	box-shadow: 0 4px 12px rgba(167,139,250,0.4);
	transform: translateY(-1px);
}
.chatbot-footer button:disabled {
	background: rgba(167,139,250,0.3);
	cursor: not-allowed;
}
.chatbot-toggle {
	width: 50px;
	height: 50px;
	border-radius: 50%;
	background: linear-gradient(135deg, #7c3aed, #a78bfa);
	color: #fff;
	border: none;
	font-size: 22px;
	cursor: pointer;
	box-shadow: 0 2px 16px rgba(124,58,237,0.4);
	display: flex;
	align-items: center;
	justify-content: center;
	transition: transform 0.2s, box-shadow 0.2s;
	animation: chatFloat 3s ease-in-out infinite;
}
.chatbot-toggle:hover {
	transform: scale(1.12);
	box-shadow: 0 4px 24px rgba(124,58,237,0.7);
	animation: none;
}
@keyframes chatFloat {
	0%, 100% { transform: translateY(0); }
	50% { transform: translateY(-6px); }
}
</style>
