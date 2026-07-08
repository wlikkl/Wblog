<template>
	<div class="blog-detail">
		<div class="article-card glass-card">
			<div class="article-top" v-if="blog.top"><i class="arrow alternate circle up icon"></i> 置顶</div>
			<div class="article-body">
				<h1 class="article-title">{{ blog.title }}</h1>
				<div class="article-meta">
					<span class="meta-item"><i class="small calendar icon"></i> {{ blog.createTime | dateFormat('YYYY-MM-DD') }}</span>
					<span class="meta-item"><i class="small eye icon"></i> {{ blog.views }}</span>
					<span class="meta-item"><i class="small pencil alternate icon"></i> {{ blog.words }}字</span>
					<span class="meta-item"><i class="small clock icon"></i> {{ blog.readTime }}分</span>
					<a class="meta-item" @click.prevent="bigFontSize=!bigFontSize">
						<i class="font icon"></i> 字体
					</a>
					<a class="meta-item" @click.prevent="changeFocusMode">
						<i class="book icon"></i> 专注
					</a>
				</div>
				<router-link :to="`/category/${blog.category.name}`" class="category-tag" v-if="blog.category">
					<i class="small folder open icon"></i> {{ blog.category.name }}
				</router-link>
				<div class="article-content typo js-toc-content match-braces rainbow-braces"
					v-lazy-container="{selector: 'img'}" v-viewer
					:class="{'big-font':bigFontSize}" v-html="blog.content"></div>
				<div class="article-reward" v-if="blog.appreciation">
					<el-popover placement="top" width="220" trigger="click">
						<div style="text-align:center">
							<div style="font-size:12px;color:var(--text-secondary);margin-bottom:8px;">一毛是鼓励</div>
							<img :src="$store.state.siteInfo.reward" style="width:100%;border-radius:8px">
							<div style="font-size:12px;color:var(--text-secondary);margin-top:8px;">一块是真爱</div>
						</div>
						<el-button slot="reference" class="reward-btn">赞赏</el-button>
					</el-popover>
				</div>
				<el-divider></el-divider>
				<div class="article-tags">
					<router-link :to="`/tag/${tag.name}`" class="tag-pill" v-for="(tag,index) in blog.tags" :key="index">#{{ tag.name }}</router-link>
				</div>
			</div>
		</div>

		<div class="article-info glass-card">
			<ul>
				<li>作者：{{ $store.state.introduction.name }} <router-link to="/about">（联系作者）</router-link></li>
				<li>发表时间：{{ blog.createTime | dateFormat('YYYY-MM-DD HH:mm') }}</li>
				<li>最后修改：{{ blog.updateTime | dateFormat('YYYY-MM-DD HH:mm') }}</li>
				<li>本站点采用<a href="https://creativecommons.org/licenses/by/4.0/" target="_blank"> 署名 4.0 国际 (CC BY 4.0) </a>创作共享协议。</li>
			</ul>
		</div>

		<div class="comment-section glass-card">
			<CommentList :page="0" :blogId="blogId" v-if="blog.commentEnabled"/>
			<div v-else class="comment-closed">评论已关闭</div>
		</div>
	</div>
</template>

