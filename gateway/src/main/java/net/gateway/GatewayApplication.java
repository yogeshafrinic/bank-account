package net.gateway;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public ZuulFilter zuulFilter(){
		return new ZuulFilter() {
			@Override
			public String filterType() {
				return "post";
			}

			@Override
			public int filterOrder() {
				return 999999;
			}

			@Override
			public boolean shouldFilter() {
				return true;
			}

			@Override
			public Object run() {
				final List<String> routingDebug = (List<String>) RequestContext.getCurrentContext().get("routingDebug");
				routingDebug.forEach(System.out::println);
				return null;
			}
		};
	}

}
