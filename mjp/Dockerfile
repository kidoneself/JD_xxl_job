### 1. Get Linux
FROM alpine:latest
WORKDIR /app
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

### 4. mysql
RUN apk add --update mysql mysql-client && rm -f /var/cache/apk/*
COPY startup.sh /startup.sh
COPY my.cnf /etc/mysql/my.cnf
COPY tables_xxl_job.sql /etc/mysql/init.sql


#### 5. jar
EXPOSE 8081
EXPOSE 3306
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ADD xxl-job-admin-*.jar /admin.jar
ADD xxl-job-executor-sample*.jar /execute.jar

RUN #chmod +x startup.sh
ENTRYPOINT ["sh","/startup.sh"]

