# **07-kubernetes服务发现**

# 1 Service 概述

## 1.1 为什么要使用Service



`Kubernetes Pod`是平凡的，由`Deployment`等控制器管理的`Pod`对象都是有生命周期的，它们会被创建，也会意外挂掉。虽然它们可以由控制器自动重建或者滚动更新，但是重建或更新之后的`Pod`对象的IP地址等都会发生新的变化。这样就会导致一个问题，如果一组`Pod`（称为`backend`）为其它`Pod`（称为 `frontend`）提供服务，那么那些`frontend`该如何发现，并连接到这组`Pod`中的哪些`backend`呢？  这时候就用到了：`Service`

**示例说明为什么要使用Service**



如下图所示，当`Nginx Pod`作为客户端访问`Tomcat Pod`中的应用时，`IP`的变动或应用规模的缩减会导致客户端访问错误。而`Pod`规模的扩容又会使得客户端无法有效的使用新增的`Pod`对象，从而影响达成规模扩展之目的。为此，`Kubernetes`特地设计了`Service`资源来解决此类问题。

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591235734474-3618c915-3548-4650-a6d5-59b78fdde1a6.png)



## 1.2 Service实现原理



`Service`资源基于标签选择器将一组`Pod`定义成一个逻辑组合，并通过自己的`IP`地址和端口调度代理请求至组内的`Pod`对象之上，如下图所示，它向客户端隐藏了真实的、处理用户请求的`Pod`资源，使得客户端的请求看上去就像是由`Service`直接处理并响应一样。

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591235753204-dcee3cd7-24da-440e-a9fb-c4020a4130f1.png)



`Service`对象的`IP`地址也称为`Cluster IP`，它位于`Kubernetes`集群配置指定专用`IP`地址的范围之内，是一种虚拟`IP`地址，它在`Service`对象创建后既保持不变，并且能够被同一集群中的`Pod`资源所访问。



`Service`端口用于接收客户端请求并将其转发至其后端的`Pod`中的相应端口之上，因此，这种代理机构也称为“端口代理”(`port proxy`)或四层代理，工作于`TCP/IP`协议栈的传输层。



`Service`资源会通过`API Server`持续监视着（`watch`）标签选择器匹配到的后端`Pod`对象，并实时跟踪各对象的变动，例如，`IP`地址变动、对象增加或减少等。`Service`并不直接链接至`Pod`对象，它们之间还有一个中间层——`Endpoints`资源对象，它是一个由`IP`地址和端口组成的列表，这些`IP`地址和端口则来自由`Service`的标签选择器匹配到的`Pod`资源。当创建`service`对象时，其关联的`Endpoints`对象会自动创建。



## 1.3 虚拟IP和服务代理



一个`Service`对象就是工作节点上的一些`iptables`或`ipvs`规则，用于将到达`Service`对象`IP`地址的流量调度转发至相应的`Endpoints`对象指定的`IP`地址和端口之上。`kube-proxy`组件通过`API Server`持续监控着各`Service`及其关联的`Pod`对象，并将其创建或变动实时反映到当前工作节点上的`iptables`规则或`ipvs`规则上。



`ipvs`是借助于`Netfilter`实现的网络请求报文调度框架，支持`rr`、`wrr`、`lc`、`wlc`、`sh`、`sed`和`nq`等十余种调度算法，用户空间的命令行工具是`ipvsadm`，用于管理工作与`ipvs`之上的调度规则。



`Service IP`事实上是用于生成`iptables`或`ipvs`规则时使用的`IP`地址，仅用于实现`Kubernetes`集群网络的内部通信，并且能够将规则中定义的转发服务的请求作为目标地址予以相应，这也是将其称为虚拟`IP`的原因之一。



`kube-proxy`将请求代理至相应端点的方式有三种：**userspace（用户空间）**、**iptables**和**ipvs**。



### 1.3.1 userspace代理模式



`userspace`是`Linux`操作系统的用户空间。这种模式下，`kube-proxy`负责跟踪`API Server`上`Service`和`Endpoints`对象的变动（创建或移除），并据此调整`Service`资源的定义。对于每个`Service`对象，它会随机打开一个本地端口（运行于用户控件的`kube-proxy`进程负责监听），任何到达此端口的连接请求都将代理至当前`Service`资源后端的各`Pod`对象上，至于会挑选中哪个`Pod`对象则取决于当前`Service`资源的调度方式（通过`Service`的`SessionAffinity`来确定），默认的调度算法是轮循（`round-robin`）。



其代理的过程是：请求到达`service`后，其被转发至内核空间，经由套接字送往用户空间的`kube-proxy`，而后再由它送回内核空间，并调度至后端`Pod`。其传输效率太低，在`1.1`版本前是默认的转发策略。

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591235881149-87a1067e-6a12-403b-902c-18144f397460.png)



### 1.3.2 iptables代理模式



`iptables`代理模式中，`kube-proxy`负责跟踪`API Server`上`Service`和`Endpoints`对象的变动（创建或移除），并据此作出`Service`资源定义的变动。同时，对于每个`Service`对象，它都会创建`iptables`规则直接捕获到达`Cluster IP`（虚拟IP）和`Port`的流量，并将其重定向至当前`Service`的后端。对于每个`Endpoints`对象，`Service`资源会为其创建`iptables`规则并关联至挑选的后端`Pod`资源，默认的调度算法是随机调度（`random`）。实现基于客户端`IP`的会话亲和性（来自同一个用户的请求始终调度到后端固定的一个`Pod`），可将`service.spec.sessionAffinity`的值设置为`“ClientIP”`（默认值为`“None”`）。



其代理过程是：请求到达`service`后，其请求被相关`service`上的`iptables`规则进行调度和目标地址转换（`DNAT`）后再转发至集群内的`Pod`对象之上。



相对`userspace`模式来说，`iptables`模式无须将流量在用户空间和内核空间来回切换，因而更加高效和可靠。其缺点是`iptables`代理模型不会在被挑中的`Pod`资源无响应时自动进行重定向；而`userspace`模式则可以。

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591235914956-5ff2731e-ac9d-4302-934f-1e8c69b6193d.png)



### 1.3.3 ipvs代理模式



`kube-proxy`跟踪`API Server`上`Service`的`Endpoints`对象的变动，据此来调用`netlink`接口创建`ipvs`规则，并确保与`API Server`中的变动保持同步，其请求流量的调度功能由`ipvs`实现，其余的功能由`iptables`实现。`ipvs`支持众多调度算法，如`rr`、`lc`、`dh`、`sh`、`sed`和`nq`等。

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591235928942-42884e42-0c7e-4637-af11-756acefad41d.png)



# 2 Service资源的基础应用



创建`Service`对象的常用方法有两种，一是直接使用命令`“kubectl expose”`命令，二是使用资源清单配置文件。定义`Service`资源清单文件时，`spec`的两个较为常用的内嵌字段分别是`selector`和`port`，分别用于定义使用的标签选择器和要暴露的端口。



**一、命令方式定义**



1）首先创建一组`pod`资源

# 服务发现-Service

# 1 Service 概述

## 1.1 为什么要使用Service



```
Kubernetes Pod`是平凡的，由`Deployment`等控制器管理的`Pod`对象都是有生命周期的，它们会被创建，也会意外挂掉。虽然它们可以由控制器自动重建或者滚动更新，但是重建或更新之后的`Pod`对象的IP地址等都会发生新的变化。这样就会导致一个问题，如果一组`Pod`（称为`backend`）为其它`Pod`（称为 `frontend`）提供服务，那么那些`frontend`该如何发现，并连接到这组`Pod`中的哪些`backend`呢？  这时候就用到了：`Service
```



**示例说明为什么要使用Service**



如下图所示，当`Nginx Pod`作为客户端访问`Tomcat Pod`中的应用时，`IP`的变动或应用规模的缩减会导致客户端访问错误。而`Pod`规模的扩容又会使得客户端无法有效的使用新增的`Pod`对象，从而影响达成规模扩展之目的。为此，`Kubernetes`特地设计了`Service`资源来解决此类问题。

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591235734474-3618c915-3548-4650-a6d5-59b78fdde1a6.png)



## 1.2 Service实现原理



`Service`资源基于标签选择器将一组`Pod`定义成一个逻辑组合，并通过自己的`IP`地址和端口调度代理请求至组内的`Pod`对象之上，如下图所示，它向客户端隐藏了真实的、处理用户请求的`Pod`资源，使得客户端的请求看上去就像是由`Service`直接处理并响应一样。

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591235753204-dcee3cd7-24da-440e-a9fb-c4020a4130f1.png)



`Service`对象的`IP`地址也称为`Cluster IP`，它位于`Kubernetes`集群配置指定专用`IP`地址的范围之内，是一种虚拟`IP`地址，它在`Service`对象创建后既保持不变，并且能够被同一集群中的`Pod`资源所访问。



`Service`端口用于接收客户端请求并将其转发至其后端的`Pod`中的相应端口之上，因此，这种代理机构也称为“端口代理”(`port proxy`)或四层代理，工作于`TCP/IP`协议栈的传输层。



