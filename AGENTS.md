# NBlog 项目知识库

## 项目概述

基于 NBlog（Spring Boot + Vue 2.x 博客系统，原作者 Naccl，GitHub 2.7k⭐）深度定制的个人博客。

- **仓库**: https://github.com/wlikkl/Wblog.git
- **服务器**: 112.74.97.85（阿里云 2H2G Ubuntu）
- **品牌名**: wilk（原 Naccl 已全站替换）

## 技术栈

### 前端
- **blog-view**: Vue 2.x + Semantic UI + Element UI（前台博客）
- **blog-cms**: Vue 2.x + Element UI + mavon-editor（后台管理）
- **构建**: `npm run build`，输出到 `dist/`

### 后端
- **Spring Boot 2.2.7** + MyBatis + Spring Security + JWT
- **Java 11**（需 JAXB 兼容修复）
- **MySQL 8.0** + **Redis**

### 部署
- **Nginx** 反向代理（80端口）
- **Mineradio** 音乐播放器（端口3003，/mineradio/ 路径）

## 服务器信息

```
SSH: root@112.74.97.85
密码: Luohz20030512
MySQL root 密码: [见 application-prod.properties]
后端 JAR: /root/nblog/blog-api/target/blog-api-0.0.1.jar
启动命令: cd /root/nblog/blog-api && nohup java -Xmx512m -jar target/blog-api-0.0.1.jar --spring.profiles.active=prod > /tmp/nblog.log 2>&1 &
前端目录: /var/www/html/
后台目录: /var/www/html/admin/
音乐播放器: /root/mineradio (端口3003)
Nginx配置: /etc/nginx/sites-enabled/nblog
```

## 重要文件路径

### 后端 (blog-api)
- `src/main/java/top/naccl/controller/ChatController.java` - AI 聊天
- `src/main/java/top/naccl/controller/admin/AIController.java` - AI 文章/配图生成
- `src/main/java/top/naccl/controller/admin/ArticleGeneratorController.java` - 文章生成配置
- `src/main/java/top/naccl/controller/admin/ServerMonitorController.java` - 服务器监控 API
- `src/main/resources/application-dev.properties` - 开发配置
- `src/main/resources/application-prod.properties` - 生产配置（MySQL密码123456）

### 前端 (blog-view)
- `src/App.vue` - 星空粒子背景、鼠标光晕、阅读进度条、页面过渡
- `src/components/ChatBot.vue` - AI 聊天浮动按钮
- `src/components/effect/StarfieldBackground.vue` - 星空粒子 Canvas 组件
- `src/components/index/Header.vue` - 打字机效果 + SVG 波浪
- `src/components/blog/BlogItem.vue` - 3D 卡片倾斜效果
- `src/assets/css/theme.css` - 紫罗兰暗黑玻璃主题
- `src/assets/css/typo.css` - 文章排版暗色适配
- `src/plugins/axios.js` - axios 配置（baseURL 生产用 /api/）

### 后台管理 (blog-cms)
- `src/views/monitor/Monitor.vue` - 服务器监控（有独立主题切换）
- `src/views/system/ArticleGenerator.vue` - 文章生成配置页
- `src/api/monitor.js` - 监控 API
- `src/api/articleGenerator.js` - 文章生成配置 API
- `src/assets/styles/themes/` - violet/dark/cyber/light 主题文件（当前未启用，默认亮色）
- `src/router/index.js` - 路由配置

## 主题系统

### 前台 (blog-view) - 紫罗兰暗黑玻璃风
- 主色: `#a78bfa` (紫罗兰)
- 背景: `#0e0a14` (深紫黑)
- 玻璃卡片: `rgba(14,10,20,0.55)` + `backdrop-filter: blur(20px)`
- 动态效果: 星空粒子、鼠标光晕、3D 卡片倾斜、打字机标题、SVG 波浪、滚动渐入、页面过渡、阅读进度条、导航栏滚动变色

### 后台 (blog-cms) - 默认 Element UI 亮色
- 暗色主题代码已删除，恢复默认亮色
- Monitor 页面有独立主题切换（violet/dark/cyber）

## AI 功能

### Agnes API 配置
```
Chat URL: https://apihub.agnes-ai.com/v1/chat/completions
Image URL: https://apihub.agnes-ai.com/v1/images/generations
Model: agnes-2.0-flash
Image Model: agnes-image-2.1-flash
API Key: sk-1abnfGshjccgt9JJga2BCzH1WC71LgFppZuIXjBrC4CQeo0O
```

