package com.library.scloud.learn.invoker.ribbon;

import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;

import java.util.List;

/**
 * @ClassName: MyRule
 * @Description: 自定义 Ribbon 负载均衡规则
 * @author feifei.liu
 * @date 2018/12/28 17:day01
 */
public class MyRule extends ZoneAvoidanceRule implements IRule {
    private ILoadBalancer lb;


    @Override
    public Server choose(Object key) {
        List<Server> servers = lb.getAllServers();
        System.out.println("这是自定义服务器规则类，输出服务器信息：");
        for (Server s: servers) {
            System.out.println("    "+ s.getHostPort());
        }
        return super.choose(key);
    }

    public MyRule() {
    }

    public MyRule(ILoadBalancer lb) {
        this.lb = lb;
    }

    @Override
    public void setLoadBalancer(ILoadBalancer lb) {
        this.lb = lb;
    }

    @Override
    public ILoadBalancer getLoadBalancer() {
        return this.lb;
    }

}