`Service`资源会通过`API Server`持续监视着（`watch`）标签选择器匹配到的后端`Pod`对象，并实时跟踪各对象的变动，例如，`IP`地址变动、对象增加或减少等。`Service`并不直接链接至`Pod`对象，它们之间还有一个中间层——`Endpoints`资源对象，它是一个由`IP`地址和端口组成的列表，这些`IP`地址和端口则来自由`Service`的标签选择器匹配到的`Pod`资源。当创建`service`对象时，其关联的`Endpoints`对象会自动创建。



## 1.3 虚拟IP和服务代理



一个`Service`对象就是工作节点上的一些`iptables`或`ipvs`规则，用于将到达`Service`对象`IP`地址的流量调度转发至相应的`Endpoints`对象指定的`IP`地址和端口之上。`kube-proxy`组件通过`API Server`持续监控着各`Service`及其关联的`Pod`对象，并将其创建或变动实时反映到当前工作节点上的`iptables`规则或`ipvs`规则上。



`ipvs`是借助于`Netfilter`实现的网络请求报文调度框架，支持`rr`、`wrr`、`lc`、`wlc`、`sh`、`sed`和`nq`等十余种调度算法，用户空间的命令行工具是`ipvsadm`，用于管理工作与`ipvs`之上的调度规则。



`Service IP`事实上是用于生成`iptables`或`ipvs`规则时使用的`IP`地址，仅用于实现`Kubernetes`集群网络的内部通信，并且能够将规则中定义的转发服务的请求作为目标地址予以相应，这也是将其称为虚拟`IP`的原因之一。



`kube-proxy`将请求代理至相应端点的方式有三种：**userspace（用户空间）**、**iptables**和**ipvs**。



### 1.3.1 userspace代理模式



`userspace`是`Linux`操作系统的用户空间。这种模式下，`kube-proxy`负责跟踪`API Server`上`Service`和`Endpoints`对象的变动（创建或移除），并据此调整`Service`资源的定义。对于每个`Service`对象，它会随机打开一个本地端口（运行于用户控件的`kube-proxy`进程负责监听），任何到达此端口的连接请求都将代理至当前`Service`资源后端的各`Pod`对象上，至于会挑选中哪个`Pod`对象则取决于当前`Service`资源的调度方式（通过`Service`的`SessionAffinity`来确定），默认的调度算法是轮循（`round-robin`）。



其代理的过程是：请求到达`service`后，其被转发至内核空间，经由套接字送往用户空间的`kube-proxy`，而后再由它送回内核空间，并调度至后端`Pod`。其传输效率太低，在`1.1`版本前是默认的转发策略。

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591235881149-87a1067e-6a12-403b-902c-18144f397460.png)



### 1.3.2 iptables代理模式



`iptables`代理模式中，`kube-proxy`负责跟踪`API Server`上`Service`和`Endpoints`对象的变动（创建或移除），并据此作出`Service`资源定义的变动。同时，对于每个`Service`对象，它都会创建`iptables`规则直接捕获到达`Cluster IP`（虚拟IP）和`Port`的流量，并将其重定向至当前`Service`的后端。对于每个`Endpoints`对象，`Service`资源会为其创建`iptables`规则并关联至挑选的后端`Pod`资源，默认的调度算法是随机调度（`random`）。实现基于客户端`IP`的会话亲和性（来自同一个用户的请求始终调度到后端固定的一个`Pod`），可将`service.spec.sessionAffinity`的值设置为`“ClientIP”`（默认值为`“None”`）。



其代理过程是：请求到达`service`后，其请求被相关`service`上的`iptables`规则进行调度和目标地址转换（`DNAT`）后再转发至集群内的`Pod`对象之上。



相对`userspace`模式来说，`iptables`模式无须将流量在用户空间和内核空间来回切换，因而更加高效和可靠。其缺点是`iptables`代理模型不会在被挑中的`Pod`资源无响应时自动进行重定向；而`userspace`模式则可以。

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591235914956-5ff2731e-ac9d-4302-934f-1e8c69b6193d.png)



### 1.3.3 ipvs代理模式



`kube-proxy`跟踪`API Server`上`Service`的`Endpoints`对象的变动，据此来调用`netlink`接口创建`ipvs`规则，并确保与`API Server`中的变动保持同步，其请求流量的调度功能由`ipvs`实现，其余的功能由`iptables`实现。`ipvs`支持众多调度算法，如`rr`、`lc`、`dh`、`sh`、`sed`和`nq`等。

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591235928942-42884e42-0c7e-4637-af11-756acefad41d.png)



# 2 Service资源的基础应用



创建`Service`对象的常用方法有两种，一是直接使用命令`“kubectl expose”`命令，二是使用资源清单配置文件。定义`Service`资源清单文件时，`spec`的两个较为常用的内嵌字段分别是`selector`和`port`，分别用于定义使用的标签选择器和要暴露的端口。



### **一、命令方式定义**



#### 1）首先创建一组`pod`资源

```perl
[root@k8s-master ~]# kubectl run nginx --image=nginx:1.12 --replicas=3    #创建pod资源指定副本数量为3个
[root@k8s-master ~]# kubectl get pods -o wide
NAME                     READY   STATUS    RESTARTS   AGE     IP            NODE        NOMINATED NODE   READINESS GATES
nginx-67685f79b5-688s7   1/1     Running   0          5s   10.244.2.61   k8s-node2   <none>           <none>
nginx-67685f79b5-gpc2f   1/1     Running   0          5s   10.244.1.63   k8s-node1   <none>           <none>
nginx-67685f79b5-grlrz   1/1     Running   0          5s   10.244.2.60   k8s-node2   <none>           <none>
[root@k8s-master ~]# kubectl get deployment    #查看deployment控制器资源
NAME    READY   UP-TO-DATE   AVAILABLE   AGE
nginx   3/3     3            3           35s
```

#### 2）为其创建对应的`service`资源

```perl
#下面这条命令表示为deployment控制器资源nginx创建一个service对象，并取名为nginx、端口为80、pod内暴露端口80、协议为TCP协议。
[root@k8s-master ~]# kubectl expose deployment nginx --name=nginx --port=80 --target-port=80 --protocol=TCP
service/nginx exposed
[root@k8s-master ~]# kubectl get service    #查看创建的service资源
NAME         TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)   AGE
kubernetes   ClusterIP   10.96.0.1        <none>        443/TCP   27d
nginx        ClusterIP   10.104.116.156   <none>        80/TCP    9s
```

#### 3）查看生成的`endpoints`对应关系

```perl
[root@k8s-master ~]# kubectl get endpoints
NAME         ENDPOINTS                                      AGE
kubernetes   192.168.1.31:6443                              27d
nginx        10.244.1.63:80,10.244.2.60:80,10.244.2.61:80   29s
```

### 二、资源清单定义

#### 1）编写资源清单文件

**(这里先定义一个Deployment控制器资源对象，然后定义Service进行关联)。注：同一个文件编写多个资源对象时，通过---进行分割。**

```yaml
[root@k8s-master ~]# vim manfests/service-demo.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: service-deploy
  namespace: default
spec:
  replicas: 3
  selector:
    matchLabels:
      app: service-deploy-demo
  template:
    metadata:
      name: svc-deploy
      labels:
        app: service-deploy-demo
    spec:
      containers:
      - name: svc-pod
        image: ikubernetes/myapp:v1
        imagePullPolicy: IfNotPresent
        ports: 
        - name: http
          containerPort: 80

---
#定义service
apiVersion: v1
kind: Service
metadata:
  name: service-demo    #service名称
spec:
  selector:    #用于匹配后端的Pod资源对象，需和上面定义pod的标签一致
    app: service-deploy-demo
  ports:
  - port: 80    #service端口号
    targetPort: 80    #后端Pod端口号
    protocol: TCP    #使用的协议
```

#### 2）创建并查看

```shell
[root@k8s-master ~]# kubectl apply -f manfests/service-demo.yaml    #创建资源对象
deployment.apps/service-deploy created
service/service-demo created
[root@k8s-master ~]# kubectl get svc    #查看service资源对象，"kubectl get svc"等于"kubectl get service"
NAME           TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)   AGE
kubernetes     ClusterIP   10.96.0.1        <none>        443/TCP   27d
nginx          ClusterIP   10.104.116.156   <none>        80/TCP    80m
service-demo   ClusterIP   10.98.31.157     <none>        80/TCP    7s
[root@k8s-master ~]# kubectl get pods -o wide -l app=service-deploy-demo    #查看pod资源对象
NAME                              READY   STATUS    RESTARTS   AGE     IP            NODE        NOMINATED NODE   READINESS GATES
service-deploy-66548cc57f-982cd   1/1     Running   0          15s   10.244.2.63   k8s-node2   <none>           <none>
service-deploy-66548cc57f-blnvg   1/1     Running   0          15s   10.244.1.67   k8s-node1   <none>           <none>
service-deploy-66548cc57f-vcmxb   1/1     Running   0          15s   10.244.2.62   k8s-node2   <none>           <none>
[root@k8s-master ~]# kubectl get endpoints service-demo    查看生成的endpoints对应关系
NAME           ENDPOINTS                                      AGE
service-demo   10.244.1.67:80,10.244.2.62:80,10.244.2.63:80   43s
```

### 3）节点访问测试

 **（这里使用创建一个新的pod资源模拟客户端进行访问）**

