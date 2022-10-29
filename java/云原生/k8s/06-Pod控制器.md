# 06-Pod控制器

# 1 控制器介绍

## 1.1 控制器的必要性



自主式`Pod`对象由调度器调度到目标工作节点后即由相应节点上的`kubelet`负责监控其容器的存活状态，容器主进程崩溃后，`kubelet`能够自动重启相应的容器。但对出现非主进程崩溃类的容器错误却无从感知，这便依赖于`pod`资源对象定义的存活探测，以便`kubelet`能够探知到此类故障。但若`pod`被删除或者工作节点自身发生故障（工作节点上都有`kubelet`，`kubelet`不可用，因此其健康状态便无法保证），则便需要控制器来处理相应的容器重启和配置。



## 1.2 常见的工作负载控制器



`Pod`控制器由`master`的`kube-controller-manager`组件提供，常见的此类控制器有：

**ReplicationController**

**ReplicaSet：**代用户创建指定数量的`pod`副本数量，确保`pod`副本数量符合预期状态，并且支持滚动式自动扩容和缩容功能

**Deployment：**工作在`ReplicaSet`之上，用于管理无状态应用，目前来说最好的控制器。支持滚动更新和回滚功能，还提供声明式配置。

**DaemonSet：**用于确保集群中的每一个节点只运行特定的`pod`副本，常用于实现系统级**后台任务。比如`ELK`服务

**StatefulSet：**管理有状态应用

**Job：**只要完成就立即退出，不需要重启或重建

**CronJob：**周期性任务控制，不需要持续后台运行



## 1.3 Pod控制器概述



`Kubernetes`的核心功能之一还在于要确保各资源对象的当前状态（`status`）以匹配用户期望的状态（`spec`），使当前状态不断地向期望状态“和解”（`reconciliation`）来完成容器应用管理。而这些则是`kube-controller-manager`的任务。



创建为具体的控制器对象之后，每个控制器均通过`API Server`提供的接口持续监控相关资源对象的当前状态，并在因故障、更新或其他原因导致系统状态发生变化时，尝试让资源的当前状态想期望状态迁移和逼近。

`List-Watch`是`kubernetes`实现的核心机制之一，在资源对象的状态发生变动时，由`API Server`负责写入`etcd`并通过水平触发（`level-triggered`）机制主动通知给相关的客户端程序以确保其不会错过任何一个事件。控制器通过`API Server`的`watch`接口实时监控目标资源对象的变动并执行和解操作，但并不会与其他控制器进行任何交互。



## 1.4 Pod和Pod控制器



`Pod`控制器资源通过持续性地监控集群中运行着的`Pod`资源对象来确保受其管控的资源严格符合用户期望的状态，例如资源副本的数量要精确符合期望等。通常，一个`Pod`控制器资源至少应该包含三个基本的组成部分：



标签选择器：匹配并关联`Pod`资源对象，并据此完成受其管控的`Pod`资源计数。

期望的副本数：期望在集群中精确运行着的`Pod`资源的对象数量。

Pod模板：用于新建`Pod`资源对象的`Pod`模板资源。



# 2 ReplicaSet控制器



## 2.1 ReplicaSet概述



`ReplicaSe`t是取代早期版本中的`ReplicationController`控制器，其功能基本上与`ReplicationController`相同



`ReplicaSet`（简称RS）是`Pod`控制器类型的一种实现，用于确保由其管控的`Pod`对象副本数在任意时刻都能精确满足期望的数量。`ReplicaSet`控制器资源启动后会查找集群中匹配器标签选择器的`Pod`资源对象，当前活动对象的数量与期望的数量不吻合时，多则删除，少则通过`Pod`模板创建以补足。



`ReplicaSet`能够实现以下功能：

- **确保Pod资源对象的数量精确反映期望值：**`ReplicaSet`需要确保由其控制运行的Pod副本数量精确吻合配置中定义的期望值，否则就会自动补足所缺或终止所余。

- **确保Pod健康运行：**探测到由其管控的`Pod`对象因其所在的工作节点故障而不可用时，自动请求由调度器于其他工作节点创建缺失的`Pod`副本。

- **弹性伸缩：**可通过`ReplicaSet`控制器动态扩容或者缩容`Pod`资源对象的数量。必要时还可以通过`HPA`控制器实现`Pod`资源规模的自动伸缩。



## 2.2 创建ReplicaSet

### 2.2.1 核心字段



spec字段一般嵌套使用以下几个属性字段：

```perl
replicas	<integer>：指定期望的Pod对象副本数量
selector	<Object>：当前控制器匹配Pod对象副本的标签选择器，支持matchLabels和matchExpressions两种匹配机制
template	<Object>：用于定义Pod时的Pod资源信息
minReadySeconds	<integer>：用于定义Pod启动后多长时间为可用状态，默认为0秒
```

### 2.2.2 ReplicaSet示例

```yaml
#(1)命令行查看ReplicaSet清单定义规则
[root@k8s-master ~]# kubectl explain rs
[root@k8s-master ~]# kubectl explain rs.spec
[root@k8s-master ~]# kubectl explain rs.spec.template


#(2)创建ReplicaSet示例
[root@k8s-master ~]# vim manfests/rs-demo.yaml
apiVersion: apps/v1  #api版本定义
kind: ReplicaSet  #定义资源类型为ReplicaSet
metadata:  #元数据定义
  name: myapp
  namespace: default
spec:  #ReplicaSet的规格定义
  replicas: 2  #定义副本数量为2个
  selector:  #标签选择器，定义匹配Pod的标签
    matchLabels:
      app: myapp
      release: canary
  template:  #Pod的模板定义
    metadata:  #Pod的元数据定义
      name: myapp-pod  #自定义Pod的名称
      labels:  #定义Pod的标签，需要和上面的标签选择器内匹配规则中定义的标签一致，可以多出其他标签
        app: myapp
        release: canary
    spec:  #Pod的规格定义
      containers:  #容器定义
      - name: myapp-containers  #容器名称
        image: ikubernetes/myapp:v1  #容器镜像
        imagePullPolicy: IfNotPresent  #拉取镜像的规则
        ports:  #暴露端口
        - name: http  #端口名称
          containerPort: 80
 

#(3)创建ReplicaSet定义的Pod
[root@k8s-master ~]# kubectl apply -f manfests/rs-demo.yaml
replicaset.apps/myapp created
[root@k8s-master ~]# kubectl get rs    #查看创建的ReplicaSet控制器
NAME    DESIRED   CURRENT   READY   AGE
myapp   4         4         4       3m23s
[root@k8s-master ~]# kubectl get pods   #通过查看pod可以看出pod命令是规则是前面是replicaset控制器的名称加随机生成的字符串
NAME          READY   STATUS    RESTARTS   AGE
myapp-bln4v   1/1     Running   0          6s
myapp-bxpzt   1/1     Running   0          6s


#(4)修改Pod的副本数量
[root@k8s-master ~]# kubectl edit rs myapp
  replicas: 4
[root@k8s-master ~]# kubectl get rs -o wide 
NAME    DESIRED   CURRENT   READY   AGE    CONTAINERS         IMAGES                 SELECTOR
myapp   4         4         4       2m50s   myapp-containers   ikubernetes/myapp:v2   app=myapp,release=canary
[root@k8s-master ~]# kubectl get pods --show-labels
NAME          READY   STATUS    RESTARTS   AGE     LABELS
myapp-8hkcr   1/1     Running   0          2m2s    app=myapp,release=canary
myapp-bln4v   1/1     Running   0          3m40s   app=myapp,release=canary
myapp-bxpzt   1/1     Running   0          3m40s   app=myapp,release=canary
myapp-ql2wk   1/1     Running   0          2m2s    app=myapp,release=canary
```

### 2.2.3 更新ReplicaSet控制器



**修改Pod模板：升级应用**



修改上面创建的`replicaset`示例文件，将镜像`ikubernetes/myapp:v1`改为`v2`版本

