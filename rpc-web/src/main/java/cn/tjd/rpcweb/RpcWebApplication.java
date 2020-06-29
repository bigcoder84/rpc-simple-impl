package cn.tjd.rpcweb;

import cn.tjd.rpcweb.listener.InitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RpcWebApplication {
	@Value("${zookeeper.cluster}")
	private String zkCluster;

	public static void main(String[] args) {
		SpringApplication.run(RpcWebApplication.class, args);
	}

	@Bean
	public ServletListenerRegistrationBean servletListenerRegistrationBean(){
		ServletListenerRegistrationBean servletListenerRegistrationBean =new ServletListenerRegistrationBean();
		servletListenerRegistrationBean.setListener(new InitListener(zkCluster));
		return servletListenerRegistrationBean;
	}

}
