

# 03-Kubernetes快速入门

# 1 Kubernetes的核心对象



`API Server`提供了`RESTful`风格的编程接口，其管理的资源是`Kubernetes API`中的端点，用于存储某种`API`对象的集合，例如，内置`Pod`资源是包含了所有`Pod`对象的集合。资源对象是用于表现集群状态的实体，常用于描述应于哪个节点进行容器化应用、需要为其配置什么资源以及应用程序的管理策略等，例如，重启、升级及容错机制。另外，一个对象也是一种“意向记录“——一旦创建，`Kubernetes`就需要一直确保对象始终存在。`Pod`、`Deployment`和`Service`等都是最常用的核心对象。



## 1.1 Pod资源对象



`Pod`资源对象是一种集合了一到多个应用容器、存储资源、专用`IP`及支撑容器运行的其他选项的逻辑组件，如图所示。`Pod`代表着`Kubernetes`的部署单元及原子运行单元，即一个应用程序的单一运行实例，它通常由共享资源且关系紧密的一个或多个应用容器组成。



`Kubernetes`的网络模型要求其各`Pod`对象的`IP`地址位于同一网络平面内（同一`IP`网段），各`Pod`之间可使用其`IP`地址直接进行通信，无论它们运行于集群内的哪个工作节点上，这些`Pod`对象都像运行于同一局域网中的多个主机。



不过，`Pod`对象中的各进程均运行于彼此隔离的容器中，并于容器间共享两种关键资源：**网络**和**存储卷**。

- **网络**：每个`Pod`对象都会被分配一个集群内专用的`IP`地址，也称为`Pod IP`，同一`Pod`内部的所有容器共享`Pod`对象的`Network`和`UTS`名称空间，其中包括主机名、`IP`地址和端口等。因此，这些容器间的通信可以基于本地回环接口`lo`进行，而与`Pod`外的其他组件的通信则需要使用`Service`资源对象的`ClusterIP`及相应的端口完成。

- **存储卷**：用户可以为`Pod`对象配置一组“存储卷”资源，这些资源可以共享给其内部的所有容器使用，从而完成容器间数据的共享。存储卷还可以确保在容器终止后重启，甚至是被删除后也能确保数据不会丢失，从而保证了生命周期内的`Pod`对象数据的持久化存储。



一个`Pod`对象代表某个应用程序的一个特定实例，如果需要扩展应用程序，则意味着为此应用程序同时创建多个`Pod`实例，每个实例均代表应用程序的一个运行的“副本”(`replica`)。这些副本化的`Pod`对象的创建和管理通常由另一组称为“控制器”(`Controller`)的对象实现，例如，`Deployment`控制器对象。



创建`Pod`时，还可以使用`Pod Preset`对象为`Pod`注入特定的信息，如`ConfigMap`、`Secret`、存储卷、挂载卷和环境变量等。有了`Pod Preset`对象，`Pod`模板的创建者就无须为每个模板显示提供所有信息，因此，也就无须事先了解需要配置的每个应用的细节即可完成模板定义。



基于期望的目标状态和各节点的资源可用性，`Master`会将`Pod`对象调度至某选定的工作节点运行，工作节点于指向的镜像仓库（`image register`）下载镜像，并于本地的容器运行时环境中启动容器。`Master`会将整个集群的状态保存于`etcd`中，并通过`API Server`共享给集群的各组件及客户端。



## 1.2 Controller



`Kubernetes`集群的设计中，`Pod`是有生命周期的对象。通过手动创建或由`Controller`（控制器）直接创建的`Pod`对象会被“调度器”（`Scheduler`）调度至集群中的某工作节点运行，待到容器应用进程运行结束之后正常终止，随后就会被删除。另外，节点资源耗尽或故障也会导致`Pod`对象被回收。



但`Pod`对象本身并不具有“自愈”功能，若是因为工作节点甚至是调度器自身导致了运行失败，那么它将会被删除；同样，资源耗尽或节点故障导致的回收操作也会删除相关的`Pod`对象。在设计上，`Kubernetes`使用”控制器“实现对一次性的（用后即弃）`Pod`对象的管理操作，例如，要确保部署的应用程序的Pod副本数量严格反映用户期望的数目，以及基于`Pod`模板来创建`Pod`对象等，从而实现`Pod`对象的扩缩容、滚动更新和自愈能力等。例如，某节点发生故障时，相关的控制器会将此节点上运行的`Pod`对象重新调度到其他节点进行重建。