```perl
[root@k8s-master ~]# vim manfests/rs-demo.yaml
    spec:  #Pod的规格定义
      containers:  #容器定义
      - name: myapp-containers  #容器名称
        image: ikubernetes/myapp:v2  #容器镜像
        imagePullPolicy: IfNotPresent  #拉取镜像的规则
        ports:  #暴露端口
        - name: http  #端口名称
          containerPort: 80
[root@k8s-master ~]# kubectl apply -f manfests/rs-demo.yaml  #执行apply让其重载
[root@k8s-master ~]# kubectl get pods -o custom-columns=Name:metadata.name,Image:spec.containers[0].image
Name          Image
myapp-bln4v   ikubernetes/myapp:v1
myapp-bxpzt   ikubernetes/myapp:v1

#说明：这里虽然重载了，但是已有的pod所使用的镜像仍然是v1版本的，只是新建pod时才会使用v2版本，这里测试先手动删除已有的pod。
[root@k8s-master ~]# kubectl delete pods -l app=myapp  #删除标签app=myapp的pod资源
pod "myapp-bln4v" deleted
pod "myapp-bxpzt" deleted
[root@k8s-master ~]# kubectl get pods -o custom-columns=Name:metadata.name,Image:spec.containers[0].image   #再次查看通过ReplicaSet新建的pod资源对象。镜像已使用v2版本
Name          Image
myapp-mdn8j   ikubernetes/myapp:v2
myapp-v5bgr   ikubernetes/myapp:v2
```

### 扩容和缩容

可以直接通过`vim` 编辑清单文件修改`replicas`字段，也可以通过`kubect edit` 命令去编辑。`kubectl`还提供了一个专用的子命令`scale`用于实现应用规模的伸缩，支持从资源清单文件中获取新的目标副本数量，也可以直接在命令行通过`“--replicas”`选项进行读取。

```perl
[root@k8s-master ~]# kubectl get rs    #查看ReplicaSet
NAME    DESIRED   CURRENT   READY   AGE
myapp   2         2         2       154m
[root@k8s-master ~]# kubectl get pods    #查看Pod
NAME          READY   STATUS    RESTARTS   AGE
myapp-mdn8j   1/1     Running   0          5m26s
myapp-v5bgr   1/1     Running   0          5m26s

#扩容
[root@k8s-master ~]# kubectl scale replicasets myapp --replicas=5    #将上面的Deployments控制器myapp的Pod副本数量提升为5个
replicaset.extensions/myapp scaled
[root@k8s-master ~]# kubectl get rs    #查看ReplicaSet
NAME    DESIRED   CURRENT   READY   AGE
myapp   5         5         5       156m
[root@k8s-master ~]# kubectl get pods    #查看Pod
NAME          READY   STATUS    RESTARTS   AGE
myapp-lrrp8   1/1     Running   0          8s
myapp-mbqf8   1/1     Running   0          8s
myapp-mdn8j   1/1     Running   0          6m48s
myapp-ttmf5   1/1     Running   0          8s
myapp-v5bgr   1/1     Running   0          6m48s

#收缩
[root@k8s-master ~]# kubectl scale replicasets myapp --replicas=3
replicaset.extensions/myapp scaled
[root@k8s-master ~]# kubectl get rs
NAME    DESIRED   CURRENT   READY   AGE
myapp   3         3         3       159m
[root@k8s-master ~]# kubectl get pods 
NAME          READY   STATUS    RESTARTS   AGE
myapp-mdn8j   1/1     Running   0          10m
myapp-ttmf5   1/1     Running   0          3m48s
myapp-v5bgr   1/1     Running   0          10m
```

### 2.2.4 删除ReplicaSet控制器资源



使用`Kubectl delete`命令删除`ReplicaSet`对象时默认会一并删除其管控的各`Pod`对象，有时，考虑到这些`Pod`资源未必由其创建，或者即便由其创建也并非自身的组成部分，这时候可以添加`“--cascade=false”`选项，取消级联关系。

```perl
[root@k8s-master ~]# kubectl get rs
NAME    DESIRED   CURRENT   READY   AGE
myapp   3         3         3       162m
[root@k8s-master ~]# kubectl get pods 
NAME          READY   STATUS    RESTARTS   AGE
myapp-mdn8j   1/1     Running   0          12m
myapp-ttmf5   1/1     Running   0          6m18s
myapp-v5bgr   1/1     Running   0          12m
[root@k8s-master ~]# kubectl delete replicasets myapp --cascade=false
replicaset.extensions "myapp" deleted
[root@k8s-master ~]# kubectl get rs
No resources found.
[root@k8s-master ~]# kubectl get pods 
NAME          READY   STATUS    RESTARTS   AGE
myapp-mdn8j   1/1     Running   0          13m
myapp-ttmf5   1/1     Running   0          7m
myapp-v5bgr   1/1     Running   0          13m

#通过上面的示例可以看出，添加--cascade=false参数后再删除ReplicaSet资源对象时并没有将其管控的Pod资源对象一并删除。
```

# 3 Deployment控制器

## 3.1 Deployment概述



`Deployment`(简写为`deploy`)是`kubernetes`控制器的又一种实现，它构建于`ReplicaSet`控制器之上，可为`Pod`和`ReplicaSet`资源提供声明式更新。



`Deployment`控制器资源的主要职责是为了保证`Pod`资源的健康运行，其大部分功能均可通过调用`ReplicaSet`实现，同时还增添部分特性。

- 事件和状态查看：必要时可以查看`Deployment`对象升级的详细进度和状态。

- 回滚：升级操作完成后发现问题时，支持使用回滚机制将应用返回到前一个或由用户指定的历史记录中的版本上。

- 版本记录：对`Deployment`对象的每一个操作都予以保存，以供后续可能执行的回滚操作使用。

- 暂停和启动：对于每一次升级，都能够随时暂停和启动。

- 多种自动更新方案：一是`Recreate`，即重建更新机制，全面停止、删除旧有的`Pod`后用新版本替代；另一个是`RollingUpdate`，即滚动升级机制，逐步替换旧有的`Pod`至新的版本。



## 3.2 创建Deployment



Deployment其核心资源和ReplicaSet相似

```yaml
#(1)命令行查看ReplicaSet清单定义规则
[root@k8s-master ~]# kubectl explain deployment
[root@k8s-master ~]# kubectl explain deployment.spec
[root@k8s-master ~]# kubectl explain deployment.spec.template


#(2)创建Deployment示例
[root@k8s-master ~]# vim manfests/deploy-demo.yaml
apiVersion: apps/v1  #api版本定义
kind: Deployment  #定义资源类型为Deploymant
metadata:  #元数据定义
  name: deploy-demo  #deployment控制器名称
  namespace: default  #名称空间
spec:  #deployment控制器的规格定义
  replicas: 2  #定义副本数量为2个
  selector:  #标签选择器，定义匹配Pod的标签
    matchLabels:
      app: deploy-app
      release: canary
  template:  #Pod的模板定义
    metadata:  #Pod的元数据定义
      labels:  #定义Pod的标签，需要和上面的标签选择器内匹配规则中定义的标签一致，可以多出其他标签
        app: deploy-app
        release: canary
    spec:  #Pod的规格定义
      containers:  #容器定义
      - name: myapp  #容器名称
        image: ikubernetes/myapp:v1  #容器镜像
        ports:  #暴露端口
        - name: http  #端口名称
          containerPort: 80



#(3)创建Deployment对象
[root@k8s-master ~]# kubectl apply -f manfests/deploy-demo.yaml 
deployment.apps/deploy-demo created



#(4)查看资源对象
[root@k8s-master ~]# kubectl get deployment    #查看Deployment资源对象
NAME          READY   UP-TO-DATE   AVAILABLE   AGE
deploy-demo   2/2     2            2           10s
[root@k8s-master ~]# kubectl get replicaset    #查看ReplicaSet资源对象
NAME                     DESIRED   CURRENT   READY   AGE
deploy-demo-78c84d4449   2         2         2       20s
[root@k8s-master ~]# kubectl get pods    #查看Pod资源对象
NAME                           READY   STATUS    RESTARTS   AGE
deploy-demo-78c84d4449-22btc   1/1     Running   0          23s
deploy-demo-78c84d4449-5fn2k   1/1     Running   0          23s
---
说明：
通过查看资源对象可以看出，Deployment会自动创建相关的ReplicaSet控制器资源，并以"[DEPLOYMENT-name]-[POD-TEMPLATE-HASH-VALUE]"格式为其命名，其中的hash值由Deployment自动生成。而Pod名则是以ReplicaSet控制器的名称为前缀，后跟5位随机字符。
```

## 3.3 更新策略



ReplicaSet控制器的应用更新需要手动分成多步并以特定的次序进行，过程繁杂且容易出错，而Deployment却只需要由用户指定在Pod模板中要改动的内容，（如镜像文件的版本），余下的步骤便会由其自动完成。Pod副本数量也是一样。



Deployment控制器支持两种更新策略：滚动更新（rollingUpdate）和重建创新（Recreate），默认为滚动更新



