# 02-kubeadm初始化集群

# 1.0 kubeadm介绍



`kubeadm`是`Kubernetes`项目自带的及集群构建工具，负责执行构建一个最小化的可用集群以及将其启动等的必要基本步骤，`kubeadm`是`Kubernetes`集群全生命周期的管理工具，可用于实现集群的部署、升级、降级及拆除。`kubeadm`部署`Kubernetes`集群是将大部分资源以`pod`的方式运行，例如（`kube-proxy`、`kube-controller-manager`、`kube-scheduler`、`kube-apiserver`、`flannel`)都是以`pod`方式运行。

`Kubeadm`仅关心如何初始化并启动集群，余下的其他操作，例如安装`Kubernetes Dashboard`、监控系统、日志系统等必要的附加组件则不在其考虑范围之内，需要管理员自行部署。

`Kubeadm`集成了`Kubeadm init`和`kubeadm join`等工具程序，其中`kubeadm init`用于集群的快速初始化，其核心功能是部署Master节点的各个组件，而`kubeadm join`则用于将节点快速加入到指定集群中，它们是创建`Kubernetes`集群最佳实践的“快速路径”。另外，`kubeadm token`可于集群构建后管理用于加入集群时使用的认证令牌（t`oken`)，而`kubeadm reset`命令的功能则是删除集群构建过程中生成的文件以重置回初始状态。

