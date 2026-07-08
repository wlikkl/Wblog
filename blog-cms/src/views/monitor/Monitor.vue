<template>
	<div class="monitor-page" :class="'theme-' + theme">
		<!-- 顶部栏 -->
		<div class="monitor-header">
			<h2>服务器监控</h2>
			<div class="header-right">
				<span class="refresh-time" v-if="lastRefresh">更新于 {{ lastRefresh }}</span>
				<el-button size="small" @click="fetchData" :loading="loading">
					<i class="el-icon-refresh"></i> 刷新
				</el-button>
				<div class="theme-switcher">
					<el-button-group>
						<el-button size="small" :type="theme === 'violet' ? 'primary' : ''" @click="setTheme('violet')">紫罗兰</el-button>
						<el-button size="small" :type="theme === 'dark' ? 'primary' : ''" @click="setTheme('dark')">暗黑</el-button>
						<el-button size="small" :type="theme === 'cyber' ? 'primary' : ''" @click="setTheme('cyber')">赛博朋克</el-button>
					</el-button-group>
				</div>
			</div>
		</div>

		<!-- 系统信息条 -->
		<div class="sys-info-bar" v-if="data.system">
			<span><i class="el-icon-monitor"></i> {{ data.system.hostname }}</span>
			<span><i class="el-icon-cpu"></i> {{ data.system.os }}</span>
			<span><i class="el-icon-coffee-cup"></i> Java {{ data.system.java }}</span>
			<span v-if="data.cpu"><i class="el-icon-s-opportunity"></i> {{ data.cpu.model }}</span>
			<span v-if="data.uptime"><i class="el-icon-time"></i> 运行 {{ formatUptime(data.uptime) }}</span>
		</div>

		<!-- 概览卡片 -->
		<el-row :gutter="20" class="card-row">
			<el-col :xs="12" :sm="6">
				<div class="stat-card cpu-card">
					<div class="stat-icon"><i class="el-icon-cpu"></i></div>
					<div class="stat-info">
						<div class="stat-label">CPU 使用率</div>
						<div class="stat-value">{{ data.cpu ? data.cpu.usage : 0 }}%</div>
						<div class="stat-sub">{{ data.cpu ? data.cpu.cores : 0 }} 核心 · 负载 {{ data.cpu ? data.cpu.load : 0 }}</div>
					</div>
					<div class="stat-ring">
						<svg viewBox="0 0 36 36">
							<path class="ring-bg" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" fill="none" stroke-width="3"/>
							<path class="ring-fill" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" fill="none" stroke-width="3"
								:stroke-dasharray="(data.cpu ? data.cpu.usage : 0) + ', 100'"/>
						</svg>
					</div>
				</div>
			</el-col>
			<el-col :xs="12" :sm="6">
				<div class="stat-card mem-card">
					<div class="stat-icon"><i class="el-icon-coin"></i></div>
					<div class="stat-info">
						<div class="stat-label">系统内存</div>
						<div class="stat-value">{{ data.memory && data.memory.systemUsage ? data.memory.systemUsage : 0 }}%</div>
						<div class="stat-sub" v-if="data.memory && data.memory.systemTotal">{{ formatBytes(data.memory.systemUsed) }} / {{ formatBytes(data.memory.systemTotal) }}</div>
					</div>
					<div class="stat-ring">
						<svg viewBox="0 0 36 36">
							<path class="ring-bg" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" fill="none" stroke-width="3"/>
							<path class="ring-fill" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" fill="none" stroke-width="3"
								:stroke-dasharray="(data.memory && data.memory.systemUsage ? data.memory.systemUsage : 0) + ', 100'"/>
						</svg>
					</div>
				</div>
			</el-col>
			<el-col :xs="12" :sm="6">
				<div class="stat-card jvm-card">
					<div class="stat-icon"><i class="el-icon-s-platform"></i></div>
					<div class="stat-info">
						<div class="stat-label">JVM 内存</div>
						<div class="stat-value">{{ data.memory ? data.memory.jvmUsage : 0 }}%</div>
						<div class="stat-sub" v-if="data.memory">{{ formatBytes(data.memory.jvmUsed) }} / {{ formatBytes(data.memory.jvmMax) }}</div>
					</div>
					<div class="stat-ring">
						<svg viewBox="0 0 36 36">
							<path class="ring-bg" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" fill="none" stroke-width="3"/>
							<path class="ring-fill" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" fill="none" stroke-width="3"
								:stroke-dasharray="(data.memory ? data.memory.jvmUsage : 0) + ', 100'"/>
						</svg>
					</div>
				</div>
			</el-col>
			<el-col :xs="12" :sm="6">
				<div class="stat-card thread-card">
					<div class="stat-icon"><i class="el-icon-sort"></i></div>
					<div class="stat-info">
						<div class="stat-label">线程数</div>
						<div class="stat-value">{{ data.threads ? data.threads.count : 0 }}</div>
						<div class="stat-sub">守护 {{ data.threads ? data.threads.daemon : 0 }} · 峰值 {{ data.threads ? data.threads.peak : 0 }}</div>
					</div>
					<div class="thread-bars">
						<div class="thread-bar" :style="{height: threadHeight(data.threads ? data.threads.count : 1)}"></div>
						<div class="thread-bar daemon" :style="{height: threadHeight(data.threads ? data.threads.daemon : 0)}"></div>
					</div>
				</div>
			</el-col>
		</el-row>

		<!-- 图表区 -->
		<el-row :gutter="20" class="card-row">
			<el-col :xs="24" :sm="12">
				<div class="chart-card">
					<h3>CPU & 内存历史</h3>
					<div ref="cpuMemChart" style="height: 300px;"></div>
				</div>
			</el-col>
			<el-col :xs="24" :sm="12">
				<div class="chart-card">
					<h3>网络 I/O</h3>
					<div ref="networkChart" style="height: 300px;"></div>
				</div>
			</el-col>
		</el-row>

		<!-- 磁盘 -->
		<div class="detail-card">
			<h3><i class="el-icon-folder-opened"></i> 磁盘使用</h3>
			<div class="disk-list">
				<div class="disk-item" v-for="(disk, idx) in data.disks" :key="idx">
					<div class="disk-header">
						<span class="disk-mount">{{ disk.mount }}</span>
						<span class="disk-type">{{ disk.type }}</span>
						<span class="disk-usage">{{ disk.usage }}%</span>
					</div>
					<div class="disk-bar">
						<div class="disk-bar-fill" :style="{width: disk.usage + '%'}"></div>
					</div>
					<div class="disk-detail">
						<span>已用 {{ formatBytes(disk.used) }}</span>
						<span>可用 {{ formatBytes(disk.usable) }}</span>
						<span>总计 {{ formatBytes(disk.total) }}</span>
					</div>
				</div>
			</div>
		</div>

		<!-- 网络 & JVM 详情 -->
		<el-row :gutter="20" class="card-row">
			<el-col :xs="24" :sm="12">
				<div class="detail-card">
					<h3><i class="el-icon-connection"></i> 网络流量</h3>
					<div class="detail-grid" v-if="data.network">
						<div class="detail-item">
							<div class="detail-label">接收 (RX)</div>
							<div class="detail-value rx">{{ data.network.rxMB }} MB</div>
						</div>
						<div class="detail-item">
							<div class="detail-label">发送 (TX)</div>
							<div class="detail-value tx">{{ data.network.txMB }} MB</div>
						</div>
					</div>
				</div>
			</el-col>
			<el-col :xs="24" :sm="12">
				<div class="detail-card">
					<h3><i class="el-icon-s-platform"></i> JVM 详情</h3>
					<div class="detail-grid" v-if="data.memory">
						<div class="detail-item">
							<div class="detail-label">Heap 已用</div>
							<div class="detail-value">{{ formatBytes(data.memory.heapUsed) }}</div>
						</div>
						<div class="detail-item">
							<div class="detail-label">Heap 最大</div>
							<div class="detail-value">{{ formatBytes(data.memory.heapMax) }}</div>
						</div>
						<div class="detail-item">
							<div class="detail-label">Non-Heap</div>
							<div class="detail-value">{{ formatBytes(data.memory.nonHeapUsed) }}</div>
						</div>
						<div class="detail-item">
							<div class="detail-label">GC 暂停</div>
							<div class="detail-value">{{ gcCount }}</div>
						</div>
					</div>
				</div>
			</el-col>
		</el-row>
	</div>