<script>
	import {getBlogById} from "@/api/blog";
	import CommentList from "@/components/comment/CommentList";
	import {mapState} from "vuex";
	import {SET_FOCUS_MODE, SET_IS_BLOG_RENDER_COMPLETE} from '@/store/mutations-types';

	export default {
		name: "Blog",
		components: {CommentList},
		data() { return {blog: {}, bigFontSize: false} },
		computed: {
			blogId() { return parseInt(this.$route.params.id) },
			...mapState(['siteInfo', 'focusMode'])
		},
		beforeRouteEnter(to, from, next) {
			next(vm => vm.$store.commit(SET_IS_BLOG_RENDER_COMPLETE, false))
		},
		beforeRouteLeave(to, from, next) {
			this.$store.commit(SET_FOCUS_MODE, false)
			tocbot.destroy()
			next()
		},
		beforeRouteUpdate(to, from, next) {
			if (to.path !== from.path) {
				this.$store.commit(SET_FOCUS_MODE, false)
				this.getBlog(to.params.id)
				this.$store.commit(SET_IS_BLOG_RENDER_COMPLETE, false)
				next()
			}
		},
		created() { this.getBlog() },
		methods: {
			getBlog(id = this.blogId) {
				const blogToken = window.localStorage.getItem(`blog${id}`)
				const adminToken = window.localStorage.getItem('adminToken')
				const token = adminToken ? adminToken : (blogToken ? blogToken : '')
				getBlogById(token, id).then(res => {
					if (res.code === 200) {
						this.blog = res.data
						document.title = this.blog.title + this.siteInfo.webTitleSuffix
						this.$nextTick(() => {
							Prism.highlightAll()
							this.$store.commit(SET_IS_BLOG_RENDER_COMPLETE, true)
						})
					} else { this.msgError(res.msg) }
				}).catch(() => { this.msgError("请求失败") })
			},
			changeFocusMode() { this.$store.commit(SET_FOCUS_MODE, !this.focusMode) }
		}
	}
</script>

<style scoped>
.blog-detail {
	max-width: 860px;
	margin: 0 auto;
}
.article-card {
	overflow: hidden;
	position: relative;
}
.article-top {
	position: absolute; top: 12px; right: -28px;
	background: linear-gradient(135deg, #a78bfa, #a855f7);
	color: #fff; font-size: 12px; font-weight: 600;
	padding: 3px 36px; transform: rotate(45deg); z-index: 2;
}
.article-body { padding: 36px 40px; }
.article-title {
	font-size: 28px !important;
	font-weight: 700 !important;
	margin: 0 0 16px 0 !important;
	color: var(--text-primary) !important;
	line-height: 1.4;
}
.article-meta {
	display: flex; flex-wrap: wrap; gap: 16px;
	margin-bottom: 16px;
}
.meta-item {
	font-size: 13px; color: var(--text-secondary);
	display: inline-flex; align-items: center; gap: 4px;
	cursor: pointer; transition: color .2s;
}
.meta-item:hover { color: var(--accent); }
.category-tag {
	display: inline-flex; align-items: center; gap: 4px;
	padding: 4px 14px; border-radius: 20px;
	background: linear-gradient(135deg, rgba(167,139,250,0.12), rgba(168,85,247,0.12));
	border: 1px solid rgba(167,139,250,0.15);
	color: var(--accent) !important;
	font-size: 13px; font-weight: 500;
	transition: all .2s; margin-bottom: 20px;
}
.category-tag:hover {
	background: linear-gradient(135deg, rgba(167,139,250,0.2), rgba(168,85,247,0.2));
	transform: translateY(-1px);
}
.article-content {
	font-size: 16px; line-height: 1.9;
	color: var(--text-primary);
}
.article-content.big-font { font-size: 18px; }
.article-reward { text-align: center; margin: 2em 0; }
.reward-btn {
	background: linear-gradient(135deg, #a78bfa, #a855f7) !important;
	color: #fff !important; border: none !important;
	padding: 8px 28px !important; border-radius: 8px !important;
	font-size: 14px !important; font-weight: 500 !important;
}
.article-tags { display: flex; flex-wrap: wrap; gap: 8px; }
.tag-pill {
	font-size: 13px; color: var(--text-secondary) !important;
	padding: 2px 10px; border-radius: 6px;
	background: rgba(255,255,255,0.03);
	transition: all .2s;
}
.tag-pill:hover { background: rgba(167,139,250,0.1); color: var(--accent) !important; }
.article-info {
	padding: 20px 24px; margin: 20px 0;
	font-size: 14px; color: var(--text-secondary);
}
.article-info ul { margin: 0; padding-left: 16px; }
.article-info li { margin: 4px 0; }
.comment-section {
	padding: 24px;
}
.comment-closed {
	text-align: center;
	color: var(--text-secondary);
	font-size: 15px;
	padding: 20px;
}
.el-divider { margin: 1.5rem 0 !important; }
h1::before, h2::before, h3::before, h4::before, h5::before, h6::before {
	display: block; content: " ";
	height: 60px; margin-top: -60px; visibility: hidden;
}
</style>