- 滚动更新（rollingUpdate）：即在删除一部分旧版本Pod资源的同时，补充创建一部分新版本的Pod对象进行应用升级，其优势是升级期间，容器中应用提供的服务不会中断，但更新期间，不同客户端得到的相应内容可能会来自不同版本的应用。

- 重新创建（Recreate）：即首先删除现有的Pod对象，而后由控制器基于新模板重行创建出新版本的资源对象。



Deployment控制器的滚动更新操作并非在同一个ReplicaSet控制器对象下删除并创建Pod资源，新控制器的Pod对象数量不断增加，直到旧控制器不再拥有Pod对象，而新控制器的副本数量变得完全符合期望值为止。如图所示

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591155444760-3fb3602d-e45e-4182-8205-341e18982342.png)



滚动更新时，应用还要确保可用的Pod对象数量不低于某阀值以确保可以持续处理客户端的服务请求，变动的方式和Pod对象的数量范围将通过`kubectl explain deployment.spec.strategy.rollingUpdate.maxSurge`和`kubectl explain deployment.spec.strategy.rollingUpdate.maxUnavailable`两个属性同时进行定义。其功能如下：



- maxSurge：指定升级期间存在的总`Pod`对象数量最多可超出期望值的个数，其值可以是`0`或正整数，也可以是一个期望值的百分比；例如，如果期望值为`3`，当前的属性值为`1`，则表示`Pod`对象的总数不能超过`4`个。

- maxUnavailable：升级期间正常可用的`Pod`副本数（包括新旧版本）最多不能低于期望值的个数，其值可以是`0`或正整数，也可以是期望值的百分比；默认值为`1`，该值意味着如果期望值是`3`，则升级期间至少要有两个`Pod`对象处于正常提供服务的状态。



![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591155453040-14014bdd-e2d0-4903-9932-729312bd5638.png)



`maxSurge`和`maxUnavailable`属性的值不可同时为`0`，否则`Pod`对象的副本数量在符合用户期望的数量后无法做出合理变动以进行滚动更新操作。



Deployment控制器可以保留其更新历史中的旧ReplicaSet对象版本，所保存的历史版本数量由`kubectl explain deployment.spec.revisionHistoryLimit`参数指定。只有保存于`revision`历史中的`ReplicaSet`版本可用于回滚。

注：为了保存版本升级的历史，需要在创建`Deployment`对象时于命令中使用`“--record”`选项。



## 3.4 Deployment更新升级



修改`Pod`模板相关的配置参数便能完成`Deployment`控制器资源的更新。由于是声明式配置，因此对`Deployment`控制器资源的修改尤其适合使用`apply`和`patch`命令来进行；如果仅只是修改容器镜像，`“set image”`命令更为易用。



### 1）首先通过`set image`命令将上面创建的`Deployment`对象的镜像版本改为`v2`版本

```shell
#打开1个终端进行升级
[root@k8s-master ~]# kubectl set image deployment/deploy-demo myapp=ikubernetes/myapp:v2 
deployment.extensions/deploy-demo image updated

#同时打开终端2进行查看pod资源对象升级过程
[root@k8s-master ~]# kubectl get pods -l app=deploy-app -w
NAME                           READY   STATUS    RESTARTS   AGE
deploy-demo-78c84d4449-2rvxr   1/1     Running   0          33s
deploy-demo-78c84d4449-nd7rr   1/1     Running   0          33s
deploy-demo-7c66dbf45b-7k4xz   0/1     Pending   0          0s
deploy-demo-7c66dbf45b-7k4xz   0/1     Pending   0          0s
deploy-demo-7c66dbf45b-7k4xz   0/1     ContainerCreating   0          0s
deploy-demo-7c66dbf45b-7k4xz   1/1     Running             0          2s
deploy-demo-78c84d4449-2rvxr   1/1     Terminating         0          49s
deploy-demo-7c66dbf45b-r88qr   0/1     Pending             0          0s
deploy-demo-7c66dbf45b-r88qr   0/1     Pending             0          0s
deploy-demo-7c66dbf45b-r88qr   0/1     ContainerCreating   0          0s
deploy-demo-7c66dbf45b-r88qr   1/1     Running             0          1s
deploy-demo-78c84d4449-2rvxr   0/1     Terminating         0          50s
deploy-demo-78c84d4449-nd7rr   1/1     Terminating         0          51s
deploy-demo-78c84d4449-nd7rr   0/1     Terminating         0          51s
deploy-demo-78c84d4449-nd7rr   0/1     Terminating         0          57s
deploy-demo-78c84d4449-nd7rr   0/1     Terminating         0          57s
deploy-demo-78c84d4449-2rvxr   0/1     Terminating         0          60s
deploy-demo-78c84d4449-2rvxr   0/1     Terminating         0          60s


#同时打开终端3进行查看pod资源对象变更过程
[root@k8s-master ~]# kubectl get deployment deploy-demo -w 
NAME          READY   UP-TO-DATE   AVAILABLE   AGE
deploy-demo   2/2     2            2           37s
deploy-demo   2/2     2            2           47s
deploy-demo   2/2     2            2           47s
deploy-demo   2/2     0            2           47s
deploy-demo   2/2     1            2           47s
deploy-demo   3/2     1            3           49s
deploy-demo   2/2     1            2           49s
deploy-demo   2/2     2            2           49s
deploy-demo   3/2     2            3           50s
deploy-demo   2/2     2            2           51s


# 升级完成再次查看rs的情况，以下可以看到原的rs作为备份，而现在启动的是新的rs
[root@k8s-master ~]# kubectl get rs 
NAME                     DESIRED   CURRENT   READY   AGE
deploy-demo-78c84d4449   0         0         0       4m41s
deploy-demo-7c66dbf45b   2         2         2       3m54s
```

### 2）`Deployment`扩容

```perl
#1、使用kubectl scale命令扩容
[root@k8s-master ~]# kubectl scale deployment deploy-demo --replicas=3
deployment.extensions/deploy-demo scaled
[root@k8s-master ~]# kubectl get pods 
NAME                           READY   STATUS    RESTARTS   AGE
deploy-demo-7c66dbf45b-7k4xz   1/1     Running   0          10m
deploy-demo-7c66dbf45b-gq2tw   1/1     Running   0          3s
deploy-demo-7c66dbf45b-r88qr   1/1     Running   0          10m

#2、使用直接修改配置清单方式进行扩容
[root@k8s-master ~]# vim manfests/deploy-demo.yaml 
spec:  #deployment控制器的规格定义
  replicas: 4  #定义副本数量为2个
[root@k8s-master ~]# kubectl apply -f manfests/deploy-demo.yaml 
deployment.apps/deploy-demo configured
[root@k8s-master ~]# kubectl get pods 
NAME                           READY   STATUS    RESTARTS   AGE
deploy-demo-78c84d4449-6rmnm   1/1     Running   0          61s
deploy-demo-78c84d4449-9xfp9   1/1     Running   0          58s
deploy-demo-78c84d4449-c2m6h   1/1     Running   0          61s
deploy-demo-78c84d4449-sfxps   1/1     Running   0          57s

#3、使用kubectl patch打补丁的方式进行扩容
[root@k8s-master ~]# kubectl patch deployment deploy-demo -p '{"spec":{"replicas":5}}'
deployment.extensions/deploy-demo patched
[root@k8s-master ~]# 
[root@k8s-master ~]# kubectl get pods 
NAME                           READY   STATUS    RESTARTS   AGE
deploy-demo-78c84d4449-6rmnm   1/1     Running   0          3m44s
deploy-demo-78c84d4449-9xfp9   1/1     Running   0          3m41s
deploy-demo-78c84d4449-c2m6h   1/1     Running   0          3m44s
deploy-demo-78c84d4449-sfxps   1/1     Running   0          3m40s
deploy-demo-78c84d4449-t7jxb   1/1     Running   0          3s
```



## 3.5 金丝雀发布



采用先添加再删除的方式，且可用`Pod`资源对象总数不低于期望值的方式进行，配置如下：



### 1）添加其总数多余期望值一个

```perl
[root@k8s-master ~]# kubectl patch deployment deploy-demo -p '{"spec":{"strategy":{"rollingUpdate":{"maxSurge":1,"maxUnavailable":0}}}}'
deployment.extensions/deploy-demo patched
```

### 2）启动更新过程，在修改相应容器的镜像版本后立即暂停更新进度。