控制器本身也是一种资源类型，它有着多种实现，其中与工作负载相关的实现如`Replication Controller`、`Deployment`、`StatefulSet`、`DaemonSet`和`Jobs`等，也可统称它们为`Pod`控制器。



`Pod`控制器的定义通常由期望的副本数量、`Pod`模板和标签选择器（`Label Selector`）组成。`Pod`控制器会根据标签选择器对`Pod`对象的标签进行匹配检查，所有满足选择条件的`Pod`对象都将受控于当前控制器并计入其副本总数，并确保此数目能够精确反映期望的副本数。



## 1.3 Service



尽管`Pod`对象可以拥有`IP`地址，但此地址无法确保在`Pod`对象重启或被重建后保持不变，这会为集群中的`Pod`应用间依赖关系的维护带来麻烦：前端`Pod`应用（依赖方）无法基于固定地址持续跟踪后端`Pod`应用（被依赖方）。于是，`Service`资源被用于在被访问的`Pod`对象中添加一个有这固定`IP`地址的中间层，客户端向此地址发起访问请求后由相关的`Service`资源调度并代理至后端的`Pod`对象。



换言之，`Service`是“微服务”的一种实现，事实上它是一种抽象：通过规则定义出由多个`Pod`对象组合而成的逻辑集合，并附带访问这组`Pod`对象的策略。`Service`对象挑选、关联`Pod`对象的方式同`Pod`控制器一样，都是要基于`Label Selector`进行定义，其示意图如下

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591069933056-4463ef12-0c5c-4510-800f-122bda5e57f3.png)



`Service IP`是一种虚拟`IP`，也称为`Cluster IP`，它专用于集群内通信，通常使用专用的地址段，如`“10.96.0.0/12”`网络，各`Service`对象的`IP`地址在此范围内由系统动态分配。



集群内的`Pod`对象可直接请求此类的`Cluster IP`，例如，图中来自`Pod client`的访问请求即可以`Service`的`Cluster IP`作为目标地址，但集群网络属于私有网络地址，它们仅在集群内部可达。将集群外部的访问流量引入集群内部的常用方法是通过节点网络进行，实现方法是通过工作节点的`IP`地址和某端口（`NodePort`）接入请求并将其代理至相应的`Service`对象的`Cluster IP`上的服务端口，而后由`Service`对象将请求代理至后端的`Pod`对象的`Pod IP`及应用程序监听的端口。因此，图中的`External Clients`这种来自集群外部的客户端无法直接请求此`Service`提供的服务，而是需要事先经由某一个工作节点（如`NodeY`）的`IP`地址进行，这类请求需要两次转发才能到达目标`Pod`对象，因此在通信效率上必然存在负面影响。



事实上，`NodePort`会部署于集群中的每一个节点，这就意味着，集群外部的客户端通过任何一个工作节点的`IP`地址来访问定义好的`NodePort`都可以到达相应的`Service`对象。此种场景下，如果存在集群外部的一个负载均衡器，即可将用户请求负载均衡至集群中的部分或者所有节点。这是一种称为`“LoadBalancer”`类型的`Service`，它通常是由`Cloud Provider`自动创建并提供的软件负载均衡器，不过，也可以是有管理员手工配置的诸如`F5`一类的硬件设备。



简单来说，`Service`主要有三种常用类型：第一种是仅用于集群内部通信的`ClusterIP`类型；第二种是接入集群外部请求的`NodePort`类型，它工作与每个节点的主机`IP`之上；第三种是`LoadBalancer`类型，它可以把外部请求负载均衡至多个`Node`的主机`IP`的`NodePort`之上。此三种类型中，每一种都以其前一种为基础才能实现，而且第三种类型中的`LoadBalancer`需要协同集群外部的组件才能实现，并且此外部组件并不接受`Kubernetes`的管理。



# 2 命令式容器应用编排



## 2.1 部署应用Pod



在`Kubernetes`集群上自主运行的`Pod`对象在非计划内终止后，其生命周期即告结束，用户需要再次手动创建类似的`Pod`对象才能确保其容器中的依然可得。对于`Pod`数量众多的场景，尤其是对微服务业务来说，用户必将疲于应付此类需求。`Kubernetes`的工作负载（`workload`）类型的控制器能够自动确保由其管控的`Pod`对象按用户期望的方式运行，因此，Pod的创建和管理大多会通过这种类型的控制器来进行，包括`Deployment`、`ReplicasSet`、`ReplicationController`等。



### 1）创建Deployment控制器对象



