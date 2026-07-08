<template>
	<div id="app" :class="themeClass">
		<router-view/>
	</div>
</template>

<script>
	export default {
		name: 'App',
		computed: {
			themeClass() {
				const theme = localStorage.getItem('admin-theme') || 'violet'
				return 'theme-' + theme
			}
		},
		created() {
			// 初始化主题
			const theme = localStorage.getItem('admin-theme') || 'violet'
			document.body.classList.add('theme-' + theme)
		}
	}
</script>

<style>
/* 主题 CSS 已在 main.js 中全局导入 */
</style>