`Service`资源的默认类型为`ClusterIP`，仅能接收`kubernetes`集群节点访问、或集群内部的`pod`对象中的客户端程序访问。

```perl

[root@k8s-master ~]# kubectl run busybox --image=busybox --rm -it -- /bin/sh    #使用busybox创建一个临时pod客户端
/ # wget -O - -q http://10.98.31.157/    #访问上面创建的service对象的Cluster IP
Hello MyApp | Version: v1 | <a href="hostname.html">Pod Name</a>
/ # for i in 1 2 3 4; do wget -O - -q http://10.98.31.157/hostname.html; done    #循环访问测试站点下的hostname.html页面，可以看出是轮循的分配给后端的pod资源。
service-deploy-66548cc57f-982cd
service-deploy-66548cc57f-blnvg
service-deploy-66548cc57f-982cd
service-deploy-66548cc57f-982cd

#说明：myapp容器中的“/hostname.html"页面能够输出当前容器的主机名。
```

## 2.1 Service会话粘性



`Service`资源支持`Session affinity`（粘性会话或会话粘性）机制，能够将来自同一个客户端的请求始终转发至同一个后端的`Pod`对象。这意味着会影响调度算法的流量分发功能，进而降低其负载均衡的效果。所以，当客户端访问`pod`中的应用程序时，如果有基于客户端身份保存某些私有信息，并基于这些私有信息追踪用户的活动等一类的需求时，就可以启用`session affinity`机制。



`Session affinity`的效果仅在一段时间期限内生效，默认值为`10800`秒，超出此时长之后，客户端的再次访问会被调度算法重新调度。`Service`资源的`Session affinity`机制仅能基于客户端的`IP`地址识别客户端身份，把经由同一个`NAT`服务器进行源地址转换的所有客户端识别为同一个客户端，便导致调度效果不佳，所以，这种方法并不常用。



`Service`资源通过`service.spec.sessionAffinity`和`service.spec.sessionAffinityConfig`两个字段配置粘性会话。`sessionAffinity`字段用于定义要使用的粘性会话的类型，仅支持使用`“None”`和`“ClientIp”`两种属性值。

- None：不使用`sessionAffinity`，默认值。

- ClientIP：基于客户端`IP`地址识别客户端身份，把来自同一个源`IP`地址的请求始终调度至同一个`Pod`对象。



**示例**



这里将上面创建的`service-demo`资源对象进行修改

```yaml
[root@k8s-master ~]# vim manfests/service-demo.yaml
......
spec:
  selector:
    app: service-deploy-demo
  ports:
  - port: 80
    targetPort: 80
    protocol: TCP
  sessionAffinity: ClientIP    #指定使用ClientIP
  sessionAffinityConfig:
    clientIP:
      timeoutSeconds: 10    #配置session超时时间，这里为了看效果设置的比较短
[root@k8s-master ~]# kubectl apply -f manfests/service-demo.yaml 
deployment.apps/service-deploy unchanged

#同样使用pod客户端访问测试
/ # for i in 1 2 3 4; do wget -O - -q http://10.98.31.157/hostname.html; done
service-deploy-66548cc57f-blnvg
service-deploy-66548cc57f-blnvg
service-deploy-66548cc57f-blnvg
service-deploy-66548cc57f-blnvg
#等待10秒过后再次访问
/ # for i in 1 2 3 4; do wget -O - -q http://10.98.31.157/hostname.html; done
service-deploy-66548cc57f-vcmxb
service-deploy-66548cc57f-vcmxb
service-deploy-66548cc57f-vcmxb
service-deploy-66548cc57f-vcmxb
```

## 2.2 类型



`Service`的`IP`地址只能够在集群内部可访问，对一些应用（如`frontend`）的某些部分，可能希望通过外部（`kubernetes`集群外部）`IP`地址暴露`Service`，这时候就需要使用到`NodePort`。`kubernetes`

`ServiceTypes`支持四种类型：`ClusterIP`、`NodePort`、`LoadBalancer`、`ExternalName`，其默认是`Cluster IP`类型。



- **ClusterIP**：通过集群内部`IP`地址暴露服务，此地址仅在集群内部可进行通行，无法被集群外部的客户端访问。

- **NodePort**：通过每个`Node`上的`IP`和静态端口（`NodePort`）暴露服务，会自动为`Service`分配集群`IP`地址，并将此作为`NodePort`的路有目标。通过请求`: --> : --> :`访问到一个`NodePort`服务。

- **LoadBalancer**：构建在`NodePort`之上，并创建一个外部负载均衡器，路由到`ClusterIP`。因此`LoadBalancer`一样具有`NodePort`和`ClusterIP`。

- **EXternalName**：通过返回`CNAME`和它的值，可以将服务映射到`externalName`字段的内容。换言之，此种类型并非定义由`Kubernetes`集群提供的服务，而是把集群外部的某服务以`DNS CNAME`记录的方式映射到集群内，从而让集群内的`Pod`资源能够访问外部的`Service`的一种实现方式。这种类型的`Service`没有`ClusterIP`和`NodePort`，也没有标签选择器用于选择`Pod`资源，因此也不会有`Endpoints`存在。



### 2.2.1 ClusterIP类型的Service示例



#### 1）编写配置清单文件

（这里使用`redis`作为示例）；先创建一个`deployment`，启动`redis pod`；再使用`service`绑定这个`deployment`下的`pod`资源。

```yaml
[root@k8s-master ~]# vim manfests/redis-svc.yaml    #编写yaml格式的清单文件
apiVersion: apps/v1
kind: Deployment
metadata:
  name: redis-deploy
  namespace: default
spec:
  replicas: 2
  selector:
    matchLabels:
      app: redis
  template:
    metadata:
      labels:
        app: redis
    spec:
      containers:
      - name: redis-pod
        image: redis
        ports:
        - name: redis
          containerPort: 6379
---
apiVersion: v1
kind: Service
metadata:
  name: redis-svc    #service对象名
spec:
  type: ClusterIP    #这里指定使用ClusterIP，默认也是ClusterIP，这里可有可无
  selector:
    app: redis    #匹配上面定义的pod资源
  ports:
  - port: 6379    #service端口
    targetPort: 6379    #后端pod端口
    protocol: TCP    #协议
    
[root@k8s-master ~]# kubectl apply -f manfests/redis-svc.yaml     #创建资源对象
deployment.apps/redis-deploy created
service/redis-svc created
```

#### 2）查看创建的资源对象

```perl
[root@k8s-master ~]# kubectl get svc    #查看service资源
NAME           TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)    AGE
kubernetes     ClusterIP   10.96.0.1        <none>        443/TCP    27d
nginx          ClusterIP   10.104.116.156   <none>        80/TCP     17h
redis-svc      ClusterIP   10.102.44.127    <none>        6379/TCP   8s
service-demo   ClusterIP   10.98.31.157     <none>        80/TCP     16h

[root@k8s-master ~]# kubectl get pods -l app=redis -o wide     #查看标签app=redis的pod资源
NAME                            READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
redis-deploy-6559cc4c4c-5v7kx   1/1     Running   0          33s   10.244.2.65   k8s-node2   <none>           <none>
redis-deploy-6559cc4c4c-npdtf   1/1     Running   0          33s   10.244.1.69   k8s-node1   <none>           <none>

[root@k8s-master ~]# kubectl describe svc redis-svc    #查看redis-svc资源对象详细信息
Name:              redis-svc
Namespace:         default
Labels:            <none>
Annotations:       kubectl.kubernetes.io/last-applied-configuration:
                     {"apiVersion":"v1","kind":"Service","metadata":{"annotations":{},"name":"redis-svc","namespace":"default"},"spec":{"ports":[{"port":6379,"...
Selector:          app=redis
Type:              ClusterIP
IP:                10.102.44.127
Port:              <unset>  6379/TCP
TargetPort:        6379/TCP
Endpoints:         10.244.1.69:6379,10.244.2.65:6379    #可以看出这里已经和上面的pod资源绑定
Session Affinity:  None
Events:            <none>
```

#### 3）集群内部进行测试

```perl
#(1)集群内部的节点上面测试
[root@k8s-master ~]# redis-cli -h 10.102.44.127
10.102.44.127:6379> ping
PON

#(2)在后端pod上面进行测试
[root@k8s-master ~]# kubectl exec redis-deploy-6559cc4c4c-5v7kx -it -- /bin/sh
# redis-cli -h 10.102.44.127
10.102.44.127:6379> ping
PONG
```

### 2.2.2 NodePort类型的Service示例



`NodePort`即节点`Port`，通常在安装部署`Kubernetes`集群系统时会预留一个端口范围用于`NodePort`，默认为`30000~32767`之间的端口。定义`NodePort`类型的`Service`资源时，需要使用`.spec.type`明确指定类型为`NodePort`。



#### 1）编写配置清单文件

（这里使用`nginx`作为示例）；先创建一个`deployment`，启动`nginx pod`；再使用`service`绑定这个`deployment`下的`pod`资源。

