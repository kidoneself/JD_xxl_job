#!/bin/sh
#后台执行admin程序，并把日志输出为admin.log下，日志是每次启动都会覆盖原有日志，若想保留上次启动日志，只需将>替换为>>即可,执行的脚本必须为一行，并且最后一个执行不可以加&进行后台运行，必须有一个挂起的程序，否则docker容器会停止
nohup java -jar /app2.jar  > /app2.log & nohup java -jar /app.jar  > /app.log