### 功能
1. **AI 聊天** (ChatBot.vue) - 浮动按钮，调用 /api/chat
2. **AI 生成文章** (WriteBlog.vue) - 调用 /admin/ai/article
3. **AI 生成配图** (WriteBlog.vue) - 调用 /admin/ai/image
4. **AI 文章自动定时生成** (daily-article-generator.py + ArticleGeneratorController)
5. **图片本地存储** - 保存到 /root/nblog/upload/ai/

## 定时任务

```bash
# MySQL 保活（每2分钟）
*/2 * * * * /root/mysql-guard.sh

# 数据库备份（每6小时，保留7天）
0 */6 * * * /root/nblog-backup.sh

# 自动生成文章（每天8点）
0 8 * * * /usr/bin/python3 /root/daily-article-generator.py >> /tmp/daily-article.log 2>&1
```

## Nginx 路由配置

```
/admin/login        → localhost:8090 (登录 API)
/admin/ai/          → localhost:8090 (AI 接口，120s超时)
/admin/monitor      → localhost:8090 (监控 API)
/admin/article-generator → localhost:8090 (文章生成配置)
/admin/             → localhost:8090 (其他管理 API)
= /admin            → /var/www/html/admin/index.html (SPA)
/api/               → localhost:8090 (博客 API)
/image/             → localhost:8090 (图片代理)
/mineradio/         → localhost:3003 (音乐播放器)
/vendor/            → localhost:3003
/                   → SPA fallback
```

## 部署流程

### 前端部署
```bash
# 1. 修改 baseURL
sed -i '' "s|baseURL: 'http://localhost:8090/'|baseURL: '/api/'|" blog-view/src/plugins/axios.js

# 2. 构建
cd blog-view && npm run build

# 3. 打包上传
cd dist && tar czf /tmp/frontend-dist.tar.gz .
scp /tmp/frontend-dist.tar.gz root@112.74.97.85:/tmp/

# 4. 服务器解压
ssh root@112.74.97.85 "rm -rf /var/www/html/static /var/www/html/index.html && cd /var/www/html && tar xzf /tmp/frontend-dist.tar.gz"

# 5. 恢复本地配置
sed -i '' "s|baseURL: '/api/'|baseURL: 'http://localhost:8090/'|" blog-view/src/plugins/axios.js
```

### 后端部署
```bash
# 1. 上传源码
scp blog-api/src/main/java/.../NewFile.java root@112.74.97.85:/root/nblog/blog-api/src/.../

# 2. 服务器构建
ssh root@112.74.97.85 "cd /root/nblog/blog-api && mvn clean package -DskipTests -q"

# 3. 重启
ssh root@112.74.97.85 "kill \$(pgrep -f blog-api) && cd /root/nblog/blog-api && nohup java -Xmx512m -jar target/blog-api-0.0.1.jar --spring.profiles.active=prod > /tmp/nblog.log 2>&1 &"
```

### 后台管理部署
```bash
cd blog-cms && npm run build
cd dist && tar czf /tmp/admin-dist.tar.gz .
scp /tmp/admin-dist.tar.gz root@112.74.97.85:/tmp/
ssh root@112.74.97.85 "rm -rf /var/www/html/admin/static /var/www/html/admin/index.html && cd /var/www/html/admin && tar xzf /tmp/admin-dist.tar.gz"
```

## 常见问题

### MySQL 数据库丢失
MySQL 在 OOM 时会丢失数据库。守卫脚本 `/root/mysql-guard.sh` 每2分钟检查，自动从备份恢复。

### 内存优化
- swappiness=10（尽量用 swap）
- MySQL innodb_buffer_pool_size=64M
- Java -Xmx512m
- Mineradio --max-old-space-size=64
- 已禁用 aegis/packagekit/udisks2

### JWT Token 过期
后台登录 JWT 有时效，过期后需重新登录。日志中会出现 `ExpiredJwtException`，属正常行为。

### 文章密码保护
SQL 插入文章时 `password` 字段必须设为空字符串 `''`，不能设为 NULL，否则会被判定为密码保护文章。

## Git 仓库

```bash
# 当前 remote
origin: https://github.com/wlikkl/Wblog.git

# 推送
git push origin master

# Git 历史已重写，所有 commit 作者为 wilk <i@wilk.top>
# GitHub 贡献者只显示 wilk 一人
```