```perl
[root@k8s-master ~]# vim manfests/nginx-svc.yaml    #编写yaml格式的清单文件
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deploy
  namespace: default
spec:
  replicas: 2
  selector:
    matchLabels:
      app: nginx 
  template:
    metadata:
      labels:
        app: nginx 
    spec:
      containers:
      - name: nginx-pod
        image: nginx:1.12 
        ports:
        - name: nginx 
          containerPort: 6379
---
apiVersion: v1
kind: Service
metadata:
  name: nginx-svc    #service对象名
spec:
  type: NodePort    #这里指定使用ClusterIP，默认也是ClusterIP，这里可有可无
  selector:
    app: nginx    #匹配上面定义的pod资源
  ports:
  - port: 80    #service端口
    targetPort: 80    #后端pod端口
    nodePort: 30080    #节点端口
    protocol: TCP    #协议
    
[root@k8s-master ~]# kubectl apply -f manfests/nginx-svc.yaml    #创建资源对象
deployment.apps/nginx-deploy created
service/nginx-svc created
```

#### 2）查看创建的资源对象

```perl
[root@k8s-master ~]# kubectl get svc    #查看service资源
NAME           TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)        AGE
kubernetes     ClusterIP   10.96.0.1       <none>        443/TCP        27d
nginx-svc      NodePort    10.105.21.137   <none>        80:30080/TCP   4s
redis-svc      ClusterIP   10.102.44.127   <none>        6379/TCP       55m
service-demo   ClusterIP   10.98.31.157    <none>        80/TCP         16h

[root@k8s-master ~]# kubectl get pods -l app=nginx -o wide    #查看标签app=nginx的pod资源
NAME                           READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
nginx-deploy-b6f876447-nlv6h   1/1     Running   0          33s   10.244.1.71   k8s-node1   <none>           <none>
nginx-deploy-b6f876447-xmn2t   1/1     Running   0          33s   10.244.2.66   k8s-node2   <none>           <none>

[root@k8s-master ~]# kubectl describe svc nginx-svc    #查看nginx-svc资源对象详细信息
Name:                     nginx-svc
Namespace:                default
Labels:                   <none>
Annotations:              kubectl.kubernetes.io/last-applied-configuration:
                            {"apiVersion":"v1","kind":"Service","metadata":{"annotations":{},"name":"nginx-svc","namespace":"default"},"spec":{"ports":[{"nodePort":30...
Selector:                 app=nginx
Type:                     NodePort
IP:                       10.105.21.137
Port:                     <unset>  80/TCP
TargetPort:               80/TCP
NodePort:                 <unset>  30080/TCP    #这里可以看到多了NodePort且端口为30080
Endpoints:                10.244.1.71:80,10.244.2.66:80
Session Affinity:         None
External Traffic Policy:  Cluster
Events:                   <none>
```

#### 3）集群外部进行测试

```perl
[root@courtoap ~]# curl 192.168.1.31:30080    #访问集群master节点的30080端口
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
[root@courtoap ~]# curl 192.168.1.32:30080    #访问集群node节点的30080端口
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
```

通过上面的测试可以看到通过`NodePort`的方式实现了从集群外部端口进行访问，实践中并不建议自定义使用的节点端口，因为容易产生冲突。建议让其自动生成即可。



## 2.3 Headless类型的Service资源



`Service`对象隐藏了各`Pod`资源，并负责将客户端请求流量调度至该组`Pod`对象之上，但也可能存在客户端直接访问`Service`资源后端的所有`Pod`资源，这时就应该向客户端暴露每个`Pod`资源的`IP`地址，而不是中间层`Service`对象的`ClusterIP`，这种类型的`Service`资源便称为`Headless Service`（无头服务）。



`Headless Service`对象没有`ClusterIP`，因此便没有相关负载均衡或代理问题，其如何为此类`Service`配置`IP`地址，其取决于标签选择器的定义。



- 具有标签选择器：端点控制器（`Endpoints Controller`）会在`API`中为其创建`Endpoints`记录，并将`ClusterDNS`服务中的`A`记录直接解析到此`Service`后端的各`Pod`对象的`IP`地址上。

- 没有标签选择器：端点控制器（`Endpoints Controller`）不会再`API`中为其创建`Endpoints`记录，`ClusterDNS`的配置分为两种情形，对`ExternalName`类型的服务创建`CNAME`记录，对其他三种类型来说，为那些与当前`Service`共享名称的所有`Endpoints`对象创建一条记录。



### 2.3.1 Headless类型的Service示例



配置`Service`资源配置清单时，只需要将`ClusterIP`字段的值设置为`“None”`即为其定义为`Headless`类型。



#### 1）编写配置清单文件

（这里使用`apache`作为示例）；先创建一个`deployment`，启动`apache pod`；再使用`service`绑定这个`deployment`下的`pod`资源。

```yaml
[root@k8s-master ~]# vim manfests/httpd-svc-headless.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: httpd-deploy
  namespace: default
spec:
  replicas: 3 
  selector:
    matchLabels:
      app: httpd 
  template:
    metadata:
      labels:
        app: httpd
    spec:
      containers:
      - name: httpd-pod
        image: httpd
        ports:
        - name: httpd
          containerPort: 80
---
apiVersion: v1
kind: Service
metadata:
  name: httpd-svc    #service对象名
spec:
  clusterIP: None    #将ClusterIP字段设置为None即表示为headless类型的service资源对象
  selector:
    app: httpd    #匹配上面定义的pod资源
  ports:
  - port: 80    #service端口
    targetPort: 80    #后端pod端口
    protocol: TCP    #协议
    
[root@k8s-master ~]# kubectl apply -f manfests/httpd-svc-headless.yaml 
deployment.apps/httpd-deploy created
service/httpd-svc created
```

#### 2）查看创建的资源对象

```perl
[root@k8s-master ~]# kubectl get svc    #查看service资源
NAME           TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)        AGE
httpd-svc      ClusterIP   None            <none>        80/TCP         4s
kubernetes     ClusterIP   10.96.0.1       <none>        443/TCP        27d
nginx-svc      NodePort    10.105.21.137   <none>        80:30080/TCP   112m
redis-svc      ClusterIP   10.102.44.127   <none>        6379/TCP       168m
service-demo   ClusterIP   10.98.31.157    <none>        80/TCP         18h

[root@k8s-master ~]# kubectl get pods -l app=httpd -o wide     #查看标签app=httpd的pod资源
NAME                            READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
httpd-deploy-5494485b74-4vx64   1/1     Running   0          27s   10.244.2.72   k8s-node2   <none>           <none>
httpd-deploy-5494485b74-j6hwm   1/1     Running   0          27s   10.244.2.71   k8s-node2   <none>           <none>
httpd-deploy-5494485b74-jn48q   1/1     Running   0          27s   10.244.1.74   k8s-node1   <none>           <none>

[root@k8s-master ~]# kubectl describe svc/httpd-svc    #查看httpd-svc资源对象详细信息
Name:              httpd-svc
Namespace:         default
Labels:            <none>
Annotations:       kubectl.kubernetes.io/last-applied-configuration:
                     {"apiVersion":"v1","kind":"Service","metadata":{"annotations":{},"name":"httpd-svc","namespace":"default"},"spec":{"clusterIP":"None","por...
Selector:          app=httpd
Type:              ClusterIP
IP:                None
Port:              <unset>  80/TCP
TargetPort:        80/TCP
Endpoints:         10.244.1.74:80,10.244.2.71:80,10.244.2.72:80
Session Affinity:  None
Events:            <none>
```

#### 3）测试资源发现

由Headless Service工作特性可知，它记录于ClusterDNS的A记录的相关解析结果是后端Pod资源的IP地址。意味着客户端通过Service资源的名称发现的是各Pod资源。

```perl
#(1)通过创建一个专用的测试Pod资源对象，而后通过其交互式接口进行测试
[root@k8s-master ~]# kubectl run cirror-$RANDOM --rm -it --image=cirros -- /bin/sh
/ # nslookup httpd-svc
Server:    10.96.0.10
Address 1: 10.96.0.10 kube-dns.kube-system.svc.cluster.local

Name:      httpd-svc
Address 1: 10.244.2.71 10-244-2-71.httpd-svc.default.svc.cluster.local
Address 2: 10.244.1.74 10-244-1-74.httpd-svc.default.svc.cluster.local
Address 3: 10.244.2.72 10-244-2-72.httpd-svc.default.svc.cluster.local

#(2)直接在kubernetes集群上解析
[root@k8s-master ~]# dig -t A  httpd-svc.default.svc.cluster.local. @10.96.0.10
......
;; ANSWER SECTION:
httpd-svc.default.svc.cluster.local. 26	IN A	10.244.2.72
httpd-svc.default.svc.cluster.local. 26	IN A	10.244.2.71
httpd-svc.default.svc.cluster.local. 26	IN A	10.244.1.74
......
```

# 服务发现-Ingress



`Kubernetes`提供了两种内建的云端负载均衡机制（`cloud load balancing`）用于发布公共应用，一种是工作于传输层的`Service`资源，它实现的是`“TCP负载均衡器”`，另一种是`Ingress`资源，它实现的是`“HTTP(S)负载均衡器”`。

- **TCP负载均衡器** 

- **HTTP(S)负载均衡器** 

