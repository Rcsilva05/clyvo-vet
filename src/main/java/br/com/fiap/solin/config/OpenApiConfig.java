package br.com.fiap.solin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI solinOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SOLIN API")
                        .description("API REST para monitoramento da saude de pets. " +
                                "Permite o registro diario de eventos (urina, alimentacao, hidratacao) " +
                                "e gera alertas automaticos baseados em regras de frequencia.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe SOLIN - FIAP")
                                .email("solin@fiap.com.br"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
