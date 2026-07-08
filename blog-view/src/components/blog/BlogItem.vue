<template>
	<div>
		<div class="blog-card glass-card fade-up tilt-card"
			v-for="item in blogList" :key="item.id"
			@mousemove="onTilt($event)" @mouseleave="onTiltLeave">
			<div class="tilt-glow"></div>
			<div class="blog-card-top" v-if="item.top">
				<i class="arrow alternate circle up icon"></i> 置顶
			</div>
			<div class="blog-card-inner tilt-inner">
				<div class="blog-meta">
					<span class="meta-item">
						<i class="small calendar icon"></i> {{ item.createTime | dateFormat('YYYY-MM-DD') }}
					</span>
					<span class="meta-item">
						<i class="small eye icon"></i> {{ item.views }}
					</span>
					<span class="meta-item">
						<i class="small pencil alternate icon"></i> {{ item.words }}字
					</span>
					<span class="meta-item">
						<i class="small clock icon"></i> {{ item.readTime }}分
					</span>
				</div>

				<h2 class="blog-title">
					<a href="javascript:;" @click.prevent="toBlog(item)">{{ item.title }}</a>
				</h2>

				<div class="blog-category">
					<router-link :to="`/category/${item.category.name}`" class="category-tag">
						<i class="small folder open icon"></i> {{ item.category.name }}
					</router-link>
				</div>

				<div class="blog-desc typo line-numbers match-braces rainbow-braces"
					v-lazy-container="{selector: 'img'}" v-viewer v-html="item.description"></div>

				<div class="blog-footer">
					<a href="javascript:;" @click.prevent="toBlog(item)" class="read-more">
						阅读全文 <i class="arrow right icon"></i>
					</a>
					<div class="blog-tags">
						<router-link :to="`/tag/${tag.name}`" class="tag-pill" v-for="(tag,index) in item.tags" :key="index">
							#{{ tag.name }}
						</router-link>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	export default {
		name: "BlogItem",
		props: {
			blogList: {type: Array, required: true}
		},
		mounted() {
			this.observer = new IntersectionObserver((entries) => {
				entries.forEach(e => {
					if (e.isIntersecting) e.target.classList.add('visible')
				})
			}, {threshold: 0.1})
			this.$nextTick(() => {
				this.$el.querySelectorAll('.fade-up').forEach(el => this.observer.observe(el))
			})
		},
		beforeDestroy() {
			if (this.observer) this.observer.disconnect()
		},
		methods: {
			toBlog(blog) {
				this.$store.dispatch('goBlogPage', blog)
			},
			onTilt(e) {
				const card = e.currentTarget
				const rect = card.getBoundingClientRect()
				const x = e.clientX - rect.left
				const y = e.clientY - rect.top
				const rx = ((y - rect.height / 2) / rect.height) * 10
				const ry = ((x - rect.width / 2) / rect.width) * -10
				card.style.transform = `perspective(1000px) rotateX(${rx}deg) rotateY(${ry}deg)`
				card.style.setProperty('--mx', (x / rect.width * 100) + '%')
				card.style.setProperty('--my', (y / rect.height * 100) + '%')
			},
			onTiltLeave(e) {
				e.currentTarget.style.transform = 'perspective(1000px) rotateX(0) rotateY(0)'
			}
		}
	}
</script>

<style scoped>
.blog-card {
	margin-bottom: 24px;
	overflow: hidden;
	position: relative;
	transition: transform 0.4s ease-out;
}
.blog-card-top {
	position: absolute;
	top: 12px;
	right: -28px;
	background: linear-gradient(135deg, #a78bfa, #a855f7);
	color: #fff;
	font-size: 12px;
	font-weight: 600;
	padding: 3px 36px;
	transform: rotate(45deg);
	z-index: 2;
}
.blog-card-inner {
	padding: 28px 32px;
	position: relative;
	z-index: 1;
}
.blog-meta {
	display: flex;
	flex-wrap: wrap;
	gap: 16px;
	margin-bottom: 12px;
}
.meta-item {
	font-size: 13px;
	color: var(--text-secondary);
	display: inline-flex;
	align-items: center;
	gap: 4px;
}
.blog-title {
	margin: 0 0 14px 0 !important;
	font-size: 22px !important;
	font-weight: 700 !important;
	line-height: 1.4;
}
.blog-title a {
	color: var(--text-primary) !important;
	text-decoration: none;
	transition: color .2s;
}
.blog-title a:hover {
	color: var(--accent) !important;
}
.blog-category {
	margin-bottom: 16px;
}
.category-tag {
	display: inline-flex;
	align-items: center;
	gap: 4px;
	padding: 4px 14px;
	border-radius: 20px;
	background: linear-gradient(135deg, rgba(167,139,250,0.12), rgba(168,85,247,0.12));
	border: 1px solid rgba(167,139,250,0.15);
	color: var(--accent) !important;
	font-size: 13px;
	font-weight: 500;
	transition: all .2s;
}
.category-tag:hover {
	background: linear-gradient(135deg, rgba(167,139,250,0.2), rgba(168,85,247,0.2));
	transform: translateY(-1px);
}
.blog-desc {
	font-size: 15px;
	line-height: 1.8;
	color: var(--text-secondary);
	margin-bottom: 20px;
	overflow: hidden;
	max-height: 120px;
}
.blog-footer {
	display: flex;
	align-items: center;
	justify-content: space-between;
	flex-wrap: wrap;
	gap: 12px;
	padding-top: 16px;
	border-top: 1px solid rgba(255,255,255,0.06);
}
.read-more {
	display: inline-flex;
	align-items: center;
	gap: 4px;
	padding: 6px 18px;
	border-radius: 8px;
	background: rgba(167,139,250,0.1);
	color: var(--accent) !important;
	font-size: 14px;
	font-weight: 500;
	transition: all .2s;
	text-decoration: none;
}
.read-more:hover {
	background: rgba(167,139,250,0.2);
	transform: translateX(2px);
}
.blog-tags {
	display: flex;
	flex-wrap: wrap;
	gap: 8px;
}
.tag-pill {
	font-size: 13px;
	color: var(--text-secondary) !important;
	padding: 2px 8px;
	border-radius: 6px;
	background: rgba(255,255,255,0.03);
	transition: all .2s;
	text-decoration: none;
}
.tag-pill:hover {
	background: rgba(167,139,250,0.1);
	color: var(--accent) !important;
}
</style>