无论是`iptables`还是`ipvs`模型的`Service`资源都配置于`Linux`内核中的`Netfilter`之上进行四层调度，是一种类型更为通用的调度器，支持调度`HTTP`、`MySQL`等应用层服务。不过，也正是由于工作于传输层从而使得它无法做到类似卸载`HTTPS`中的`SSL`会话等一类操作，也不支持基于`URL`的请求调度机制，而且，`Kubernetes`也不支持为此类负载均衡器配置任何类型的健康状态检查机制。

`HTTP(S)`负载均衡器是应用层负载均衡机制的一种，支持根据环境做出更好的调度决策。与传输层调度器相比，它提供了诸如可自定义`URL`映射和`TLS`卸载等功能，并支持多种类型的后端服务器健康状态检查机制。



# 3 Ingress概述



## 3.1 什么是Ingress？



通常情况下，`service`和`pod`仅可在集群内部网络中通过`IP`地址访问。所有到达边界路由器的流量或被丢弃或被转发到其他地方。从概念上讲，可能像下面这样：

Ingress是授权入站连接到达集群服务的规则集合。

你可以给`Ingress`配置提供外部可访问的`URL`、负载均衡、`SSL`、基于名称的虚拟主机等。用户通过`POST Ingress`资源到`API Server`的方式来请求`Ingress`。`Ingress controller`负责实现`Ingress`，通常使用负载平衡器，它还可以配置边界路由和其他前端，这有助于以`HA`方式处理流量。

```perl
 internet
     |
------------
[ Services ]
```

```perl
 internet
     |
[ Ingress ]
--|-----|--
[ Services ]
```

## 3.2 Ingress和Ingress Controller



`Ingress`是`Kubernetes API`的标准资源类型之一，它其实就是一组基于`DNS`名称（`host`）或`URL`路径把请求转发至指定的`Service`资源的规则，用于将集群外部的请求流量转发至集群内部完成服务发布。然而，`Ingress`资源自身并不能进行“流量穿透”，它仅是一组路由规则的集合，这些规则要想真正发挥作用还需要其他功能的辅助，如监听某套接字，然后根据这些规则的匹配机制路由请求流量。这种能够为`Ingress`资源监听套接字并转发流量的组件称为`Ingress`控制器（`Ingress Controller`）。



`Ingress`控制器并不直接运行为`kube-controller-manager`的一部分，它是`Kubernetes`集群的一个重要组件，类似`CoreDNS`，需要在集群上单独部署。



## 3.3 Ingress工作流程



如下图所示，流量到达外部负载均衡器（`externalLB`）后，首先转发至`Service`资源`Ingres-nginx`上，然后通过`Ingress`控制器基于`Ingress`资源定义的规则将客户端请求流量直接转发至与`Service`对应的后端`Pod`资源之上。这种转发机制会绕过`Service`资源(`app Service`；`api Service`)，从而省去了由`kube-proxy`实现的端口代理开销。`Ingress`规则需要由一个`Service`资源对象辅助识别相关的所有`Pod`资源。如下`Ingress`通过`app service`资源去匹配后端的`pod1`和`pod2`；这个`app service`只是起到一个辅助识别功能。



![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591236199535-d45a91ce-6932-4433-a5de-14564205a145.png)

## 3.4 先决条件



在使用`Ingress resource`之前，必须先了解下面几件事情。`Ingress`是`beta`版本的`resource`，在`kubernetes1.1`之前还没有。你需要一个`Ingress Controller`来实现`Ingress`，单纯的创建一个`Ingress`没有任何意义。



`GCE/GKE`会在`master`节点上部署一个`Ingress Controller`。你可以在一个`Pod`中部署任意个自定义的`Ingress Controller`。你必须正确的`annotate`每个`Ingress`，比如运行多个`Ingress Controller`和关闭`glbc`。



## 3.5 Ingress清单文件几个字段说明



`Ingress`资源是基于`HTTP`虚拟主机或`URL`的转发规则，`spec`字段中嵌套了`rules`、`backend`、`tls`等字段进行定义。下面这个示例中，它包含了一个转发规则，把发往`www.ilinux.io`的请求代理给名为`myapp-svc`的`Service`资源。

```perl
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: ingress-demo
  namespace: default
  annotations:
    kubernetes.io/ingress.class: "nginx"
spec:
  rules:
  - host: www.ilinux.io
    http:
      paths:
      - backend:
          serviceName: myapp-svc
          servicePort: 80

#说明：上面资源清单文件中的annotations用于识别其所属的Ingress控制器的类别，这一点在集群上部署多个Ingress控制器时尤为重要。
```

`Ingress Spec`（`# kubectl explain ingress.spec`）中的字段是定义`Ingress`资源的核心组成部分，主要嵌套如下三个字段：

- **rules	<[]Object>：**用于定义当前`Ingress`资源的转发规则列表；未由`rules`定义规则，或者没有匹配到任何规则时，所有流量都会转发到由`backend`定义的默认后端。

- **backend	：**默认的后端用于服务那些没有匹配到任何规则的请求；定义`Ingress`资源时，至少应该定义`backend`或`rules`两者之一；此字段用于让负载均衡器指定一个全局默认的后端。

- **tls	<[]Object>：**`TLS`配置，目前仅支持通过默认端口`443`提供服务；如果要配置指定的列表成员指向了不同的主机，则必须通过`SNI TLS`扩展机制来支持此功能。

`ingress.spec.rules.http.paths.backend`对象的定义由两个必须的内嵌字段组成：`serviceName`和`servicePort`，分别用于指定流量转发的后端目标`Service`资源的名称和端口。



# 4 部署Ingress Controller（Nginx）



## 4.1 描述



`Ingress` 控制器自身是运行于`Pod`中的容器应用，一般是`Nginx`或`Envoy`一类的具有代理及负载均衡功能的守护进程，它监视着来自`API Server`的`Ingress`对象状态，并根据规则生成相应的应用程序专有格式的配置文件并通过重载或重启守护进程而使新配置生效。



`Ingress`控制器其实就是托管于`Kubernetes`系统之上的用于实现在应用层发布服务的`Pod`资源，跟踪`Ingress`资源并实时生成配置规则。



运行为`Pod`资源的`Ingress`控制器进程通过下面两种方式接入外部请求流量：

1. 以`Deployment`控制器管理`Ingress`控制器的`Pod`资源，通过`NodePort`或`LoadBalancer`类型的`Service`对象为其接入集群外部的请求流量，这就意味着，定义一个`Ingress`控制器时，必须在其前端定义一个专用的`Service`资源。

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591236257333-8ec59ad6-37fd-4d80-be59-3f7c20abf96f.png)



1. 借助于`DaemonSet`控制器，将`Ingress`控制器的`Pod`资源各自以单一实例的方式运行于集群的所有或部分工作节点之上，并配置这类`Pod`对象以`HostPort`（如下图中的a）或`HostNetwork`（如下图中的b）的方式在当前节点接入外部流量。

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591236268701-ab604fc2-f398-49d6-bd1e-13761d3148fe.png)



## 4.2 部署



