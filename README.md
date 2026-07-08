<p align="center">
	<img src="./pic/NBlog.png" alt="wilk's Blog" style="width: 200px; height: 200px">
</p>
<p align="center">
	<img src="https://img.shields.io/badge/JDK-11+-orange">
	<img src="https://img.shields.io/badge/SpringBoot-2.2.7.RELEASE-brightgreen">
	<img src="https://img.shields.io/badge/MyBatis-3.5.5-red">
	<img src="https://img.shields.io/badge/Vue-2.6.11-brightgreen">
	<img src="https://img.shields.io/badge/license-MIT-blue">
</p>



## 简介

基于 [NBlog](https://github.com/Naccl/NBlog) 深度定制的个人博客系统，采用紫罗兰暗黑玻璃主题。

技术栈：Spring Boot 2.2.7 + Vue 2.x + MySQL 8.0 + Redis + Nginx

预览地址：

前台：[http://112.74.97.85](http://112.74.97.85)

后台：[http://112.74.97.85/admin/](http://112.74.97.85/admin/)



## 功能特性

### 前台 (blog-view)

- 紫罗兰暗黑玻璃主题
- 星空粒子背景动画
- 鼠标跟随光晕
- 3D 卡片倾斜效果
- 打字机效果标题
- SVG 底部波浪
- 滚动渐入动画
- 页面切换过渡
- 阅读进度条
- 导航栏滚动变色
- AI 聊天助手（浮动按钮）
- 音乐播放器（Mineradio）

### 后台管理 (blog-cms)

- 服务器监控面板（CPU/内存/磁盘/网络）
- AI 文章生成配置
- AI 生成配图
- 文章管理（富文本编辑器）
- 分类/标签管理
- 评论管理
- 友情链接管理
- 站点设置



## 技术栈

### 后端

1. 核心框架：[Spring Boot](https://github.com/spring-projects/spring-boot) 2.2.7
2. 安全框架：[Spring Security](https://github.com/spring-projects/spring-security)
3. Token：[jjwt](https://github.com/jwtk/jjwt)
4. ORM 框架：[MyBatis](https://github.com/mybatis/spring-boot-starter)
5. 分页插件：[PageHelper](https://github.com/pagehelper/Mybatis-PageHelper)
6. NoSQL 缓存：[Redis](https://github.com/redis/redis)
7. Markdown 转 HTML：[commonmark-java](https://github.com/commonmark/commonmark-java)
8. 离线 IP 地址库：[ip2region](https://github.com/lionsoul2014/ip2region)
9. 定时任务：[quartz](https://github.com/quartz-scheduler/quartz)
10. UserAgent 解析：[yauaa](https://github.com/nielsbasjes/yauaa)
11. AI 接口：Agnes API (agnes-2.0-flash / agnes-image-2.1-flash)

### 前端

- 核心框架：Vue 2.x + Vue Router + Vuex
- UI 框架：Semantic UI + Element UI
- 构建工具：@vue/cli 4.x
- 文章排版：基于 [typo.css](https://github.com/sofish/typo.css) 修改



## AI 功能配置

### Agnes API

```properties
# application.properties
chat.api.url=https://apihub.agnes-ai.com/v1/chat/completions
chat.api.model=agnes-2.0-flash
chat.image.url=https://apihub.agnes-ai.com/v1/images/generations
chat.image.model=agnes-image-2.1-flash
chat.api.key=sk-xxx
```

### 功能入口

- **AI 聊天**：前台右下角浮动按钮
- **AI 生成文章**：后台 → 写文章 → "AI写文章" 按钮
- **AI 生成配图**：后台 → 写文章 → "AI配图" 按钮
- **AI 文章自动定时生成**：后台 → 系统管理 → 文章生成配置



## 开发环境

### 后端

1. 确保安装 JDK 11+ 和 Maven
2. 启动 MySQL 8.0 和 Redis
3. 创建数据库 `nblog`，执行 `/blog-api/nblog.sql` 初始化
4. 修改配置 `/blog-api/src/main/resources/application-dev.properties`
5. 在 `blog-api` 目录执行 `mvn clean package -DskipTests`
6. 启动：`java -jar target/blog-api-0.0.1.jar`

### 前端

1. 在 `blog-view` 目录执行 `npm install` 安装依赖
2. 执行 `npm run serve` 启动前台开发服务器（端口 8080）
3. 在 `blog-cms` 目录执行 `npm install` 安装依赖
4. 执行 `npm run serve` 启动后台开发服务器（端口 8080）

### 本地开发配置

`blog-view/src/plugins/axios.js`：
```javascript
baseURL: 'http://localhost:8090/'
```

`blog-cms/src/util/request.js`：
```javascript
baseURL: 'http://localhost:8090/'
```



## 部署

### 服务器信息

- 服务器：112.74.97.85（阿里云 Ubuntu）
- SSH：root
- 后端端口：8090
- Nginx 端口：80

### 前端部署

```bash
# 修改 baseURL 为生产环境
sed -i '' "s|baseURL: 'http://localhost:8090/'|baseURL: '/api/'|" blog-view/src/plugins/axios.js

# 构建
cd blog-view && npm run build

# 上传到服务器
cd dist && tar czf /tmp/frontend-dist.tar.gz .
scp /tmp/frontend-dist.tar.gz root@112.74.97.85:/tmp/

# 服务器解压
ssh root@112.74.97.85 "rm -rf /var/www/html/static /var/www/html/index.html && cd /var/www/html && tar xzf /tmp/frontend-dist.tar.gz"
```

### 后端部署

```bash
# 上传源码
scp -r blog-api/src/main/java/.../NewFile.java root@112.74.97.85:/root/nblog/blog-api/src/.../

# 服务器构建
ssh root@112.74.97.85 "cd /root/nblog/blog-api && mvn clean package -DskipTests -q"

# 重启服务
ssh root@112.74.97.85 "kill \$(pgrep -f blog-api) && cd /root/nblog/blog-api && nohup java -Xmx512m -jar target/blog-api-0.0.1.jar --spring.profiles.active=prod > /tmp/nblog.log 2>&1 &"
```

### 后台管理部署

```bash
cd blog-cms && npm run build
cd dist && tar czf /tmp/admin-dist.tar.gz .
scp /tmp/admin-dist.tar.gz root@112.74.97.85:/tmp/
ssh root@112.74.97.85 "rm -rf /var/www/html/admin/static /var/www/html/admin/index.html && cd /var/www/html/admin && tar xzf /tmp/admin-dist.tar.gz"
```



## Nginx 配置

```nginx
# Admin API
location /admin/login { proxy_pass http://localhost:8090; }
location /admin/ai/ { proxy_pass http://localhost:8090; proxy_read_timeout 120s; }
location /admin/monitor { proxy_pass http://localhost:8090; }
location /admin/article-generator { proxy_pass http://localhost:8090; }
location /admin/ { proxy_pass http://localhost:8090; }

# Admin SPA
location = /admin { root /var/www/html; try_files /admin/index.html =404; }

# 博客 API
location /api/ { proxy_pass http://localhost:8090/; proxy_read_timeout 120s; }

# 音乐播放器
location /mineradio/ { proxy_pass http://localhost:3003/; }

# 前台 SPA
location / { try_files $uri $uri/ /index.html; }
```



## 定时任务

```bash
# MySQL 保活（每2分钟）
*/2 * * * * /root/mysql-guard.sh

# 数据库备份（每6小时，保留7天）
0 */6 * * * /root/nblog-backup.sh

# 自动生成文章（每天8点）
0 8 * * * /usr/bin/python3 /root/daily-article-generator.py >> /tmp/daily-article.log 2>&1
```



## 常见问题

### MySQL 数据库丢失

MySQL 在 OOM 时会丢失数据库。守卫脚本 `/root/mysql-guard.sh` 每2分钟检查，自动从备份恢复。

### 内存优化

- swappiness=10（尽量用 swap）
- MySQL innodb_buffer_pool_size=64M
- Java -Xmx512m
- Mineradio --max-old-space-size=64

### JWT Token 过期

后台登录 JWT 有时效，过期后需重新登录。日志中出现 `ExpiredJwtException` 属正常行为。



## LICENSE

[MIT](https://github.com/Naccl/NBlog/blob/master/LICENSE)

## Credits

- 基于 [NBlog](https://github.com/Naccl/NBlog) by [Naccl](https://github.com/Naccl)
- 仓库地址：https://github.com/wlikkl/Wblog.git