```perl
[root@k8s-master ~]# kubectl set image deployment/deploy-demo myapp=ikubernetes/myapp:v3 && kubectl rollout pause deployment deploy-demo 
deployment.extensions/deploy-demo image updated
deployment.extensions/deploy-demo paused


#查看
[root@k8s-master ~]# kubectl get deployment    #查看deployment资源对象
NAME          READY   UP-TO-DATE   AVAILABLE   AGE
deploy-demo   6/5     1            6           37m
[root@k8s-master ~]# kubectl get pods -o custom-columns=Name:metadata.name,Image:spec.containers[0].image    #查看pod资源对象的name和image
Name                           Image
deploy-demo-6bf8dbdc9f-fjnzn   ikubernetes/myapp:v3
deploy-demo-78c84d4449-6rmnm   ikubernetes/myapp:v1
deploy-demo-78c84d4449-9xfp9   ikubernetes/myapp:v1
deploy-demo-78c84d4449-c2m6h   ikubernetes/myapp:v1
deploy-demo-78c84d4449-sfxps   ikubernetes/myapp:v1
deploy-demo-78c84d4449-t7jxb   ikubernetes/myapp:v1
[root@k8s-master ~]# kubectl rollout status deployment/deploy-demo    #查看更新情况
Waiting for deployment "deploy-demo" rollout to finish: 1 out of 5 new replicas have been updated...
---
#通过上面查看可以看出，当前的pod数量为6个，因为此前我们定义的期望值为5个，这里多出了一个，且这个镜像版本为v3版本。



#全部更新
[root@k8s-master ~]# kubectl rollout resume deployment deploy-demo
deployment.extensions/deploy-demo resumed
#再次查看
[root@k8s-master ~]# kubectl get deployment     #查看deployment资源对象
NAME          READY   UP-TO-DATE   AVAILABLE   AGE
deploy-demo   5/5     5            5           43m
[root@k8s-master ~]# kubectl get pods -o custom-columns=Name:metadata.name,Image:spec.containers[0].image    #查看pod资源对象的name和image
Name                           Image
deploy-demo-6bf8dbdc9f-2z6gt   ikubernetes/myapp:v3
deploy-demo-6bf8dbdc9f-f79q2   ikubernetes/myapp:v3
deploy-demo-6bf8dbdc9f-fjnzn   ikubernetes/myapp:v3
deploy-demo-6bf8dbdc9f-pjf4z   ikubernetes/myapp:v3
deploy-demo-6bf8dbdc9f-x7fnk   ikubernetes/myapp:v3
[root@k8s-master ~]# kubectl rollout status deployment/deploy-demo     #查看更新情况
Waiting for deployment "deploy-demo" rollout to finish: 1 out of 5 new replicas have been updated...
Waiting for deployment "deploy-demo" rollout to finish: 1 out of 5 new replicas have been updated...
Waiting for deployment spec update to be observed...
Waiting for deployment spec update to be observed...
Waiting for deployment "deploy-demo" rollout to finish: 1 out of 5 new replicas have been updated...
Waiting for deployment "deploy-demo" rollout to finish: 1 out of 5 new replicas have been updated...
Waiting for deployment "deploy-demo" rollout to finish: 2 out of 5 new replicas have been updated...
Waiting for deployment "deploy-demo" rollout to finish: 2 out of 5 new replicas have been updated...
Waiting for deployment "deploy-demo" rollout to finish: 2 out of 5 new replicas have been updated...
Waiting for deployment "deploy-demo" rollout to finish: 3 out of 5 new replicas have been updated...
Waiting for deployment "deploy-demo" rollout to finish: 3 out of 5 new replicas have been updated...
Waiting for deployment "deploy-demo" rollout to finish: 3 out of 5 new replicas have been updated...
Waiting for deployment "deploy-demo" rollout to finish: 4 out of 5 new replicas have been updated...
Waiting for deployment "deploy-demo" rollout to finish: 4 out of 5 new replicas have been updated...
Waiting for deployment "deploy-demo" rollout to finish: 4 out of 5 new replicas have been updated...
Waiting for deployment "deploy-demo" rollout to finish: 1 old replicas are pending termination...
Waiting for deployment "deploy-demo" rollout to finish: 1 old replicas are pending termination...
deployment "deploy-demo" successfully rolled out
```

## 3.6 回滚Deployment控制器下的应用发布



若因各种原因导致滚动更新无法正常进行，如镜像文件获取失败，等等；则应该将应用回滚到之前的版本，或者回滚到指定的历史记录中的版本。则通过`kubectl rollout undo`命令完成。如果回滚到指定版本则需要添加`--to-revision`选项



### 1）回到上一个版本

```perl
[root@k8s-master ~]# kubectl rollout undo deployment/deploy-demo
deployment.extensions/deploy-demo rolled back
[root@k8s-master ~]# kubectl get pods -o custom-columns=Name:metadata.name,Image:spec.containers[0].image
Name                           Image
deploy-demo-78c84d4449-2xspz   ikubernetes/myapp:v1
deploy-demo-78c84d4449-f8p46   ikubernetes/myapp:v1
deploy-demo-78c84d4449-mnmvc   ikubernetes/myapp:v1
deploy-demo-78c84d4449-tsl7r   ikubernetes/myapp:v1
deploy-demo-78c84d4449-xdt8j   ikubernetes/myapp:v1
```

### 2）回滚到指定版本

```perl
#通过该命令查看更新历史记录
[root@k8s-master ~]# kubectl rollout history deployment/deploy-demo
deployment.extensions/deploy-demo 
REVISION  CHANGE-CAUSE
2         <none>
4         <none>
5         <none>

#回滚到版本2
[root@k8s-master ~]# kubectl rollout undo deployment/deploy-demo --to-revision=2
deployment.extensions/deploy-demo rolled back
[root@k8s-master ~]# kubectl get pods -o custom-columns=Name:metadata.name,Image:spec.containers[0].image
Name                           Image
deploy-demo-7c66dbf45b-42nj4   ikubernetes/myapp:v2
deploy-demo-7c66dbf45b-8zhf5   ikubernetes/myapp:v2
deploy-demo-7c66dbf45b-bxw7x   ikubernetes/myapp:v2
deploy-demo-7c66dbf45b-gmq8x   ikubernetes/myapp:v2
deploy-demo-7c66dbf45b-mrfdb   ikubernetes/myapp:v2
```

# 4 DaemonSet控制器

## 4.1 DaemonSet概述



`DaemonSet`用于在集群中的全部节点上同时运行一份指定`Pod`资源副本，后续新加入集群的工作节点也会自动创建一个相关的`Pod`对象，当从集群移除借点时，此类`Pod`对象也将被自动回收而无需重建。管理员也可以使用节点选择器及节点标签指定仅在具有特定特征的节点上运行指定的`Pod`对象。

**应用场景**



- 运行集群存储的守护进程，如在各个节点上运行`glusterd`或`ceph`。

- 在各个节点上运行日志收集守护进程，如`fluentd`和`logstash`。

- 在各个节点上运行监控系统的代理守护进程，如`Prometheus Node Exporter`、`collectd`、`Datadog agent`、`New Relic agent`和`Ganglia gmond`等。



## 4.2 创建DaemonSet



`DaemonSet`控制器的`spec`字段中嵌套使用的相同字段`selector`、`template`和`minReadySeconds`，并且功能和用法基本相同，但它不支持`replicas`，因为毕竟不能通过期望值来确定`Pod`资源的数量，而是基于节点数量。



这里使用`nginx`来示例，生产环境就比如使用上面提到的`logstash`等。

