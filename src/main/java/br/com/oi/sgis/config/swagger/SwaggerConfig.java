package br.com.oi.sgis.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig{

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("br.com.oi.sgis.controller"))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .globalResponseMessage(RequestMethod.POST, responseMessageForGet());
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("API SGIS")
                .description("API para verificação de rotas do SGIS")
                .version("0.0.1")
                .contact(new Contact("Waltenes Placido Sardinha Junior", "","waltenes.junior@squadra.com.br"))
                .build();
    }

    private List<ResponseMessage> responseMessageForGet(){
        List<ResponseMessage> errorsList = new ArrayList<>();
        errorsList.add(new ResponseMessageBuilder()
                .code(500)
                .message("Something unexpected happened on the server")
                .build());
        errorsList.add(new ResponseMessageBuilder()
                .code(403)
                .message("Access denied")
                .build());    
        
        return errorsList;
    }
    
}