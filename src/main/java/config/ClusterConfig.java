package config;

import cluster.LoadBalancer;
import cluster.RandomLoadBalancer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ClusterConfig {
    LoadBalancer loadBalanceInstance=new RandomLoadBalancer();

}
