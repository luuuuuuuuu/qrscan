package com.gentlelu;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**(集成jsp)创建ServletInitalizer集成SpringbootServletInitalizer，绑定自己添加了@SpringbootApplication类
 * Created by lu on 2018/7/5.
 */
public class ServletInitalizer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
        return builder.sources(QrscanApplication.class);
    }

}
