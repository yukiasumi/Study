```perl
keystore: 密钥仓库的位置
ca-cert: CA的证书
ca-key: CA的私钥
ca-password: CA的密码
cert-file: 出口，服务器的未签名证书
cert-signed: 已签名的服务器证书
```



```perl
keytool -keystore server.keystore.jks -alias localhost -validity 6666 -genkey

1qaz@WSX
1qaz@WSX

您的名字与姓氏是什么?
  [yes]:  localhost
您的组织单位名称是什么?
  [yes]:  neptune
您的组织名称是什么?
  [yes]:  neptune
您所在的城市或区域名称是什么?
  [yes]:  shanghai
您所在的省/市/自治区名称是什么?
  [yes]:  shanghai
该单位的双字母国家/地区代码是什么?
  [yes]:  CH
CN=localhost, OU=neptune, O=neptune, L=shanghai, ST=shanghai, C=CH是否正确?
  [否]:  是

输入 <localhost> 的密钥口令
1qaz@WSX
1qaz@WSX


验证证书内容
keytool -list -v -keystore server.keystore.jks


openssl req -new -x509 -keyout ca-key -out ca-cert -days 365

[root@Neptune ~]# openssl req -new -x509 -keyout ca-key -out ca-cert -days 365
Generating a 2048 bit RSA private key
...................................................................+++
......................................+++
writing new private key to 'ca-key'
Enter PEM pass phrase:
Verifying - Enter PEM pass phrase:
-----
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
-----
Country Name (2 letter code) [XX]:CH
State or Province Name (full name) []:shanghai
Locality Name (eg, city) [Default City]:shanghai
Organization Name (eg, company) [Default Company Ltd]:Neptune
Organizational Unit Name (eg, section) []:Neptune
Common Name (eg, your name or your server's hostname) []:localhost
Email Address []:此处不填，直接回车


```





```perl
keytool -keystore server.truststore.jks -alias CARoot -import -file ca-cert
1qaz@WSX
1qaz@WSX
是否信任此证书? [否]:  是
证书已添加到密钥库中

keytool -keystore client.truststore.jks -alias CARoot -import -file ca-cert
1qaz@WSX
1qaz@WSX
是否信任此证书? [否]:  是
证书已添加到密钥库中

keytool -keystore server.keystore.jks -alias localhost -certreq -file cert-file

openssl x509 -req -CA ca-cert -CAkey ca-key -in cert-file -out cert-signed -days 6666 -CAcreateserial -passin pass:1qaz@WSX

keytool -keystore server.keystore.jks -alias CARoot -import -file ca-cert
keytool -keystore server.keystore.jks -alias localhost -import -file cert-signed

```

执行脚本

```shell
#!/bin/bash
#Step 1
keytool -keystore server.keystore.jks -alias localhost -validity 3650 -keyalg RSA -genkey
#Step 2
openssl req -new -x509 -keyout ca-key -out ca-cert -days 3650
keytool -keystore server.truststore.jks -alias CARoot -import -file ca-cert
keytool -keystore client.truststore.jks -alias CARoot -import -file ca-cert
#Step 3
keytool -keystore server.keystore.jks -alias localhost -certreq -file cert-file
openssl x509 -req -CA ca-cert -CAkey ca-key -in cert-file -out cert-signed -days 3650 -CAcreateserial -passin pass:123456
keytool -keystore server.keystore.jks -alias CARoot -import -file ca-cert
keytool -keystore server.keystore.jks -alias localhost -import -file cert-signed
#Step 4
keytool -keystore client.keystore.jks -alias localhost -validity 3650 -keyalg RSA -genkey
```

kafka配置文件server.properties

```properties
listeners=PLAINTEXT://localhost:9092,SSL://localhost:9093

ssl.keystore.location=/root/ssl/server.keystore.jks
ssl.keystore.password=123456
ssl.key.password=123456
ssl.truststore.location=/root/ssl/server.truststore.jks
ssl.truststore.password=123456
ssl.client.auth=required
security.inter.broker.protocol=SSL
ssl.endpoint.identification.algorithm=
producer.ssl.endpoint.identification.algorithm=
consumer.ssl.endpoint.identification.algorithm=







listeners=PLAINTEXT://0.0.0.0:9965,SSL://0.0.0.0:9955
advertised.listeners=PLAINTEXT://localhost:9965,SSL://localhost:9955
ssl.keystore.location=/home/sdut/ssl/server.keystore.jks
ssl.keystore.password=sdut123456
ssl.key.password=sdut123456
ssl.truststore.location=/home/sdut/ssl/server.truststore.jks
ssl.truststore.password=sdut123456
ssl.client.auth=none

```

Kafka客户端的client-ssl.properties 文件

```properties
security.protocol=SSL
ssl.truststore.location=/root/ssl/server.keystore.jks
ssl.truststore.password=123456
```

kafka命令

```perl
kafka-console-producer.sh --broker-list localhost:9093 --topic test --producer.config client-ssl.properties
kafka-console-consumer.sh --bootstrap-server localhost:9093 --topic test --consumer.config client-ssl.properties
```