</template>

<script>
	import echarts from 'echarts'
	import {getServerMonitor} from '@/api/monitor'

	export default {
		name: 'ServerMonitor',
		data() {
			return {
				theme: localStorage.getItem('monitor-theme') || 'violet',
				loading: false,
				data: {},
				lastRefresh: '',
				gcCount: 0,
				cpuMemChart: null,
				networkChart: null,
				history: {
					time: [],
					cpu: [],
					mem: [],
					rx: [],
					tx: []
				},
				pollTimer: null,
				themeConfigs: {
					violet: {
						bg: '#0e0a14',
						cardBg: 'rgba(20,16,30,0.85)',
						border: 'rgba(167,139,250,0.15)',
						text: '#ede8f0',
						textSecondary: 'rgba(255,255,255,0.55)',
						accent: '#a78bfa',
						accent2: '#c4b5fd',
						accentDeep: '#7c3aed',
						cpu: '#a78bfa',
						mem: '#c4b5fd',
						rx: '#60a5fa',
						tx: '#34d399',
						chartBg: 'rgba(14,10,20,0.6)',
						ringBg: 'rgba(255,255,255,0.08)',
						diskBar: 'rgba(167,139,250,0.6)'
					},
					dark: {
						bg: '#0d1117',
						cardBg: 'rgba(22,27,34,0.9)',
						border: 'rgba(240,246,252,0.1)',
						text: '#e6edf3',
						textSecondary: 'rgba(240,246,252,0.5)',
						accent: '#58a6ff',
						accent2: '#79c0ff',
						accentDeep: '#1f6feb',
						cpu: '#58a6ff',
						mem: '#79c0ff',
						rx: '#3fb950',
						tx: '#d29922',
						chartBg: 'rgba(13,17,23,0.7)',
						ringBg: 'rgba(240,246,252,0.08)',
						diskBar: 'rgba(88,166,255,0.6)'
					},
					cyber: {
						bg: '#0a0e17',
						cardBg: 'rgba(12,20,35,0.9)',
						border: 'rgba(0,255,200,0.15)',
						text: '#e0f7fa',
						textSecondary: 'rgba(224,247,250,0.55)',
						accent: '#00ffc8',
						accent2: '#00e5ff',
						accentDeep: '#00bfa5',
						cpu: '#00ffc8',
						mem: '#00e5ff',
						rx: '#ffab40',
						tx: '#ff4081',
						chartBg: 'rgba(10,14,23,0.7)',
						ringBg: 'rgba(0,255,200,0.08)',
						diskBar: 'rgba(0,255,200,0.5)'
					}
				}
			}
		},
		computed: {
			t() {
				return this.themeConfigs[this.theme] || this.themeConfigs.violet
			}
		},
		mounted() {
			this.fetchData()
			this.startPoll()
			window.addEventListener('resize', this.handleResize)
		},
		beforeDestroy() {
			this.stopPoll()
			window.removeEventListener('resize', this.handleResize)
			if (this.cpuMemChart) this.cpuMemChart.dispose()
			if (this.networkChart) this.networkChart.dispose()
		},
		methods: {
			hexToRgba(hex, alpha) {
				const r = parseInt(hex.slice(1,3), 16)
				const g = parseInt(hex.slice(3,5), 16)
				const b = parseInt(hex.slice(5,7), 16)
				return `rgba(${r},${g},${b},${alpha})`
			},
			setTheme(t) {
				this.theme = t
				localStorage.setItem('monitor-theme', t)
				this.$nextTick(() => {
					this.updateCharts()
				})
			},
			fetchData() {
				this.loading = true
				getServerMonitor().then(res => {
					this.data = res.data
					const now = new Date()
					this.lastRefresh = now.getHours().toString().padStart(2, '0') + ':' + now.getMinutes().toString().padStart(2, '0') + ':' + now.getSeconds().toString().padStart(2, '0')

					// 记录历史
					const timeStr = now.getHours().toString().padStart(2, '0') + ':' + now.getMinutes().toString().padStart(2, '0')
					this.history.time.push(timeStr)
					this.history.cpu.push(this.data.cpu ? this.data.cpu.usage : 0)
					this.history.mem.push(this.data.memory && this.data.memory.systemUsage ? this.data.memory.systemUsage : 0)
					this.history.rx.push(this.data.network ? this.data.network.rxMB : 0)
					this.history.tx.push(this.data.network ? this.data.network.txMB : 0)
					// 最多保留 30 个点
					if (this.history.time.length > 30) {
						this.history.time.shift()
						this.history.cpu.shift()
						this.history.mem.shift()
						this.history.rx.shift()
						this.history.tx.shift()
					}

					this.$nextTick(() => {
						this.updateCharts()
					})
					this.loading = false
				}).catch(() => {
					this.loading = false
				})
			},
			updateCharts() {
				this.initCpuMemChart()
				this.initNetworkChart()
			},
			initCpuMemChart() {
				if (this.cpuMemChart) this.cpuMemChart.dispose()
				if (!this.$refs.cpuMemChart) return
				this.cpuMemChart = echarts.init(this.$refs.cpuMemChart)
				this.cpuMemChart.setOption({
					backgroundColor: 'transparent',
					tooltip: {
						trigger: 'axis',
						backgroundColor: this.t.cardBg,
						borderColor: this.t.border,
						textStyle: {color: this.t.text}
					},
					legend: {
						data: ['CPU', '内存'],
						textStyle: {color: this.t.textSecondary},
						top: 0
					},
					grid: {left: 40, right: 20, top: 40, bottom: 30},
					xAxis: {
						type: 'category',
						data: this.history.time,
						axisLine: {lineStyle: {color: this.t.border}},
						axisLabel: {color: this.t.textSecondary}
					},
					yAxis: {
						type: 'value',
						max: 100,
						axisLine: {lineStyle: {color: this.t.border}},
						axisLabel: {color: this.t.textSecondary, formatter: '{value}%'},
						splitLine: {lineStyle: {color: this.t.border}}
					},
					series: [
						{
							name: 'CPU',
							type: 'line',
							smooth: true,
							data: this.history.cpu,
							itemStyle: {color: this.t.cpu},
							lineStyle: {color: this.t.cpu, width: 2},
						areaStyle: {color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
							{offset: 0, color: this.hexToRgba(this.t.cpu, 0.3)},
							{offset: 1, color: 'transparent'}
						])}
					},
					{
						name: '内存',
						type: 'line',
						smooth: true,
						data: this.history.mem,
						itemStyle: {color: this.t.mem},
						lineStyle: {color: this.t.mem, width: 2},
						areaStyle: {color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
							{offset: 0, color: this.hexToRgba(this.t.mem, 0.3)},
							{offset: 1, color: 'transparent'}
						])}
						}
					]
				})
			},
			initNetworkChart() {
				if (this.networkChart) this.networkChart.dispose()
				if (!this.$refs.networkChart) return
				this.networkChart = echarts.init(this.$refs.networkChart)
				this.networkChart.setOption({
					backgroundColor: 'transparent',
					tooltip: {
						trigger: 'axis',
						backgroundColor: this.t.cardBg,
						borderColor: this.t.border,
						textStyle: {color: this.t.text}
					},
					legend: {
						data: ['接收 (RX)', '发送 (TX)'],
						textStyle: {color: this.t.textSecondary},
						top: 0
					},
					grid: {left: 50, right: 20, top: 40, bottom: 30},
					xAxis: {
						type: 'category',
						data: this.history.time,
						axisLine: {lineStyle: {color: this.t.border}},
						axisLabel: {color: this.t.textSecondary}
					},
					yAxis: {
						type: 'value',
						axisLine: {lineStyle: {color: this.t.border}},
						axisLabel: {color: this.t.textSecondary, formatter: '{value} MB'},
						splitLine: {lineStyle: {color: this.t.border}}
					},
					series: [
						{
							name: '接收 (RX)',
							type: 'line',
							smooth: true,
							data: this.history.rx,
							itemStyle: {color: this.t.rx},
							lineStyle: {color: this.t.rx, width: 2},
						areaStyle: {color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
							{offset: 0, color: this.hexToRgba(this.t.rx, 0.3)},
							{offset: 1, color: 'transparent'}
						])}
					},
					{
						name: '发送 (TX)',
						type: 'line',
						smooth: true,
						data: this.history.tx,
						itemStyle: {color: this.t.tx},
						lineStyle: {color: this.t.tx, width: 2},
						areaStyle: {color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
							{offset: 0, color: this.hexToRgba(this.t.tx, 0.3)},
							{offset: 1, color: 'transparent'}
						])}
						}
					]
				})
			},
			handleResize() {
				if (this.cpuMemChart) this.cpuMemChart.resize()
				if (this.networkChart) this.networkChart.resize()
			},
			startPoll() {
				this.pollTimer = setInterval(() => {
					this.fetchData()
				}, 5000)
			},
			stopPoll() {
				if (this.pollTimer) clearInterval(this.pollTimer)
			},
			formatBytes(bytes) {
				if (!bytes || bytes === 0) return '0 B'
				const k = 1024
				const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
				const i = Math.floor(Math.log(bytes) / Math.log(k))
				return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
			},
			formatUptime(ms) {
				const s = Math.floor(ms / 1000)
				const d = Math.floor(s / 86400)
				const h = Math.floor((s % 86400) / 3600)
				const m = Math.floor((s % 3600) / 60)
				if (d > 0) return d + '天 ' + h + '时 ' + m + '分'
				if (h > 0) return h + '时 ' + m + '分'
				return m + '分'
			},
			threadHeight(count) {
				const max = 200
				return Math.min(count / max * 100, 100) + '%'
			}
		}
	}