[Ingress-nginx官网](https://kubernetes.github.io/ingress-nginx/)



[Ingress-nginx GitHub仓库地址](https://github.com/kubernetes/ingress-nginx)



[Ingress安装文档](https://kubernetes.github.io/ingress-nginx/deploy/)



#### 1）在`github`上下载配置清单`yaml`文件，并创建部署

```perl
[root@k8s-master ~]# mkdir ingress-nginx   #这里创建一个目录专门用于ingress-nginx(可省略)
[root@k8s-master ~]# cd ingress-nginx/
[root@k8s-master ingress-nginx]# wget  https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/static/mandatory.yaml    #下载配置清单yaml文件
[root@k8s-master ingress-nginx]# ls    #查看下载的文件
mandatory.yaml

[root@k8s-master ingress-nginx]# kubectl apply -f mandatory.yaml    #创建Ingress
namespace/ingress-nginx created
configmap/nginx-configuration created
configmap/tcp-services created
configmap/udp-services created
serviceaccount/nginx-ingress-serviceaccount created
clusterrole.rbac.authorization.k8s.io/nginx-ingress-clusterrole created
role.rbac.authorization.k8s.io/nginx-ingress-role created
rolebinding.rbac.authorization.k8s.io/nginx-ingress-role-nisa-binding created
clusterrolebinding.rbac.authorization.k8s.io/nginx-ingress-clusterrole-nisa-binding created
deployment.apps/nginx-ingress-controller created
```

#### 2）验证

```perl
[root@k8s-master ingress-nginx]# kubectl get pods -n ingress-nginx    #查看生成的pod，注意这里在ingress-nginx名称空间
NAME                                        READY   STATUS    RESTARTS   AGE
nginx-ingress-controller-79f6884cf6-5fb6v   1/1     Running   0          18m
[root@k8s-master ingress-nginx]# kubectl describe pod nginx-ingress-controller-79f6884cf6-5fb6v -n ingress-nginx    查看该pod的详细信息
Name:           nginx-ingress-controller-79f6884cf6-5fb6v
Namespace:      ingress-nginx
Priority:       0
Node:           k8s-node2/192.168.1.33
Start Time:     Fri, 27 Sep 2019 17:53:07 +0800
Labels:         app.kubernetes.io/name=ingress-nginx
                app.kubernetes.io/part-of=ingress-nginx
                pod-template-hash=79f6884cf6
Annotations:    prometheus.io/port: 10254
                prometheus.io/scrape: true
Status:         Running
IP:             10.244.2.73
......
```

#### 3）如果是裸机部署，还需要安装`service`。（比如`VMware`虚拟机、硬件服务器等）

```perl
---同样去官网下载配置清单文件，也可以自定义创建。
[root@k8s-master ingress-nginx]# wget https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/static/provider/baremetal/service-nodeport.yaml
[root@k8s-master ingress-nginx]# kubectl apply -f service-nodeport.yaml    #创建service资源
service/ingress-nginx created
[root@k8s-master ingress-nginx]# kubectl get svc -n ingress-nginx    #查看service资源
NAME            TYPE       CLUSTER-IP       EXTERNAL-IP   PORT(S)                      AGE
ingress-nginx   NodePort   10.107.40.182   <none>        80:32699/TCP,443:30842/TCP   9s
[root@k8s-master ingress-nginx]# kubectl describe svc/ingress-nginx -n ingress-nginx    #查看该service的详细信息
Name:                     ingress-nginx
Namespace:                ingress-nginx
Labels:                   app.kubernetes.io/name=ingress-nginx
                          app.kubernetes.io/part-of=ingress-nginx
Annotations:              kubectl.kubernetes.io/last-applied-configuration:
                            {"apiVersion":"v1","kind":"Service","metadata":{"annotations":{},"labels":{"app.kubernetes.io/name":"ingress-nginx","app.kubernetes.io/par...
Selector:                 app.kubernetes.io/name=ingress-nginx,app.kubernetes.io/part-of=ingress-nginx
Type:                     NodePort
IP:                       10.107.40.182
Port:                     http  80/TCP
TargetPort:               80/TCP
NodePort:                 http  32699/TCP
Endpoints:                10.244.2.73:80
Port:                     https  443/TCP
TargetPort:               443/TCP
NodePort:                 https  30842/TCP
Endpoints:                10.244.2.73:443
Session Affinity:         None
External Traffic Policy:  Cluster
Events:                   <none>
```

通过上面创建的`service`资源对象可以看出，随机分配的`http`的`NodePort`为`32668`，`https`的`NodePort`的为`30606`。该端口也可以自定义，在前面的`service`章节说过。单一般不建议自定义。



# 5 示例1：使用Ingress发布Nginx



该示例中创建的所有资源都位于新建的`testing`名称空间中。与其他的资源在逻辑上进行隔离，以方便管理。



![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591236309341-35fd8ed2-e84c-4efa-a874-87ca87c8bc49.png)



首先创建一个单独的目录为了方便管理

```perl
[root@k8s-master ~]# mkdir ingress-nginx/ingress
[root@k8s-master ~]# cd ingress-nginx/ingress/
```

#### （1）创建`testing`名称空间

**(也可以使用命令直接创建`# kubectl create namespace my-namespace`，不过这里使用资源清单格式创建) **

```perl
[root@k8s-master ingress]# vim namespace-testing.yaml    #编写namespace清单文件
apiVersion: v1
kind: Namespace
metadata:
  name: testing
  labels:
    env: testing
[root@k8s-master ingress]#
[root@k8s-master ingress]# kubectl apply -f namespace-testing.yaml    #创建namespace
namespace/testing created
[root@k8s-master ingress]#
[root@k8s-master ingress]# kubectl get namespace testing    #验证
NAME      STATUS   AGE
testing   Active   12s
```

#### **（2）**部署`nginx`实例

**这里使用`Deployment`控制器于`testing`中部署`nginx`相关的`Pod`对象。**

```perl
[root@k8s-master ingress]# vim deployment-nginx.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: deploy-nginx
  namespace: testing
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.12
        ports:
        - name: http
          containerPort: 80
[root@k8s-master ingress]# 
[root@k8s-master ingress]# kubectl apply -f deployment-nginx.yaml 
deployment.apps/deploy-nginx created
[root@k8s-master ingress]# 
[root@k8s-master ingress]# kubectl get deploy -n testing
NAME           READY   UP-TO-DATE   AVAILABLE   AGE
deploy-nginx   3/3     3            3           5s
[root@k8s-master ingress]# 
[root@k8s-master ingress]# kubectl get pods -n testing
NAME                            READY   STATUS    RESTARTS   AGE
deploy-nginx-686bddcb56-9g7pq   1/1     Running   0          6s
deploy-nginx-686bddcb56-gqpm2   1/1     Running   0          6s
deploy-nginx-686bddcb56-vtwkq   1/1     Running   0          6s
```

#### （3）创建`Service`资源

**关联后端的`Pod`资源。这里通过`service`资源`svc-nginx`的`80`端口去暴露容器的`80`端口。**

```perl
[root@k8s-master ingress]# vim service-nginx.yaml
apiVersion: v1
kind: Service
metadata:
  name: svc-nginx
  namespace: testing
  labels:
    app: svc-nginx
spec:
  selector:
    app: nginx
  ports:
  - name: http
    port: 80
    targetPort: 80
    protocol: TCP
[root@k8s-master ingress]# 
[root@k8s-master ingress]# kubectl apply -f service-nginx.yaml 
service/svc-nginx created
[root@k8s-master ingress]# 
[root@k8s-master ingress]# kubectl get svc -n testing
NAME        TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)          AGE
svc-nginx   ClusterIP   10.99.233.90   <none>        80/TCP           6s
[root@k8s-master ingress]# 
[root@k8s-master ingress]# kubectl describe svc/svc-nginx -n testing
Name:              svc-nginx
Namespace:         testing
Labels:            app=svc-nginx
Annotations:       kubectl.kubernetes.io/last-applied-configuration:
                     {"apiVersion":"v1","kind":"Service","metadata":{"annotations":{},"labels":{"app":"svc-nginx"},"name":"svc-nginx","namespace":"testing"},"s...
Selector:          app=nginx
Type:              ClusterIP
IP:                10.99.233.90
Port:              http  80/TCP
TargetPort:        80/TCP
Endpoints:         10.244.1.76:80,10.244.1.77:80,10.244.2.74:80
Session Affinity:  None
Events:            <none>
```

#### （4）创建`Ingress`资源

**匹配`Service`资源`svc-nginx`，并将`svc-nginx`的80端口暴露。**

```perl
[root@k8s-master ingress]# vim ingress-nginx.yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: nginx
  namespace: testing
  annotations:
    kubernetes.io/ingress.class: "nginx"
spec:
  rules:
  - host: nginx.ilinux.io
    http:
      paths:
      - path:
        backend:
          serviceName: svc-nginx
          servicePort: 80
[root@k8s-master ingress]# 
[root@k8s-master ingress]# kubectl apply -f ingress-nginx.yaml 
ingress.extensions/nginx created
[root@k8s-master ingress]# 
[root@k8s-master ingress]# kubectl get ingress -n testing
NAME    HOSTS              ADDRESS   PORTS   AGE
nginx   nginx.ilinux.io             80      16s
[root@k8s-master ingress]# 
[root@k8s-master ingress]# kubectl describe ingress -n testing
Name:             nginx
Namespace:        testing
Address:          
Default backend:  default-http-backend:80 (<none>)
Rules:
  Host              Path  Backends
  ----              ----  --------
  tomcat.ilinux.io  
                       svc-nginx:80 (10.244.1.76:80,10.244.1.77:80,10.244.2.74:80)
Annotations:
  kubectl.kubernetes.io/last-applied-configuration:  {"apiVersion":"extensions/v1beta1","kind":"Ingress","metadata":{"annotations":{"kubernetes.io/ingress.class":"nginx"},"name":"nginx","namespace":"testing"},"spec":{"rules":[{"host":"nginx.ilinux.io","http":{"paths":[{"backend":{"serviceName":"svc-nginx","servicePort":80},"path":null}]}}]}}

  kubernetes.io/ingress.class:  nginx
Events:                         <none>
```

#### （5）测试

**通过`Ingress`控制器的前端的`Service`资源的`NodePort`来访问此服务**

```perl
#首先查看前面部署Ingress控制器的前端的Service资源的映射端口
[root@k8s-master ingress-nginx]# kubectl get svc -n ingress-nginx
NAME            TYPE       CLUSTER-IP      EXTERNAL-IP   PORT(S)                      AGE
ingress-nginx   NodePort   10.107.40.182   <none>        80:32699/TCP,443:30842/TCP   3m59s

#终端测试，添加hosts
[root@k8s-master ~]# cat /etc/hosts
192.168.1.31	k8s-master nginx.ilinux.io
192.168.1.32	k8s-node1 nginx.ilinux.io
192.168.1.33	k8s-node2 nginx.ilinux.io
#访问测试
[root@k8s-master ~]# curl nginx.ilinux.io:32699
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
......
```

![ingress04.png](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591236349722-006f6665-12f0-41f8-8fc7-dd11f128cda8.png)





验证是否调度到后端的Pod资源，查看日志

```perl
[root@k8s-master ~]# kubectl get pods -n testing
NAME                            READY   STATUS    RESTARTS   AGE
deploy-nginx-686bddcb56-9g7pq   1/1     Running   0          56m
deploy-nginx-686bddcb56-gqpm2   1/1     Running   0          56m
deploy-nginx-686bddcb56-vtwkq   1/1     Running   0          56m
[root@k8s-master ~]# kubectl logs deploy-nginx-686bddcb56-9g7pq -n testing
10.244.2.75 - - [28/Sep/2019:02:33:45 +0000] "GET / HTTP/1.1" 200 612 "-" "curl/7.29.0" "10.244.0.0"
10.244.2.75 - - [28/Sep/2019:02:44:02 +0000] "GET / HTTP/1.1" 200 612 "-" "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36" "10.244.0.0"
```

#### （6）配置TLS Ingress资源

**（这里使用自签证书）**

```perl
1)生成key
[root@k8s-master ingress]# openssl genrsa -out tls.key 2048
2)生成证书
[root@k8s-master ingress]# openssl req -new -x509 -key tls.key -out tls.crt -subj /C=CN/ST=ShenZhen/L=ShenZhen/O=DevOps/CN=nginx.ilinux.io -days 3650

3)创建secret资源
[root@k8s-master ingress]# kubectl create secret tls nginx-ingress-secret --cert=tls.crt --key=tls.key -n testing
secret/nginx-ingress-secret created
[root@k8s-master ingress]# kubectl get secret -n testing
NAME                   TYPE                                  DATA   AGE
default-token-lfzrt    kubernetes.io/service-account-token   3      116m
nginx-ingress-secret   kubernetes.io/tls                     2      16s

4)编写Ingress资源清单文件
[root@k8s-master ingress]# vim ingress-nginx-https.yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: nginx-ingress-tls
  namespace: testing
  annotations:
    kubernetes.io/ingress.class: "nginx"
spec:
  tls:
  - hosts:
    - nginx.ilinux.io
    secretName: nginx-ingress-secret
  rules:
  - host: nginx.ilinux.io
    http:
      paths:
      - path: /
        backend:
          serviceName: svc-nginx
          servicePort: 80

5)查看Ingress资源信息
[root@k8s-master ingress]# kubectl get ingress -n testing
NAME                HOSTS             ADDRESS   PORTS     AGE
nginx               nginx.ilinux.io             80        66m
nginx-ingress-tls   nginx.ilinux.io             80, 443   15s
[root@k8s-master ingress]# kubectl describe ingress/nginx-ingress-tls -n testing
Name:             nginx-ingress-tls
Namespace:        testing
Address:          
Default backend:  default-http-backend:80 (<none>)
TLS:
  nginx-ingress-secret terminates nginx.ilinux.io
Rules:
  Host             Path  Backends
  ----             ----  --------
  nginx.ilinux.io  
                   /   svc-nginx:80 (10.244.1.76:80,10.244.1.77:80,10.244.2.74:80)
Annotations:
  kubectl.kubernetes.io/last-applied-configuration:  {"apiVersion":"extensions/v1beta1","kind":"Ingress","metadata":{"annotations":{"kubernetes.io/ingress.class":"nginx"},"name":"nginx-ingress-tls","namespace":"testing"},"spec":{"rules":[{"host":"nginx.ilinux.io","http":{"paths":[{"backend":{"serviceName":"svc-nginx","servicePort":80},"path":"/"}]}}],"tls":[{"hosts":["nginx.ilinux.io"],"secretName":"nginx-ingress-secret"}]}}

  kubernetes.io/ingress.class:  nginx
Events:
  Type    Reason  Age   From                      Message
  ----    ------  ----  ----                      -------
  Normal  CREATE  64s   nginx-ingress-controller  Ingress testing/nginx-ingress-tls
```

#### （7）测试`https`

（这里由于是自签，所以上面提示不安全）

```perl
#首先查看前面部署Ingress控制器的前端的Service资源的映射端口
[root@k8s-master ingress-nginx]# kubectl get svc -n ingress-nginx
NAME            TYPE       CLUSTER-IP      EXTERNAL-IP   PORT(S)                      AGE
ingress-nginx   NodePort   10.107.40.182   <none>        80:32699/TCP,443:30842/TCP   3m59s
```

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591236372818-5cf1df4f-6797-4fbc-a855-6f213c1e9f39.png)



# 6 示例2：使用Ingress发布多个服务



## 6.1 将不同的服务映射不同的主机上

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591236424756-5eae547f-ac23-497f-858b-f42d943f323a.png)



**准备工作：**这里创建一个目录保存本示例的所有资源配置清单

```perl
[root@k8s-master ~]# mkdir ingress-nginx/multi_svc
[root@k8s-master ~]# cd !$
```

### 6.1.1 创建名称空间



创建一个名称空间保存本示例的所有对象（方便管理）

```perl
[root@k8s-master multi_svc]# vim namespace-ms.yaml    #编写配置清单文件
apiVersion: v1
kind: Namespace
metadata:
  name: multisvc 
  labels:
    env: multisvc

[root@k8s-master multi_svc]# kubectl apply -f namespace-ms.yaml     #创建上面定义的名称空间
namespace/multisvc created

[root@k8s-master multi_svc]# kubectl get namespace multisvc    #查看名称空间
NAME       STATUS   AGE
multisvc   Active   9s
```

### 6.1.2 创建后端应用和Service



这里后端应用创建为一组`nginx`应用和一组`tomcat`应用



#### 1）编写资源清单文件

这里将`service`资源对象和`deployment`控制器写在这一个文件里

```yaml
[root@k8s-master multi_svc]# vim deploy_service-ms.yaml
#tomcat应用的Deployment控制器
apiVersion: apps/v1
kind: Deployment
metadata:
  name: tomcat-deploy
  namespace: multisvc
spec:
  replicas: 3
  selector:
    matchLabels:
      app: tomcat
  template:
    metadata:
      labels: 
        app: tomcat
    spec:
      containers:
      - name: tomcat
        image: tomcat:jdk8
        imagePullPolicy: IfNotPresent
        ports:
        - name: httpport 
          containerPort: 8080
        - name: ajpport
          containerPort: 8009
---
#tomcat应用的Service资源
apiVersion: v1
kind: Service
metadata:
  name: tomcat-svc
  namespace: multisvc
  labels:
    app: tomcat-svc
spec:
  selector:
    app: tomcat
  ports:
  - name: httpport
    port: 8080
    targetPort: 8080
    protocol: TCP
  - name: ajpport
    port: 8009
    targetPort: 8009
    protocol: TCP

---
#nginx应用的Deployment控制器
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deploy
  namespace: multisvc
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels: 
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.12
        imagePullPolicy: IfNotPresent
        ports:
        - name: http
          containerPort: 80
---
#nginx应用的Service资源
apiVersion: v1
kind: Service
metadata:
  name: nginx-svc
  namespace: multisvc
  labels:
    app: nginx-svc
spec:
  selector:
    app: nginx
  ports:
  - name: http
    port: 80
    targetPort: 80
    protocol: TCP
```

#### 2）创建上面定义资源对象并查看验证

```perl
[root@k8s-master multi_svc]# kubectl apply -f deploy_service-ms.yaml 
deployment.apps/tomcat-deploy created
service/tomcat-svc created
deployment.apps/nginx-deploy created
service/nginx-svc created
[root@k8s-master multi_svc]# kubectl get pods -n multisvc -o wide    #查看pod资源
NAME                             READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
nginx-deploy-86c667ff66-hl6rx    1/1     Running   0          13s   10.244.2.78   k8s-node2   <none>           <none>
nginx-deploy-86c667ff66-hx4j8    1/1     Running   0          13s   10.244.2.77   k8s-node2   <none>           <none>
nginx-deploy-86c667ff66-tl9mm    1/1     Running   0          13s   10.244.1.79   k8s-node1   <none>           <none>
tomcat-deploy-6484688ddc-n25hn   1/1     Running   0          13s   10.244.1.78   k8s-node1   <none>           <none>
tomcat-deploy-6484688ddc-s8dts   1/1     Running   0          13s   10.244.1.80   k8s-node1   <none>           <none>
tomcat-deploy-6484688ddc-snszk   1/1     Running   0          13s   10.244.2.76   k8s-node2   <none>           <none>
[root@k8s-master multi_svc]# kubectl get svc -n multisvc    #查看service资源对象
NAME         TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)             AGE
nginx-svc    ClusterIP   10.104.213.237   <none>        80/TCP              26s
tomcat-svc   ClusterIP   10.103.75.161    <none>        8080/TCP,8009/TCP   26s

[root@k8s-master multi_svc]# kubectl describe svc/nginx-svc -n multisvc    #查看service对象nginx-svc的详细信息
Name:              nginx-svc
Namespace:         multisvc
Labels:            app=nginx-svc
Annotations:       kubectl.kubernetes.io/last-applied-configuration:
                     {"apiVersion":"v1","kind":"Service","metadata":{"annotations":{},"labels":{"app":"nginx-svc"},"name":"nginx-svc","namespace":"multisvc"},"...
Selector:          app=nginx
Type:              ClusterIP
IP:                10.104.213.237
Port:              http  80/TCP
TargetPort:        80/TCP
Endpoints:         10.244.1.79:80,10.244.2.77:80,10.244.2.78:80
Session Affinity:  None
Events:            <none>

[root@k8s-master multi_svc]# kubectl describe svc/tomcat-svc -n multisvc    #查看service对象tomcat-svc的详细信息
Name:              tomcat-svc
Namespace:         multisvc
Labels:            app=tomcat-svc
Annotations:       kubectl.kubernetes.io/last-applied-configuration:
                     {"apiVersion":"v1","kind":"Service","metadata":{"annotations":{},"labels":{"app":"tomcat-svc"},"name":"tomcat-svc","namespace":"multisvc"}...
Selector:          app=tomcat
Type:              ClusterIP
IP:                10.103.75.161
Port:              httpport  8080/TCP
TargetPort:        8080/TCP
Endpoints:         10.244.1.78:8080,10.244.1.80:8080,10.244.2.76:8080
Port:              ajpport  8009/TCP
TargetPort:        8009/TCP
Endpoints:         10.244.1.78:8009,10.244.1.80:8009,10.244.2.76:8009
Session Affinity:  None
Events:            <none>
```

### 6.1.3 创建Ingress资源对象



#### 1）编写资源清单文件

```yaml
[root@k8s-master multi_svc]# vim ingress_host-ms.yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: multi-ingress
  namespace: multisvc
spec:
  rules:
  - host: nginx.imyapp.com
    http:
      paths: 
      - path: /
        backend:
          serviceName: nginx-svc
          servicePort: 80
  - host: tomcat.imyapp.com
    http:
      paths:
      - path: /
        backend:
          serviceName: tomcat-svc
          servicePort: 8080
```

#### 2）创建上面定义资源对象并查看验证

```perl
[root@k8s-master multi_svc]# kubectl apply -f ingress_host-ms.yaml 
ingress.extensions/multi-ingress created
[root@k8s-master multi_svc]# kubectl get ingress -n multisvc    #查看ingress资源对象
NAME            HOSTS                                ADDRESS   PORTS   AGE
multi-ingress   nginx.imyapp.com,tomcat.imyapp.com             80      18s

[root@k8s-master multi_svc]# kubectl describe ingress/multi-ingress -n multisvc    #查看ingress资源multi-ingrsss的详细信息
Name:             multi-ingress
Namespace:        multisvc
Address:          
Default backend:  default-http-backend:80 (<none>)
Rules:
  Host               Path  Backends
  ----               ----  --------
  nginx.imyapp.com   
                     /   nginx-svc:80 (10.244.1.79:80,10.244.2.77:80,10.244.2.78:80)
  tomcat.imyapp.com  
                     /   tomcat-svc:8080 (10.244.1.78:8080,10.244.1.80:8080,10.244.2.76:8080)
Annotations:
  kubectl.kubernetes.io/last-applied-configuration:  {"apiVersion":"extensions/v1beta1","kind":"Ingress","metadata":{"annotations":{},"name":"multi-ingress","namespace":"multisvc"},"spec":{"rules":[{"host":"nginx.imyapp.com","http":{"paths":[{"backend":{"serviceName":"nginx-svc","servicePort":80},"path":"/"}]}},{"host":"tomcat.imyapp.com","http":{"paths":[{"backend":{"serviceName":"tomcat-svc","servicePort":8080},"path":"/"}]}}]}}

Events:
  Type    Reason  Age   From                      Message
  ----    ------  ----  ----                      -------
  Normal  CREATE  39s   nginx-ingress-controller  Ingress multisvc/multi-ingress
```

### 6.1.4 测试访问



这是测试自定义的域名，故需要配置`host`

```perl
192.168.1.31	 nginx.imyapp.com tomcat.imyapp.com
192.168.1.32	 nginx.imyapp.com tomcat.imyapp.com
192.168.1.33	 nginx.imyapp.com tomcat.imyapp.com
```

查看部署的`Ingress`的`Service`对象的端口

```perl
[root@k8s-master multi_svc]# kubectl get svc -n ingress-nginx
NAME            TYPE       CLUSTER-IP      EXTERNAL-IP   PORT(S)                      AGE
ingress-nginx   NodePort   10.107.40.182   <none>        80:32699/TCP,443:30842/TCP   6h39m
```

访问`nginx.imyapp.com:32699`

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591236512548-8adcaad6-2e11-4d87-bfd2-d5d978f7c27a.png)