```yaml
#(1) 定义清单文件
[root@k8s-master ~]# vim manfests/daemonset-demo.yaml
apiVersion: apps/v1    #api版本定义
kind: DaemonSet    #定义资源类型为DaemonSet
metadata:    #元数据定义
  name: daemset-nginx    #daemonset控制器名称
  namespace: default    #名称空间
  labels:    #设置daemonset的标签
    app: daem-nginx    
spec:    #DaemonSet控制器的规格定义
  selector:    #指定匹配pod的标签
    matchLabels:    #指定匹配pod的标签
      app: daem-nginx    #注意：这里需要和template中定义的标签一样
  template:    #Pod的模板定义
    metadata:    #Pod的元数据定义
      name: nginx  
      labels:    #定义Pod的标签，需要和上面的标签选择器内匹配规则中定义的标签一致，可以多出其他标签
        app: daem-nginx
    spec:    #Pod的规格定义
      containers:    #容器定义
      - name: nginx-pod    #容器名字
        image: nginx:1.12    #容器镜像
        ports:    #暴露端口
        - name: http    #端口名称
          containerPort: 80    #暴露的端口



#(2)创建上面定义的daemonset控制器
[root@k8s-master ~]# kubectl apply -f manfests/daemonset-demo.yaml 
daemonset.apps/daemset-nginx created



#(3)查看验证
[root@k8s-master ~]# kubectl get pods -o wide 
NAME                  READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
daemset-nginx-7s474   1/1     Running   0          80s   10.244.1.61   k8s-node1   <none>           <none>
daemset-nginx-kxpl2   1/1     Running   0          94s   10.244.2.58   k8s-node2   <none>           <none>
[root@k8s-master ~]# kubectl describe daemonset/daemset-nginx
......
Name:           daemset-nginx
Selector:       app=daem-nginx
Node-Selector:  <none>
......
Desired Number of Nodes Scheduled: 2
Current Number of Nodes Scheduled: 2
Number of Nodes Scheduled with Up-to-date Pods: 2
Number of Nodes Scheduled with Available Pods: 2
Number of Nodes Misscheduled: 0
Pods Status:  2 Running / 0 Waiting / 0 Succeeded / 0 Failed
......
```

通过上面验证查看，`Node-Selector`字段的值为空，表示它需要运行在集群中的每个节点之上。而当前的节点数是`2`个，所以其期望的`Pod`副本数（`Desired Number of Nodes Scheduled`）为`2`，而当前也已经创建了`2`个相关的`Pod`对象。

**注意**



对于特殊的硬件的节点来说，可能有的运行程序只需要在某一些节点上运行，那么通过`Pod`模板的`spec`字段中嵌套使用`nodeSelector`字段，并确保其值定义的标签选择器与部分特定工作节点的标签匹配即可。



## 4.3 更新DaemonSet对象



`DaemonSet`自`Kubernetes1.6`版本起也开始支持更新机制，相关配置嵌套在`kubectl explain daemonset.spec.updateStrategy`字段中。其支持`RollingUpdate（滚动更新）`和`OnDelete（删除时更新）`两种策略，滚动更新为默认的更新策略。

```perl
#(1)查看镜像版本
[root@k8s-master ~]# kubectl get pods -l app=daem-nginx -o custom-columns=NAME:metadata.name,NODE:spec.nodeName,Image:spec.containers[0].image
NAME                  NODE        Image
daemset-nginx-7s474   k8s-node1   nginx:1.12
daemset-nginx-kxpl2   k8s-node2   nginx:1.12


#(2)更新
[root@k8s-master ~]# kubectl set image daemonset/daemset-nginx nginx-pod=nginx:1.14
[root@k8s-master ~]# kubectl get pods -l app=daem-nginx -o custom-columns=NAME:metadata.name,NODE:spec.nodeName,Image:spec.containers[0].image    #再次查看
NAME                  NODE        Image
daemset-nginx-74c95   k8s-node2   nginx:1.14
daemset-nginx-nz6n9   k8s-node1   nginx:1.14

#(3)查坎详细信息
[root@k8s-master ~]# kubectl describe daemonset daemset-nginx
......
Events:
  Type    Reason            Age   From                  Message
  ----    ------            ----  ----                  -------
  Normal  SuccessfulCreate  49m   daemonset-controller  Created pod: daemset-nginx-6kzg6
  Normal  SuccessfulCreate  49m   daemonset-controller  Created pod: daemset-nginx-jjnc2
  Normal  SuccessfulDelete  40m   daemonset-controller  Deleted pod: daemset-nginx-jjnc2
  Normal  SuccessfulCreate  40m   daemonset-controller  Created pod: daemset-nginx-kxpl2
  Normal  SuccessfulDelete  40m   daemonset-controller  Deleted pod: daemset-nginx-6kzg6
  Normal  SuccessfulCreate  40m   daemonset-controller  Created pod: daemset-nginx-7s474
  Normal  SuccessfulDelete  15s   daemonset-controller  Deleted pod: daemset-nginx-7s474
  Normal  SuccessfulCreate  8s    daemonset-controller  Created pod: daemset-nginx-nz6n9
  Normal  SuccessfulDelete  5s    daemonset-controller  Deleted pod: daemset-nginx-kxpl2
```

通过上面查看可以看出，默认的滚动更新策略是一次删除一个工作节点上的`Pod`资源，待其最新版本`Pod`重建完成后再开始操作另一个工作节点上的`Pod`资源。



`DaemonSet`控制器的滚动更新机制也可以借助于`minReadySeconds`字段控制滚动节奏；必要时也可以执行暂停和继续操作。其也可以进行回滚操作。



# 5 StatefulSet控制器

前面使用`Deployment`创建的`Pod`是无状态的，当挂载了`volume`之后，如果该`Pod`挂了，`Replication Controller`会再启动一个`Pod`来保证可用性，但是由于`Pod`是无状态的，`pod`挂了就会和之前的`Volume`的关系断开，新创建的`Pod`无法找到之前的`Pod`。但是对于用户来说，他们对底层的`Pod`挂了是没有感知的，但是当`Pod`挂了之后就无法再使用之前挂载的存储卷。为了解决这一问题，就引入了`StatefulSet`用于保留Pod的状态信息。



`StatefulSet`是`Pod`资源控制器的一种实现，用于部署和扩展有状态应用的`Pod`资源，确保它们的运行顺序及每个`Pod`资源的唯一性。其应用场景包括：

- 稳定的持久化存储，即`Pod`重新调度后还是能访问到相同的持久化数据，基于`PVC`来实现。
- 稳定的网络标识，即`Pod`重新调度后其`PodName`和`HostName`不变，基于`Headless Service`（即没有`Cluster IP`的`Service`）来实现
- 有序部署，有序扩展，即`Pod`是有顺序的，在部署或者扩展的时候要依据定义的顺序依次进行（即从0到N-1，在下一个`Pod`运行之前的所有之前的`Pod`必须都是`Running`和`Ready`状态），基于`init Containers`来实现
- 有序收缩，有序删除（即从N-1到0）



`StatefulSet`由以下几个部分组成：

- 用于定义网络标志（`DNS domain`）和`Headless Service`
- 用于创建`PersistentVolumes`和`VolumeClaimTemplates`
- 定义具体应用的`StatefulSet`



`StatefulSet`中的每个`Pod`的`DNS`格式为`statefulSetName-{0..N-1}.serviceName.namespace.svc.cluster.local`，其中

- serviceName：为`Headless Service`的名字
- 0..N-1：为Pod所在的序号，从0开始到N-1
- statefulSetName：为`StatefulSet`的名字
- namespace：为服务所在的`namaspace`，`Headless Service`和`StatefulSet`必须在相同的`namespace`
- .cluster.local：为`Cluster Domain`



## 5.1 为什么要有headless？

在`Deployment`中，每一个`pod`是没有名称，是随机字符串，是无序的。而`statefulSet`中是要求有序的，每一个`Pod`的名称必须是固定的。当节点挂了，重建之后的标识符是不变的，每一个节点的节点名称是不会改变的。`Pod`名称是作为`Pod`识别的唯一标识符，必须保证其标识符的稳定并且唯一。



为了实现标识符的稳定，这时候就需要一个`headless service`解析直达到`Pod`，还需要给`Pod`配置一个唯一的名称。



## 5.2 为什么要有volumeClainTemplate？

大部分有状态副本集都会用到持久存储，比如分布式系统来说，由于数据是不一样的，每个节点都需要自己专用的存储节点。而在`Deployment`中`Pod`模板中创建的存储卷是一个共享的存储卷，多个`Pod`使用同一个存储卷，而`statefulSet`定义中的每一个`Pod`都不能使用同一个存储卷，由此基于`Pod`模板创建`Pod`是不适应的，这就需要引入`volumeClainTemplate`，当在使用`StatefulSet`创建`Pod`时，会自动生成一个`PVC`，从而请求绑定一个`PV`，从而有自己专用的存储卷。`Pod`名称、`PVC`和`PV`的关系图如下：

![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591665159686-7b0d35c9-15f3-470c-9653-5d99d6288d45.png)

## 5.3 StatefulSet 定义

在创建`StatefulSet`之前需要准备的东西，创建顺序非常关键，如下

1、Volume

2、Persistent Volume

3、Persistent Volume Clain

4、Service

5、StatefulSet

Volume可以有很多中类型，比如nfs、gluster等，下面使用nfs

### statefulSet字段说明

