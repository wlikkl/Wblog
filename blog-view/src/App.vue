<template>
	<div id="app" @mousemove="onMouseMove">
		<div class="bg-ambient">
			<div class="bg-orb bg-orb-1"></div>
			<div class="bg-orb bg-orb-2"></div>
			<div class="bg-orb bg-orb-3"></div>
		</div>
		<StarfieldBackground ref="starfield" />
		<div class="cursor-glow" :style="glowStyle"></div>
		<div class="reading-progress" :style="{width: scrollPercent+'%'}"></div>
		<div class="app-content">
			<transition name="page-fade" mode="out-in">
				<router-view/>
			</transition>
		</div>
		<ChatBot/>
		<div class="float-buttons">
			<a href="/admin/" target="_blank" class="float-btn float-btn-admin" title="后台管理">
				<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="4"/><path d="M4 20c0-4 3.5-6 8-6s8 2 8 6"/></svg>
			</a>
			<a href="/mineradio/" target="_blank" class="float-btn float-btn-music" title="音乐播放器">
				<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 18V5l12-2v13"/><circle cx="6" cy="18" r="3"/><circle cx="18" cy="16" r="3"/></svg>
			</a>
		</div>
	</div>
</template>

<script>
	import ChatBot from '@/components/ChatBot'
	import StarfieldBackground from '@/components/effect/StarfieldBackground'

	export default {
		name: 'app',
		components: {ChatBot, StarfieldBackground},
		data() {
			return { mx: 0, my: 0, scrollPercent: 0 }
		},
		computed: {
			glowStyle() {
				return {
					'--mx': this.mx + 'px',
					'--my': this.my + 'px'
				}
			}
		},
		mounted() {
			window.addEventListener('scroll', this.onScroll)
		},
		beforeDestroy() {
			window.removeEventListener('scroll', this.onScroll)
		},
		methods: {
			onMouseMove(e) {
				this.mx = e.clientX
				this.my = e.clientY
			},
			onScroll() {
				const scrollTop = window.scrollY
				const docHeight = document.documentElement.scrollHeight - window.innerHeight
				this.scrollPercent = docHeight > 0 ? Math.min(100, (scrollTop / docHeight) * 100) : 0
			}
		}
	}
</script>

<style>
#app {
	min-height: 100vh;
	position: relative;
	background: #0e0a14;
	overflow-x: hidden;
}
.bg-ambient {
	position: fixed;
	inset: 0;
	z-index: 0;
	pointer-events: none;
	overflow: hidden;
}
.bg-orb {
	position: absolute;
	border-radius: 50%;
	filter: blur(80px);
	opacity: 0.55;
	animation: orbFloat 12s ease-in-out infinite alternate;
}
.bg-orb-1 {
	width: 500px;
	height: 500px;
	top: -10%;
	left: -10%;
	background: radial-gradient(circle, rgba(167,139,250,0.7), transparent);
	animation-duration: 14s;
}
.bg-orb-2 {
	width: 400px;
	height: 400px;
	bottom: -5%;
	right: -5%;
	background: radial-gradient(circle, rgba(196,181,253,0.6), transparent);
	animation-duration: 10s;
	animation-delay: -4s;
}
.bg-orb-3 {
	width: 300px;
	height: 300px;
	top: 40%;
	left: 50%;
	background: radial-gradient(circle, rgba(124,58,237,0.55), transparent);
	animation-duration: 16s;
	animation-delay: -8s;
}
@keyframes orbFloat {
	0% { transform: translate(0, 0) scale(1); }
	33% { transform: translate(30px, -20px) scale(1.1); }
	66% { transform: translate(-20px, 15px) scale(0.9); }
	100% { transform: translate(15px, -15px) scale(1.05); }
}

/* 鼠标跟随光晕 */
.cursor-glow {
	position: fixed;
	inset: 0;
	z-index: 0;
	pointer-events: none;
	background: radial-gradient(circle 300px at var(--mx, -999px) var(--my, -999px), rgba(167,139,250,0.08), transparent 70%);
	transition: background 0.3s ease-out;
}

.float-buttons {
	position: fixed;
	bottom: 100px;
	right: 24px;
	z-index: 9998;
	display: flex;
	flex-direction: column;
	gap: 10px;
}
.float-btn {
	width: 44px;
	height: 44px;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	background: rgba(255,255,255,0.06);
	border: 1px solid rgba(255,255,255,0.1);
	backdrop-filter: blur(12px);
	-webkit-backdrop-filter: blur(12px);
	cursor: pointer;
	transition: all .3s ease;
	text-decoration: none;
	color: var(--text-secondary) !important;
}
.float-btn svg {
	width: 20px;
	height: 20px;
}
.float-btn:hover {
	background: rgba(167,139,250,0.15);
	border-color: var(--accent);
	color: var(--accent) !important;
	transform: translateX(-2px);
}
.app-content {
	position: relative;
	z-index: 1;
	background: rgba(14,10,20,0.25);
	backdrop-filter: blur(4px);
	-webkit-backdrop-filter: blur(4px);
	min-height: 100vh;
}

/* ===== Reading Progress Bar ===== */
.reading-progress {
	position: fixed; top: 0; left: 0;
	height: 3px;
	background: linear-gradient(90deg, #7c3aed, #a78bfa, #c4b5fd);
	z-index: 9999;
	border-radius: 0 3px 3px 0;
	transition: width 0.15s ease-out;
	box-shadow: 0 0 8px rgba(167,139,250,0.5);
}

/* ===== Page Transition ===== */
.page-fade-enter-active, .page-fade-leave-active {
	transition: opacity 0.3s ease, transform 0.3s ease;
}
.page-fade-enter {
	opacity: 0;
	transform: translateY(12px);
}
.page-fade-leave-to {
	opacity: 0;
	transform: translateY(-12px);
}
</style>
