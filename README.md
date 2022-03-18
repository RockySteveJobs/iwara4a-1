# Iwara4A
[![GitHub issues](https://img.shields.io/github/issues/jiangdashao/iwara4a)](https://github.com/jiangdashao/iwara4a/issues)
[![GitHub forks](https://img.shields.io/github/forks/jiangdashao/iwara4a)](https://github.com/jiangdashao/iwara4a/network)
[![GitHub stars](https://img.shields.io/github/stars/jiangdashao/iwara4a)](https://github.com/jiangdashao/iwara4a/stargazers)
[![GitHub license](https://img.shields.io/github/license/jiangdashao/iwara4a)](https://github.com/jiangdashao/iwara4a)
![GitHub all releases](https://img.shields.io/github/downloads/jiangdashao/iwara4a/total)

[English Ver](/art/doc/README_EN.md)

完全基于Jetpack Compose开发的 iwara 安卓app, 采用Material You设计, 支持安卓6.0以上版本, 无多余权限请求

## ⬇ 下载
https://github.com/jiangdashao/iwara4a/releases/latest

## 截图
(截图可能已经过时，仅供参考!)
| 主页 | 播放页 | 侧边栏 | 搜索 |
| ----- | ------| ------| ------|
| <img src="art/index.png" align="left" height="400">| <img src="art/play.png" align="left" height="400">| <img src="art/drawer.png" align="left" height="400"> | <img src="art/search.png" align="left" height="400">

## ⏳ APP原理
使用 JSoup 解析I站网页数据，提取数据并渲染为原生界面，I站**任何内容与我无关**，app仅仅承担浏览器的任务

## 🔭 代码说明
* APP采用 单Activity + Navigation 的方式构建
* 路由Activity: /ui/activity/RouterActivity.kt
* 各个屏幕/导航点: /ui/screen
* 可复用的Compose组件: /ui/component
* 下载服务: /service
* 依赖注入定义: /di
* 仓库层: /repo
* 数据库: /dao
* 网络api: /api

## 🚩 特性
* Material You设计
* 暴力自动重连
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
* 下载

## 🧭 常见问题
* **为什么不能查看自己关注了哪些人？**   
  答: 因为Iwara网站端禁用了这个功能，据说是因为这个功能会导致数据库负载增大导致网站宕机，如果以后iwara重新开放这个功能，我会加上的

* **APP支持哪些安卓版本？**   
  答: 目前支持Android 6.0 以上的所有版本
  
* **在APP上登录安全吗? 会不会泄露我的密码？**   
  答: 本项目完全开源，欢迎检查代码，插件只会和iwara通信

* **有iOS版吗?**
  答: 没有, iOS应用管理严格，做了也上架不了 

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