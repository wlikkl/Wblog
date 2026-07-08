<template>
	<div :class="{'has-logo':showLogo}" class="sidebar-no-select">
		<logo v-if="showLogo" :collapse="isCollapse"/>
		<el-scrollbar wrap-class="scrollbar-wrapper">
			<el-menu
					:default-openeds="defaultOpeneds"
					:default-active="activeMenu"
					:collapse="isCollapse"
					background-color="#304156"
					text-color="#bfcbd9"
					active-text-color="#409EFF"
					:unique-opened="false"
					:collapse-transition="false"
					mode="vertical"
			>
				<sidebar-item v-for="route in routes" :key="route.path" :item="route" :base-path="route.path"/>
			</el-menu>
		</el-scrollbar>
	</div>
</template>

<script>
	import {mapGetters} from 'vuex'
	import Logo from './Logo'
	import SidebarItem from './SidebarItem'

	export default {
		components: {SidebarItem, Logo},
		data() {
			return {
				//展开所有父级菜单
				defaultOpeneds: this.$store.state.settings.defaultOpeneds
			}
		},
		computed: {
			...mapGetters([
				'sidebar'
			]),
			routes() {
				return this.$router.options.routes
			},
			activeMenu() {
				const route = this.$route
				const {meta, path} = route
				// if set path, the sidebar will highlight the path you set
				if (meta.activeMenu) {
					return meta.activeMenu
				}
				return path
			},
			showLogo() {
				return this.$store.state.settings.sidebarLogo
			},
			isCollapse() {
				return !this.sidebar.opened
			}
		}
	}
</script>

<style scoped>
	.sidebar-no-select {
		user-select: none;
	}
</style>