```perl
[root@k8s-master ~]# kubectl explain statefulset
KIND:     StatefulSet
VERSION:  apps/v1

DESCRIPTION:
     StatefulSet represents a set of pods with consistent identities. Identities
     are defined as: - Network: A single stable DNS and hostname. - Storage: As
     many VolumeClaims as requested. The StatefulSet guarantees that a given
     network identity will always map to the same storage identity.
FIELDS:
   apiVersion	<string>
   kind	<string>
   metadata	<Object>
   spec	<Object>
   status	<Object>

[root@k8s-master ~]# kubectl explain statefulset.spec
podManagementPolicy	<string>    #Pod管理策略
replicas	<integer>    #Pod副本数量
revisionHistoryLimit	<integer>    #历史版本限制
selector	<Object> -required-    #标签选择器，根据标签选择管理的Pod资源；必选字段
serviceName	<string> -required-    #服务名称，必选字段
template	<Object> -required-    #模板，定义pod资源，必选字段
updateStrategy	<Object>    #更新策略
volumeClaimTemplates	<[]Object>    #存储卷申请模板，列表对象形式
```

### 5.3.1 清单定义StatefulSet

通过上面的描述，下面示例定义`StatefulSet`资源，在定义之前首先得准备`PV`资源对象。这里同样使用NFS作为后端存储。

#### 1）准备`NFS`（安装软件省略，[参考](https://www.cnblogs.com/yanjieli/p/11338268.html)）

```perl
(1)创建存储卷对应的目录
[root@storage ~]# mkdir /data/volumes/v{1..5} -p

(2)修改nfs的配置文件
[root@storage ~]# vim /etc/exports
/data/volumes/v1  192.168.1.0/24(rw,no_root_squash)
/data/volumes/v2  192.168.1.0/24(rw,no_root_squash)
/data/volumes/v3  192.168.1.0/24(rw,no_root_squash)
/data/volumes/v4  192.168.1.0/24(rw,no_root_squash)
/data/volumes/v5  192.168.1.0/24(rw,no_root_squash)

(3)查看nfs的配置
[root@storage ~]# exportfs -arv
exporting 192.168.1.0/24:/data/volumes/v5
exporting 192.168.1.0/24:/data/volumes/v4
exporting 192.168.1.0/24:/data/volumes/v3
exporting 192.168.1.0/24:/data/volumes/v2
exporting 192.168.1.0/24:/data/volumes/v1

(4)使配置生效
[root@storage ~]# showmount -e
Export list for storage:
/data/volumes/v5 192.168.1.0/24
/data/volumes/v4 192.168.1.0/24
/data/volumes/v3 192.168.1.0/24
/data/volumes/v2 192.168.1.0/24
/data/volumes/v1 192.168.1.0/24
```

#### 2）创建PV；这里创建5个PV，存储大小各不相等，是否可读也不相同，这里新创建一个目录用于存放statefulset所有的资源清单文件等

```yaml
[root@k8s-master ~]# mkdir statefulset && cd statefulset

(1)编写创建pv的资源清单
[root@k8s-master statefulset]# vim pv-nfs.yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: pv-nfs-001
  labels:
    name: pv001
spec:
  nfs:
    path: /data/volumes/v1
    server: 192.168.1.34
    readOnly: false 
  accessModes: ["ReadWriteOnce","ReadWriteMany"]
  capacity:
    storage: 5Gi
  persistentVolumeReclaimPolicy: Retain
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: pv-nfs-002
  labels:
    name: pv002
spec:
  nfs:
    path: /data/volumes/v2
    server: 192.168.1.34
    readOnly: false 
  accessModes: ["ReadWriteOnce"]
  capacity:
    storage: 5Gi
  persistentVolumeReclaimPolicy: Retain
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: pv-nfs-003
  labels:
    name: pv003
spec:
  nfs:
    path: /data/volumes/v3
    server: 192.168.1.34
    readOnly: false 
  accessModes: ["ReadWriteOnce","ReadWriteMany"]
  capacity:
    storage: 5Gi
  persistentVolumeReclaimPolicy: Retain
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: pv-nfs-004
  labels:
    name: pv004
spec:
  nfs:
    path: /data/volumes/v4
    server: 192.168.1.34
    readOnly: false 
  accessModes: ["ReadWriteOnce","ReadWriteMany"]
  capacity:
    storage: 5Gi
  persistentVolumeReclaimPolicy: Retain
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: pv-nfs-005
  labels:
    name: pv005
spec:
  nfs:
    path: /data/volumes/v5
    server: 192.168.1.34
    readOnly: false 
  accessModes: ["ReadWriteOnce","ReadWriteMany"]
  capacity:
    storage: 5Gi
  persistentVolumeReclaimPolicy: Retain

(2)创建PV
[root@k8s-master statefulset]# kubectl apply -f pv-nfs.yaml  
persistentvolume/pv-nfs-001 created
persistentvolume/pv-nfs-002 created
persistentvolume/pv-nfs-003 created
persistentvolume/pv-nfs-004 created
persistentvolume/pv-nfs-005 created

(3)查看PV
[root@k8s-master statefulset]# kubectl get pv 
NAME         CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS      CLAIM   STORAGECLASS   REASON   AGE
pv-nfs-001   2Gi        RWO,RWX        Retain           Available                                   3s
pv-nfs-002   5Gi        RWO            Retain           Available                                   3s
pv-nfs-003   5Gi        RWO,RWX        Retain           Available                                   3s
pv-nfs-004   5Gi        RWO,RWX        Retain           Available                                   3s
pv-nfs-005   5Gi        RWO,RWX        Retain           Available                                   3s
```

#### 3）编写定义`StatefulSet`的资源清单，首先我们要定义一个`Headless Service`，这里`headless Service`和`StatefulSet`写在一个文件。

```yaml
[root@k8s-master statefulset]# vim statefulset-demo.yaml
#定义一个Headless Service
apiVersion: v1
kind: Service
metadata:
  name: nginx-svc
  labels:
    app: nginx-svc
spec:
  ports:
  - name: http
    port: 80
  clusterIP: None
  selector:
    app: nginx-pod
---
#定义StatefulSet
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: nginx-statefulset
spec:
  serviceName: nginx-svc    #指定service，和上面定义的service对应
  replicas: 5    #指定副本数量
  selector:    #指定标签选择器，和后面的pod的标签对应
    matchLabels:
      app: nginx-pod
  template:    #定义后端Pod的模板
    metadata:
      labels:
        app: nginx-pod
    spec:
      containers:
      - name: nginx
        image: nginx:1.12
        imagePullPolicy: IfNotPresent
        ports:
        - name: http
          containerPort: 80
        volumeMounts:
        - name: nginxdata
          mountPath: /usr/share/nginx/html
  volumeClaimTemplates:    #定义存储卷申请模板
  - metadata: 
      name: nginxdata
    spec:
      accessModes: ["ReadWriteOnce"]
      resources:
        requests:
          storage: 5Gi

```

解析上面的资源清单：由于StatefulSet资源依赖于一个事先存在的Service资源，所以需要先定义一个名为nginx-svc的Headless Service资源，用于关联到每个Pod资源创建DNS资源记录。接着定义了一个名为nginx-statefulset的StatefulSet资源，它通过Pod模板创建了5个Pod资源副本，并基于volumeClaiTemplate向前面创建的PV进行了请求大小为5Gi的专用存储卷。

#### 4）创建`StatefulSet`资源，这里打开另外一个窗口实时查看`pod`

