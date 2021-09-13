# Iwara4A
[![GitHub issues](https://img.shields.io/github/issues/jiangdashao/iwara4a)](https://github.com/jiangdashao/iwara4a/issues)
[![GitHub forks](https://img.shields.io/github/forks/jiangdashao/iwara4a)](https://github.com/jiangdashao/iwara4a/network)
[![GitHub stars](https://img.shields.io/github/stars/jiangdashao/iwara4a)](https://github.com/jiangdashao/iwara4a/stargazers)
[![GitHub license](https://img.shields.io/github/license/jiangdashao/iwara4a)](https://github.com/jiangdashao/iwara4a)
![GitHub all releases](https://img.shields.io/github/downloads/jiangdashao/iwara4a/total)

**完全基于Jetpack Compose开发的 iwara.tv 安卓app**

* 本项目开发目的是为了实践安卓前沿开发技术
* 本项目仅供个人使用及学习交流，请勿用于商业用途
* Discord群: https://discord.gg/ceqzvbF2u9
* 无偿赞助: https://afdian.net/@re_ovo

## ⬇ 下载
https://github.com/jiangdashao/iwara4a/releases/latest

## 截图
(截图可能已经过时，仅供参考!)
| 主页 | 播放页 | 侧边栏 |
| ----- | ------| ------|
| <img src="art/index.png" align="left" height="400">| <img src="art/play.png" align="left" height="400">| <img src="art/drawer.png" align="left" height="400"> |


## 🚩 已经实现的功能
* 暴力自动重连，解决iwara土豆服务器总是无响应问题
* 登录/查看个人信息
* 浏览订阅更新列表
* 播放视频
* 查看图片
* 查看评论
* 点赞
* 关注
* 评论
* 分享  
* 搜索
* 榜单

## 🧭 常见问题
* **为什么不能查看自己关注了哪些人？**   
  答: 因为Iwara网站端禁用了这个功能，据说是因为这个功能会导致数据库负载增大导致网站宕机，如果以后iwara重新开放这个功能，我会加上的

* **APP支持哪些安卓版本？**   
  答: 目前支持Android 7.0 以上的所有版本
  
* **在APP上登录安全吗? 会不会泄露我的密码？**   
  答: 本项目完全开源，欢迎检查代码，插件只会和iwara通信
  
* **下载的视频为什么没了?**    
  答: 目前下载功能非常简单，用的非常原始的代码实现的文件下载，不支持断线重连，也不支持多线程下载，所以一旦出现网络波动，或者app进程被杀死，就会下载失败，下载失败不会通知你。不过欢迎提交PR改进。

## 🎨 主要技术栈
* MVVM 架构
* 单Activity + 导航
* Jetpack Compose (构建UI)
* Kotlin Coroutine (协程)
* Okhttp + Jsoup (解析网页)
* Retrofit (访问Restful API)
* Hilt (依赖注入)
* Paging3 (分页加载)
* Navigation (导航)

## 📜 更新计划
* 英/日/德 语言支持
* 支持切换里站外站
* 完全移除LiveData，迁移到flow
* 重构UI代码，增强可读性和减少重复绘制

## 🔒 不考虑支持的功能
* 论坛