`kubectl run`命令可用于命令行直接创建`Deploymen`t控制器，并以 `--image`选项指定的镜像运行`Pod`中的容器，`--dry-run`选项可以用于命令的测试，但并不真正执行资源对象的创建过程。

```perl
# 创建一个名字叫做nginx的deployment控制器，并指定pod镜像使用nginx:1.12版本，并暴露容器内的80端口，并指定副本数量为1个，并先通过--dry-run测试命令是否错误。
[root@k8s-master ~]# kubectl run nginx --image=nginx:1.12 --port=80 --replicas=1 --dry-run=true
[root@k8s-master ~]# kubectl run nginx --image=nginx:1.12 --port=80 --replicas=1
deployment.apps/nginx created

[root@k8s-master ~]# kubectl get pods    #查看所有pod对象
NAME                     READY   STATUS    RESTARTS   AGE
nginx-685cc95cd4-9z4f4   1/1     Running   0          89s


###参数说明：
--image      指定需要使用到的镜像。
--port       指定容器需要暴露的端口。
--replicas   指定目标控制器对象要自动创建Pod对象的副本数量。
```

### 2）打印资源对象的相关信息

kubectl get 命令可用来获取各种资源对象的相关信息，它既能显示对象类型特有格式的简要信息，也能按照指定格式为YAML或JSON的详细信息，或者使用Go模板自定义要显示的属性及信息等。

```perl

[root@k8s-master ~]# kubectl get deployment    #查看所有deployment控制器对象
NAME    READY   UP-TO-DATE   AVAILABLE   AGE
nginx   1/1     1            1           66s

###字段说明：
NAME    资源对象名称
READY   期望由当前控制器管理的Pod对象副本数及当前已有的Pod对象副本数
UP-TO-DATE   更新到最新版本定义的Pod对象的副本数量，在控制器的滚动更新模式下，表示已经完成版本更新的Pod对象的副本数量
AVAILABLE    当前处于可用状态的Pod对象的副本数量，即可正常提供服务的副本数。
AGE    Pod的存在时长

说明：Deployment资源对象通过ReplicaSet控制器实例完成对Pod对象的控制，而非直接控制。另外，通过控制器创建的Pod对象都会被自动附加一个标签。格式为“run=<Controller_Name>”。
[root@k8s-master ~]# kubectl get deployment -o wide    #查看deployment控制器对象的详细信息
NAME    READY   UP-TO-DATE   AVAILABLE   AGE   CONTAINERS   IMAGES       SELECTOR
nginx   1/1     1            1           69m   nginx        nginx:1.12   run=nginx


[root@k8s-master ~]# kubectl get pods     #查看pod资源
NAME                     READY   STATUS    RESTARTS   AGE
nginx-685cc95cd4-9z4f4   1/1     Running   0          72m

[root@k8s-master ~]# kubectl get pods -o wide
NAME                     READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
nginx-685cc95cd4-9z4f4   1/1     Running   0          73m   10.244.1.12   k8s-node1   <none>           <none>

###字段说明：
NAME       pode资源对象名称
READY      pod中容器进程初始化完成并能够正常提供服务时即为就绪状态，此字段用于记录处于就绪状态的容器数量
STATUS     pod的当前状态，其值有Pending、Running、Succeeded、Failed和Unknown等其中之一
RESTARTS   Pod重启的次数
IP         pod的IP地址，通常由网络插件自动分配
NODE       pod被分配的节点。
```

### **3）访问Pod对象**



这里部署的是`pod`是运行的为`nginx`程序，所以我们可以访问是否`ok`，在`kubernetes`集群中的任意一个节点上都可以直接访问`Pod`的`IP`地址。

```perl
[root@k8s-master ~]# kubectl get pods -o wide    #查看pod详细信息
NAME                     READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
nginx-685cc95cd4-9z4f4   1/1     Running   0          88m   10.244.1.12   k8s-node1   <none>           <none>

[root@k8s-master ~]# curl 10.244.1.12    #kubernetes集群的master节点上访问
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>


[root@k8s-node2 ~]# curl 10.244.1.12    #kubernetes集群的node节点上访问
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>
```

上面访问是基于一个`pod`的情况下，但是，当这个`pod`由于某种原因意外挂掉了，或者所在的节点挂掉了，那么`deployment`控制器会立即创建一个新的`pod`，这时候再去访问这个`IP`就访问不到了，而我们不可能每次去到节点上看到`IP`再进行访问。测试如下：

