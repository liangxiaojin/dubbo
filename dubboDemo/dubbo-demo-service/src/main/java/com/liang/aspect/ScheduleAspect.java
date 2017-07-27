package com.liang.aspect;

import com.liang.lock.RedisLock;
import com.liang.util.GetHostName;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by setup on 2017/7/26.
 */
@Component
@Aspect
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Order(1)
public class ScheduleAspect {

    @Autowired
    private RedisTemplate redisTemplate;
    @Pointcut("execution(* com.liang.schedule.*.*(..))")
    public void aspect(){}

    /**
     * 定义环绕声通知，使用在方法aspect上注册的切入点
     * @param point
     */
    @Around("aspect()")
    public void doBasicProfiling(ProceedingJoinPoint point) throws NoSuchMethodException {
        Class aClass = point.getTarget().getClass();
        String methodName = point.getSignature().getName();
        Class[] parameterTypes = ((MethodSignature)point.getSignature()).getMethod().getParameterTypes();
        System.out.println(" class = "+aClass+" methodName = "+methodName);
        Method method =  aClass.getMethod(methodName,parameterTypes);
        boolean present =  method.isAnnotationPresent(Scheduled.class);
        if(present){
            String hostName = GetHostName.get();
            System.out.println(" hostName "+hostName);

            String key = aClass.getName()+"_"+method.getName();
            RedisLock lock = new RedisLock(redisTemplate,key,0);
            try {
                System.out.println(" 执行schedule "+key);
                if(lock.tryLock()){
                    System.out.println(" 抢到锁了，开始 ");
                    point.proceed();
                }else {
                    System.out.println(" 没抢到，sleep一会 ");

                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }finally {
                System.out.println(" 结束schedule ");
                lock.unlock();
            }
        }
    }


}
