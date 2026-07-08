<template>
	<footer class="site-footer glass-card">
		<div class="footer-inner">
			<div class="footer-grid">
				<div class="footer-col">
					<h4>{{ siteInfo.footerImgTitle }}</h4>
					<img :src="siteInfo.footerImgUrl" class="footer-img" alt="">
				</div>
				<div class="footer-col">
					<h4>最新博客</h4>
					<div class="footer-links">
						<a href="javascript:;" @click.prevent="toBlog(item)" v-for="item in newBlogList" :key="item.id">{{ item.title }}</a>
					</div>
				</div>
				<div class="footer-col footer-quote">
					<p class="hitokoto">{{ hitokoto.hitokoto }}</p>
					<p class="hitokoto-from" v-text="hitokoto.from?`——《${hitokoto.from}》`:''"></p>
				</div>
			</div>
			<div class="footer-divider"></div>
			<div class="footer-bottom">
				<div class="footer-actions">
					<a href="/admin/" target="_blank" class="footer-action-link">
						<i class="user secret icon"></i> 后台管理
					</a>
					<a href="/mineradio/" target="_blank" class="footer-action-link">
						<i class="music icon"></i> 音乐播放器
					</a>
				</div>
				<p>
					<span v-if="siteInfo.copyright">{{ siteInfo.copyright.title }}</span>
					<router-link to="/" v-if="siteInfo.copyright">{{ siteInfo.copyright.siteName }}</router-link>
					<span v-if="siteInfo.copyright && siteInfo.beian"> | </span>
					<a rel="external nofollow noopener" href="https://beian.miit.gov.cn/" target="_blank">{{ siteInfo.beian }}</a>
				</p>
				<div class="github-badge" v-for="(item,index) in badges" :key="index">
					<a rel="external nofollow noopener" :href="item.url" target="_blank" :title="item.title">
						<span class="badge-subject">{{ item.subject }}</span>
						<span class="badge-value" :class="`bg-${item.color}`">{{ item.value }}</span>
					</a>
				</div>
			</div>
		</div>
	</footer>
</template>

<script>
	export default {
		name: "Footer",
		props: {
			siteInfo: {type: Object, required: true},
			badges: {type: Array, required: true},
			newBlogList: {type: Array, required: true},
			hitokoto: {type: Object, required: true}
		},
		methods: {
			toBlog(blog) { this.$store.dispatch('goBlogPage', blog) }
		}
	}
</script>

<style scoped>
@import "../../assets/css/badge.css";
.site-footer {
	margin-top: 40px;
	padding: 32px 0 20px;
	border-radius: 0 !important;
	border-left: none !important;
	border-right: none !important;
	border-bottom: none !important;
}
.footer-inner {
	max-width: 1400px;
	margin: 0 auto;
	padding: 0 20px;
}
.footer-grid {
	display: grid;
	grid-template-columns: 1fr 2fr 2fr;
	gap: 30px;
}
.footer-col h4 {
	font-size: 15px;
	font-weight: 600;
	color: var(--text-primary);
	margin-bottom: 12px;
}
.footer-img {
	width: 100px;
	border-radius: 8px;
}
.footer-links {
	display: flex;
	flex-direction: column;
	gap: 6px;
}
.footer-links a {
	font-size: 13px;
	color: var(--text-secondary) !important;
	transition: color .2s;
	padding: 2px 0;
}
.footer-links a:hover { color: var(--accent) !important; }
.hitokoto {
	font-size: 14px;
	color: var(--text-secondary);
	font-style: italic;
	line-height: 1.7;
}
.hitokoto-from { font-size: 12px; color: var(--text-secondary); text-align: right; }
.footer-divider {
	height: 1px;
	background: rgba(255,255,255,0.06);
	margin: 20px 0 16px;
}
.footer-bottom {
	display: flex;
	justify-content: center;
	align-items: center;
	flex-wrap: wrap;
	gap: 12px;
	font-size: 13px;
	color: var(--text-secondary);
}
.footer-bottom a { color: var(--accent) !important; }
.github-badge a { color: #fff !important; }
.footer-actions {
	display: flex;
	gap: 16px;
	margin-bottom: 12px;
}
.footer-action-link {
	display: inline-flex;
	align-items: center;
	gap: 6px;
	padding: 6px 18px;
	border-radius: 8px;
	background: rgba(167,139,250,0.08);
	color: var(--accent) !important;
	font-size: 13px;
	font-weight: 500;
	transition: all .2s;
	text-decoration: none;
}
.footer-action-link:hover {
	background: rgba(167,139,250,0.18);
	transform: translateY(-1px);
}
@media (max-width: 768px) {
	.footer-grid { grid-template-columns: 1fr; gap: 20px; }
}
</style>