</script>

<style scoped>
/* === Theme variables === */
.theme-violet {
	--m-bg: #0e0a14;
	--m-card: rgba(20,16,30,0.85);
	--m-border: rgba(167,139,250,0.15);
	--m-text: #ede8f0;
	--m-text2: rgba(255,255,255,0.55);
	--m-accent: #a78bfa;
	--m-accent2: #c4b5fd;
	--m-accentDeep: #7c3aed;
	--m-cpu: #a78bfa;
	--m-mem: #c4b5fd;
	--m-rx: #60a5fa;
	--m-tx: #34d399;
	--m-chartBg: rgba(14,10,20,0.6);
	--m-ringBg: rgba(255,255,255,0.08);
	--m-diskBar: rgba(167,139,250,0.6);
}
.theme-dark {
	--m-bg: #0d1117;
	--m-card: rgba(22,27,34,0.9);
	--m-border: rgba(240,246,252,0.1);
	--m-text: #e6edf3;
	--m-text2: rgba(240,246,252,0.5);
	--m-accent: #58a6ff;
	--m-accent2: #79c0ff;
	--m-accentDeep: #1f6feb;
	--m-cpu: #58a6ff;
	--m-mem: #79c0ff;
	--m-rx: #3fb950;
	--m-tx: #d29922;
	--m-chartBg: rgba(13,17,23,0.7);
	--m-ringBg: rgba(240,246,252,0.08);
	--m-diskBar: rgba(88,166,255,0.6);
}
.theme-cyber {
	--m-bg: #0a0e17;
	--m-card: rgba(12,20,35,0.9);
	--m-border: rgba(0,255,200,0.15);
	--m-text: #e0f7fa;
	--m-text2: rgba(224,247,250,0.55);
	--m-accent: #00ffc8;
	--m-accent2: #00e5ff;
	--m-accentDeep: #00bfa5;
	--m-cpu: #00ffc8;
	--m-mem: #00e5ff;
	--m-rx: #ffab40;
	--m-tx: #ff4081;
	--m-chartBg: rgba(10,14,23,0.7);
	--m-ringBg: rgba(0,255,200,0.08);
	--m-diskBar: rgba(0,255,200,0.5);
}

