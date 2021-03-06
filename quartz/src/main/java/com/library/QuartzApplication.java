package com.library;

//import com.library.sboot.authorsettings.AuthorSettings;
//import com.library.sboot.datarest.MyRepositoryRestMvcConfiguration;
//import com.library.sboot.jpa.support.CustomRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liuff on 2017/4/4.
 */
@RestController
@SpringBootApplication //Spring Boot项目的核心注解，主要目的是开启自动配置
@ComponentScan("com.library")
@EnableAutoConfiguration
@EnableTransactionManagement //开启声明式事务支持，自动扫描注解@Transactional
//@EnableJpaRepositories(repositoryBaseClass = CustomRepositoryImpl.class,basePackages = {"com.library"})
//@Import({MyRepositoryRestMvcConfiguration.class})
public class QuartzApplication {
    @Value("${book.author}")
    private String bookAuthor;
    @Value("${book.name}")
    private String bookName;
    @Autowired
//    private AuthorSettings authorSettings;

//    @RequestMapping("/")
//    String index() {
        //return "Hello Spring Boot !";
        //return "book name is:"+bookName+" and book author is:"+bookAuthor;
//        return "author name is "+authorSettings.getName()+" and author age is "+authorSettings.getAge();
//    }

    public static void main(String[] args) {
//        SpringApplication.run(LibraryApplication.class, args);
        SpringApplication application = new SpringApplication(QuartzApplication.class);
//        application.setBannerMode(Banner.Mode.OFF); // 关闭 banner
        application.run(args);
    }
}
