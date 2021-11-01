### 1. Get Linux
FROM alpine:3.7

### 2. Get Java via the package manager
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories \
&& apk update \
&& apk upgrade \
&& apk add --no-cache bash \
&& apk add --no-cache --virtual=build-dependencies unzip \
&& apk add --no-cache curl \
&& apk add --no-cache openjdk8-jre

### 3. Get Python, PIP

RUN apk add --no-cache python3 \
&& python3 -m ensurepip \
&& pip3 install --upgrade pip setuptools \
&& rm -r /usr/lib/python*/ensurepip && \
if [ ! -e /usr/bin/pip ]; then ln -s pip3 /usr/bin/pip ; fi && \
if [[ ! -e /usr/bin/python ]]; then ln -sf /usr/bin/python3 /usr/bin/python; fi && \
rm -r /root/.cache

### Get Flask for the app
RUN pip install --trusted-host pypi.python.org flask

####
#### OPTIONAL : 4. SET JAVA_HOME environment variable, uncomment the line below if you need it

#ENV JAVA_HOME="/usr/lib/jvm/java-1.8-openjdk"

### 4. mysql
RUN apk add --update mysql mysql-client && rm -f /var/cache/apk/*

# These lines moved to the end allow us to rebuild image quickly after only these files were modified.
COPY startup.sh /startup.sh
COPY my.cnf /etc/mysql/my.cnf
COPY tables_xxl_job.sql /etc/mysql/init.sql

####
EXPOSE 8081
EXPOSE 3306
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ADD xxl-job-admin-*.jar /app.jar
ADD xxl-job-executor-sample*.jar /app2.jar

#ENTRYPOINT ["sh","-c","java -jar /app.jar"]
#CMD nohup java -jar /app.jar &
#ENTRYPOINT ["java","-jar","/app2.jar"]
#ADD start.sh /start.sh
# 赋予文件权限并运行start.sh脚本
# RUN chmod +x start.sh
RUN chmod +x startup.sh
ENTRYPOINT ["sh","/startup.sh"]
#ENTRYPOINT ["sh","/start.sh"]

