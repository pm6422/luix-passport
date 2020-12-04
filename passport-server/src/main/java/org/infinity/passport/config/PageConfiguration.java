package org.infinity.passport.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Nonnull;
import java.util.List;

@Configuration
public class PageConfiguration implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        PageableHandlerMethodArgumentResolver pageResolver = new PageableHandlerMethodArgumentResolver();
        // Set default page info if it is null
        // The max page size is 2000. Refer to DEFAULT_MAX_PAGE_SIZE of PageableHandlerMethodArgumentResolverSupport
        pageResolver.setFallbackPageable(PageRequest.of(0, 2000));
        resolvers.add(pageResolver);
    }


    private static class AdditionalPageableHandlerMethodArgumentResolver extends PageableHandlerMethodArgumentResolver {
        private final SortArgumentResolver sortResolver;

        public AdditionalPageableHandlerMethodArgumentResolver(SortArgumentResolver sortResolver) {
            super(sortResolver);
            this.sortResolver = sortResolver;
        }

        @Override
        @Nonnull
        public Pageable resolveArgument(@Nonnull MethodParameter methodParameter, ModelAndViewContainer mavContainer,
                                        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
            String page = webRequest.getParameter(getParameterNameToUse(getPageParameterName(), methodParameter));
            String pageSize = webRequest.getParameter(getParameterNameToUse(getSizeParameterName(), methodParameter));

            Sort sort = sortResolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
            Pageable pageable = getPageable(methodParameter, page, pageSize);

            if (sort.isSorted()) {
                return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
            }

            return pageable;
        }
    }

    private static class DefaultSortHandlerMethodArgumentResolver extends SortHandlerMethodArgumentResolver {
        private static final String DEFAULT_ADDITIONAL_SORT_FIELD = "id";

        @Override
        @Nonnull
        public Sort resolveArgument(@Nonnull MethodParameter parameter, ModelAndViewContainer mavContainer,
                                    @Nonnull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
            Sort sort = super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
            return sort.and(Sort.by(DEFAULT_ADDITIONAL_SORT_FIELD).descending());
        }
    }
}
