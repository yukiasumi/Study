# 05-pod状态和生命周期

# 1 什么是Pod



`Pod`是`kubernetes`中你可以创建和部署的最小也是最简的单位。`Pod`代表着集群中运行的进程。

`Pod`中封装着应用的容器（有的情况下是好几个容器），存储、独立的网络`IP`，管理容器如何运行的策略选项。`Pod`代表着部署的一个单位：`kubernetes`中应用的一个实例，可能由一个或者多个容器组合在一起共享资源。

在`Kubernetes`集群中`Pod`有如下两种方式：

- **一个Pod中运行一个容器**。“每个`Pod`中一个容器”的模式是最常见的用法；在这种使用方式中，你可以把`Pod`想象成单个容器的封装，`Kubernetes`管理的是`Pod`而不是直接管理容器。
- **在一个Pod中同时运行多个容器**。一个`Pod`也可以同时封装几个需要紧密耦合互相协作的容器，它们之间共享资源。这些在同一个`Pod`中的容器可以互相协作成为一个`service`单位——一个容器共享文件，另一个`“sidecar”`容器来更新这些文件。`Pod`将这些容器的存储资源作为一个实体来管理。



`Pod`中共享的环境包括`Linux`的`namespace`、`cgroup`和其他可能的隔绝环境，这一点跟`Docker`容器一致。在`Pod`的环境中，每个容器可能还有更小的子隔离环境。



`Pod`中的容器共享`IP`地址和端口号，它们之间可以通过`localhost`互相发现。它们之间可以通过进程间通信，例如`SystemV`信号或者`POSIX`共享内存。不同`Pod`之间的容器具有不同的`IP`地址，不能直接通过`IPC`通信。



`Pod`中的容器也有访问共享`volume`的权限，这些`volume`会被定义成`pod`的一部分并挂载到应用容器的文件系统中。



就像每个应用容器，`pod`被认为是临时（非持久的）实体。在`Pod`的生命周期中讨论过，`pod`被创建后，被分配一个唯一的`ID（UID）`，调度到节点上，并一致维持期望的状态直到被终结（根据重启策略）或者被删除。如果`node`死掉了，分配到了这个`node`上的`pod`，在经过一个超时时间后会被重新调度到其他`node`节点上。一个给定的`pod`（如`UID`定义的）不会被“重新调度”到新的节点上，而是被一个同样的`pod`取代，如果期望的话甚至可以是相同的名字，但是会有一个新的`UID`。

`Docker`是`kubernetes`中最常用的容器运行时，但是`Pod`也支持其他容器运行时。



# 2 Pod中如何管理多个容器



`Pod`中可以同时运行多个进程（作为容器运行）协同工作。同一个`Pod`中的容器会自动的分配到同一个`node`上。同一个`Pod`中的容器共享资源、网络环境和依赖，它们总是被同时调度。



注意在一个`Pod`中同时运行多个容器是一种比较高级的用法。只有当你的容器需要紧密配合协作的时候才考虑用这种模式。例如，你有一个容器作为`web`服务器运行，需要用到共享的`volume`，有另一个`“sidecar”`容器来从远端获取资源更新这些文件，如下图所示：

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591154309035-81105a6e-1a33-46c6-91bb-863ebe58dc75.png)



`Pod`中可以共享两种资源：网络和存储

**网络**：每个`pod`都会被分配一个唯一的`IP`地址。`Pod`中的所有容器共享网络空间，包括`IP`地址和端口。`Pod`内部的容器可以使用`localhost`互相通信。`Pod`中的容器与外界通信时，必须分配共享网络资源（例如使用宿主机的端口映射）。

**存储**：可以为一个`Pod`指定多个共享的`Volume`。`Pod`中的所有容器都可以访问共享的`volume`。`Volume`也可以用来持久化`Pod`中的存储资源，以防容器重启后文件丢失。



# 3 使用Pod



