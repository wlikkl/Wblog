<template>
	<div class="tag-card glass-card fade-up">
		<div class="tag-header">
			<i class="tags icon"></i> 标签云
		</div>
		<div class="tag-body">
			<router-link :to="`/tag/${tag.name}`" class="tag-item" v-for="(tag,index) in tagList" :key="index">
				{{ tag.name }}
			</router-link>
		</div>
	</div>
</template>

<script>
	export default {
		name: "Tags",
		props: {tagList: {type: Array, required: true}}
	}
</script>

<style scoped>
.tag-card {
	padding: 0;
	margin-top: 20px;
	overflow: hidden;
}
.tag-header {
	padding: 14px 18px;
	font-size: 15px;
	font-weight: 600;
	color: var(--text-primary);
	border-bottom: 1px solid rgba(255,255,255,0.06);
}
.tag-body {
	padding: 14px 16px;
	display: flex;
	flex-wrap: wrap;
	gap: 6px;
}
.tag-item {
	display: inline-block;
	padding: 4px 12px;
	border-radius: 6px;
	background: rgba(167,139,250,0.06);
	color: var(--text-secondary) !important;
	font-size: 12px;
	transition: all .2s;
}
.tag-item:hover {
	background: rgba(167,139,250,0.14);
	color: var(--accent) !important;
	transform: translateY(-1px);
}
</style>
