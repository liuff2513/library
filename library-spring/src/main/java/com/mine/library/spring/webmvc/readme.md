# Overview
*Spring MVC 4.x*
## 三层架构&MVC
*   说到Spring MVC，不得不先来谈谈什么是MVC，它和三层架构是什么关系。
可能很多人会说：
    
*   MVC：Model + View + Controller (数据模型+视图+控制器)。
    三层架构：Presentation tier + Application tier + Data tier (展现层+应用层+数据访问层)。
    
那MVC和三层架构有什么关系呢？

*   实际上MVC只存在三层架构的展现层， M实际上是数据模型，是包含数据的对象。
    在Spring MVC里，有一个专门的类叫Model，用来和V之间的数据交互、传值；
    V指的是视图页面，包含JSP、Freemarker、Velocity、Thymeleaf、Title等；
    C就是控制器（Spring MVC的注解@Controller的类）。
    
*   而三层架构师整个应用的架构，是由Spring架构负责管理的。一般项目结构都有Service层、DAO层，
    这两个反馈在应用层和数据访问层。
弄清MVC和三层架构的关系对我们理解Spring MVC和进行Web开发至关重要。
## Spring MVC项目快速搭建

## Spring MVC基本配置
*   Spring MVC的定制配置需要我们的配置类继承一个WebMvcConfigurerAdapter类，并在此类使用@EnableWebMvc注解，来开启对
Spring MVC的配置支持，这样我们就可以重写这个类的方法，完成我们的常用配置。
我们将示例WebMvcConfig配置类继承WebMvcConfigurerAdapter。
### 静态资源映射
*   程序的静态文件（js、css、图片）等需要直接访问，这时我们可以在配置里重写addResourceHandlers方法来实现。
### 拦截器配置
*   拦截器（Interceptor）实现对每一个请求处理前后进行相关的业务处理，类似于Servlet的Filter。
*   可让普通的Bean实现HandlerInterceptor接口或者继承HandlerInterceptorAdapter类来实现自定义拦截器。
*   通过重写WebMvcConfigurerAdapter的addInterceptors方法来注册自定义的拦截器。
### @controllerAdvice