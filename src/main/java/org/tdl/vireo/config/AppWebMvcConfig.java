package org.tdl.vireo.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.tdl.vireo.Application;

import edu.tamu.framework.config.CoreWebMvcConfig;

@Configuration
@EnableWebMvc
@DependsOn("systemDataLoader")
public class AppWebMvcConfig extends CoreWebMvcConfig {

    @Value("${app.ui.path}")
    private String path;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**").addResourceLocations("WEB-INF" + path + "/");

        // TODO: investigate and implement dynamic resource locations at runtime via symlinks
        // paths: "/data/documents/**", "/conf/theme/**"
        // locations: BASE_PATH + symlink
        registry.addResourceHandler("/public/**").addResourceLocations("file:" + Application.BASE_PATH + "public/");

        registry.setOrder(Integer.MAX_VALUE - 2);
    }

    @Bean
    public TomcatEmbeddedServletContainerFactory containerFactory() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1);
            }
        });
        return factory;
    }

}
