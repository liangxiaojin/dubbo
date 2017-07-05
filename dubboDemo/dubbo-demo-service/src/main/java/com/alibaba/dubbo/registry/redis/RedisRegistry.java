//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alibaba.dubbo.registry.redis;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.NamedThreadFactory;
import com.alibaba.dubbo.common.utils.UrlUtils;
import com.alibaba.dubbo.registry.NotifyListener;
import com.alibaba.dubbo.registry.support.FailbackRegistry;
import com.alibaba.dubbo.rpc.RpcException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class RedisRegistry extends FailbackRegistry {
    private static final Logger logger = LoggerFactory.getLogger(RedisRegistry.class);
    private static final int DEFAULT_REDIS_PORT = 6379;
    private static final String DEFAULT_ROOT = "dubbo";
    private final ScheduledExecutorService expireExecutor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("DubboRegistryExpireTimer", true));
    private final ScheduledFuture<?> expireFuture;
    private final String root;
    private final Map<String, JedisPool> jedisPools = new ConcurrentHashMap();
    private final ConcurrentMap<String, RedisRegistry.Notifier> notifiers = new ConcurrentHashMap();
    private final int reconnectPeriod;
    private final int expirePeriod;
    private volatile boolean admin = false;
    private boolean replicate;

    public RedisRegistry(URL url) {
        super(url);
        if(url.isAnyHost()) {
            throw new IllegalStateException("registry address == null");
        } else {
//            Config config = new Config();
//            config.testOnBorrow = url.getParameter("test.on.borrow", true);
//            config.testOnReturn = url.getParameter("test.on.return", false);
//            config.testWhileIdle = url.getParameter("test.while.idle", false);
//            if(url.getParameter("max.idle", 0) > 0) {
//                config.maxIdle = url.getParameter("max.idle", 0);
//            }
//
//            if(url.getParameter("min.idle", 0) > 0) {
//                config.minIdle = url.getParameter("min.idle", 0);
//            }
//
//            if(url.getParameter("max.active", 0) > 0) {
//                config.maxActive = url.getParameter("max.active", 0);
//            }
//
//            if(url.getParameter("max.wait", url.getParameter("timeout", 0)) > 0) {
//                config.maxWait = (long)url.getParameter("max.wait", url.getParameter("timeout", 0));
//            }
//
//            if(url.getParameter("num.tests.per.eviction.run", 0) > 0) {
//                config.numTestsPerEvictionRun = url.getParameter("num.tests.per.eviction.run", 0);
//            }
//
//            if(url.getParameter("time.between.eviction.runs.millis", 0) > 0) {
//                config.timeBetweenEvictionRunsMillis = (long)url.getParameter("time.between.eviction.runs.millis", 0);
//            }
//
//            if(url.getParameter("min.evictable.idle.time.millis", 0) > 0) {
//                config.minEvictableIdleTimeMillis = (long)url.getParameter("min.evictable.idle.time.millis", 0);
//            }

            String cluster = url.getParameter("cluster", "failover");
            if(!"failover".equals(cluster) && !"replicate".equals(cluster)) {
                throw new IllegalArgumentException("Unsupported redis cluster: " + cluster + ". The redis cluster only supported failover or replicate.");
            } else {
                this.replicate = "replicate".equals(cluster);
                ArrayList addresses = new ArrayList();
                addresses.add(url.getAddress());
                String[] backups = url.getParameter("backup", new String[0]);
                if(backups != null && backups.length > 0) {
                    addresses.addAll(Arrays.asList(backups));
                }

                String address;
                String host;
                int port;
                for(Iterator group = addresses.iterator(); group.hasNext(); this.jedisPools.put(address, new JedisPool(new GenericObjectPoolConfig(), host, port, url.getParameter("timeout", 1000)))) {
                    address = (String)group.next();
                    int i = address.indexOf(58);
                    if(i > 0) {
                        host = address.substring(0, i);
                        port = Integer.parseInt(address.substring(i + 1));
                    } else {
                        host = address;
                        port = 6379;
                    }
                }

                this.reconnectPeriod = url.getParameter("reconnect.period", 3000);
                String group1 = url.getParameter("group", "dubbo");
                if(!group1.startsWith("/")) {
                    group1 = "/" + group1;
                }

                if(!group1.endsWith("/")) {
                    group1 = group1 + "/";
                }

                this.root = group1;
                this.expirePeriod = url.getParameter("session", '\uea60');
                this.expireFuture = this.expireExecutor.scheduleWithFixedDelay(new Runnable() {
                    public void run() {
                        try {
                            RedisRegistry.this.deferExpired();
                        } catch (Throwable var2) {
                            RedisRegistry.logger.error("Unexpected exception occur at defer expire time, cause: " + var2.getMessage(), var2);
                        }

                    }
                }, (long)(this.expirePeriod / 2), (long)(this.expirePeriod / 2), TimeUnit.MILLISECONDS);
            }
        }
    }

    private void deferExpired() {
        Iterator i$ = this.jedisPools.entrySet().iterator();

        while(i$.hasNext()) {
            Entry entry = (Entry)i$.next();
            JedisPool jedisPool = (JedisPool)entry.getValue();

            try {
                Jedis t = (Jedis)jedisPool.getResource();

                try {
                    Iterator i$1 = (new HashSet(this.getRegistered())).iterator();

                    while(i$1.hasNext()) {
                        URL url = (URL)i$1.next();
                        if(url.getParameter("dynamic", true)) {
                            String key = this.toCategoryPath(url);
                            if(t.hset(key, url.toFullString(), String.valueOf(System.currentTimeMillis() + (long)this.expirePeriod)).longValue() == 1L) {
                                t.publish(key, "register");
                            }
                        }
                    }

                    if(this.admin) {
                        this.clean(t);
                    }

                    if(!this.replicate) {
                        break;
                    }
                } finally {
                    jedisPool.returnResource(t);
                }
            } catch (Throwable var13) {
                logger.warn("Failed to write provider heartbeat to redis registry. registry: " + (String)entry.getKey() + ", cause: " + var13.getMessage(), var13);
            }
        }

    }

    private void clean(Jedis jedis) {
        Set keys = jedis.keys(this.root + "*");
        if(keys != null && keys.size() > 0) {
            Iterator i$ = keys.iterator();

            while(true) {
                String key;
                Map values;
                do {
                    do {
                        if(!i$.hasNext()) {
                            return;
                        }

                        key = (String)i$.next();
                        values = jedis.hgetAll(key);
                    } while(values == null);
                } while(values.size() <= 0);

                boolean delete = false;
                long now = System.currentTimeMillis();
                Iterator i$1 = values.entrySet().iterator();

                while(i$1.hasNext()) {
                    Entry entry = (Entry)i$1.next();
                    URL url = URL.valueOf((String)entry.getKey());
                    if(url.getParameter("dynamic", true)) {
                        long expire = Long.parseLong((String)entry.getValue());
                        if(expire < now) {
                            jedis.hdel(key, (String)entry.getKey());
                            delete = true;
                            if(logger.isWarnEnabled()) {
                                logger.warn("Delete expired key: " + key + " -> value: " + (String)entry.getKey() + ", expire: " + new Date(expire) + ", now: " + new Date(now));
                            }
                        }
                    }
                }

                if(delete) {
                    jedis.publish(key, "unregister");
                }
            }
        }
    }

    public boolean isAvailable() {
        Iterator i$ = this.jedisPools.values().iterator();

        while(i$.hasNext()) {
            JedisPool jedisPool = (JedisPool)i$.next();

            try {
                Jedis t = (Jedis)jedisPool.getResource();

                boolean var4;
                try {
                    if(!t.isConnected()) {
                        continue;
                    }

                    var4 = true;
                } finally {
                    jedisPool.returnResource(t);
                }

                return var4;
            } catch (Throwable var10) {
                ;
            }
        }

        return false;
    }

    public void destroy() {
        super.destroy();

        try {
            this.expireFuture.cancel(true);
        } catch (Throwable var6) {
            logger.warn(var6.getMessage(), var6);
        }

        Iterator i$;
        try {
            i$ = this.notifiers.values().iterator();

            while(i$.hasNext()) {
                RedisRegistry.Notifier entry = (RedisRegistry.Notifier)i$.next();
                entry.shutdown();
            }
        } catch (Throwable var7) {
            logger.warn(var7.getMessage(), var7);
        }

        i$ = this.jedisPools.entrySet().iterator();

        while(i$.hasNext()) {
            Entry entry1 = (Entry)i$.next();
            JedisPool jedisPool = (JedisPool)entry1.getValue();

            try {
                jedisPool.destroy();
            } catch (Throwable var5) {
                logger.warn("Failed to destroy the redis registry client. registry: " + (String)entry1.getKey() + ", cause: " + var5.getMessage(), var5);
            }
        }

    }

    public void doRegister(URL url) {
        String key = this.toCategoryPath(url);
        String value = url.toFullString();
        String expire = String.valueOf(System.currentTimeMillis() + (long)this.expirePeriod);
        boolean success = false;
        RpcException exception = null;
        Iterator i$ = this.jedisPools.entrySet().iterator();

        while(i$.hasNext()) {
            Entry entry = (Entry)i$.next();
            JedisPool jedisPool = (JedisPool)entry.getValue();

            try {
                Jedis t = (Jedis)jedisPool.getResource();

                try {
                    t.hset(key, value, expire);
                    t.publish(key, "register");
                    success = true;
                    if(!this.replicate) {
                        break;
                    }
                } finally {
                    jedisPool.returnResource(t);
                }
            } catch (Throwable var16) {
                exception = new RpcException("Failed to register service to redis registry. registry: " + (String)entry.getKey() + ", service: " + url + ", cause: " + var16.getMessage(), var16);
            }
        }

        if(exception != null) {
            if(!success) {
                throw exception;
            }

            logger.warn(exception.getMessage(), exception);
        }

    }

    public void doUnregister(URL url) {
        String key = this.toCategoryPath(url);
        String value = url.toFullString();
        RpcException exception = null;
        boolean success = false;
        Iterator i$ = this.jedisPools.entrySet().iterator();

        while(i$.hasNext()) {
            Entry entry = (Entry)i$.next();
            JedisPool jedisPool = (JedisPool)entry.getValue();

            try {
                Jedis t = (Jedis)jedisPool.getResource();

                try {
                    t.hdel(key, value);
                    t.publish(key, "unregister");
                    success = true;
                    if(!this.replicate) {
                        break;
                    }
                } finally {
                    jedisPool.returnResource(t);
                }
            } catch (Throwable var15) {
                exception = new RpcException("Failed to unregister service to redis registry. registry: " + (String)entry.getKey() + ", service: " + url + ", cause: " + var15.getMessage(), var15);
            }
        }

        if(exception != null) {
            if(!success) {
                throw exception;
            }

            logger.warn(exception.getMessage(), exception);
        }

    }

    public void doSubscribe(URL url, NotifyListener listener) {
        String service = this.toServicePath(url);
        RedisRegistry.Notifier notifier = (RedisRegistry.Notifier)this.notifiers.get(service);
        if(notifier == null) {
            RedisRegistry.Notifier success = new RedisRegistry.Notifier(service);
            this.notifiers.putIfAbsent(service, success);
            notifier = (RedisRegistry.Notifier)this.notifiers.get(service);
            if(notifier == success) {
                notifier.start();
            }
        }

        boolean success1 = false;
        RpcException exception = null;
        Iterator i$ = this.jedisPools.entrySet().iterator();

        while(i$.hasNext()) {
            Entry entry = (Entry)i$.next();
            JedisPool jedisPool = (JedisPool)entry.getValue();

            try {
                Jedis t = (Jedis)jedisPool.getResource();

                try {
                    if(service.endsWith("*")) {
                        this.admin = true;
                        Set keys = t.keys(service);
                        if(keys != null && keys.size() > 0) {
                            HashMap serviceKeys = new HashMap();

                            Iterator i$1;
                            String sk;
                            Object sk1;
                            for(i$1 = keys.iterator(); i$1.hasNext(); ((Set)sk1).add(sk)) {
                                sk = (String)i$1.next();
                                String serviceKey = this.toServicePath(sk);
                                sk1 = (Set)serviceKeys.get(serviceKey);
                                if(sk1 == null) {
                                    sk1 = new HashSet();
                                    serviceKeys.put(serviceKey, sk1);
                                }
                            }

                            i$1 = serviceKeys.values().iterator();

                            while(i$1.hasNext()) {
                                Set sk2 = (Set)i$1.next();
                                this.doNotify(t, sk2, url, Arrays.asList(new NotifyListener[]{listener}));
                            }
                        }
                    } else {
                        this.doNotify(t, t.keys(service + "/" + "*"), url, Arrays.asList(new NotifyListener[]{listener}));
                    }

                    success1 = true;
                    break;
                } finally {
                    jedisPool.returnResource(t);
                }
            } catch (Throwable var22) {
                exception = new RpcException("Failed to subscribe service from redis registry. registry: " + (String)entry.getKey() + ", service: " + url + ", cause: " + var22.getMessage(), var22);
            }
        }

        if(exception != null) {
            if(!success1) {
                throw exception;
            }

            logger.warn(exception.getMessage(), exception);
        }

    }

    public void doUnsubscribe(URL url, NotifyListener listener) {
    }

    private void doNotify(Jedis jedis, String key) {
        Iterator i$ = (new HashMap(this.getSubscribed())).entrySet().iterator();

        while(i$.hasNext()) {
            Entry entry = (Entry)i$.next();
            this.doNotify(jedis, Arrays.asList(new String[]{key}), (URL)entry.getKey(), new HashSet((Collection)entry.getValue()));
        }

    }

    private void doNotify(Jedis jedis, Collection<String> keys, URL url, Collection<NotifyListener> listeners) {
        if(keys != null && keys.size() != 0 && listeners != null && listeners.size() != 0) {
            long now = System.currentTimeMillis();
            ArrayList result = new ArrayList();
            List categories = Arrays.asList(url.getParameter("category", new String[0]));
            String consumerService = url.getServiceInterface();
            Iterator i$ = keys.iterator();

            while(true) {
                String listener;
                String category;
                do {
                    do {
                        if(!i$.hasNext()) {
                            if(result != null && result.size() != 0) {
                                i$ = listeners.iterator();

                                while(i$.hasNext()) {
                                    NotifyListener listener1 = (NotifyListener)i$.next();
                                    this.notify(url, listener1, result);
                                }

                                return;
                            }

                            return;
                        }

                        listener = (String)i$.next();
                        if("*".equals(consumerService)) {
                            break;
                        }

                        category = this.toServiceName(listener);
                    } while(!category.equals(consumerService));

                    category = this.toCategoryName(listener);
                } while(!categories.contains("*") && !categories.contains(category));

                ArrayList urls = new ArrayList();
                Map values = jedis.hgetAll(listener);
                if(values != null && values.size() > 0) {
                    Iterator i$1 = values.entrySet().iterator();

                    label78:
                    while(true) {
                        Entry entry;
                        URL u;
                        do {
                            if(!i$1.hasNext()) {
                                break label78;
                            }

                            entry = (Entry)i$1.next();
                            u = URL.valueOf((String)entry.getKey());
                        } while(u.getParameter("dynamic", true) && Long.parseLong((String)entry.getValue()) < now);

                        if(UrlUtils.isMatch(url, u)) {
                            urls.add(u);
                        }
                    }
                }

                if(urls.isEmpty()) {
                    urls.add(url.setProtocol("empty").setAddress("0.0.0.0").setPath(this.toServiceName(listener)).addParameter("category", category));
                }

                result.addAll(urls);
                if(logger.isInfoEnabled()) {
                    logger.info("redis notify: " + listener + " = " + urls);
                }
            }
        }
    }

    private String toServiceName(String categoryPath) {
        String servicePath = this.toServicePath(categoryPath);
        return servicePath.startsWith(this.root)?servicePath.substring(this.root.length()):servicePath;
    }

    private String toCategoryName(String categoryPath) {
        int i = categoryPath.lastIndexOf("/");
        return i > 0?categoryPath.substring(i + 1):categoryPath;
    }

    private String toServicePath(String categoryPath) {
        int i;
        if(categoryPath.startsWith(this.root)) {
            i = categoryPath.indexOf("/", this.root.length());
        } else {
            i = categoryPath.indexOf("/");
        }

        return i > 0?categoryPath.substring(0, i):categoryPath;
    }

    private String toServicePath(URL url) {
        return this.root + url.getServiceInterface();
    }

    private String toCategoryPath(URL url) {
        return this.toServicePath(url) + "/" + url.getParameter("category", "providers");
    }

    private class Notifier extends Thread {
        private final String service;
        private volatile Jedis jedis;
        private volatile boolean first = true;
        private volatile boolean running = true;
        private final AtomicInteger connectSkip = new AtomicInteger();
        private final AtomicInteger connectSkiped = new AtomicInteger();
        private final Random random = new Random();
        private volatile int connectRandom;

        private void resetSkip() {
            this.connectSkip.set(0);
            this.connectSkiped.set(0);
            this.connectRandom = 0;
        }

        private boolean isSkip() {
            int skip = this.connectSkip.get();
            if(skip >= 10) {
                if(this.connectRandom == 0) {
                    this.connectRandom = this.random.nextInt(10);
                }

                skip = 10 + this.connectRandom;
            }

            if(this.connectSkiped.getAndIncrement() < skip) {
                return true;
            } else {
                this.connectSkip.incrementAndGet();
                this.connectSkiped.set(0);
                this.connectRandom = 0;
                return false;
            }
        }

        public Notifier(String service) {
            super.setDaemon(true);
            super.setName("DubboRedisSubscribe");
            this.service = service;
        }

        public void run() {
            while(this.running) {
                try {
                    if(!this.isSkip()) {
                        try {
                            Iterator t = RedisRegistry.this.jedisPools.entrySet().iterator();

                            while(t.hasNext()) {
                                Entry entry = (Entry)t.next();
                                JedisPool jedisPool = (JedisPool)entry.getValue();

                                try {
                                    this.jedis = (Jedis)jedisPool.getResource();

                                    try {
                                        if(!this.service.endsWith("*")) {
                                            if(!this.first) {
                                                this.first = false;
                                                RedisRegistry.this.doNotify(this.jedis, this.service);
                                                this.resetSkip();
                                            }

                                            this.jedis.psubscribe(RedisRegistry.this.new NotifySub(jedisPool), new String[]{this.service + "/" + "*"});
                                            break;
                                        }

                                        if(!this.first) {
                                            this.first = false;
                                            Set t1 = this.jedis.keys(this.service);
                                            if(t1 != null && t1.size() > 0) {
                                                Iterator i$ = t1.iterator();

                                                while(i$.hasNext()) {
                                                    String s = (String)i$.next();
                                                    RedisRegistry.this.doNotify(this.jedis, s);
                                                }
                                            }

                                            this.resetSkip();
                                        }

                                        this.jedis.psubscribe(RedisRegistry.this.new NotifySub(jedisPool), new String[]{this.service});
                                        break;
                                    } finally {
                                        jedisPool.returnBrokenResource(this.jedis);
                                    }
                                } catch (Throwable var14) {
                                    RedisRegistry.logger.warn("Failed to subscribe service from redis registry. registry: " + (String)entry.getKey() + ", cause: " + var14.getMessage(), var14);
                                }
                            }
                        } catch (Throwable var15) {
                            RedisRegistry.logger.error(var15.getMessage(), var15);
                            sleep((long)RedisRegistry.this.reconnectPeriod);
                        }
                    }
                } catch (Throwable var16) {
                    RedisRegistry.logger.error(var16.getMessage(), var16);
                }
            }

        }

        public void shutdown() {
            try {
                this.running = false;
                this.jedis.disconnect();
            } catch (Throwable var2) {
                RedisRegistry.logger.warn(var2.getMessage(), var2);
            }

        }
    }

    private class NotifySub extends JedisPubSub {
        private final JedisPool jedisPool;

        public NotifySub(JedisPool jedisPool) {
            this.jedisPool = jedisPool;
        }

        public void onMessage(String key, String msg) {
            if(RedisRegistry.logger.isInfoEnabled()) {
                RedisRegistry.logger.info("redis event: " + key + " = " + msg);
            }

            if(msg.equals("register") || msg.equals("unregister")) {
                try {
                    Jedis t = (Jedis)this.jedisPool.getResource();

                    try {
                        RedisRegistry.this.doNotify(t, key);
                    } finally {
                        this.jedisPool.returnResource(t);
                    }
                } catch (Throwable var9) {
                    RedisRegistry.logger.error(var9.getMessage(), var9);
                }
            }

        }

        public void onPMessage(String pattern, String key, String msg) {
            this.onMessage(key, msg);
        }

        public void onSubscribe(String key, int num) {
        }

        public void onPSubscribe(String pattern, int num) {
        }

        public void onUnsubscribe(String key, int num) {
        }

        public void onPUnsubscribe(String pattern, int num) {
        }
    }
}