```perl
[root@k8s-master ~]# kubectl get pods -o wide
NAME                     READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
nginx-685cc95cd4-9z4f4   1/1     Running   0          99m   10.244.1.12   k8s-node1   <none>           <none>

[root@k8s-master ~]# kubectl delete pods nginx-685cc95cd4-9z4f4    #删除上面的pod
pod "nginx-685cc95cd4-9z4f4" deleted

[root@k8s-master ~]# kubectl get pods -o wide    #可以看出，当上面pod刚删除，接着deployment控制器又马上创建了一个新的pod，且这次分配在k8s-node2节点上了。
NAME                     READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
nginx-685cc95cd4-z5z9p   1/1     Running   0          89s   10.244.2.14   k8s-node2   <none>           <none>


[root@k8s-master ~]# curl 10.244.1.12    #访问之前的pod，可以看到已经不能访问
curl: (7) Failed connect to 10.244.1.12:80; 没有到主机的路由
[root@k8s-master ~]# 
[root@k8s-master ~]# curl 10.244.2.14    #访问新的pod，可以正常访问
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>
```

## 2.2 部署Service对象



简单来说，一个`Service`对象可视作通过其标签选择器过滤出的一组`Pod`对象，并能够为此组`Pod`对象监听的套接字提供端口代理及调度服务。就好比上面做的测试，如果没有`Service`，那么每次都得去访问`pod`对象自己的地址等。且那还只是创建了一个`pod`对象，如果是多个。那么该如何是好？故使用`Service`解决此问题。



**1）创建Service对象(将Service端口代理至Pod端口示例)**



`"kubectl expose"`命令可用于创建`Service`对象以将应用程序“暴露”（`expose`）于网络中。

```perl
#方法一
[root@k8s-master ~]# kubectl expose deployment nginx --name=nginx-svc --port=80 --target-port=80 --protocol=TCP    #为deployment的nginx创建service，取名叫nginx-svc，并通过service的80端口转发至容器的80端口上。
service/nginx-svc exposed
#方法二
[root@k8s-master ~]# kubectl expose deployment/nginx --name=nginx-svc --port=80 --target-port=80 --protocol=TCP
service/nginx-svc exposed

###参数说明：
--name    指定service对象的名称
--port    指定service对象的端口
--target-port    指定pod对象容器的端口
--protocol    指定协议

[root@k8s-master ~]# kubectl get svc     #查看service对象。或者kubectl get service
NAME         TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)   AGE
kubernetes   ClusterIP   10.96.0.1       <none>        443/TCP   25h
nginx-svc    ClusterIP   10.109.54.136   <none>        80/TCP    41s
```

这时候可以在`kubernetes`集群上所有节点上直接访问`nginx-svc`的`cluster-ip`及可访问到名为`deployment`控制器下`nginx`的`pod`。并且，集群中的别的新建的`pod`都可以直接访问这个`IP`或者这个`service`名称即可访问到名为`deployment`控制器下`nginx`的`pod`。示例：

```perl
# master节点上通过ServiceIP进行访问
[root@k8s-master ~]# curl 10.109.54.136 
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>


#新建一个客户端pod进行访问，这里这个客户端使用busybox镜像，且pod副本数量为1个，-it表示进入终端模式。--restart=Never，表示从不重启。
[root@k8s-master ~]# kubectl run client --image=busybox --replicas=1 -it --restart=Never
If you don't see a command prompt, try pressing enter.
/ # wget -O - -q 10.109.54.136    #访问上面创建的（service）nginx-svc的IP
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
......
/ # 
/ # wget -O - -q nginx-svc    #访问上面创建的（service）名称nginx-svc
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
```

### **2）创建Service对象(将创建的Pod对象使用“NodePort”类型的服务暴露到集群外部)**

