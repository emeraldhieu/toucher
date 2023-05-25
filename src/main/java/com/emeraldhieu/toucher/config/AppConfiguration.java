package com.emeraldhieu.toucher.config;

import com.emeraldhieu.toucher.touch.Route;
import com.emeraldhieu.toucher.touch.RouteProvider;
import com.emeraldhieu.toucher.touch.TouchProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Bean
    public RouteProvider routeProvider() {
        RouteProvider routeProvider = new RouteProvider();
        routeProvider.addRoute(new Route("StopA", "StopB", 4.5));
        routeProvider.addRoute(new Route("StopB", "StopC", 6.25));
        routeProvider.addRoute(new Route("StopA", "StopC", 8.45));
        return routeProvider;
    }

    @Bean
    public TouchProcessor touchProcessor(RouteProvider routeProvider) {
        return new TouchProcessor(routeProvider);
    }
}
