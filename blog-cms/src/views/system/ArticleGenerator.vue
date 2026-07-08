<template>
	<div class="article-generator">
		<el-card class="box-card">
			<div slot="header" class="card-header">
				<span><i class="el-icon-setting"></i> 自动生成文章配置</span>
				<el-button type="primary" size="small" @click="saveConfig" :loading="saving">
					<i class="el-icon-check"></i> 保存配置
				</el-button>
			</div>

			<el-form :model="config" label-width="140px" label-position="left">
				<!-- 基本设置 -->
				<el-divider content-position="left">基本设置</el-divider>
				
				<el-form-item label="启用自动生成">
					<el-switch v-model="config.enabled" active-color="#67C23A"></el-switch>
					<span class="form-tip">开启后将按设定频率自动生成文章</span>
				</el-form-item>

				<el-form-item label="生成频率">
					<el-select v-model="config.frequency" placeholder="请选择生成频率">
						<el-option label="每天" value="daily"></el-option>
						<el-option label="每周" value="weekly"></el-option>
						<el-option label="每月" value="monthly"></el-option>
						<el-option label="自定义Cron" value="custom"></el-option>
					</el-select>
				</el-form-item>

				<el-form-item label="生成时间" v-if="config.frequency !== 'custom'">
					<el-time-picker v-model="config.generateTime" format="HH:mm" value-format="HH:mm" placeholder="选择时间"></el-time-picker>
				</el-form-item>

				<el-form-item label="Cron表达式" v-if="config.frequency === 'custom'">
					<el-input v-model="config.cronExpression" placeholder="例: 0 8 * * * (每天8点)"></el-input>
					<span class="form-tip">格式: 秒 分 时 日 月 周</span>
				</el-form-item>

				<!-- 内容设置 -->
				<el-divider content-position="left">内容设置</el-divider>

				<el-form-item label="文章主题">
					<el-checkbox-group v-model="config.topics">
						<el-checkbox label="erp">
							<el-tag type="success" size="small">ERP</el-tag>
						</el-checkbox>
						<el-checkbox label="ai">
							<el-tag type="primary" size="small">AI</el-tag>
						</el-checkbox>
						<el-checkbox label="manufacturing">
							<el-tag type="warning" size="small">制造业</el-tag>
						</el-checkbox>
					</el-checkbox-group>
				</el-form-item>

				<el-form-item label="自定义主题">
					<el-tag v-for="(topic, index) in config.customTopics" :key="index" closable @close="removeCustomTopic(index)">
						{{ topic }}
					</el-tag>
					<el-input v-if="inputVisible" v-model="inputValue" ref="saveTagInput" size="small" @keyup.enter.native="addCustomTopic" @blur="addCustomTopic" style="width: 200px; margin-left: 10px;"></el-input>
					<el-button v-else size="small" @click="showInput">+ 添加主题</el-button>
				</el-form-item>

				<el-form-item label="文章字数">
					<el-input-number v-model="config.articleWordCount" :min="500" :max="5000" :step="500"></el-input-number>
					<span class="form-tip">字</span>
				</el-form-item>

				<!-- 图片设置 -->
				<el-divider content-position="left">图片设置</el-divider>

				<el-form-item label="生成封面图">
					<el-switch v-model="config.generateCoverImage" active-color="#67C23A"></el-switch>
					<span class="form-tip">为文章生成一张封面图</span>
				</el-form-item>

				<el-form-item label="生成正文配图">
					<el-switch v-model="config.generateBodyImages" active-color="#67C23A"></el-switch>
					<span class="form-tip">在文章正文中插入配图</span>
				</el-form-item>

				<el-form-item label="正文配图数量" v-if="config.generateBodyImages">
					<el-input-number v-model="config.bodyImageCount" :min="1" :max="6" :step="1"></el-input-number>
					<span class="form-tip">张</span>
				</el-form-item>

				<!-- 发布设置 -->
				<el-divider content-position="left">发布设置</el-divider>

				<el-form-item label="文章分类">
					<el-input v-model="config.category" placeholder="例: AI技术"></el-input>
				</el-form-item>

				<el-form-item label="文章标签">
					<el-tag v-for="(tag, index) in config.tags" :key="index" closable @close="removeTag(index)">
						{{ tag }}
					</el-tag>
					<el-input v-if="tagInputVisible" v-model="tagInputValue" ref="saveTagInput2" size="small" @keyup.enter.native="addTag" @blur="addTag" style="width: 120px; margin-left: 10px;"></el-input>
					<el-button v-else size="small" @click="showTagInput">+ 添加标签</el-button>
				</el-form-item>

				<el-form-item label="自动发布">
					<el-switch v-model="config.autoPublish" active-color="#67C23A"></el-switch>
					<span class="form-tip">生成后自动发布到前台</span>
				</el-form-item>
			</el-form>
		</el-card>

		<!-- 预览当前配置 -->
		<el-card class="box-card" style="margin-top: 20px;">
			<div slot="header" class="card-header">
				<span><i class="el-icon-view"></i> 当前配置预览</span>
			</div>
			<el-descriptions :column="2" border>
				<el-descriptions-item label="状态">
					<el-tag :type="config.enabled ? 'success' : 'info'">{{ config.enabled ? '已启用' : '已禁用' }}</el-tag>
				</el-descriptions-item>
				<el-descriptions-item label="生成频率">{{ frequencyText }}</el-descriptions-item>
				<el-descriptions-item label="文章主题">
					<el-tag v-for="topic in config.topics" :key="topic" size="small" style="margin-right: 5px;">{{ topic.toUpperCase() }}</el-tag>
				</el-descriptions-item>
				<el-descriptions-item label="文章字数">{{ config.articleWordCount }} 字</el-descriptions-item>
				<el-descriptions-item label="封面图">{{ config.generateCoverImage ? '✅ 生成' : '❌ 不生成' }}</el-descriptions-item>
				<el-descriptions-item label="正文配图">{{ config.generateBodyImages ? `✅ ${config.bodyImageCount} 张` : '❌ 不生成' }}</el-descriptions-item>
				<el-descriptions-item label="分类">{{ config.category }}</el-descriptions-item>
				<el-descriptions-item label="标签">
					<el-tag v-for="tag in config.tags" :key="tag" size="small" type="info" style="margin-right: 5px;">{{ tag }}</el-tag>
				</el-descriptions-item>
			</el-descriptions>
		</el-card>
	</div>