```perl
[root@k8s-master ~]# kubectl run mynginx --image=nginx:1.12 --port=80 --replicas=2    #创建一个deployments控制器并使用nginx镜像作为容器运行的应用。
[root@k8s-master ~]# kubectl get pods    #查看创建的pod
NAME                     READY   STATUS    RESTARTS   AGE
client                   1/1     Running   0          15h
mynginx-68676f64-28fm7   1/1     Running   0          24s
mynginx-68676f64-9q8dj   1/1     Running   0          24s
nginx-685cc95cd4-z5z9p   1/1     Running   0          16h
[root@k8s-master ~]# 
[root@k8s-master ~]# kubectl expose deployments/mynginx --type="NodePort" --port=80 --name=mynginx-svc    #创建一个service对象，并将mynginx创建的pod对象使用NodePort类型暴露到集群外部。
service/mynginx-svc exposed
[root@k8s-master ~]# 
[root@k8s-master ~]# kubectl get svc    #查看service
NAME          TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)        AGE
kubernetes    ClusterIP   10.96.0.1       <none>        443/TCP        41h
mynginx-svc   NodePort    10.111.89.58    <none>        80:30884/TCP   10s
nginx-svc     ClusterIP   10.109.54.136   <none>        80/TCP         15h

###字段说明：
PORT(S)     这里的mynginx-svc对象可以看出，集群中各工作节点会捕获发往本地的目标端口为30884的流量，并将其代理至当前service对象的80端口。于是集群外部的用户可以使用当前集群中任一节点的此端口来请求Service对象上的服务。

[root@k8s-master ~]# 
[root@k8s-master ~]# netstat -nlutp |grep 30884    #查看master节点上是否有监听上面的30884端口
tcp6       0      0 :::30884                :::*                    LISTEN      7340/kube-proxy
[root@k8s-node1 ~]# 
[root@k8s-node1 ~]# netstat -nlutp |grep 30884    #查看node节点是否有监听上面的30884端口
tcp6       0      0 :::30884                :::*                    LISTEN      2537/kube-proxy
```

客户端访问`kubernetes`集群的`30884`端口

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591070035455-112322a1-4777-41e4-b62d-ac94a3b28bc7.png)

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591070040518-d0106591-f429-4d76-8215-f0ff72022a2a.png)



### **3）Service资源对象的描述**

`“kuberctl describe services”`命令用于打印`Service`对象的详细信息，它通常包括`Service`对象的`Cluster IP`，关联`Pod`对象使用的标签选择器及关联到的`Pod`资源的端点等。示例

```perl
[root@k8s-master ~]# kubectl describe service mynginx-svc
Name:                     mynginx-svc
Namespace:                default
Labels:                   run=mynginx
Annotations:              <none>
Selector:                 run=mynginx
Type:                     NodePort
IP:                       10.111.89.58
Port:                     <unset>  80/TCP
TargetPort:               80/TCP
NodePort:                 <unset>  30884/TCP
Endpoints:                10.244.1.14:80,10.244.2.15:80
Session Affinity:         None
External Traffic Policy:  Cluster
Events:                   <none>

###字段说明：
Selector      当前Service对象使用的标签选择器，用于选择关联的Pod对象
Type          即Service的类型，其值可以是ClusterIP、NodePort和LoadBalancer等其中之一
IP            当前Service对象的ClusterIP
Port          暴露的端口，即当前Service用于接收并响应的端口
TargetPort    容器中的用于暴露的目标端口，由Service Port路由请求至此端口
NodePort      当前Service的NodePort，它是否存在有效值与Type字段中的类型相关
Endpoints     后端端点，即被当前Service的Selector挑中的所有Pod的IP及其端口
Session Affinity    是否启用会话粘性
External Traffic Policy    外部流量的调度策略
```

## 2.3 扩容和缩容



所谓的“伸缩（`Scaling`）”就是指改变特定控制器上`Pod`副本数量的操作，“扩容（`scaling up`）”即为增加副本数量，而“缩容（`scaling down`）"则指缩减副本数量。不过，不论是扩容还是缩容，其数量都需要由用户明确给出。



`Service`对象内建的负载均衡机制可在其后端副本数量不止一个时自动进行流量分发，它还会自动监控关联到的`Pod`的健康状态，以确保将请求流量分发至可用的后端`Pod`对象。若某`Deployment`控制器管理包含多个`Pod`实例，则必要时用户还可以为其使用“滚动更新”机制将其容器镜像升级到新的版本或变更那些支持动态修改的`Pod`属性。



使用`kubect run`命令创建`Deployment`对象时，`“--replicas=”`选项能够指定由该对象创建或管理的`Pod`对象副本的数量，且其数量支持运行时进行修改，并立即生效。`“kubectl scale”`命令就是专用于变动控制器应用规模的命令，它支持对`Deployment`资源对象的扩容和缩容操作。



上面示例中创建的`Deployment`对象`nginx`仅创建了一个`Pod`对象，其所能够承载的访问请求数量即受限于这单个`Pod`对象的服务容量。请求流量上升到接近或超出其容量之前，可以通过`kubernetes`的“扩容机制”来扩招`Pod`的副本数量，从而提升其服务容量。