```perl
[root@k8s-master statefulset]# kubectl apply -f statefulset-demo.yaml 
service/nginx-svc created
statefulset.apps/nginx-statefulset created

[root@k8s-master statefulset]# kubectl get svc   #查看创建的无头服务nginx-svc
NAME         TYPE        CLUSTER-IP   EXTERNAL-IP   PORT(S)   AGE
kubernetes   ClusterIP   10.96.0.1    <none>        443/TCP   5d19h
nginx-svc    ClusterIP   None         <none>        80/TCP    29s

[root@k8s-master statefulset]# kubectl get pv     #查看PV绑定
NAME         CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS      CLAIM                                   STORAGECLASS   REASON   AGE
pv-nfs-001   2Gi        RWO,RWX        Retain           Available                                                                   3m49s
pv-nfs-002   5Gi        RWO            Retain           Bound       default/nginxdata-nginx-statefulset-0                           3m49s
pv-nfs-003   5Gi        RWO,RWX        Retain           Bound       default/nginxdata-nginx-statefulset-1                           3m49s
pv-nfs-004   5Gi        RWO,RWX        Retain           Bound       default/nginxdata-nginx-statefulset-2                           3m49s
pv-nfs-005   5Gi        RWO,RWX        Retain           Available                                                                   3m48s
[root@k8s-master statefulset]# kubectl get pvc     #查看PVC绑定
NAME                            STATUS   VOLUME       CAPACITY   ACCESS MODES   STORAGECLASS   AGE
nginxdata-nginx-statefulset-0   Bound    pv-nfs-002   5Gi        RWO                           21s
nginxdata-nginx-statefulset-1   Bound    pv-nfs-003   5Gi        RWO,RWX                       18s
nginxdata-nginx-statefulset-2   Bound    pv-nfs-004   5Gi        RWO,RWX                       15s
[root@k8s-master statefulset]# kubectl get statefulset    #查看StatefulSet
NAME                READY   AGE
nginx-statefulset   3/3     58s

[root@k8s-master statefulset]# kubectl get pods    #查看Pod信息
NAME                  READY   STATUS    RESTARTS   AGE
nginx-statefulset-0   1/1     Running   0          78s
nginx-statefulset-1   1/1     Running   0          75s
nginx-statefulset-2   1/1     Running   0          72s


[root@k8s-master ~]# kubectl get pods -w    #动态查看pod创建过程，可以发现它是按照顺序从0-(n-1)的顺序创建
nginx-statefulset-0   0/1   Pending   0     0s
nginx-statefulset-0   0/1   Pending   0     0s
nginx-statefulset-0   0/1   Pending   0     1s
nginx-statefulset-0   0/1   ContainerCreating   0     1s
nginx-statefulset-0   1/1   Running             0     3s
nginx-statefulset-1   0/1   Pending             0     0s
nginx-statefulset-1   0/1   Pending             0     0s
nginx-statefulset-1   0/1   Pending             0     1s
nginx-statefulset-1   0/1   ContainerCreating   0     1s
nginx-statefulset-1   1/1   Running             0     3s
nginx-statefulset-2   0/1   Pending             0     0s
nginx-statefulset-2   0/1   Pending             0     0s
nginx-statefulset-2   0/1   Pending             0     2s
nginx-statefulset-2   0/1   ContainerCreating   0     2s
nginx-statefulset-2   1/1   Running             0     4s
```

#### 5）删除测试，同样在另外一个窗口动态查看`pod`

```perl
[root@k8s-master statefulset]# kubectl delete -f statefulset-demo.yaml 
service "nginx-svc" deleted
statefulset.apps "nginx-statefulset" deleted

[root@k8s-master ~]# kubectl get pods -w     #动态查看删除过程，可以也是按照顺序删除，逆向关闭。
NAME                  READY   STATUS    RESTARTS   AGE
nginx-statefulset-0   1/1     Running   0          18m
nginx-statefulset-1   1/1     Running   0          18m
nginx-statefulset-2   1/1     Running   0          18m
nginx-statefulset-2   1/1     Terminating   0          18m
nginx-statefulset-0   1/1     Terminating   0          18m
nginx-statefulset-1   1/1     Terminating   0          18m
nginx-statefulset-2   0/1     Terminating   0          18m
nginx-statefulset-0   0/1     Terminating   0          18m
nginx-statefulset-1   0/1     Terminating   0          18m
nginx-statefulset-2   0/1     Terminating   0          18m
nginx-statefulset-2   0/1     Terminating   0          18m
nginx-statefulset-2   0/1     Terminating   0          18m
nginx-statefulset-1   0/1     Terminating   0          18m
nginx-statefulset-1   0/1     Terminating   0          18m
nginx-statefulset-0   0/1     Terminating   0          18m
nginx-statefulset-0   0/1     Terminating   0          18m


此时PVC依旧存在的，再重新创建pod时，依旧会重新去绑定原来的PVC
[root@k8s-master statefulset]# kubectl apply -f statefulset-demo.yaml 
service/nginx-svc created
statefulset.apps/nginx-statefulset created

[root@k8s-master statefulset]# kubectl get pvc     #查看PVC绑定
NAME                            STATUS   VOLUME       CAPACITY   ACCESS MODES   STORAGECLASS   AGE
nginxdata-nginx-statefulset-0   Bound    pv-nfs-002   5Gi        RWO                           30m
nginxdata-nginx-statefulset-1   Bound    pv-nfs-003   5Gi        RWO,RWX                       30m
nginxdata-nginx-statefulset-2   Bound    pv-nfs-004   5Gi        RWO,RWX                       30m
```

#### 6）名称解析，在创建的每一个`Pod`中，每一个`Pod`自己的名称都是可以被解析的，如下：

```perl
[root@k8s-master statefulset]# kubectl get pods -o wide 
NAME                  READY   STATUS    RESTARTS   AGE   IP            NODE        NOMINATED NODE   READINESS GATES
nginx-statefulset-0   1/1     Running   0          12m   10.244.2.96   k8s-node2   <none>           <none>
nginx-statefulset-1   1/1     Running   0          12m   10.244.1.96   k8s-node1   <none>           <none>
nginx-statefulset-2   1/1     Running   0          12m   10.244.2.97   k8s-node2   <none>           <none>


[root@k8s-master statefulset]# dig -t A nginx-statefulset-0.nginx-svc.default.svc.cluster.local @10.96.0.10
......
;; ANSWER SECTION:
nginx-statefulset-0.nginx-svc.default.svc.cluster.local. 30 IN A 10.244.2.96

[root@k8s-master statefulset]# dig -t A nginx-statefulset-1.nginx-svc.default.svc.cluster.local @10.96.0.10
......
;; ANSWER SECTION:
nginx-statefulset-1.nginx-svc.default.svc.cluster.local. 30 IN A 10.244.1.96

[root@k8s-master statefulset]# dig -t A nginx-statefulset-2.nginx-svc.default.svc.cluster.local @10.96.0.10
......
;; ANSWER SECTION:
nginx-statefulset-2.nginx-svc.default.svc.cluster.local. 30 IN A 10.244.2.97

也可以进入到容器中进行解析，通过对Pod的名称解析得到IP
# pod_name.service_name.ns_name.svc.cluster.local
eg: nginx-statefulset-0.nginx-svc.default.svc.cluster.local
```

### 5.3.2 StatefulSet资源扩缩容



`StatefulSet`资源的扩缩容与`Deployment`资源相似，即通过修改资源的副本数来改动其目标`Pod`资源数量。对`StatefulSet`资源来说，`kubectl scale`和`kubectl patch`命令均可以实现此功能，也可以使用`kubectl edit`命令直接修改其副本数，或者修改资源清单文件，由`kubectl apply`命令重新声明。



#### 1）通过`scale`将`nginx-statefulset`资源副本数量扩容为4个

```perl
[root@k8s-master statefulset]# kubectl scale statefulset/nginx-statefulset --replicas=4   #扩容副本增加到4个
statefulset.apps/nginx-statefulset scaled
[root@k8s-master statefulset]# kubectl get pods     #查看pv信息
NAME                  READY   STATUS    RESTARTS   AGE
nginx-statefulset-0   1/1     Running   0          16m
nginx-statefulset-1   1/1     Running   0          16m
nginx-statefulset-2   1/1     Running   0          16m
nginx-statefulset-3   1/1     Running   0          3s

[root@k8s-master statefulset]# kubectl get pv   #查看pv绑定
NAME         CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS      CLAIM                                   STORAGECLASS   REASON   AGE
pv-nfs-001   2Gi        RWO,RWX        Retain           Available                                                                   21m
pv-nfs-002   5Gi        RWO            Retain           Bound       default/nginxdata-nginx-statefulset-0                           21m
pv-nfs-003   5Gi        RWO,RWX        Retain           Bound       default/nginxdata-nginx-statefulset-1                           21m
pv-nfs-004   5Gi        RWO,RWX        Retain           Bound       default/nginxdata-nginx-statefulset-2                           21m
pv-nfs-005   5Gi        RWO,RWX        Retain           Bound       default/nginxdata-nginx-statefulset-3                           21m
```

#### 2）通过`patch`将`nginx-statefulset`资源副本数量缩容为3个

