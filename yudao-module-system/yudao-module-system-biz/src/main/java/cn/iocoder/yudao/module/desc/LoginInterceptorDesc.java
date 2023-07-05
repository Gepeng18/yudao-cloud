package cn.iocoder.yudao.module.desc;

import cn.iocoder.yudao.framework.security.core.annotations.PreAuthenticated;
import cn.iocoder.yudao.framework.security.core.aop.PreAuthenticatedAspect;
import cn.iocoder.yudao.framework.security.core.filter.TokenAuthenticationFilter;
import cn.iocoder.yudao.module.system.api.oauth2.OAuth2TokenApi;

public class LoginInterceptorDesc {
    /**
     * 实现：加了 {@link PreAuthenticated} 注解的方法，必须先登录
     * 1. {@link TokenAuthenticationFilter} 先从header请求头中获取到对应的token（uuid），然后调用{@link OAuth2TokenApi#checkAccessToken(String)}
     *    根据token获取到用户信息，构建成 LoginUser model 后，塞到 SecurityContextHolder.getContext() 中去。
     * 2. {@link PreAuthenticatedAspect} 检测所有带了 {@link PreAuthenticated} 注解的方法，执行相应逻辑：
     *    如果从SecurityContextHolder.getContext()取不到user，则抛出异常。
     */
}
