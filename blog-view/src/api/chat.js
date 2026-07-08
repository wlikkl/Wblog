import axios from '@/plugins/axios'

export function sendChatMessage(message) {
	return axios({
		url: '/chat',
		method: 'POST',
		data: {message}
	})
}