</template>

<script>
import {getConfig, saveConfig} from '@/api/articleGenerator'

export default {
	name: 'ArticleGenerator',
	data() {
		return {
			config: {
				enabled: true,
				frequency: 'daily',
				cronExpression: '0 8 * * *',
				generateTime: '08:00',
				topics: ['erp', 'ai', 'manufacturing'],
				customTopics: [],
				generateCoverImage: true,
				generateBodyImages: true,
				bodyImageCount: 3,
				articleWordCount: 2000,
				category: 'AI技术',
				tags: ['AI', '技术博客'],
				autoPublish: true
			},
			saving: false,
			inputVisible: false,
			inputValue: '',
			tagInputVisible: false,
			tagInputValue: ''
		}
	},
	computed: {
		frequencyText() {
			const map = {
				'daily': '每天',
				'weekly': '每周',
				'monthly': '每月',
				'custom': '自定义: ' + this.config.cronExpression
			}
			return map[this.config.frequency] || this.config.frequency
		}
	},
	created() {
		this.loadConfig()
	},
	methods: {
		async loadConfig() {
			try {
				const res = await getConfig()
				if (res.code === 200) {
					this.config = {...this.config, ...res.data}
				}
			} catch (e) {
				console.error('加载配置失败', e)
			}
		},
		async saveConfig() {
			this.saving = true
			try {
				const res = await saveConfig(this.config)
				if (res.code === 200) {
					this.$message.success('配置保存成功')
				} else {
					this.$message.error(res.msg || '保存失败')
				}
			} catch (e) {
				this.$message.error('保存失败: ' + e.message)
			} finally {
				this.saving = false
			}
		},
		removeCustomTopic(index) {
			this.config.customTopics.splice(index, 1)
		},
		showInput() {
			this.inputVisible = true
			this.$nextTick(() => {
				this.$refs.saveTagInput.focus()
			})
		},
		addCustomTopic() {
			const value = this.inputValue
			if (value) {
				this.config.customTopics.push(value)
			}
			this.inputVisible = false
			this.inputValue = ''
		},
		removeTag(index) {
			this.config.tags.splice(index, 1)
		},
		showTagInput() {
			this.tagInputVisible = true
			this.$nextTick(() => {
				this.$refs.saveTagInput2.focus()
			})
		},
		addTag() {
			const value = this.tagInputValue
			if (value) {
				this.config.tags.push(value)
			}
			this.tagInputVisible = false
			this.tagInputValue = ''
		}
	}
}
</script>

<style scoped>
.article-generator {
	padding: 20px;
}
.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
}
.card-header span {
	font-size: 16px;
	font-weight: bold;
}
.form-tip {
	margin-left: 10px;
	color: #909399;
	font-size: 12px;
}
.el-divider {
	margin: 20px 0;
}
</style>