.monitor-page {
	background: var(--m-bg);
	color: var(--m-text);
	min-height: 100vh;
	padding: 20px;
}

/* Header */
.monitor-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20px;
	flex-wrap: wrap;
	gap: 12px;
}
.monitor-header h2 {
	margin: 0;
	font-size: 22px;
	font-weight: 700;
	color: var(--m-text);
}
.header-right {
	display: flex;
	align-items: center;
	gap: 12px;
	flex-wrap: wrap;
}
.refresh-time {
	font-size: 13px;
	color: var(--m-text2);
}

/* System Info Bar */
.sys-info-bar {
	display: flex;
	flex-wrap: wrap;
	gap: 20px;
	padding: 12px 20px;
	margin-bottom: 20px;
	background: var(--m-card);
	border: 1px solid var(--m-border);
	border-radius: 12px;
	font-size: 13px;
	color: var(--m-text2);
}
.sys-info-bar span {
	display: inline-flex;
	align-items: center;
	gap: 6px;
}
.sys-info-bar i {
	color: var(--m-accent);
}

/* Card Row */
.card-row {
	margin-bottom: 20px;
}

/* Stat Cards */
.stat-card {
	background: var(--m-card);
	border: 1px solid var(--m-border);
	border-radius: 14px;
	padding: 20px;
	display: flex;
	align-items: center;
	gap: 16px;
	transition: border-color .3s, box-shadow .3s;
	position: relative;
	overflow: hidden;
}
.stat-card:hover {
	border-color: var(--m-accent);
	box-shadow: 0 4px 20px rgba(0,0,0,0.3);
}
.stat-icon {
	width: 48px;
	height: 48px;
	border-radius: 12px;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 24px;
	flex-shrink: 0;
}
.cpu-card .stat-icon { background: rgba(167,139,250,0.15); color: var(--m-cpu); }
.mem-card .stat-icon { background: rgba(196,181,253,0.15); color: var(--m-mem); }
.jvm-card .stat-icon { background: rgba(96,165,250,0.15); color: var(--m-rx); }
.thread-card .stat-icon { background: rgba(52,211,153,0.15); color: var(--m-tx); }
.theme-dark .cpu-card .stat-icon { background: rgba(88,166,255,0.15); }
.theme-dark .mem-card .stat-icon { background: rgba(121,192,255,0.15); }
.theme-dark .jvm-card .stat-icon { background: rgba(63,185,80,0.15); }
.theme-dark .thread-card .stat-icon { background: rgba(210,153,34,0.15); }
.theme-cyber .cpu-card .stat-icon { background: rgba(0,255,200,0.12); }
.theme-cyber .mem-card .stat-icon { background: rgba(0,229,255,0.12); }
.theme-cyber .jvm-card .stat-icon { background: rgba(255,171,64,0.12); }
.theme-cyber .thread-card .stat-icon { background: rgba(255,64,129,0.12); }

