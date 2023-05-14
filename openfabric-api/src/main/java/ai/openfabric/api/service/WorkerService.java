package ai.openfabric.api.service;
import ai.openfabric.api.repository.WorkerRepository;
import ai.openfabric.api.model.Worker;
import java.util.List;
import java.util.Optional;
import java.util.*;
 
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.OneToMany;
import org.springframework.stereotype.Service;

import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.core.DockerClientImpl;
import com.spotify.docker.client.DockerClient.ListContainersParam;

import java.time.Duration;
import java.util.*;
import com.github.dockerjava.api.model.Container;
import java.io.*;
 
 import com.github.dockerjava.core.InvocationBuilder.AsyncResultCallback;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.api.command.InspectContainerResponse;


@Service("workerService")
public class WorkerService {
    @Autowired
    private WorkerRepository repository;
     private DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

    private DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
        .dockerHost(config.getDockerHost())
        .sslConfig(config.getSSLConfig())
        .maxConnections(100)
        .connectionTimeout(Duration.ofSeconds(30))
        .responseTimeout(Duration.ofSeconds(45))
        .build();

    private DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

    public void add(Worker worker){
        repository.save(worker);
    }

    private Worker containerToWorker(Container container){
        Worker worker = new Worker();
        worker.id = container.getId();
        worker.name = container.getNames()[0];
        worker.ports = container.getPorts().toString();
        worker.status = container.getStatus();
        worker.image = container.getImage();
        worker.state = container.getState();
        return worker;
    }

    public List<Worker> list(Integer limit, Integer offset){
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        List<Worker> workers = new ArrayList<Worker>();
        for(Integer i = limit * offset; i - limit < limit * offset && i < containers.size(); i++){
            Worker newWorker = this.containerToWorker(containers.get(i));
            this.add(newWorker);
            workers.add(newWorker);
        }
        return workers;
    }

    public Statistics statsById(String id){
        AsyncResultCallback<Statistics> callback = new AsyncResultCallback<>();
        dockerClient.statsCmd(id).exec(callback);
        Statistics stats = null;
        try {
            stats = callback.awaitResult();
            callback.close();
        } catch (RuntimeException | IOException e) {
            // you may want to throw an exception here
        }
        return stats; 
    }

    public String create(){
        String newId = dockerClient.createContainerCmd("alpine").withCmd("sleep", "600").exec().getId();
        return "New Dummy Test Worker Created id: " + newId;
    }

    public String start(String id){
        try {
            dockerClient.startContainerCmd(id).exec();
            return "Container Started";
        } catch (RuntimeException e){
            return e.toString();
        }
    }

    public String stop(String id){
        try {
            dockerClient.stopContainerCmd(id).exec();
            return "Container Stopped";
        } catch (RuntimeException e){
            return e.toString();
        }
    }

    public String pause(String id){
        try {
            dockerClient.pauseContainerCmd(id).exec();
            return "Container Paused";
        } catch (RuntimeException e){
            return e.toString();
        }
    }

    public String unpause(String id){
        try {
            dockerClient.unpauseContainerCmd(id).exec();
            return "Container Unpaused";
        } catch (RuntimeException e){
            return e.toString();    
        }
    }

    public InspectContainerResponse information(String id){
        return dockerClient.inspectContainerCmd(id).exec();
    }
}