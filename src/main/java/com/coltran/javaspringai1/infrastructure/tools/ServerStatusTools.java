package com.coltran.javaspringai1.infrastructure.tools;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Configuration
public class ServerStatusTools {

    private static final Logger log = LoggerFactory.getLogger(ServerStatusTools.class);
    public record StatusRequest (String serviceName) {}
    public record StatusResponse(String status, int uptimeSeconds) {}

    @Bean
    @Description("Get health status and uptime in seconds of a specific microservice.")
    public Function<StatusRequest, StatusResponse> checkServiceHealth() {
        return request -> {
            log.info("AI Request status for service: {}", request.serviceName);

            if("payment-service".equalsIgnoreCase(request.serviceName)){
                return new StatusResponse("HEALTHY", 12000);
            } else if ("inventory-service".equalsIgnoreCase(request.serviceName)){
                return new StatusResponse("CRITICAL-FAILURE", 0);
            }

            return new StatusResponse("UNKOWN_SERVICE", 0);
        };
    }
    
}