.stat-info { flex: 1; min-width: 0; }
.stat-label {
	font-size: 13px;
	color: var(--m-text2);
	margin-bottom: 4px;
}
.stat-value {
	font-size: 28px;
	font-weight: 700;
	color: var(--m-text);
	line-height: 1.2;
}
.stat-sub {
	font-size: 12px;
	color: var(--m-text2);
	margin-top: 4px;
}

/* Ring */
.stat-ring {
	width: 52px;
	height: 52px;
	flex-shrink: 0;
}
.stat-ring svg { width: 100%; height: 100%; transform: rotate(-90deg); }
.ring-bg { stroke: var(--m-ringBg); }
.cpu-card .ring-fill { stroke: var(--m-cpu); }
.mem-card .ring-fill { stroke: var(--m-mem); }
.jvm-card .ring-fill { stroke: var(--m-accent); }
.thread-card .ring-fill { stroke: var(--m-tx); }

/* Thread bars */
.thread-bars {
	display: flex;
	align-items: flex-end;
	gap: 4px;
	height: 40px;
}
.thread-bar {
	width: 8px;
	background: var(--m-accent);
	border-radius: 3px 3px 0 0;
	min-height: 4px;
	transition: height .5s ease;
}
.thread-bar.daemon {
	background: var(--m-accent2);
}

