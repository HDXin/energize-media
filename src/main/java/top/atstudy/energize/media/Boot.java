package top.atstudy.energize.media;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import top.atstudy.framework.config.IComponentConfig;
import top.atstudy.framework.core.Constant;
import top.atstudy.framework.core.Handlers;
import top.atstudy.framework.core.Interceptors;
import top.atstudy.framework.core.Plugins;
import top.atstudy.framework.starter.ApplicationStarter;

import java.util.List;

/**
 * @author huangdexin @ harley
 * @email huangdexin@kuaicto.com
 * @date 2019/7/11 9:47
 */
@SpringBootApplication
@EnableConfigurationProperties
public class Boot extends ApplicationStarter {
    /******* Fields Area *******/
    /******* Construction Area *******/
    public Boot() {
    }

    @Override
    public void configConsts(Constant constant) {
    }

    @Override
    public void configHandler(Handlers handlers) {
    }

    @Override
    public void configInterceptor(Interceptors interceptors) {
    }

    @Override
    public void configPlugin(Plugins plugins) {
    }

    @Override
    public void configComponent(List<IComponentConfig> list) {
    }

    @Override
    public void init(BeanFactory applicationContext) {
        super.init(applicationContext);
    }

    public static void main(String[] args) {
        new Boot().runApplication(args);
    }

}