[kubeadm项目地址](https://github.com/kubernetes/kubeadm)

[kubeadm官方文档](https://kubernetes.io/docs/reference/setup-tools/kubeadm/)



# 2.0  Kubeadm部署Kubernetes集群



## 2.1 架构图



![img](https://cdn.nlark.com/yuque/0/2020/png/1495606/1591069652829-66adb41b-0e10-43a1-af46-774eae7da766.png)



## 2.2 环境规划

| 操作系统         | IP           | CPU/Mem | 主机名     | 角色   |
| ---------------- | ------------ | ------- | ---------- | ------ |
| CentOS7.4-86_x64 | 192.168.1.31 | 2/2G    | k8s-master | Master |
| CentOS7.4-86_x64 | 192.168.1.32 | 2/2G    | k8s-node1  | Node   |
| CentOS7.4-86_x64 | 192.168.1.33 | 2/2G    | k8s-node2  | Node   |

| name    | version |
| ------- | ------- |
| Docker  | 18.09.7 |
| kubeadm | 1.15.2  |
| kubelet | 1.15.2  |
| kubectl | 1.15.2  |



**说明：下面初始化环境工作master节点和node节点都需要执行**



### 1）关闭防火墙

```perl
systemctl stop firewalld
systemctl disable firewalld
```

### 2）关闭`selinux`

```perl
sed -i 's/enforcing/disabled/' /etc/selinux/config
setenforce 0
```

### 3）如需要关闭`swap`，（由于服务器本来配置就低，这里就不关闭swap，在后面部署过程中忽略swap报错即可）

```perl
swapoff -a  #临时
vim /etc/fstab    #永久
```

### 4）时间同步

```perl
ntpdate 0.rhel.pool.ntp.org
```

### 5）`host`绑定

```perl
# vim /etc/hosts
192.168.1.31	k8s-master
192.168.1.32	k8s-node1
192.168.1.33	k8s-node2
```

## 2.3 安装docker



**master节点和所有node节点都需要执行**



### 1）配置`docker`的`yum`仓库（这里使用阿里云仓库）

```perl
yum -y install yum-utils device-mapper-persistent-data lvm2
yum-config-manager --add-repo https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
```

### 2）安装`docker`

```perl
yum -y install docker-ce-18.09.7 docker-ce-cli-18.09.7 containerd.io
```

### 3）修改docker cgroup driver为systemd

根据文档[CRI installation](https://kubernetes.io/docs/setup/cri/)中的内容，对于使用systemd作为init system的Linux的发行版，使用systemd作为docker的cgroup driver可以确保服务器节点在资源紧张的情况更加稳定，因此这里修改各个节点上docker的cgroup driver为systemd。

```perl
mkdir /etc/docker    #没启动docker之前没有该目录
vim /etc/docker/daemon.json    #如果不存在则创建
{
  "exec-opts": ["native.cgroupdriver=systemd"]
}
```

### 4）启动docker

```perl
systemctl restart docker    #启动docker
systemctl enable docker    #开机自启动

docker info |grep Cgroup
Cgroup Driver: systemd
```

## 2.4 安装kubeadm

**master节点和所有node节点都需要执行**

### 1）配置kubenetes的yum仓库（这里使用阿里云仓库）

```perl
cat <<EOF > /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64/
enabled=1
gpgcheck=1
repo_gpgcheck=1
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg
        https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF

yum makecache
```

### 2）安装`kubelat`、`kubectl`、`kubeadm`

```perl
yum -y install kubelet-1.15.2 kubeadm-1.15.2 kubectl-1.15.2

rpm -aq kubelet kubectl kubeadm
kubectl-1.15.2-0.x86_64
kubelet-1.15.2-0.x86_64
kubeadm-1.15.2-0.x86_64
```

### 3）将`kubelet`加入开机启动，这里刚安装完成不能直接启动。（因为目前还没有集群还没有建立）

```perl
systemctl enable kubelet
```

## 2.5 初始化Master



**注意：在master节点执行**



通过`kubeadm --help`帮助手册可以看到可以通过`kubeadm init`初始化一个`master`节点，然后再通过`kubeadm join`将一个`node`节点加入到集群中

```perl
[root@k8s-master ~]# kubeadm --help
Usage:
  kubeadm [command]

Available Commands:
  alpha       Kubeadm experimental sub-commands
  completion  Output shell completion code for the specified shell (bash or zsh)
  config      Manage configuration for a kubeadm cluster persisted in a ConfigMap in the cluster
  help        Help about any command
  init        Run this command in order to set up the Kubernetes control plane
  join        Run this on any machine you wish to join an existing cluster
  reset       Run this to revert any changes made to this host by 'kubeadm init' or 'kubeadm join'
  token       Manage bootstrap tokens
  upgrade     Upgrade your cluster smoothly to a newer version with this command
  version     Print the version of kubeadm

Flags:
  -h, --help                     help for kubeadm
      --log-file string          If non-empty, use this log file
      --log-file-max-size uint   Defines the maximum size a log file can grow to. Unit is megabytes. If the value is 0, the maximum file size is unlimited. (default 1800)
      --rootfs string            [EXPERIMENTAL] The path to the 'real' host root filesystem.
      --skip-headers             If true, avoid header prefixes in the log messages
      --skip-log-headers         If true, avoid headers when opening log files
  -v, --v Level                  number for the log level verbosity

Use "kubeadm [command] --help" for more information about a command.
```

### 1）配置忽略swap报错

```perl
[root@k8s-master ~]# vim /etc/sysconfig/kubelet
KUBELET_EXTRA_ARGS="--fail-swap-on=false"
```

### 2）初始化master

```perl
--kubernetes-version    #指定Kubernetes版本
--image-repository   #由于kubeadm默认是从官网k8s.grc.io下载所需镜像，国内无法访问，所以这里通过--image-repository指定为阿里云镜像仓库地址
--pod-network-cidr    #指定pod网络段
--service-cidr    #指定service网络段
--ignore-preflight-errors=Swap    #忽略swap报错信息
```

```perl
[root@k8s-master ~]# kubeadm init --kubernetes-version=v1.15.2 --image-repository registry.aliyuncs.com/google_containers --pod-network-cidr=10.244.0.0/16 --service-cidr=10.96.0.0/12 --ignore-preflight-errors=Swap

......
Your Kubernetes control-plane has initialized successfully!

To start using your cluster, you need to run the following as a regular user:

  mkdir -p $HOME/.kube
  sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
  sudo chown $(id -u):$(id -g) $HOME/.kube/config

You should now deploy a pod network to the cluster.
Run "kubectl apply -f [podnetwork].yaml" with one of the options listed at:
  https://kubernetes.io/docs/concepts/cluster-administration/addons/

Then you can join any number of worker nodes by running the following on each as root:

kubeadm join 192.168.1.31:6443 --token a4pjca.ubxvfcsry1je626j \
    --discovery-token-ca-cert-hash sha256:784922b9100d1ecbba01800e7493f4cba7ae5c414df68234c5da7bca4ef0c581
```

### 3）按照上面初始化成功提示创建配置文件

```perl
[root@k8s-master ~]# mkdir -p $HOME/.kube
[root@k8s-master ~]# cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
[root@k8s-master ~]# chown $(id -u):$(id -g) $HOME/.kube/config
```

```perl
[root@k8s-master ~]# docker image ls   #初始化完成后可以看到所需镜像也拉取下来了
REPOSITORY                                                        TAG                 IMAGE ID            CREATED             SIZE
registry.aliyuncs.com/google_containers/kube-scheduler            v1.15.2             88fa9cb27bd2        2 weeks ago         81.1MB
registry.aliyuncs.com/google_containers/kube-proxy                v1.15.2             167bbf6c9338        2 weeks ago         82.4MB
registry.aliyuncs.com/google_containers/kube-apiserver            v1.15.2             34a53be6c9a7        2 weeks ago         207MB
registry.aliyuncs.com/google_containers/kube-controller-manager   v1.15.2             9f5df470155d        2 weeks ago         159MB
registry.aliyuncs.com/google_containers/coredns                   1.3.1               eb516548c180        7 months ago        40.3MB
registry.aliyuncs.com/google_containers/etcd                      3.3.10              2c4adeb21b4f        8 months ago        258MB
registry.aliyuncs.com/google_containers/pause                     3.1                 da86e6ba6ca1        20 months ago       742kB
```

### 4）添加flannel网络组件 [flannel项目地址](https://github.com/coreos/flannel)

```perl
方法一
[root@k8s-master ~]# kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
[root@k8s-master ~]# kubectl get pods -n kube-system |grep flannel    #验证flannel网络插件是否部署成功（Running即为成功）

# 由于flannel默认是从国外拉取镜像，所以经常拉取不到，故使用下面方法二进行安装

方法二
[root@k8s-master ~]# wget https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
[root@k8s-master ~]# sed -i 's#quay.io#quay-mirror.qiniu.com#g' kube-flannel.yml    #替换仓库地址
[root@k8s-master ~]# kubectl apply -f kube-flannel.yml
```

### 2.6 加入Node节点

向集群中添加新节点，执行在kubeadm init 输出的kubeadm join命令，再在后面同样添加忽略swap报错参数。

### 1）配置忽略swap报错

```perl
[root@k8s-node1 ~]# vim /etc/sysconfig/kubelet
KUBELET_EXTRA_ARGS="--fail-swap-on=false"

[root@k8s-node2 ~]# vim /etc/sysconfig/kubelet
KUBELET_EXTRA_ARGS="--fail-swap-on=false"
```

### 2）加入node1节点

```perl
[root@k8s-node1 ~]# kubeadm join 192.168.1.31:6443 --token a4pjca.ubxvfcsry1je626j --discovery-token-ca-cert-hash sha256:784922b9100d1ecbba01800e7493f4cba7ae5c414df68234c5da7bca4ef0c581 --ignore-preflight-errors=Swap
[preflight] Running pre-flight checks
	[WARNING Swap]: running with swap on is not supported. Please disable swap
[preflight] Reading configuration from the cluster...
[preflight] FYI: You can look at this config file with 'kubectl -n kube-system get cm kubeadm-config -oyaml'
[kubelet-start] Downloading configuration for the kubelet from the "kubelet-config-1.15" ConfigMap in the kube-system namespace
[kubelet-start] Writing kubelet configuration to file "/var/lib/kubelet/config.yaml"
[kubelet-start] Writing kubelet environment file with flags to file "/var/lib/kubelet/kubeadm-flags.env"
[kubelet-start] Activating the kubelet service
[kubelet-start] Waiting for the kubelet to perform the TLS Bootstrap...

This node has joined the cluster:
* Certificate signing request was sent to apiserver and a response was received.
* The Kubelet was informed of the new secure connection details.

Run 'kubectl get nodes' on the control-plane to see this node join the cluster.
```

### 3）加入node2节点

```perl
[root@k8s-node2 ~]# kubeadm join 192.168.1.31:6443 --token a4pjca.ubxvfcsry1je626j --discovery-token-ca-cert-hash sha256:784922b9100d1ecbba01800e7493f4cba7ae5c414df68234c5da7bca4ef0c581 --ignore-preflight-errors=Swap
[preflight] Running pre-flight checks
	[WARNING Swap]: running with swap on is not supported. Please disable swap
[preflight] Reading configuration from the cluster...
[preflight] FYI: You can look at this config file with 'kubectl -n kube-system get cm kubeadm-config -oyaml'
[kubelet-start] Downloading configuration for the kubelet from the "kubelet-config-1.15" ConfigMap in the kube-system namespace
[kubelet-start] Writing kubelet configuration to file "/var/lib/kubelet/config.yaml"
[kubelet-start] Writing kubelet environment file with flags to file "/var/lib/kubelet/kubeadm-flags.env"
[kubelet-start] Activating the kubelet service
[kubelet-start] Waiting for the kubelet to perform the TLS Bootstrap...

This node has joined the cluster:
* Certificate signing request was sent to apiserver and a response was received.
* The Kubelet was informed of the new secure connection details.

Run 'kubectl get nodes' on the control-plane to see this node join the cluster.
```

## 2.7 检查集群状态



### 1）在master节点输入命令检查集群状态，返回如下结果则集群状态正常

```perl
[root@k8s-master ~]# kubectl get nodes
NAME         STATUS     ROLES    AGE     VERSION
k8s-master   Ready      master   9m40s   v1.15.2
k8s-node1    NotReady   <none>   28s     v1.15.2
k8s-node2    NotReady   <none>   13s     v1.15.2
```

重点查看STATUS内容为Ready时，则说明集群状态正常。



### 2）查看集群客户端和服务端程序版本信息

```perl
[root@k8s-master ~]# kubectl version --short=true
Client Version: v1.15.2
Server Version: v1.15.2
```

### 3）查看集群信息

```perl
[root@k8s-master ~]# kubectl cluster-info
Kubernetes master is running at https://192.168.1.31:6443
KubeDNS is running at https://192.168.1.31:6443/api/v1/namespaces/kube-system/services/kube-dns:dns/proxy

To further debug and diagnose cluster problems, use 'kubectl cluster-info dump'.
```

### 4）查看每个节点下载的镜像

```perl
master节点：
[root@k8s-master ~]# docker images
REPOSITORY                                                        TAG                 IMAGE ID            CREATED             SIZE
registry.aliyuncs.com/google_containers/kube-apiserver            v1.15.2             34a53be6c9a7        2 weeks ago         207MB
registry.aliyuncs.com/google_containers/kube-controller-manager   v1.15.2             9f5df470155d        2 weeks ago         159MB
registry.aliyuncs.com/google_containers/kube-scheduler            v1.15.2             88fa9cb27bd2        2 weeks ago         81.1MB
registry.aliyuncs.com/google_containers/kube-proxy                v1.15.2             167bbf6c9338        2 weeks ago         82.4MB
quay-mirror.qiniu.com/coreos/flannel                              v0.11.0-amd64       ff281650a721        6 months ago        52.6MB
registry.aliyuncs.com/google_containers/coredns                   1.3.1               eb516548c180        7 months ago        40.3MB
registry.aliyuncs.com/google_containers/etcd                      3.3.10              2c4adeb21b4f        8 months ago        258MB
registry.aliyuncs.com/google_containers/pause                     3.1                 da86e6ba6ca1        20 months ago       742kB

node1节点
[root@k8s-node1 ~]# docker images
REPOSITORY                                           TAG                 IMAGE ID            CREATED             SIZE
registry.aliyuncs.com/google_containers/kube-proxy   v1.15.2             167bbf6c9338        2 weeks ago         82.4MB
quay-mirror.qiniu.com/coreos/flannel                 v0.11.0-amd64       ff281650a721        6 months ago        52.6MB
registry.aliyuncs.com/google_containers/coredns      1.3.1               eb516548c180        7 months ago        40.3MB
registry.aliyuncs.com/google_containers/pause        3.1                 da86e6ba6ca1        20 months ago       742kB

node2
[root@k8s-node2 ~]# docker images
REPOSITORY                                           TAG                 IMAGE ID            CREATED             SIZE
registry.aliyuncs.com/google_containers/kube-proxy   v1.15.2             167bbf6c9338        2 weeks ago         82.4MB
quay-mirror.qiniu.com/coreos/flannel                 v0.11.0-amd64       ff281650a721        6 months ago        52.6MB
registry.aliyuncs.com/google_containers/pause        3.1                 da86e6ba6ca1        20 months ago       742kB
```

## 2.8 删除节点



有时节点出现故障，需要删除节点，方法如下



### 1）在master节点上执行

```perl
kubectl drain <NODE-NAME> --delete-local-data --force --ignore-daemonsets
kubectl delete node <NODE-NAME>
```

### 2）在需要移除的节点上执行

```perl
kubeadm reset
```