/* Chart Cards */
.chart-card {
	background: var(--m-card);
	border: 1px solid var(--m-border);
	border-radius: 14px;
	padding: 20px;
}
.chart-card h3 {
	margin: 0 0 12px 0;
	font-size: 16px;
	font-weight: 600;
	color: var(--m-text);
}

/* Detail Cards */
.detail-card {
	background: var(--m-card);
	border: 1px solid var(--m-border);
	border-radius: 14px;
	padding: 20px;
	margin-bottom: 20px;
}
.detail-card h3 {
	margin: 0 0 16px 0;
	font-size: 16px;
	font-weight: 600;
	color: var(--m-text);
	display: flex;
	align-items: center;
	gap: 8px;
}
.detail-card h3 i {
	color: var(--m-accent);
}

/* Disk */
.disk-item {
	margin-bottom: 16px;
}
.disk-item:last-child { margin-bottom: 0; }
.disk-header {
	display: flex;
	align-items: center;
	gap: 12px;
	margin-bottom: 8px;
	font-size: 14px;
}
.disk-mount {
	font-weight: 600;
	color: var(--m-text);
	font-family: monospace;
}
.disk-type {
	font-size: 12px;
	color: var(--m-text2);
	background: rgba(255,255,255,0.06);
	padding: 2px 8px;
	border-radius: 4px;
}
.disk-usage {
	margin-left: auto;
	font-weight: 600;
	color: var(--m-accent);
}
.disk-bar {
	height: 8px;
	background: var(--m-ringBg);
	border-radius: 4px;
	overflow: hidden;
}
.disk-bar-fill {
	height: 100%;
	background: var(--m-diskBar);
	border-radius: 4px;
	transition: width .5s ease;
}
.disk-detail {
	display: flex;
	gap: 20px;
	margin-top: 6px;
	font-size: 12px;
	color: var(--m-text2);
}

/* Detail Grid */
.detail-grid {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 16px;
}
.detail-item {
	padding: 12px;
	background: rgba(255,255,255,0.03);
	border-radius: 10px;
	border: 1px solid var(--m-border);
}
.detail-label {
	font-size: 13px;
	color: var(--m-text2);
	margin-bottom: 4px;
}
.detail-value {
	font-size: 18px;
	font-weight: 700;
	color: var(--m-text);
}
.detail-value.rx { color: var(--m-rx); }
.detail-value.tx { color: var(--m-tx); }

/* Theme switcher button overrides */
.monitor-page .el-button--primary {
	background: var(--m-accentDeep) !important;
	border-color: var(--m-accentDeep) !important;
	color: #fff !important;
}
.monitor-page .el-button {
	background: var(--m-card) !important;
	border-color: var(--m-border) !important;
	color: var(--m-text) !important;
}
.monitor-page .el-button:hover {
	border-color: var(--m-accent) !important;
	color: var(--m-accent) !important;
}

/* Responsive */
@media (max-width: 768px) {
	.monitor-header { flex-direction: column; align-items: flex-start; }
	.header-right { width: 100%; }
	.stat-value { font-size: 22px; }
	.detail-grid { grid-template-columns: 1fr; }
}
</style>
