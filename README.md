# Overview

![image](https://github.com/Kevin-Zh-CS/movie-search-engine/blob/master/img/image2.png)



# MVC

使用了MVC（Model-View-Controller）的设计模式，底层用的SpringBoot框架，将业务逻辑、数据、界面显示分离的方法组织代码，将业务逻辑聚集到一个部件里面，在改进和个性化定制界面及用户交互的同时，不需要重新编写业务逻辑。

- **Model** - 项目中的pojo只有一个Movie类，在业务层接收搜索关键字通过文件索引查询数据并且完成数据的封装任务，是项目的核心逻辑处理的地方。
- **View** - 通过前端网页实现数据的可视化，向其传递List<Moive\>。
- **Controller** - 控制器作用于模型和视图上，用于接收前端发送的数据请求，及时调用业务层代码。

![image](https://github.com/Kevin-Zh-CS/movie-search-engine/blob/master/img/image1.png)

