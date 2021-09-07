# Iwara4A
[![GitHub issues](https://img.shields.io/github/issues/jiangdashao/iwara4a)](https://github.com/jiangdashao/iwara4a/issues)
[![GitHub forks](https://img.shields.io/github/forks/jiangdashao/iwara4a)](https://github.com/jiangdashao/iwara4a/network)
[![GitHub stars](https://img.shields.io/github/stars/jiangdashao/iwara4a)](https://github.com/jiangdashao/iwara4a/stargazers)
[![GitHub license](https://img.shields.io/github/license/jiangdashao/iwara4a)](https://github.com/jiangdashao/iwara4a)

基于Jetpack Compose开发的 iwara 安卓app, 采用Material Design, 支持夜间模式, 支持绝大多数iwara网站上的功能。如果
你觉得不错，给我点个star吧！ 并且如果你懂Jetpack Compose/Kotlin，欢迎提交PR！

注意: 使用APP需要自行注册一个iwara账号, 且APP只是替代浏览器去解析网页，所以不提供任何科学上网的功能！

Discord: https://discord.gg/ceqzvbF2u9

## 💎 爱发电
https://afdian.net/@re_ovo

## ⬇ 下载
https://github.com/jiangdashao/iwara4a/releases/latest

## 截图
(截图可能已经过时，仅供参考)
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
* 搜索

## 🎨 主要技术栈
* MVVM 架构
* Jetpack Compose (构建UI)
* Kotlin Coroutine (协程)
* Okhttp + Jsoup (解析网页)
* Retrofit (访问Restful API)
* Hilt (依赖注入)
* Paging3 (分页加载)
* Navigation (导航)

## 📜 计划中的功能
* 英/日/德 语言支持
* 支持切换里站外站

## 🗒 TODO
* 优化性能，解决popBack到首页时的卡顿和闪屏问题 (暂未定位到原因...)
* 完全移除LiveData，迁移到flow

## 🔒 不考虑支持的功能
* 论坛