访问`tomcat.imyapp.com:32699`



![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591236524807-d3c2bb52-5193-42b0-b42e-44f9bed6fa27.png)

### 6.1.5 配置Ingress处理TLS传输



这里使用自签证书，通过`OpenSSL`进行创建



#### 1）创建证书

```perl
#创建nginx.imyapp.com域名的证书
[root@k8s-master multi_svc]# openssl genrsa -out nginx.imyapp.com.key 2048
[root@k8s-master multi_svc]# openssl req -new -x509 -key nginx.imyapp.com.key -out nginx.imyapp.com.crt -subj /C=CN/ST=ShenZhen/L=ShenZhen/O=DevOps/CN=nginx.imyapp.com -days 3650

#创建tomcat.imyapp.com域名的证书
[root@k8s-master multi_svc]# openssl genrsa -out tomcat.imyapp.com.key 2048
[root@k8s-master multi_svc]# openssl req -new -x509 -key tomcat.imyapp.com.key -out tomcat.imyapp.com.crt -subj /C=CN/ST=ShenZhen/L=ShenZhen/O=DevOps/CN=tomcat.imyapp.com -days 3650

#查看生成的证书
[root@k8s-master multi_svc]# ll *.com.*
-rw-r--r-- 1 root root 1298 9月  28 17:23 nginx.imyapp.com.crt
-rw-r--r-- 1 root root 1675 9月  28 17:22 nginx.imyapp.com.key
-rw-r--r-- 1 root root 1302 9月  28 17:24 tomcat.imyapp.com.crt
-rw-r--r-- 1 root root 1679 9月  28 17:24 tomcat.imyapp.com.key
```

