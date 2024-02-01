package cn.iocoder.yudao.module.desc;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DesensitizeDesc {

    /**
     * 定义了一类注解，指定脱敏的规则，如
     * {@link cn.iocoder.yudao.framework.desensitize.core.regex.annotation.RegexDesensitize},
     *
     * 他们上面标注了一个注解，{@link cn.iocoder.yudao.framework.desensitize.core.base.annotation.DesensitizeBy}
     * 该注解的上面还有一个注解，@JsonSerialize(using = StringDesensitizeSerializer.class)
     *      该注解表示 DesensitizeBy 可以有 DesensitizationHandler 属性，该属性作用是什么呢？由StringDesensitizeSerializer提供
     * 具体逻辑见：{@link cn.iocoder.yudao.framework.desensitize.core.base.serializer.StringDesensitizeSerializer#createContextual(SerializerProvider, BeanProperty)}
     * 即提取出对应的handler，然后具体使用时，调用serialize，内部再调用对应的handler#desensitize进行序列化
     * ---- 因为以上的操作，因为我们可以在一些注解上加上 @DesensitizeBy 注解，并指定handler。
     *
     * 【举个例子】我们创建了一个注解：EmailDesensitize，该注解上还有个注解：DesensitizeBy，并指定 EmailDesensitizationHandler 作为 EmailDesensitize 的handler
     * 表示：当在一个字段上标注 EmailDesensitize 注解时，使用 EmailDesensitizationHandler 进行处理。
     * 可以看到 {@link cn.iocoder.yudao.framework.desensitize.core.regex.handler.EmailDesensitizationHandler} 该处理类继承自
     * AbstractRegexDesensitizationHandler，主要指定了使用regex进行正则匹配，而具体正则规则和替换的replacer则交由子类实现。
     * 而 EmailDesensitizationHandler 则表示，正则匹配规则和替换的replacer直接从 EmailDesensitize 注解的属性中去读即可。
     * ---- 定义了 EmailDesensitize 和 EmailDesensitizationHandler 后，我们就可以在model的属性上直接标注 EmailDesensitize 注解了。
     *
     * 因此，大概可以将该注解的逻辑分为三层
     * 1、定义了一批注解，如果要对model的某个字段进行脱敏处理，即在该字段上加上对应的注解。
     * 2、每个注解对应一个handler，如 EmailDesensitize 和 EmailDesensitizationHandler，
     *      EmailDesensitize指定了具体的规则属性和替换replacer，EmailDesensitizationHandler 负责使用 具体的规则属性和替换replacer，
     *      即定义逻辑：匹配到了规则属性后，使用replacer进行替换
     * 3、定义了DesensitizeBy 和 StringDesensitizeSerializer，指定了handler的使用逻辑，即序列化时，调用handler进行处理。
     * 【StringDesensitizeSerializer 定义了在反序列化时，调用各个handler的 desensitize 进行处理。这就产生了三个问题：
     * 1. StringDesensitizeSerializer是怎么生效的呢？  答：定义了一个DesensitizeBy注解，将StringDesensitizeSerializer加到该注解上，从此，
     *    只要加了 DesensitizeBy注解的注解，就会使用StringDesensitizeSerializer进行处理
     * 2. 怎么知道哪些类的哪些字段需要脱敏呢？ 答：定义了一堆注解，如：EmailDesensitize、RegexDesensitize注解，加载model上，通过反射感知注解
     * 3. handler是啥？ 答：就是一堆 真正脱敏的 处理器，包含处理逻辑，这些负责处理逻辑的handler和上一步介绍的注解: XXXDesensitize一一对应，
     *    并且一个handler被加到一个注解：XXXDesensitize上。
     */
}
