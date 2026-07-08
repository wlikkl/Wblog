import axios from '@/util/request'

export function getServerMonitor() {
	return axios({
		url: 'monitor',
		method: 'GET'
	})
}