#### 2）创建secrte

```perl
#创建nginx域名的secret
[root@k8s-master multi_svc]# kubectl create secret tls nginx-ingress-secret --cert=nginx.imyapp.com.crt --key=nginx.imyapp.com.key -n multisvc
secret/nginx-ingress-secret created

#创建tomcat域名的secret
[root@k8s-master multi_svc]# kubectl create secret tls tomcat-ingress-secret --cert=tomcat.imyapp.com.crt --key=tomcat.imyapp.com.key -n multisvc
secret/tomcat-ingress-secret created

#查看secret
[root@k8s-master multi_svc]# kubectl get secret -n multisvc
NAME                    TYPE                                  DATA   AGE
default-token-mf5wd     kubernetes.io/service-account-token   3      5h12m
nginx-ingress-secret    kubernetes.io/tls                     2      53s
tomcat-ingress-secret   kubernetes.io/tls                     2      27s
```

#### 3）编写带`TLS`的`Ingress`资源清单

**（这里通过复制，没有删除上面创建的`ingress`）**

```yaml
[root@k8s-master multi_svc]# cp ingress_host-ms.yaml ingress_host_https-ms.yaml
[root@k8s-master multi_svc]# vim ingress_host_https-ms.yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: multi-ingress-https
  namespace: multisvc
  annotations:
    kubernetes.io/ingress.class: "nginx"
spec:
  tls:
  - hosts:
    - nginx.imyapp.com
    secretName: nginx-ingress-secret
  - hosts: 
    - tomcat.imyapp.com
    secretName: tomcat-ingress-secret
  rules:
  - host: nginx.imyapp.com
    http:
      paths: 
      - path: /
        backend:
          serviceName: nginx-svc
          servicePort: 80
  - host: tomcat.imyapp.com
    http:
      paths:
      - path: /
        backend:
          serviceName: tomcat-svc
          servicePort: 8080
```

#### 4）创建ingress资源

```perl
[root@k8s-master multi_svc]# kubectl apply -f ingress_host_https-ms.yaml
ingress.extensions/multi-ingress-https created
[root@k8s-master multi_svc]# kubectl get ingress -n multisvc
NAME                  HOSTS                                ADDRESS   PORTS     AGE
multi-ingress         nginx.imyapp.com,tomcat.imyapp.com             80        44m
multi-ingress-https   nginx.imyapp.com,tomcat.imyapp.com             80, 443   3s
```

5）测试，通过`Ingress`控制器的前端的`Service`资源的`NodePort`来访问此服务，上面看到`ingress`控制器的`service`资源的`443`端口对应的节点的`30842`端口。



访问`nginx`



![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591236558013-780617f3-74aa-4954-ae58-176fd7c78d9a.png)



访问`tomcat`



![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591236571725-140aa089-cb93-40d1-a484-c8d2648be69e.png)



## 6.2 将不同的服务映射到相同主机的不同路径



![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591236584654-5a0d909c-7267-446c-bcd6-98c433f7312a.png)



在这种情况下，根据请求的`URL`中的路径，请求将发送到两个不同的服务。因此，客户端可以通过一个`IP`地址（`Ingress` 控制器的`IP`地址）访问两种不同的服务。



**注意**：这里`Ingress`中`path`的定义，需要与后端真实`Service`提供的`Path`一致，否则将被转发到一个不存在的`path`上，引发错误。



**Ingress定义示例**

```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: tomcat-ingress
  namespace: multisvc
spec:
  rules:
  - host: www.imyapp.com
    http:
      paths: 
      - path: /nginx
        backend:
          serviceName: nginx-svc
          servicePort: 80
      - path: /tomcat
        backend:
          serviceName: tomcat-svc
          servicePort: 8080
```

