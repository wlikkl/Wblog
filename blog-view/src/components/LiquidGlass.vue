<template>
	<div class="liquid-glass-wrap">
		<div class="liquid-glass-bg"></div>
		<div class="liquid-glass-content">
			<slot/>
		</div>
	</div>
</template>

<script>
	export default {
		name: 'LiquidGlass'
	}
</script>

<style scoped>
.liquid-glass-wrap {
	position: relative;
	overflow: hidden;
	border-radius: 20px;
}
.liquid-glass-bg {
	position: absolute;
	inset: -40px;
	z-index: 0;
	background:
		radial-gradient(ellipse at 20% 50%, rgba(120,119,198,0.3) 0%, transparent 50%),
		radial-gradient(ellipse at 80% 20%, rgba(255,120,120,0.2) 0%, transparent 50%),
		radial-gradient(ellipse at 50% 80%, rgba(120,200,255,0.25) 0%, transparent 50%);
	animation: liquidFlow 8s ease-in-out infinite alternate;
	filter: blur(40px);
}
@keyframes liquidFlow {
	0% { transform: translate(0, 0) scale(1); }
	33% { transform: translate(20px, -15px) scale(1.05); }
	66% { transform: translate(-15px, 10px) scale(0.95); }
	100% { transform: translate(10px, -10px) scale(1.02); }
}
.liquid-glass-content {
	position: relative;
	z-index: 1;
	background: rgba(255,255,255,0.08);
	backdrop-filter: blur(20px);
	-webkit-backdrop-filter: blur(20px);
	border: 1px solid rgba(255,255,255,0.15);
	border-radius: 20px;
	padding: 24px;
}
</style>