```perl
[root@k8s-master statefulset]# kubectl patch sts/nginx-statefulset -p '{"spec":{"replicas":2}}'    #通过patch打补丁方式缩容
statefulset.apps/nginx-statefulset patched

[root@k8s-master ~]# kubectl get pods -w    #动态查看缩容过程
NAME                  READY   STATUS    RESTARTS   AGE
nginx-statefulset-0   1/1     Running   0          17m
nginx-statefulset-1   1/1     Running   0          17m
nginx-statefulset-2   1/1     Running   0          17m
nginx-statefulset-3   1/1     Running   0          1m
nginx-statefulset-3   1/1     Terminating   0          20s
nginx-statefulset-3   0/1     Terminating   0          20s
nginx-statefulset-3   0/1     Terminating   0          22s
nginx-statefulset-3   0/1     Terminating   0          22s
nginx-statefulset-2   1/1     Terminating   0          24s
nginx-statefulset-2   0/1     Terminating   0          24s
nginx-statefulset-2   0/1     Terminating   0          36s
nginx-statefulset-2   0/1     Terminating   0          36s
```

### 5.3.3 更新策略

`StatefulSet`的默认更新策略为滚动更新，也可以暂停更新



#### 5.3.3.1 滚动更新示例

```perl
[root@k8s-master statefulset]# kubectl patch sts/nginx-statefulset -p '{"spec":{"replicas":4}}'    #这里先将副本扩容到4个。方便测试

[root@k8s-master ~]# kubectl set image statefulset nginx-statefulset nginx=nginx:1.14    #更新镜像版本
statefulset.apps/nginx-statefulset image updated

[root@k8s-master ~]# kubectl get pods -w    #动态查看更新
NAME                  READY   STATUS    RESTARTS   AGE
nginx-statefulset-0   1/1     Running   0          18m
nginx-statefulset-1   1/1     Running   0          18m
nginx-statefulset-2   1/1     Running   0          13m
nginx-statefulset-3   1/1     Running   0          13m
nginx-statefulset-3   1/1     Terminating   0          13m
nginx-statefulset-3   0/1     Terminating   0          13m
nginx-statefulset-3   0/1     Terminating   0          13m
nginx-statefulset-3   0/1     Terminating   0          13m
nginx-statefulset-3   0/1     Pending       0          0s
nginx-statefulset-3   0/1     Pending       0          0s
nginx-statefulset-3   0/1     ContainerCreating   0          0s
nginx-statefulset-3   1/1     Running             0          2s
nginx-statefulset-2   1/1     Terminating         0          13m
nginx-statefulset-2   0/1     Terminating         0          13m
nginx-statefulset-2   0/1     Terminating         0          14m
nginx-statefulset-2   0/1     Terminating         0          14m
nginx-statefulset-2   0/1     Pending             0          0s
nginx-statefulset-2   0/1     Pending             0          0s
nginx-statefulset-2   0/1     ContainerCreating   0          0s
nginx-statefulset-2   1/1     Running             0          1s
nginx-statefulset-1   1/1     Terminating         0          18m
nginx-statefulset-1   0/1     Terminating         0          18m
nginx-statefulset-1   0/1     Terminating         0          18m
nginx-statefulset-1   0/1     Terminating         0          18m
nginx-statefulset-1   0/1     Pending             0          0s
nginx-statefulset-1   0/1     Pending             0          0s
nginx-statefulset-1   0/1     ContainerCreating   0          0s
nginx-statefulset-1   1/1     Running             0          2s
nginx-statefulset-0   1/1     Terminating         0          18m
nginx-statefulset-0   0/1     Terminating         0          18m
nginx-statefulset-0   0/1     Terminating         0          18m
nginx-statefulset-0   0/1     Terminating         0          18m
nginx-statefulset-0   0/1     Pending             0          0s
nginx-statefulset-0   0/1     Pending             0          0s
nginx-statefulset-0   0/1     ContainerCreating   0          0s
nginx-statefulset-0   1/1     Running             0          2s

[root@k8s-master statefulset]# kubectl get pods -l app=nginx-pod -o custom-columns=NAME:metadata.name,IMAGE:spec.containers[0].image    #查看更新完成后的镜像版本
NAME                  IMAGE
nginx-statefulset-0   nginx:1.14
nginx-statefulset-1   nginx:1.14
nginx-statefulset-2   nginx:1.14
nginx-statefulset-3   nginx:1.14   
```

通过上面示例可以看出，默认为滚动更新，倒序更新，更新完成一个接着更新下一个。



#### 5.3.3.2 暂停更新示例

有时候设定了一个更新操作，但是又不希望一次性全部更新完成，想先更新几个，观察其是否稳定，然后再更新所有的。这时候只需要将`.spec.spec.updateStrategy.rollingUpdate.partition`字段的值进行修改即可。（默认值为`0`，所以我们看到了更新效果为上面那样，全部更新）。该字段表示如果设置为`2`，那么只有当编号大于等于`2`的才会进行更新。类似于金丝雀的发布方式。示例如下：

```perl
[root@k8s-master ~]# kubectl patch sts/nginx-statefulset -p '{"spec":{"updateStrategy":{"rollingUpdate":{"partition":2}}}}}'     #将更新值partition设置为2
statefulset.apps/nginx-statefulset patched

[root@k8s-master ~]# kubectl set image statefulset nginx-statefulset nginx=nginx:1.12    #更新镜像版本
statefulset.apps/nginx-statefulset image updated

[root@k8s-master ~]# kubectl get pods -w     #动态查看更新
NAME                  READY   STATUS    RESTARTS   AGE
nginx-statefulset-0   1/1     Running   0          11m
nginx-statefulset-1   1/1     Running   0          11m
nginx-statefulset-2   1/1     Running   0          11m
nginx-statefulset-3   1/1     Running   0          11m
nginx-statefulset-3   1/1     Terminating   0          12m
nginx-statefulset-3   0/1     Terminating   0          12m
nginx-statefulset-3   0/1     Terminating   0          12m
nginx-statefulset-3   0/1     Terminating   0          12m
nginx-statefulset-3   0/1     Pending       0          0s
nginx-statefulset-3   0/1     Pending       0          0s
nginx-statefulset-3   0/1     ContainerCreating   0          0s
nginx-statefulset-3   1/1     Running             0          2s
nginx-statefulset-2   1/1     Terminating         0          11m
nginx-statefulset-2   0/1     Terminating         0          11m
nginx-statefulset-2   0/1     Terminating         0          12m
nginx-statefulset-2   0/1     Terminating         0          12m
nginx-statefulset-2   0/1     Pending             0          0s
nginx-statefulset-2   0/1     Pending             0          0s
nginx-statefulset-2   0/1     ContainerCreating   0          0s
nginx-statefulset-2   1/1     Running             0          2s

[root@k8s-master statefulset]# kubectl get pods -l app=nginx-pod -o custom-columns=NAME:metadata.name,IMAGE:spec.containers[0].image    #查看更新完成后的镜像版本，可以发现只有当编号大于等于2的进行了更新。
NAME                  IMAGE
nginx-statefulset-0   nginx:1.14
nginx-statefulset-1   nginx:1.14
nginx-statefulset-2   nginx:1.12
nginx-statefulset-3   nginx:1.12



将剩余的也全部更新，只需要将更新策略的partition的值改为0即可，如下：
[root@k8s-master ~]# kubectl patch sts/nginx-statefulset -p '{"spec":{"updateStrategy":{"rollingUpdate":{"partition":0}}}}}'    #将更新值partition设置为0
statefulset.apps/nginx-statefulset patche
[root@k8s-master ~]# kubectl get pods -w    #动态查看更新
NAME                  READY   STATUS    RESTARTS   AGE
nginx-statefulset-0   1/1     Running   0          18m
nginx-statefulset-1   1/1     Running   0          18m
nginx-statefulset-2   1/1     Running   0          6m44s
nginx-statefulset-3   1/1     Running   0          6m59s
nginx-statefulset-1   1/1     Terminating   0          19m
nginx-statefulset-1   0/1     Terminating   0          19m
nginx-statefulset-1   0/1     Terminating   0          19m
nginx-statefulset-1   0/1     Terminating   0          19m
nginx-statefulset-1   0/1     Pending       0          0s
nginx-statefulset-1   0/1     Pending       0          0s
nginx-statefulset-1   0/1     ContainerCreating   0          0s
nginx-statefulset-1   1/1     Running             0          2s
nginx-statefulset-0   1/1     Terminating         0          19m
nginx-statefulset-0   0/1     Terminating         0          19m
nginx-statefulset-0   0/1     Terminating         0          19m
nginx-statefulset-0   0/1     Terminating         0          19m
nginx-statefulset-0   0/1     Pending             0          0s
nginx-statefulset-0   0/1     Pending             0          0s
nginx-statefulset-0   0/1     ContainerCreating   0          0s
nginx-statefulset-0   1/1     Running             0          2s
```

