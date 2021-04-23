package config;

import executor.TaskExecutor;
import executor.ThreadPoolTaskExecutorImpl;
import lombok.Getter;
import protocol.Protocol;
import protocol.ToyProtocol;


@Getter
public class ProtocolConfig {
    Protocol protocolInstance=new ToyProtocol();
    TaskExecutor executorInstance=new ThreadPoolTaskExecutorImpl();

}
