#!/bin/sh

export ALPINE_VERSION=latest
export IMAGE_NAME=local/wangxian/alpine-mysql
export CONTAINER_NAME=pyja
export MYSQL_ROOT_PASSWORD=jd@xxljob
export MYSQL_USER=jdxxljob
export MYSQL_PASSWORD=jd@xxljob
export MYSQL_DATABASE=admin
export HOST_ADDRESS1=3306
export HOST_ADDRESS2=8081

if [ ! -d "/run/mysqld" ]; then
  mkdir -p /run/mysqld
fi

if [ -d /app/mysql ]; then
  echo "[i] MySQL directory already present, skipping creation"
else
  echo "[i] MySQL data directory not found, creating initial DBs"

  mysql_install_db --user=root >/dev/null

  if [ "$MYSQL_ROOT_PASSWORD" = "" ]; then
    MYSQL_ROOT_PASSWORD=jd@xxljob
    echo "[i] MySQL root Password: $MYSQL_ROOT_PASSWORD"
  fi

  MYSQL_DATABASE=${MYSQL_DATABASE:-""}
  MYSQL_USER=${MYSQL_USER:-""}
  MYSQL_PASSWORD=${MYSQL_PASSWORD:-""}
  echo "[i] MySQL root Password: $MYSQL_DATABASE"
  echo "[i] MySQL root Password: $MYSQL_USER"
  echo "[i] MySQL root Password: $MYSQL_PASSWORD"
  tfile=$(mktemp)
  if [ ! -f "$tfile" ]; then
    return 1
  fi
  cat <<EOF >"$tfile"
USE mysql;
FLUSH PRIVILEGES;
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY "$MYSQL_ROOT_PASSWORD" WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION;
EOF
  echo ">>>>>init sql<<<<<"
  if [ "$MYSQL_DATABASE" != "" ]; then
    echo "[i] Creating database: $MYSQL_DATABASE"
    echo "CREATE DATABASE IF NOT EXISTS \`$MYSQL_DATABASE\` CHARACTER SET utf8 COLLATE utf8_general_ci;" >>"$tfile"

    if [ "$MYSQL_USER" != "" ]; then
      echo "[i] Creating user: $MYSQL_USER with password $MYSQL_PASSWORD"
      echo "GRANT ALL ON \`$MYSQL_DATABASE\`.* to '$MYSQL_USER'@'%' IDENTIFIED BY '$MYSQL_PASSWORD';" >>"$tfile"
    fi
  fi

  /usr/bin/mysqld --user=root --bootstrap --verbose=0 <"$tfile"
  rm -f "$tfile"
fi
echo ">>>>>start sql<<<<<"
nohup /usr/bin/mysqld --user=root --console &
echo ">>>>>sleep 10s<<<<<"
sleep 10s
echo ">>>>>init database<<<<<"
mysql </etc/mysql/init.sql

sleep 3
echo ">>>>>start jar<<<<<"
nohup java -jar /admin.jar >/admin.log &
sleep 30
java -jar /execute.jar >/execute.log
