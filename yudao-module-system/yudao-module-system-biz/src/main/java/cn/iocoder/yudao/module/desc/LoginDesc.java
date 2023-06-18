package cn.iocoder.yudao.module.desc;

import cn.iocoder.yudao.module.system.controller.admin.auth.AuthController;
import cn.iocoder.yudao.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import cn.iocoder.yudao.module.system.controller.admin.auth.vo.AuthSocialLoginReqVO;

public class LoginDesc {

    /**
     * 首先，只要涉及三方登录，一定会先调用 /admin-api/system/auth/social-auth-redirect
     * {@link AuthController#socialLogin(Integer, String)} 生成跳转地址，供前端跳转
     *
     * 前端调用后，通常会返回给前端一个code、state，并且此时，前端是能拿到用户点击的socialType的，
     * 于是后面流程中，前端拿着 socialType、code、state获取token。
     *
     * 首次三方登录时，调用 /admin-api/system/auth/social-login 接口 {@link AuthController#socialQuickLogin(AuthSocialLoginReqVO)}
     * 前端传入type、code和state，表示是某一次具体的三方登录，这时候后端唯一的逻辑就是拿着code、state调用第三方的接口，
     * 获取用户的信息（openId、accessToken、code、state这些信息），存入数据库中。
     * 然后拿着socialUserId 和 type 去绑定表中找不到，抛出异常。前端提示请绑定。
     * (注意，调用authorize后返回authorizeUrl时，因为需要扫码，所以会传入一个UUID，这个UUID标识本次登录。所以后面必须拿code和这个UUID进行登录，
     * 才能和之前扫的码对应上)
     *
     * 当用户输入用户名和密码后，点击登录，会调用 http://127.0.0.1:48081/admin-api/system/auth/login 接口
     * {@link AuthController#login(AuthLoginReqVO)}，这时候会传type、code和state
     * 登录逻辑中，会先根据用户名和密码做校验，然后获得用户信息
     * 登录逻辑中会判断，如果 socialType 非空，说明需要绑定社交用户
     * 1. 先根据type、code、state 从socialUser表中查出刚刚那个没有绑定的社交用户（这就是code和state的作用，为了和上次的社交用户关联上）
     * 2. 绑定表中如果存在socialUserId + userType 的记录，即社交用户绑定过其他用户，则解绑(删除绑定关系)
     * 3. 绑定表中如果存在userId、userType、socialType 的记录，userId + userType 可以唯一确定一个用户，所以存在这条记录意味着
     *    某一系统用户绑定过该社交类型的账号，所以需要解绑
     * 4. 将用户和社交账号绑定起来（userId + userType 可以唯一确定一个用户，socialUserId 可以唯一确定一个社交用户，socialType 为冗余字段）
     *    所以这里就是将绑定关系插入绑定表中（userId + userType，socialUserId，socialType 这四个字段）
     *
     * 后续快捷登录时，由三方的平台回调 /admin-api/system/auth/social-login 接口
     * {@link AuthController#socialQuickLogin(AuthSocialLoginReqVO)},这时候前端依旧会传来socialType、code、status，
     * 然后调用三方接口获取三方用户信息，根据socialType和三方用户uuid从socialUser表中获取socialUser，将信息更新到socialUser表中。
     * 根据 socialUserId 和 userType 从绑定表中查询到该用户的userId（socialUserId 代表一个社交账号，他有且可以绑定一个类型，如admin or APP）
     * 然后根据userId 从user表中读取用户信息，生成访问令牌返回给端上。
     *
     */
}