你很少会直接在`kubernetes`中创建单个`Pod`。因为`Pod`的生命周期是短暂的，用后即焚的实体。当`Pod`被创建后（不论是由你直接创建还是被其它`Controller`），都会被`Kubernetes`调度到集群的`Node`上。直到`Pod`的进程终止、被删掉、因为缺少资源而被驱逐、或者`Node`故障之前这个`Pod`都会一直保持在那个`Node`上。



`Pod`不会自愈。如果`Pod`运行的`Node`故障，或者是调度器本身故障，这个`Pod`就会被删除。同样的，如果`Pod`所在`Node`缺少资源或者`Pod`处于维护状态，`Pod`也会被驱逐。`Kubernetes`使用更高级的称为`Controller`的抽象层，来管理`Pod`实例。虽然可以直接使用`Pod`，但是在`Kubernetes`中通常是使用`Controller`来管理`Pod`的。



`Controller`可以创建和管理多个`Pod`，提供副本管理、滚动升级和集群级别的自愈能力。例如，如果一个`Node`故障，`Controller`就能自动将该节点上的`Pod`调度到其他健康的`Node`上。

注意：重启`Pod`中的容器跟重启`Pod`不是一回事。`Pod`只提供容器的运行环境并保持容器的运行状态，重启容器不会造成`Pod`重启。



# 4 Pod对象的生命周期



