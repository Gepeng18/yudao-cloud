package cn.iocoder.yudao.module.desc;

import cn.iocoder.yudao.framework.datapermission.config.YudaoDataPermissionAutoConfiguration;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.datapermission.core.aop.DataPermissionAnnotationAdvisor;
import cn.iocoder.yudao.framework.datapermission.core.aop.DataPermissionAnnotationInterceptor;
import cn.iocoder.yudao.framework.datapermission.core.aop.DataPermissionContextHolder;
import cn.iocoder.yudao.framework.datapermission.core.db.DataPermissionDatabaseInterceptor;
import cn.iocoder.yudao.framework.datapermission.core.rule.DataPermissionRuleFactoryImpl;
import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRule;
import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import cn.iocoder.yudao.module.system.api.permission.PermissionApiImpl;
import cn.iocoder.yudao.module.system.framework.datapermission.config.DataPermissionConfiguration;

public class DataPermissionDesc {
    /**
     * 【第一部分】
     * 1、定义一个注解 {@link DataPermission}，该注解指定是否开启数据权限，加了注解的方法需要使用哪些规则，或者不使用哪些规则
     * 2、定义一个拦截器{@link DataPermissionAnnotationInterceptor},负责读取类或者方法上的注解，判断哪些类的哪些方法上有DataPermission注解，
     *    然后该注解的属性是什么，保存到 {@link DataPermissionContextHolder} 中（但是这里注意：这里实际是一个ThreadLocal，
     *    ThreadLocal中存的是LinkedList<DataPermission>，使用 List 的原因，存在方法的嵌套调用）
     * 3、定义了一个Advisor {@link DataPermissionAnnotationAdvisor} 将拦截器加到配置中
     * 所以重点就是这个拦截器的逻辑，拦截器其实就是去读取方法中的 DataPermission 注解，然后将注解的属性写到ThreadLocal中，然后执行方法，执行完成后，再把ThreadLocal中的数据清除。
     *
     * 【第二部分】
     * 1、定义一批Rule规则（当前系统中只有一种规则 {@link DeptDataPermissionRule}），该规则的主要作用为：传入一个tableName，返回一个expression，
     *    这个expression即表示对sql做什么改动。
     *    该方法中会调用 {@link PermissionApiImpl} 获取登录用户的数据权限，如 是否可查看全部数据、是否可查看自己的数据、可查看的部门编号数组。
     *    然后根据获得的数据权限，去生成不同的 Expression，如 column == ? or column in (....)，都是使用 Expression 进行表示
     * 2、定义了一个配置类{@link DataPermissionConfiguration}，在这个配置类中调用 {@link DeptDataPermissionRule#addDeptColumn(Class)}，
     *    表示要对什么表的什么字段做处理，这里将在第三部分被使用到
     *
     * 【第三部分】
     * 1、当规则：DeptDataPermissionRule准备好后，下面我们要开始将规则和DataPermission进行整合了。首先定义一个 {@link DataPermissionRuleFactoryImpl}，该类中包含所有规则。
     *    该类从 {@link DataPermissionContextHolder} 中拿到 DataPermission 配置，然后根据是否开启，包含哪些rule，排除哪些rule，最终返回调用特定的DAO方法能使用的rules。
     * 2、扩展Mybatis-plus的拦截器：{@link DataPermissionDatabaseInterceptor}，该类难度高，最核心。该类的目的就是调用 ruleFactoryImpl 获取针对某个DAO方法有效的rules，
     *    放到ThreadLocal中，然后在调用这些rules修改mybatis的BoundSql。比如针对select进行什么修改，针对insert进行什么修改等，
     *    【在mybatis的拦截器中先获取执行的DAO方法的所有table，再针对每个table调用所有rule的expression，最后将这些expression进行整合】
     * 3、DataPermissionRuleFactoryImpl 被配置类 {@link YudaoDataPermissionAutoConfiguration} 配置到IOC容器中。
     *    同时，DataPermissionDatabaseInterceptor 也被配置到mybatis的拦截器链中。
     */
}
