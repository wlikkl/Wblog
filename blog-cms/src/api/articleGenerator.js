import axios from '@/util/request'

export function getConfig() {
	return axios({
		url: '/admin/article-generator/config',
		method: 'get'
	})
}

export function saveConfig(data) {
	return axios({
		url: '/admin/article-generator/config',
		method: 'post',
		data
	})
}
