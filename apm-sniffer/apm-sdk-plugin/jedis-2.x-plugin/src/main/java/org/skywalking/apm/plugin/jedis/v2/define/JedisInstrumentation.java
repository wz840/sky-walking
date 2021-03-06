package org.skywalking.apm.plugin.jedis.v2.define;

import java.net.URI;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.skywalking.apm.plugin.jedis.v2.JedisConstructorWithShardInfoArgInterceptor;
import org.skywalking.apm.plugin.jedis.v2.JedisConstructorWithUriArgInterceptor;
import org.skywalking.apm.plugin.jedis.v2.JedisMethodInterceptor;
import org.skywalking.apm.plugin.jedis.v2.RedisMethodMatch;

import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static org.skywalking.apm.agent.core.plugin.bytebuddy.ArgumentTypeNameMatch.takesArgumentWithType;
import static org.skywalking.apm.agent.core.plugin.match.NameMatch.byName;

/**
 * {@link JedisInstrumentation} presents that skywalking intercept all constructors and methods of {@link
 * redis.clients.jedis.Jedis}. {@link JedisConstructorWithShardInfoArgInterceptor} intercepts all constructor with
 * argument {@link redis.clients.jedis.HostAndPort} ,{@link JedisConstructorWithUriArgInterceptor} intercepts the
 * constructors with uri argument and the other constructor intercept by class {@link
 * JedisConstructorWithShardInfoArgInterceptor}. {@link JedisMethodInterceptor} intercept all methods of {@link
 * redis.clients.jedis.Jedis}.
 *
 * @author zhangxin
 */
public class JedisInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String HOST_AND_PORT_ARG_TYPE_NAME = "redis.clients.jedis.HostAndPort";
    private static final String ENHANCE_CLASS = "redis.clients.jedis.Jedis";
    private static final String CONSTRUCTOR_WITH_STRING_ARG_INTERCEPT_CLASS = "org.skywalking.apm.plugin.jedis.v2.JedisConstructorWithStringArgInterceptor";
    private static final String CONSTRUCTOR_WITH_SHARD_INFO_ARG_INTERCEPT_CLASS = "org.skywalking.apm.plugin.jedis.v2.JedisConstructorWithShardInfoArgInterceptor";
    private static final String CONSTRUCTOR_WITH_URI_ARG_INTERCEPT_CLASS = "org.skywalking.apm.plugin.jedis.v2.JedisConstructorWithUriArgInterceptor";
    private static final String JEDIS_METHOD_INTERCET_CLASS = "org.skywalking.apm.plugin.jedis.v2.JedisMethodInterceptor";

    @Override
    public ClassMatch enhanceClass() {
        return byName(ENHANCE_CLASS);
    }

    @Override
    protected ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[] {
            new ConstructorInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getConstructorMatcher() {
                    return takesArgument(0, String.class);
                }

                @Override
                public String getConstructorInterceptor() {
                    return CONSTRUCTOR_WITH_STRING_ARG_INTERCEPT_CLASS;
                }
            },
            new ConstructorInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getConstructorMatcher() {
                    return takesArgumentWithType(0, HOST_AND_PORT_ARG_TYPE_NAME);
                }

                @Override
                public String getConstructorInterceptor() {
                    return CONSTRUCTOR_WITH_SHARD_INFO_ARG_INTERCEPT_CLASS;
                }
            },
            new ConstructorInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getConstructorMatcher() {
                    return takesArgument(0, URI.class);
                }

                @Override
                public String getConstructorInterceptor() {
                    return CONSTRUCTOR_WITH_URI_ARG_INTERCEPT_CLASS;
                }
            }
        };
    }

    @Override
    protected InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {
            new InstanceMethodsInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return RedisMethodMatch.INSTANCE.getJedisMethodMatcher();
                }

                @Override
                public String getMethodsInterceptor() {
                    return JEDIS_METHOD_INTERCET_CLASS;
                }

                @Override public boolean isOverrideArgs() {
                    return false;
                }
            }
        };
    }
}
