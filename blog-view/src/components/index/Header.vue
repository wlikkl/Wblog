<template>
	<header ref="header">
		<div class="view">
			<div class="bg-gradient"></div>
			<div class="bg-orb-header bg-orb-h1"></div>
			<div class="bg-orb-header bg-orb-h2"></div>
			<div class="bg-orb-header bg-orb-h3"></div>
		</div>
		<div class="text-malfunction" :class="{typed:typeDone}" :data-word="displayText">
			{{ displayText }}<span class="cursor" v-if="!typeDone">|</span>
			<div class="line"></div>
		</div>
		<div class="wrapper">
			<i class="ali-iconfont icon-down" @click="scrollToMain"></i>
		</div>
		<svg class="header-wave" viewBox="0 0 1440 120" preserveAspectRatio="none">
			<path d="M0,60 C240,120 480,0 720,60 C960,120 1200,0 1440,60 L1440,120 L0,120 Z"
				fill="rgba(14,10,20,0.98)"/>
			<path d="M0,80 C240,110 480,40 720,80 C960,120 1200,40 1440,80 L1440,120 L0,120 Z"
				fill="rgba(14,10,20,0.90)"/>
			<path d="M0,98 C360,115 720,60 1080,100 C1200,112 1350,70 1440,98 L1440,120 L0,120 Z"
				fill="rgba(14,10,20,0.80)"/>
		</svg>
	</header>
</template>

<script>
	import {mapState} from 'vuex'
	import defaultSettings from '@/settings'

	export default {
		name: "Header",
		data() {
			return {
				defaultSettings,
				displayText: '',
				typeDone: false
			}
		},
		computed: {
			...mapState(['clientSize'])
		},
		watch: {
			'clientSize.clientHeight'() {
				this.setHeaderHeight()
			}
		},
		mounted() {
			this.setHeaderHeight()
			this.startTyping()
		},
		methods: {
			setHeaderHeight() {
				this.$refs.header.style.height = this.clientSize.clientHeight + 'px'
			},
			scrollToMain() {
				window.scrollTo({top: this.clientSize.clientHeight, behavior: 'smooth'})
			},
			startTyping() {
				const full = this.defaultSettings.malfunctionText
				let i = 0
				const timer = setInterval(() => {
					this.displayText = full.substring(0, i + 1)
					i++
					if (i >= full.length) {
						clearInterval(timer)
						setTimeout(() => { this.typeDone = true }, 300)
					}
				}, 120)
			}
		},
	}
</script>

