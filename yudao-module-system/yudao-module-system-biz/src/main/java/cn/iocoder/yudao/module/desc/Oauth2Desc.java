package cn.iocoder.yudao.module.desc;

import cn.iocoder.yudao.framework.security.core.filter.TokenAuthenticationFilter;
import cn.iocoder.yudao.module.system.controller.admin.oauth2.OAuth2ClientController;
import cn.iocoder.yudao.module.system.controller.admin.oauth2.OAuth2OpenController;
import cn.iocoder.yudao.module.system.service.oauth2.OAuth2TokenServiceImpl;

import javax.servlet.http.HttpServletRequest;

public class Oauth2Desc {
    /**
     * 第一个类：{@link OAuth2OpenController}
     * 该类实现了oauth2的功能，实现了 /authorize、/token 等接口。
     * get - /authorize {@link OAuth2OpenController#authorize(String)}
     *       根据传入的clientId 读取 client，然后从 approve 表中根据userId、clientId 查找之前授权过的 scope，
     *       然后将client和之前授权过的scope都返回出去。会将client对应的scope都返回，而之前授权过则返回true，未授权则返回false
     * post - /authorize {@link OAuth2OpenController#approveOrDeny(String, String, String, String, Boolean, String)}
     *       1. 根据传入的clientId查出对应的client，然后查询对应的responseType、scopes、redirectUrl是否在client对应的范围中
     *       2. 授权相关操作，如果自动授权为true，则 先根据clientId计算出对应的scope，如果本次用户需要的scope都包含其中，则都存入approve表，且返回校验通过
     *                                          不满足上述情况，则继续判断：从approve表中搜出所有同意且未过期的scope，如果本次用户需要的scope都包含其中，则校验通过
     *                      如果自动授权为false，则 将用户此次申请的scope全部存入approve表，同意为true，不同意为false
     *       3. 如果是 code 授权码模式，则发放 code 授权码，并重定向
     *          3.1 在code表中存入 userId、code、本次同意的scopes、clientId、过期时间、redirectUri 信息。
     *          3.2 返回url让前端进行重定向：redirectUri + code
     *  post - /token {@link OAuth2OpenController#postAccessToken(HttpServletRequest, String, String, String, String, String, String, String, String)}
     *       1. 校验传入的clientId和clientSecret、grantType、scopes、redirectUri。其实就是根据clientId搜出client，然后校验这些信息
     *       2. 如果是授权模式，则
     *          2.1 校验code（查询是否存在，是否过期），clientId、redirectUri、是否一致
     *          2.2 从code表中删除
     *          2.3 创建访问令牌
     *              2.3.1 根据clientId读取client表
     *              2.3.2 创建刷新令牌：在refresh表中插入uuid-token、usrId、clientId、scopes、expireTime
     *              2.3.3 创建访问令牌：在access表中插入uuid-token、userId、clientId、scopes、refresh-token、expireTime
     *              返回端上，访问令牌、刷新令牌、访问令牌的过期时间、授权范围
     *  login 接口：校验完用户名和密码，然后开始走上面的/token post请求的2.3步
     *  logout接口：删除访问令牌
     *        1. 根据访问令牌查询是否存在
     *        2. 从access表中删除访问令牌
     *        3. 从refresh表中删除访问令牌
     *  refresh接口：接口传入refreshToken，
     *        1. 先从refresh表中校验refreshToken是否存在
     *        2. 从access表中根据refreshToken查出所有的accessToken。
     *        3. 根据查询出的accessToken从access表中删除所有项
     *        4. 已过期的情况下，从refresh表中删除刷新令牌
     *        5. 创建访问令牌：在access表中插入uuid-token、userId、clientId、scopes、refresh-token、expireTime
     *  social-login 接口：
     *        1. 根据乱七八糟的方法，获取到userId
     *        2. 根据userId查询到user信息
     *        3. 创建访问令牌（即走上面的/token post请求的2.3步）
     *
     *  client操作：{@link OAuth2ClientController}
     *        主要干两件事
     *        1. 对 OAuth2Client 表进行增删改查
     *        2. 发送广播消息通知更新客户端（查询接口未做该操作）
     *
     *  在某些方法上有着 hasScope 注解，如何实现的呢？
     *        登录的时候，在spring security的上下文中保存用户的信息，其中就包含 user的scopes信息，所以只需判断接口中的scope是否在
     *        user的scopes中，如果在，则表明有scope权限。
     *  拦截器{@link TokenAuthenticationFilter} 会拦截用户传来的token，然后调用http请求校验token {@link OAuth2TokenServiceImpl#checkAccessToken(String)}，
     *        并根据checkAccess方法的返回值构建LoginUser放在spring security的上下文中。
     *        检查的逻辑就是根据accessToken从access表中查询DO，然后校验是否存在令牌和是否过期。
     */
}
