package it.tredi.ecm.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import it.tredi.ecm.service.bean.SessionConversationAttributeStore;

@Configuration
public class SessionAttributeBeanConfiguration {

	@Bean
    public RequestMappingHandlerAdapterPostProcessor requestMappingHandlerAdapterPostProcessor() {
        return new RequestMappingHandlerAdapterPostProcessor();
    }

	public static class RequestMappingHandlerAdapterPostProcessor implements BeanPostProcessor {

		@Value("${num.conversation.toKeep}")
		private int numConversationsToKeep = 10;

		@Bean
		public SessionAttributeStore sessionAttributeStore() {
			SessionConversationAttributeStore sessionConversationAttributeStore = new SessionConversationAttributeStore();
			sessionConversationAttributeStore.setNumConversationsToKeep(numConversationsToKeep);
			return sessionConversationAttributeStore;
		}

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof RequestMappingHandlerAdapter) {
                ((RequestMappingHandlerAdapter) bean).setSessionAttributeStore(sessionAttributeStore());
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }
    }
}
