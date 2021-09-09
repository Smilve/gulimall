package com.lvboaa.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvboaa.common.utils.PageUtils;
import com.lvboaa.common.utils.Query;

import com.lvboaa.gulimall.product.dao.CategoryDao;
import com.lvboaa.gulimall.product.entity.CategoryEntity;
import com.lvboaa.gulimall.product.service.CategoryService;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    List<Long> catIdList = null;


    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 存入redis使用json格式字符串，可以跨平台兼容

        /**
         *  1.空结果缓存：解决缓存穿透
         *  2.设置过期时间(加随机值)：解决缓存雪崩
         *  3.加锁：解决缓存击穿  最难实现
         */
        List<CategoryEntity> entities;
        String list = redisTemplate.opsForValue().get("listWithTree");
        if (StringUtils.isEmpty(list)) {
            System.out.println("缓存不命中，将要查询数据库......");
            return getListWithTreeWithRedisLock();
        }
        System.out.println("缓存命中，直接返回........");
        entities = JSON.parseObject(list, new TypeReference<List<CategoryEntity>>() {
        });

        return entities;
    }

    public List<CategoryEntity> getListWithTreeWithRedisLock() {

        // 1、占分布式锁
        String uuid = UUID.randomUUID().toString();
        Boolean local = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS);

        if (local){
            System.out.println("获取分布式锁成功");
            // 加锁成功... 执行业务 访问数据库  但是如果程序报异常，没有执行删除操作，导致分布式死锁问题 finally处理的话程序可能执行到这服务器宕机 一样的
            // 解决办法：设置过期时间，设置过期时间和设置锁必须是原子性的，不然断电等也会产生异常
            List<CategoryEntity> data;
            try{
                data = getDataFromDB();
            }finally {
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                // 原子操作 对比之后删除锁，成功返回1l 失败返回0l
                Long lock = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                        Arrays.asList("lock"), uuid);// lock 放在 KEYS[1] uuid 放在ARGV[1]
            }
            return data;
        }else {
            // 加锁失败... 重试  休眠一段时间 自旋锁
            System.out.println("获取分布式锁失败，等待重试");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getListWithTreeWithRedisLock();
        }
    }

    public List<CategoryEntity> getListWithTreeWithLocalLock() {
        /**
         *  只要是同一把锁，就能锁住需要这个锁的所有线程
         *  1. synchronized (this)：springboot所有组件在容器中都是单例的，可以实现，也可以在这个方法上加关键字
         *     得到锁以后需要再去缓存中确定一次，如果没有才继续查询，存入缓存也需要加锁同步，本地单容器测试只访问一次数据库
         *     (单例应用可以这样，但是分布式就不行了，所以我们需要分布式锁)
         *     本地锁:synchronized或者JUC的lock只能锁定本地线程的叫本地锁
         *     本地多容器测试发现每个容器都会访问一次数据库，这里需要加上分布式锁(所有容器只访问一次数据库)
         *  2. 分布式锁
         *     所有的容器都去同一个地方占坑，redis或数据库，任何所有容器都能访问的地方
         *     比如说redis的set有一个nx(不存在才插入)的参数；因为redis是单线程的，可以使用这个作为分布式锁
         *
         */
        synchronized (this) {
            return getDataFromDB();
        }
    }

    public List<CategoryEntity> getDataFromDB(){
        String list = redisTemplate.opsForValue().get("listWithTree");
        if (!StringUtils.isEmpty(list)) {
            List<CategoryEntity> entities = JSON.parseObject(list, new TypeReference<List<CategoryEntity>>() {
            });
            return JSON.parseObject(list, new TypeReference<List<CategoryEntity>>() {
            });
        }

        System.out.println("查询了数据库.......");

        List<CategoryEntity> entities = baseMapper.selectList(null);
        if (entities == null || entities.size() == 0) {
            redisTemplate.opsForValue().set("listWithTree", "1", 5, TimeUnit.MINUTES);
            return null;
        }

        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map(menu -> {
            //找到子菜单
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((m1, m2) ->
                (m1.getSort() == null ? 0 : m1.getSort()) - (m2.getSort() == null ? 0 : m2.getSort())
        ).collect(Collectors.toList());

        // 放入返回也要进行加锁同步，不然会多次访问数据库
        // 缓存中没有需要把从数据库读出来的数据存入缓存，需要将对象转为json格式  可以跨平台兼容
        redisTemplate.opsForValue().set("listWithTree", JSON.toJSONString(entities), 5, TimeUnit.MINUTES);
        return entities;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //检查当前删除的菜单，是否被别的地方引用（是否是别的父类别，是的话需要都查出来删除）
        List<CategoryEntity> entities = baseMapper.selectList(null);

        catIdList = new CopyOnWriteArrayList<>();
        asList.stream().forEach(id ->
                getDelIds(id, entities));
        catIdList.addAll(asList);
        //list去重 但是mybatis-plus会自动去重
        //catIdList = new CopyOnWriteArrayList<>(new HashSet<>(catIdList));

        // 逻辑删除
        baseMapper.deleteBatchIds(catIdList);
    }

    public void getDelIds(Long id, List<CategoryEntity> all) {
        List<Long> collect = all.stream().filter(ce ->
                ce.getParentCid().equals(id)
        ).map(ce -> {
            getDelIds(ce.getCatId(), all);
            return ce.getCatId();
        }).collect(Collectors.toList());
        catIdList.addAll(collect);
    }

    public List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity ->
                categoryEntity.getParentCid().equals(root.getCatId())
        ).map(categoryEntity -> {
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((m1, m2) ->
                (m1.getSort() == null ? 0 : m1.getSort()) - (m2.getSort() == null ? 0 : m2.getSort())
        ).collect(Collectors.toList());

        return children;
    }


}