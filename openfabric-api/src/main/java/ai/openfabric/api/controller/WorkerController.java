package ai.openfabric.api.controller;
import ai.openfabric.api.service.WorkerService;
import ai.openfabric.api.model.Worker;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
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
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.command.InfoCmd;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.command.InspectContainerResponse;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.github.dockerjava.api.model.Statistics;

@RestController
@RequestMapping("${node.api.path}/worker")
public class WorkerController { 
    @Autowired
    private WorkerService workerService;

    @GetMapping(path = "/list")
    public @ResponseBody List<Worker> listWorkers(@RequestParam Integer limit, @RequestParam Integer offset) {
        return workerService.list(limit, offset);
    }

    @GetMapping(path = "/statistics/{id}")
    public @ResponseBody Statistics workerStatistics(@PathVariable("id") String id) {
        return workerService.statsById(id);
    }

    @GetMapping(path = "/information/{id}")
    public @ResponseBody InspectContainerResponse workerInfo(@PathVariable("id") String id) {
        return workerService.information(id);
    }

    @PostMapping(path = "/create")
    public @ResponseBody String createWorker() throws FileNotFoundException{
        return workerService.create();
    }

    @PostMapping(path = "/start")
    public @ResponseBody String startWorker(@RequestBody String id) throws FileNotFoundException{
        return workerService.start(id);
    }

    @PostMapping(path = "/stop")
    public @ResponseBody String stopWorker(@RequestBody String id) throws FileNotFoundException{
        return workerService.stop(id);
    }

    @PostMapping(path = "/pause")
    public @ResponseBody String pauseWorker(@RequestBody String id) throws FileNotFoundException{
        return workerService.pause(id);
    }

    @PostMapping(path = "/unpause")
    public @ResponseBody String unpauseWorker(@RequestBody String id) throws FileNotFoundException{
        return workerService.unpause(id);
    }


}
