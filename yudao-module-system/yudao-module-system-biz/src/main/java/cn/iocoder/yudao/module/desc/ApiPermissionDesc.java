package cn.iocoder.yudao.module.desc;

import cn.iocoder.yudao.framework.security.core.service.SecurityFrameworkServiceImpl;
import cn.iocoder.yudao.module.system.api.permission.PermissionApiImpl;
import org.springframework.security.access.prepost.PreAuthorize;

public class ApiPermissionDesc {

    /**
     * 权限校验
     * 一个用户是否具有访问一个接口的权限，需要两点
     * 1、该接口具有什么权限才能访问
     * 2、该用户是否具有该权限
     * 针对条件1，使用 {@link PreAuthorize} 在接口上表明访问该资源需要什么权限，随后使用 PreAuthorize 提供的能力，
     * 调用@ss.hasPermission('具体的权限名称')来判断条件2，即判断当前登录用户是否具有该权限。
     * 判断条件为：主要调用 {@link SecurityFrameworkServiceImpl#hasPermission(String)} 方法，从本地缓存中判断当前用户是否具有该权限
     *    本地缓存的key为 KeyValue<用户id, List<权限名>>，value为true or false，过期时间设置为1分钟。
     * 如果本地缓存没有，或者过期，调用 rpc 请求去判断该用户是否具有某种权限。
     * rpcImpl实现为：{@link PermissionApiImpl#hasAnyPermissions(Long, String...)}
     *    1. 先从本地缓存中根据userId找到对应的roleId，然后再从本地缓存中根据roleId读取role实体，判断是否打开，打开则表示有效
     *    2. 这些roleId是否包含admin，包含则表明肯定具有权限（这里是将超管权限单独判断）
     *    3. 根据权限从menu表中读所有该权限能够访问的菜单（本地缓存操作），然后根据这些菜单找到所有的roleId（本地缓存操作），
     *       判断该用户所拥有的roleId是否在菜单对应的roleId中，如果有则表明该用户具有该权限
     *    注：个人认为这样判断有点拧巴，主要原因在于：设计的RBAC模型是用户-角色-菜单，而非用户-角色-权限，所以无法知道一个角色具有什么权限，
     *        只能以菜单作为中转。个人认为这是设计的不足，应该是用户-角色-权限，权限-菜单，这样判断更清晰。
     */
}
