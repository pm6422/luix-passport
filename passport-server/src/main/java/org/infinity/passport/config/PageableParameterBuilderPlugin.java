package org.infinity.passport.config;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.schema.ResolvedTypes.modelRefFactory;
import static springfox.documentation.spi.schema.contexts.ModelContext.inputParam;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
@ConditionalOnProperty(prefix = "application.swagger", value = "enabled", havingValue = "true")
public class PageableParameterBuilderPlugin implements ParameterBuilderPlugin {
    private final TypeNameExtractor nameExtractor;

    private final TypeResolver resolver;

    public PageableParameterBuilderPlugin(TypeNameExtractor nameExtractor, TypeResolver resolver) {
        this.nameExtractor = nameExtractor;
        this.resolver = resolver;
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    private Function<ResolvedType, ? extends ModelReference> createModelRefFactory(ParameterContext context) {
        ModelContext modelContext = inputParam(context.methodParameter().getParameterType(),
                context.getDocumentationType(), context.getAlternateTypeProvider(), context.getGenericNamingStrategy(),
                context.getIgnorableParameterTypes());
        return modelRefFactory(modelContext, nameExtractor);
    }

    @Override
    public void apply(ParameterContext context) {
        MethodParameter parameter = context.methodParameter();
        Class<?> type = parameter.getParameterType();
        if (Pageable.class.isAssignableFrom(type)) {
            Function<ResolvedType, ? extends ModelReference> factory = createModelRefFactory(context);

            ModelReference intModel = factory.apply(resolver.resolve(Integer.TYPE));
            ModelReference stringModel = factory.apply(resolver.resolve(List.class, String.class));

            List<Parameter> parameters = newArrayList(
                    context.parameterBuilder().parameterType("query").name("page").modelRef(intModel)
                            .description("请求的页码(从0开始)").build(),
                    context.parameterBuilder().parameterType("query").name("size").modelRef(intModel)
                            .description("每页大小").build(),
                    context.parameterBuilder().parameterType("query").name("sort").modelRef(stringModel)
                            .allowMultiple(true).description("排序方式(默认为升序): property,asc 或 property,desc")
                            .build());

            context.getOperationContext().operationBuilder().parameters(parameters);
        }
    }
}
