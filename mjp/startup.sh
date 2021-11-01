#!/bin/sh
export MYSQL_ROOT_PASSWORD=jd@xxljob
export MYSQL_USER=jdxxljob
export MYSQL_PASSWORD=jdxxljob
#export MYSQL_DATABASE=jd-xxljob
if [ ! -d "/run/mysqld" ]; then
  mkdir -p /run/mysqld
fi

if [ -d /mysql ]; then
  echo "[i] MySQL directory already present, skipping creation"
else
  echo "[i] MySQL data directory not found, creating initial DBs"

  mysql_install_db --user=root >/dev/null

  if [ "$MYSQL_ROOT_PASSWORD" = "" ]; then
    MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD
    echo "[i] MySQL root Password: $MYSQL_ROOT_PASSWORD"
  fi

  MYSQL_DATABASE=${MYSQL_DATABASE:-""}
  MYSQL_USER=${MYSQL_USER:-""}
  MYSQL_PASSWORD=${MYSQL_PASSWORD:-""}

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
echo "=================="
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

nohup /usr/bin/mysqld_safe -data &
sleep 10s
mysql </etc/mysql/init.sql

echo "=================="

nohup java -jar /app2.jar >/app2.log &
nohup java -jar /app.jar >/app.log

#/usr/bin/mysqld --user=root
