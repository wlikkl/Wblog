<template>
	<div class="random-card glass-card fade-up">
		<div class="random-header">
			<i class="bookmark icon"></i> 随机文章
		</div>
		<div class="random-body">
			<div class="random-item" v-for="blog in randomBlogList" :key="blog.id" @click.prevent="toBlog(blog)">
				<div class="random-img" :style="{'background-image':'url(' + blog.firstPicture + ')'}"></div>
				<div class="random-info">
					<div class="random-date">{{ blog.createTime | dateFormat('MM-dd') }}</div>
					<div class="random-title">{{ blog.title }}</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	export default {
		name: "RandomBlog",
		props: {randomBlogList: {type: Array, required: true}},
		methods: {
			toBlog(blog) { this.$store.dispatch('goBlogPage', blog) }
		}
	}
</script>

<style scoped>
.random-card {
	padding: 0;
	margin-top: 20px;
	overflow: hidden;
}
.random-header {
	padding: 14px 18px;
	font-size: 15px;
	font-weight: 600;
	color: var(--text-primary);
	border-bottom: 1px solid rgba(255,255,255,0.06);
}
.random-body {
	padding: 8px 12px;
}
.random-item {
	display: flex;
	gap: 10px;
	padding: 8px 0;
	cursor: pointer;
	border-radius: 8px;
	transition: all .2s;
}
.random-item:hover {
	background: rgba(255,255,255,0.03);
}
.random-img {
	width: 56px;
	height: 56px;
	border-radius: 8px;
	flex-shrink: 0;
	background-size: cover;
	background-position: center;
}
.random-info {
	flex: 1;
	min-width: 0;
	display: flex;
	flex-direction: column;
	justify-content: center;
}
.random-date {
	font-size: 11px;
	color: var(--text-secondary);
	margin-bottom: 2px;
}
.random-title {
	font-size: 13px;
	color: var(--text-primary);
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}
</style>