[官方文档](https://kubernetes.io/zh/docs/concepts/workloads/pods/pod-lifecycle/)



[中文文档](https://jimmysong.io/kubernetes-handbook/concepts/pod-state-and-lifecycle.html)



`Pod`对象自从其创建开始至其终止退出的时间范围称为其生命周期。在这段时间中，`Pod`会处于多种不同的状态，并执行一些操作；其中，创建主容器（`main container`）为必需的操作，其他可选的操作还包括运行初始化容器（`init container`）、容器启动后钩子（`post start hook`）、容器的存活性探测（`liveness probe`）、就绪性探测（`readiness probe`）以及容器终止前钩子（`pre stop hook`）等，这些操作是否执行则取决于`Pod`的定义。如下图所示：

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591154346091-2ae25005-27a0-4489-ba1e-90b0cda8d750.png)

## 4.1 Pod phase



`Pod`的`status`字段是一个`PodStatus`的对象，`PodStatus`中有一个`phase`字段。

无论是手动创建还是通过`Deployment`等控制器创建，`Pod`对象总是应该处于其生命进程中以下几个相位（`phase`）之一。

- 挂起（`Pending`）：`API Server`创建了`pod`资源对象已存入`etcd`中，但它尚未被调度完成，或者仍处于从仓库下载镜像的过程中。

- 运行中（`Running`）：`Pod`已经被调度至某节点，并且所有容器都已经被`kubelet`创建完成。

- 成功（`Succeeded`）：`Pod`中的所有容器都已经成功终止并且不会被重启

- 失败（`Failed`）：`Pod`中的所有容器都已终止了，并且至少有一个容器是因为失败终止。即容器以`非0`状态退出或者被系统禁止。
- 未知（`Unknown`）：`Api Server`无法正常获取到`Pod`对象的状态信息，通常是由于无法与所在工作节点的`kubelet`通信所致。

![img](https://cdn.nlark.com/yuque/0/2020/jpeg/1495606/1591154358789-d8f5ff25-5731-441b-b7dc-afe3fab76bd1.jpeg)

## 4.2 Pod的创建过程



`Pod`是`kubernetes`的基础单元，理解它的创建过程对于了解系统运作大有裨益。如下图描述了一个`Pod`资源对象的典型创建过程。

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591154377660-215421d3-06d4-4e21-a220-6889a955e346.png)



1. 用户通过`kubectl`或其他`API`客户端提交了`Pod Spec`给`API Server`。

1. `API Server`尝试着将`Pod`对象的相关信息存入`etcd`中，待写入操作执行完成，`API Server`即会返回确认信息至客户端。

1. `API Server`开始反映`etcd`中的状态变化。

1. 所有的`kubernetes`组件均使用`“watch”`机制来跟踪检查`API Server`上的相关的变动。

1. `kube-scheduler`（调度器）通过其`“watcher”`觉察到`API Server`创建了新的`Pod`对象但尚未绑定至任何工作节点。

1. `kube-scheduler`为`Pod`对象挑选一个工作节点并将结果信息更新至`API Server`。

1. 调度结果信息由`API Server`更新至`etcd`存储系统，而且`API Server`也开始反映此`Pod`对象的调度结果。

1. `Pod`被调度到的目标工作节点上的`kubelet`尝试在当前节点上调用`Docker`启动容器，并将容器的结果状态返回送至`API Server`。

1. `API Server`将`Pod`状态信息存入`etcd`系统中。

1. 在`etcd`确认写入操作成功完成后，`API Server`将确认信息发送至相关的`kubelet`，事件将通过它被接受。



## 4.3 Pod生命周期中的重要行为



**1）初始化容器**



初始化容器（`init container`）即应用程序的主容器启动之前要运行的容器，常用于为主容器执行一些预置操作，它们具有两种典型特征。

1）初始化容器必须运行完成直至结束，若某初始化容器运行失败，那么`kubernetes`需要重启它直到成功完成。（注意：如果`pod`的`spec.restartPolicy`字段值为“`Never`”，那么运行失败的初始化容器不会被重启。）

2）每个初始化容器都必须按定义的顺序串行运行。



**2）容器探测**



容器探测（`container probe`）是`Pod`对象生命周期中的一项重要的日常任务，它是`kubelet`对容器周期性执行的健康状态诊断，诊断操作由容器的处理器（`handler`）进行定义。`Kubernetes`支持三种处理器用于`Pod`探测：

- `ExecAction`：在容器内执行指定命令，并根据其返回的状态码进行诊断的操作称为`Exec`探测，状态码为`0`表示成功，否则即为不健康状态。

- `TCPSocketAction`：通过与容器的某`TCP`端口尝试建立连接进行诊断，端口能够成功打开即为正常，否则为不健康状态。

- `HTTPGetAction`：通过向容器`IP`地址的某指定端口的指定`path`发起`HTTP GET`请求进行诊断，响应码为`2xx`或`3xx`时即为成功，否则为失败。



任何一种探测方式都可能存在三种结果：`“Success”（成功）`、`“Failure”（失败）`、`“Unknown”（未知）`，只有`success`表示成功通过检测。



容器探测分为两种类型：

- **存活性探测（livenessProbe）**：用于判定容器是否处于“运行”（`Running`）状态；一旦此类检测未通过，`kubelet`将杀死容器并根据重启策略（`restartPolicy`）决定是否将其重启；未定义存活检测的容器的默认状态为“`Success`”。

- **就绪性探测（readinessProbe）**：用于判断容器是否准备就绪并可对外提供服务；未通过检测的容器意味着其尚未准备就绪，端点控制器（如`Service`对象）会将其`IP`从所有匹配到此`Pod`对象的`Service`对象的端点列表中移除；检测通过之后，会再将其`IP`添加至端点列表中。



**什么时候使用存活（liveness）和就绪（readiness）探针？**



如果容器中的进程能够在遇到问题或不健康的情况下自行崩溃，则不一定需要存活探针，`kubelet`将根据`Pod`的`restartPolicy`自动执行正确的操作。



如果希望容器在探测失败时被杀死并重新启动，那么请指定一个存活探针，并指定`restartPolicy`为`Always`或`OnFailure`。



如果要仅在探测成功时才开始向`Pod`发送流量，请指定就绪探针。在这种情况下，就绪探针可能与存活探针相同，但是`spec`中的就绪探针的存在意味着`Pod`将在没有接收到任何流量的情况下启动，并且只有在探针探测成功才开始接收流量。



如果希望容器能够自行维护，可以指定一个就绪探针，该探针检查与存活探针不同的端点。



注意：如果只想在`Pod`被删除时能够排除请求，则不一定需要使用就绪探针；在删除`Pod`时，`Pod`会自动将自身置于未完成状态，无论就绪探针是否存在。当等待`Pod`中的容器停止时，`Pod`仍处于未完成状态。



## 4.4 容器的重启策略



`PodSpec`中有一个`restartPolicy`字段，可能的值为`Always`、`OnFailure`和`Never`。默认为`Always`。`restartPolicy`适用于`Pod`中的所有容器。而且它仅用于控制在同一节点上重新启动`Pod`对象的相关容器。首次需要重启的容器，将在其需要时立即进行重启，随后再次需要重启的操作将由`kubelet`延迟一段时间后进行，且反复的重启操作的延迟时长依次为`10秒、20秒、40秒... 300秒`是最大延迟时长。事实上，一旦绑定到一个节点，`Pod`对象将永远不会被重新绑定到另一个节点，它要么被重启，要么终止，直到节点发生故障或被删除。



- Always：但凡`Pod`对象终止就将其重启，默认值

- OnFailure：仅在`Pod`对象出现错误时方才将其重启

- Never：从不重启



## 4.5 initContainers 示例



### 4.5.1 编写yaml文件init-pod-demo.yaml

```yaml
[root@k8s-master ~]# vim manfests/init-pod-demo.yaml
apiVersion: v1
kind: Pod
metadata:
  name: myapp-pod
  labels:
    app: myapp
spec:
  containers:
  - name: myapp
    image: busybox
    command: ['sh', '-c', 'echo The app is running! && sleep 3600']
  initContainers:
  - name: init-myservice
    image: busybox
    command: ['sh', '-c', 'until nslookup myservice; do echo waiting for myservice; sleep 2; done;']
  - name: init-mydb
    image: busybox
    command: ['sh', '-c', 'until nslookup mydb; do echo waiting for mydb; sleep 2; done;']
```

### 4.5.2 创建myapp-pod

```perl
[root@k8s-master ~]# kubectl create -f manfests/init-pod-demo.yaml
pod/myapp-pod created
[root@k8s-master ~]# kubectl get pods
NAME        READY   STATUS     RESTARTS   AGE
myapp-pod   0/1     Init:0/2   0          3s
[root@k8s-master ~]# kubectl describe pod myapp-pod
......
Events:
  Type    Reason     Age    From                   Message
  ----    ------     ----   ----                   -------
  Normal  Scheduled  2m12s  default-scheduler      Successfully assigned default/myapp-pod to 192.168.3.27
  Normal  Pulling    2m11s  kubelet, 192.168.3.27  Pulling image "busybox"
  Normal  Pulled     2m5s   kubelet, 192.168.3.27  Successfully pulled image "busybox"
  Normal  Created    2m5s   kubelet, 192.168.3.27  Created container init-myservice
  Normal  Started    2m5s   kubelet, 192.168.3.27  Started container init-myservice
```

通过上面创建和查看状态，`pod`一直处于`init`阶段，因为还没有创建`myservice`和`mydb`，里面的`initContainers`容器中命令还未探测成功

### 4.5.3 创建myservice

```yaml
[root@k8s-master ~]# vim manfests/init-myservice.yaml
apiVersion: v1
kind: Service
metadata:
  name: myservice
spec:
  ports:
  - protocol: TCP
    port: 80
    targetPort: 80
    
[root@k8s-master ~]# kubectl create -f manfests/init-myservice.yaml
service/myservice created

[root@k8s-master ~]# kubectl get pods
NAME        READY   STATUS     RESTARTS   AGE
myapp-pod   0/1     Init:1/2   0          8m58s
```

myservice资源对象创建完成后，等待一会查看pod的状态，可以看到pod的状态中已经完成了一个init初始化。

### 4.5.4 创建mydb

```yaml
[root@k8s-master ~]# vim manfests/init-mydb.yaml
apiVersion: v1
kind: Service
metadata:
  name: mydb
spec:
  ports:
  - protocol: TCP
    port: 3306
    targetPort: 3306
    
[root@k8s-master ~]# kubectl create -f init-mydb.yaml
service/mydb created

[root@k8s-master ~]# kubectl get pods
NAME        READY   STATUS    RESTARTS   AGE
myapp-pod   1/1     Running   0          13m

[root@k8s-master ~]# kubectl describe pod myapp-pod 
......
Events:
  Type    Reason     Age    From                   Message
  ----    ------     ----   ----                   -------
  Normal  Scheduled  13m    default-scheduler      Successfully assigned default/myapp-pod to 192.168.3.27
  Normal  Pulling    13m    kubelet, 192.168.3.27  Pulling image "busybox"
  Normal  Pulled     13m    kubelet, 192.168.3.27  Successfully pulled image "busybox"
  Normal  Created    13m    kubelet, 192.168.3.27  Created container init-myservice
  Normal  Started    13m    kubelet, 192.168.3.27  Started container init-myservice
  Normal  Pulling    4m26s  kubelet, 192.168.3.27  Pulling image "busybox"
  Normal  Pulled     4m19s  kubelet, 192.168.3.27  Successfully pulled image "busybox"
  Normal  Created    4m19s  kubelet, 192.168.3.27  Created container init-mydb
  Normal  Started    4m19s  kubelet, 192.168.3.27  Started container init-mydb
  Normal  Pulling    43s    kubelet, 192.168.3.27  Pulling image "busybox"
  Normal  Pulled     28s    kubelet, 192.168.3.27  Successfully pulled image "busybox"
  Normal  Created    28s    kubelet, 192.168.3.27  Created container myapp
  Normal  Started    27s    kubelet, 192.168.3.27  Started container myapp
```

## 4.6 Pod存活性探测示例



### 4.6.1 设置exec探针示例

```yaml
[root@k8s-master ~]# vim manfests/liveness-exec.yaml
apiVersion: v1
kind: Pod
metadata:
  name: liveness-exec-pod
  namespace: default
  labels:
    test: liveness-exec
spec:
  containers:
  - name: liveness-exec-container
    image: busybox:latest
    imagePullPolicy: IfNotPresent
    command: ["/bin/sh","-c","touch /tmp/healthy; sleep 30; rm -f /tmp/healthy; sleep 3600"]
    livenessProbe:
      exec:
        command: ["test","-e","/tmp/healthy"]
      initialDelaySeconds: 1
      periodSeconds: 3

[root@k8s-master ~]# kubectl create -f manfests/liveness-exec.yaml    #创建pod
pod/liveness-exec-pod created
[root@k8s-master ~]# kubectl get pods   #查看pod
NAME                READY   STATUS    RESTARTS   AGE
liveness-exec-pod   1/1     Running   0          6s

#等待一段时间后再次查看其状态
[root@k8s-master ~]# kubectl get pods
NAME                READY   STATUS    RESTARTS   AGE
liveness-exec-pod   1/1     Running   2          2m46s
```

上面的资源清单中定义了一个`pod`对象，基于`busybox`镜像启动一个运行`“touch /tmp/healthy; sleep 30; rm -rf /tmp/healthy; sleep 3600"`命令的容器，此命令在容器启动时创建了`/tmp/healthy`文件，并于`60`秒之后将其删除。存活性探针运行`”test -e /tmp/healthy"`命令检查`/tmp/healthy`文件的存在性，若文件存在则返回状态码`0`，表示成功通过测试。在60秒内使用`“kubectl describe pods/liveness-exec-pod”`查看其详细信息，其存活性探测不会出现错误。而超过`60`秒之后，再执行该命令查看详细信息，可以发现存活性探测出现了故障，并且还可通过`“kubectl get pods"`查看该`pod`的重启的相关信息。

------

### 4.6.2 设置HTTP探针示例



基于`HTTP`的探测（`HTTPGetAction`）向目标容器发起一个`HTTP`请求，根据其响应码进行结果判定，响应码如`2xx`或`3xx`时表示测试通过。通过该命令”`# kubectl explain pod.spec.containers.livenessProbe.httpGet`“查看`httpGet`定义的字段

```perl
host	<string>：请求的主机地址，默认为Pod IP，也可以在httpHeaders中使用“Host:”来定义。
httpHeaders	<[]Object>：自定义的请求报文首部。
port	<string>：请求的端口，必选字段。
path	<string>：请求的HTTP资源路径，即URL path。
scheme	<string>：建立连接使用的协议，仅可为HTTP或HTTPS，默认为HTTP。
```

```perl
[root@k8s-master ~]# vim manfests/liveness-httpget.yaml
apiVersion: v1
kind: Pod
metadata:
  name: liveness-http
  namespace: default
  labels:
    test: liveness
spec:
  containers:
    - name: liveness-http-demo
      image: nginx:1.12
      imagePullPolicy: IfNotPresent
      ports:
      - name: http
        containerPort: 80
      lifecycle:
        postStart:
          exec:
            command: ["/bin/sh", "-c", "echo Healthz > /usr/share/nginx/html/healthz"]
      livenessProbe:
        httpGet:
          path: /healthz
          port: http
          scheme: HTTP
[root@k8s-master ~]# kubectl create -f manfests/liveness-httpget.yaml    #创建pod
pod/liveness-http created
[root@k8s-master ~]# kubectl get pods    #查看pod
NAME            READY   STATUS    RESTARTS   AGE
liveness-http   1/1     Running   0          7s

[root@k8s-master ~]# kubectl describe pods/liveness-http    #查看liveness-http详细信息
......
Containers:
  liveness-http-demo:
......
    Port:           80/TCP
    Host Port:      0/TCP
    State:          Running
      Started:      Mon, 09 Sep 2019 15:43:29 +0800
    Ready:          True
    Restart Count:  0
......
```

上面清单中定义的`httpGet`测试中，通过`lifecycle`中的`postStart hook`创建了一个专用于`httpGet`测试的页面文件`healthz`，请求的资源路径为`"/healthz"`，地址默认为`Pod IP`，端口使用了容器中顶一个端口名称`http`，这也是明确了为容器指明要暴露的端口的用途之一。并查看健康状态检测相关的信息，健康状态检测正常时，容器也将正常运行。下面通过`“kubectl exec”`命令进入容器删除由`postStart hook`创建的测试页面`healthz`。再次查看容器状态

```perl
[root@k8s-master ~]# kubectl exec pods/liveness-http -it -- /bin/sh    #进入到上面创建的pod中
# rm -rf /usr/share/nginx/html/healthz   #删除healthz测试页面
# 

[root@k8s-master ~]# kubectl get pods 
NAME            READY   STATUS    RESTARTS   AGE
liveness-http   1/1     Running   1          10m

[root@k8s-master ~]# kubectl describe pods/liveness-http
......
Containers:
  liveness-http-demo:
......
    Port:           80/TCP
    Host Port:      0/TCP
    State:          Running
      Started:      Mon, 09 Sep 2019 15:53:04 +0800
    Last State:     Terminated
      Reason:       Completed
      Exit Code:    0
      Started:      Mon, 09 Sep 2019 15:43:29 +0800
      Finished:     Mon, 09 Sep 2019 15:53:03 +0800
    Ready:          True
    Restart Count:  1
......
```

通过上面测试可以看出，当发起`http`请求失败后，容器将被杀掉后进行了重新构建。

------

### 4.6.3 设置TCP探针



基于`TCP`的存活性探测（`TCPSocketAction`）用于向容器的特定端口发起`TCP`请求并建立连接进行结果判定，连接建立成功即为通过检测。相比较来说，它比基于`HTTP`的探测要更高效、更节约资源，但精确度略低，毕竟连接建立成功未必意味着页面资源可用。通过该命令`”# kubectl explain pod.spec.containers.livenessProbe.tcpSocket“`查看`tcpSocket`定义的字段

```perl
host	<string>：请求连接的目标IP地址，默认为Pod IP
port	<string>：请求连接的目标端口，必选字段
```

```yaml
[root@k8s-master ~]# vim manfests/liveness-tcp.yaml
apiVersion: v1
kind: Pod
metadata:
  name: liveness-tcp-pod
  namespace: default
  labels:
    test: liveness-tcp
spec:
  containers:
  - name: liveness-tcp-demo
    image: nginx:1.12
    imagePullPolicy: IfNotPresent
    ports:
    - name: http
      containerPort: 80
    livenessProbe:
      tcpSocket:
        port: http
```

上面清单中定义的`tcpSocket`测试中，通过向容器的`80`端口发起请求，如果端口正常，则表明正常运行。

### 4.6.4 livenessProbe行为属性

```perl
[root@k8s-master ~]# kubectl explain pods.spec.containers.livenessProbe
KIND:     Pod
VERSION:  v1

RESOURCE: livenessProbe <Object>

exec   command 的方式探测，例如 ps 一个进程是否存在

failureThreshold    探测几次失败 才算失败， 默认是连续三次

initialDelaySeconds  初始化延迟探测，即容器启动多久之后再开始探测，默认为0秒

periodSeconds  每隔多久探测一次，默认是10秒

successThreshold  处于失败状态时，探测操作至少连续多少次的成功才算通过检测，默认为1秒

timeoutSeconds  存活性探测的超时时长，默认为1秒

httpGet   http请求探测

tcpSocket    端口探测
```

## 4.7 Pod就绪性探测示例



`Pod`对象启动后，容器应用通常需要一段时间才能完成其初始化过程，例如加载配置或数据，甚至有些程序需要运行某类的预热过程，若在这个阶段完成之前即接入客户端的请求，势必会等待太久。因此，这时候就用到了就绪性探测（`readinessProbe`）。



与存活性探测机制类似，就绪性探测是用来判断容器就绪与否的周期性（默认周期为10秒钟）操作，它用于探测容器是否已经初始化完成并可服务于客户端请求，探测操作返回`”success“`状态时，即为传递容器已经”就绪“的信号。



就绪性探测也支持`Exec`、`HTTPGet`和`TCPSocket`三种探测方式，且各自的定义机制也都相同。但与存活性探测触发的操作不同的是，探测失败时，就绪探测不会杀死或重启容器以保证其健康性，而是通知其尚未就绪，并触发依赖于其就绪状态的操作（例如，从`Service`对象中移除此`Pod`对象）以确保不会有客户端请求接入此`Pod`对象。



这里只是示例`http`探针示例，不论是`httpGet`还是`exec`还是`tcpSocket`和存活性探针类似。



### 4.7.1 设置HTTP探针示例

```perl
#终端1：
[root@k8s-master ~]# vim manfests/readiness-httpget.yaml  #编辑readiness-httpget测试pod的yaml文件
apiVersion: v1
kind: Pod
metadata:
  name: readiness-http
  namespace: default
  labels:
    test: readiness-http
spec:
  containers:
  - name: readiness-http-demo
    image: nginx:1.12
    imagePullPolicy: IfNotPresent
    ports:
    - name: http
      containerPort: 80
    readinessProbe:
      httpGet:
        path: /index.html
        port: http
        scheme: HTTP
[root@k8s-master ~]# kubectl create -f manfests/readiness-httpget.yaml    #创建pod
pod/readiness-http created
[root@k8s-master ~]# kubectl get pods      查看pod状态
NAME               READY   STATUS    RESTARTS   AGE
liveness-tcp-pod   1/1     Running   1          7d18h
readiness-http     1/1     Running   0          7s


#新打开一个终端2进入到容器里面
[root@k8s-master ~]# kubectl exec pods/readiness-http -it -- /bin/sh    #进入上面创建的pod
# rm -f /usr/share/nginx/html/index.html     #删除nginx的主页面文件
# ls /usr/share/nginx/html
50x.html
# 


#回到终端1上面查看pod状态
[root@k8s-master ~]# kubectl get pods     #查看pod状态
NAME               READY   STATUS    RESTARTS   AGE
liveness-tcp-pod   1/1     Running   1          7d18h
readiness-http     0/1     Running   0          2m44s
```

通过上面测试可以看出，当我们删除了`nginx`主页文件后，`readinessProbe`发起的测试就会失败，此时我们再查看`pod`的状态会发现并不会将`pod`删除重新启动，只是在`READY字段`可以看出，当前的`Pod`处于未就绪状态。