<style scoped>
	header {
		position: relative;
		user-select: none;
		overflow: hidden;
		background: linear-gradient(135deg, #1a0f2e 0%, #3b1f6b 35%, #6d28d9 70%, #4c1d95 100%);
	}

	.view {
		position: absolute;
		top: 0;
		right: 0;
		bottom: 0;
		left: 0;
		overflow: hidden;
	}

	.bg-gradient {
		position: absolute;
		inset: 0;
		background:
			radial-gradient(ellipse at 20% 30%, rgba(167,139,250,0.4), transparent 50%),
			radial-gradient(ellipse at 80% 70%, rgba(124,58,237,0.35), transparent 50%),
			radial-gradient(ellipse at 50% 100%, rgba(196,181,253,0.25), transparent 60%);
	}

	.bg-orb-header {
		position: absolute;
		border-radius: 50%;
		filter: blur(60px);
		opacity: 0.6;
	}

	.bg-orb-h1 {
		width: 360px; height: 360px;
		top: 10%; left: 15%;
		background: radial-gradient(circle, rgba(196,181,253,0.6), transparent);
		animation: orbH1 12s ease-in-out infinite alternate;
	}
	.bg-orb-h2 {
		width: 280px; height: 280px;
		bottom: 15%; right: 10%;
		background: radial-gradient(circle, rgba(124,58,237,0.6), transparent);
		animation: orbH2 10s ease-in-out infinite alternate;
	}
	.bg-orb-h3 {
		width: 200px; height: 200px;
		top: 50%; left: 60%;
		background: radial-gradient(circle, rgba(167,139,250,0.5), transparent);
		animation: orbH3 14s ease-in-out infinite alternate;
	}

	@keyframes orbH1 { 0% { transform: translate(0,0) scale(1) } 100% { transform: translate(40px,-30px) scale(1.15) } }
	@keyframes orbH2 { 0% { transform: translate(0,0) scale(1) } 100% { transform: translate(-30px,-20px) scale(1.1) } }
	@keyframes orbH3 { 0% { transform: translate(0,0) scale(1) } 100% { transform: translate(-20px,25px) scale(0.9) } }

	.text-malfunction {
		position: absolute;
		top: 50%; left: 50%;
		transform: translate(-50%, -50%);
		font-size: 42px;
		font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
		color: #fff;
		font-weight: 800;
		letter-spacing: 6px;
		z-index: 100;
		white-space: nowrap;
		text-shadow:
			2px 0 0 #ff0040,
			-2px 0 0 #00f0ff,
			0 0 20px rgba(167,139,250,0.8);
	}
	.text-malfunction.typed {
		animation: glitchShake 3s infinite;
	}

	/* Typewriter cursor */
	.cursor {
		font-weight: 300;
		animation: blink 0.7s step-end infinite;
		margin-left: 2px;
		color: rgba(255,255,255,0.8);
		text-shadow: none;
	}
	@keyframes blink {
		50% { opacity: 0; }
	}

	.line {
		position: absolute;
		width: calc(100% - 8px);
		left: -0.5px;
		height: 1px;
		background: #fff;
		z-index: 50;
		animation: lineMove 5s ease-out infinite;
	}

	.text-malfunction:before, .text-malfunction:after {
		content: attr(data-word);
		position: absolute;
		top: 0; left: 0;
		line-height: 50px;
		overflow: hidden;
		font-weight: 800;
		opacity: 0;
	}
	.text-malfunction.typed:before,
	.text-malfunction.typed:after {
		opacity: 1;
	}

	.text-malfunction:before {
		color: #ff0040;
		text-shadow: 1px 0 0 #ff0040;
		z-index: 30;
		animation: malfunctionAni 0.95s infinite;
		clip-path: inset(0 0 60% 0);
	}
	.text-malfunction:after {
		color: #00f0ff;
		text-shadow: -1px 0 0 #00f0ff;
		z-index: 40;
		mix-blend-mode: lighten;
		animation: malfunctionAni 1.1s infinite 0.2s;
		clip-path: inset(40% 0 0 0);
	}

	@keyframes glitchShake {
		0%,90%,100% { transform: translate(-50%,-50%); }
		92% { transform: translate(-48%,-50%); }
		94% { transform: translate(-52%,-50%); }
		96% { transform: translate(-50%,-52%); }
		98% { transform: translate(-50%,-48%); }
	}
	@keyframes lineMove {
		9% { top:38px } 14% { top:8px } 18% { top:42px } 22% { top:1px }
		32% { top:32px } 34% { top:12px } 40% { top:26px } 43% { top:7px } 99% { top:30px }
	}
	@keyframes malfunctionAni {
		10% { top:-0.4px; left:-1.1px } 20% { top:0.4px; left:-0.2px } 30% { left:.5px }
		40% { top:-0.3px; left:-0.7px } 50% { left:0.2px } 60% { top:1.8px; left:-1.2px }
		70% { top:-1px; left:0.1px } 80% { top:-0.4px; left:-0.9px } 90% { left:1.2px } 100% { left:-1.2px }
	}

	.wrapper {
		position: absolute;
		width: 100px;
		bottom: 150px;
		left: 0; right: 0;
		margin: auto;
		font-size: 26px;
		z-index: 100;
	}
	.wrapper i {
		font-size: 60px;
		opacity: 0.5;
		cursor: pointer;
		position: absolute;
		top: 55px; left: 20px;
		animation: opener .5s ease-in-out alternate infinite;
		transition: opacity .2s ease-in-out, transform .5s ease-in-out .2s;
		color: rgba(255,255,255,0.8);
	}
	.wrapper i:hover { opacity: 1; }
	@keyframes opener { 100% { top: 65px } }

	/* SVG wave at bottom */
	.header-wave {
		position: absolute;
		bottom: 0; left: 0;
		width: 100%;
		height: 120px;
		z-index: 80;
	}
</style>
