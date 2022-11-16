## 关闭进程

```powershell
tasklist | find /i "qq"
taskkill -f /pid 【pid】
```

```powershell
C:\Users\hakuou>tasklist|find /i "qq"
QQProtect.exe                 5192 Services                   0     30,424 K
QQ.exe                        1160 Console                    1    219,052 K
QQMusic.exe                  19304 Console                    1    131,660 K
QQLiveService.exe            16904 Console                    1     68,224 K

C:\Users\hakuou>taskkill -f /pid 19304
成功: 已终止 PID 为 19304 的进程。
```

## 获取命令路径

```powershell
C:\Users\hakuou>where qq
C:\Program Files (x86)\Tencent\QQ\Bin\QQ.exe

C:\Users\hakuou>where go
D:\go\bin\go.exe
```

## 修改hosts文件后刷新

```powershell
ipconfig /flushdns
```