### **扩容示例**

```perl
[root@k8s-master ~]# kubectl get pods -l run=nginx    #查看标签run=nginx的pod
NAME                     READY   STATUS    RESTARTS   AGE
nginx-685cc95cd4-z5z9p   1/1     Running   0          17h
[root@k8s-master ~]# 
[root@k8s-master ~]# kubectl scale deployments/nginx --replicas=3    #将其扩容到3个
deployment.extensions/nginx scaled
[root@k8s-master ~]# 
[root@k8s-master ~]# kubectl get pods -l run=nginx    #再次查看
NAME                     READY   STATUS    RESTARTS   AGE
nginx-685cc95cd4-f2cwb   1/1     Running   0          5s
nginx-685cc95cd4-pz9dk   1/1     Running   0          5s
nginx-685cc95cd4-z5z9p   1/1     Running   0          17h
[root@k8s-master ~]# 
[root@k8s-master ~]# kubectl describe deployments/nginx    #查看Deployment对象nginx详细信息
Name:                   nginx
Namespace:              default
CreationTimestamp:      Thu, 29 Aug 2019 15:29:31 +0800
Labels:                 run=nginx
Annotations:            deployment.kubernetes.io/revision: 1
Selector:               run=nginx
Replicas:               3 desired | 3 updated | 3 total | 3 available | 0 unavailable
StrategyType:           RollingUpdate
...

#由nginx自动创建的pod资源全部拥有同一个标签选择器“run=nginx”，因此，前面创建的Service资源对象nginx-svc的后端端点也已经通过标签选择器自动扩展到了这3个Pod对象相关的端点
[root@k8s-master ~]# kubectl describe service/nginx-svc
Name:              nginx-svc
Namespace:         default
Labels:            run=nginx
Annotations:       <none>
Selector:          run=nginx
Type:              ClusterIP
IP:                10.109.54.136
Port:              <unset>  80/TCP
TargetPort:        80/TCP
Endpoints:         10.244.1.15:80,10.244.2.14:80,10.244.2.16:80
Session Affinity:  None
Events:            <none>
```

### **缩容示例**



缩容的方式和扩容相似，只不过是将`Pod`副本的数量调至比原来小的数字即可。例如将`nginx`的`pod`副本缩减至2个

```perl
[root@k8s-master ~]# kubectl scale deployments/nginx --replicas=2
deployment.extensions/nginx scaled
[root@k8s-master ~]# 
[root@k8s-master ~]# kubectl get pods -l run=nginx
NAME                     READY   STATUS    RESTARTS   AGE
nginx-685cc95cd4-pz9dk   1/1     Running   0          10m
nginx-685cc95cd4-z5z9p   1/1     Running   0          17h
```

## 2.4 删除对象



有一些不再有价值的活动对象可使用`“kubectl delete”`命令予以删除，需要删除`Service`对象`nginx-svc`时，即可使用下面命令完成：

```perl
[root@k8s-master ~]# kubectl get services     #查看当前所有的service对象
NAME          TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)        AGE
kubernetes    ClusterIP   10.96.0.1       <none>        443/TCP        43h
mynginx-svc   NodePort    10.111.89.58    <none>        80:30884/TCP   96m
nginx-svc     ClusterIP   10.109.54.136   <none>        80/TCP         17h
[root@k8s-master ~]# kubectl delete service nginx-svc    #删除service对象nginx-svc
```

有时候要清空某一类型下的所有对象，只需要将上面的命令对象的名称缓存`“--all”`选项便能实现。例如，删除默认名称空间中所有的`Deployment`控制器的命令如下：

```perl
[root@k8s-master ~]# kubectl delete deployment --all
deployment.extensions "mynginx" deleted
```

**注意**：受控于控制器的`Pod`对象在删除后会被重建，删除此类对象需要直接删除其控制器对象。不过，删除控制器时若不想删除其`Pod`对象，可在删除命令上使用`“--cascade=false“`选项。



虽然直接命令式管理的相关功能强大且适合用于操纵`Kubernetes`资源对象，但其明显的缺点是缺乏操作行为以及待运行对象的可信源。另外，直接命令式管理资源对象存在较大的局限性，它们在设置资源对象属性方面提供的配置能力相当有限，而且还有不少资源并不支持命令操作进行创建，例如，用户无法创建带有多个容器的`Pod`对象，也无法为`Pod`对象创建存储卷。因此，管理资源对象更有效的方式是基于保存有对象配置信息的配置清单来进行。