#! /usr/bin/env bash

usage() {
    echo "Usage: $0 [-u USER] [-p PASSWORD] [-h HOST] [-P PORT] ACCOUNT_MASTER_DATABASE [SECURITY_MASTER_DATABASE]"
    exit
}

quit() {
    echo "Quitting..."
    exit -1
}

DIR=$(dirname $0)

while getopts 'u:p:h:P:' OPT; do
    case $OPT in
        u)
            DB_USER="$OPTARG";;
        p)
            DB_PASSWD="$OPTARG";;
        h)
            DB_HOST="$OPTARG";;
        P)
            DB_PORT="$OPTARG";;
        ?)
            usage
    esac
done

shift $(($OPTIND - 1))

ACCOUNT_MASTER_DATABASE=$1

if [ -z $ACCOUNT_MASTER_DATABASE ]; then
    usage
fi

echo ACCOUNT_MASTER_DATABASE: $ACCOUNT_MASTER_DATABASE
echo DB_USER: $DB_USER
echo DB_PASSWD: $DB_PASSWD
echo DB_HOST: $DB_HOST
echo DB_PORT: $DB_PORT

if [ ! -z $DB_USER ]; then
    DB_USER_ARG=--user=$DB_USER
else
    DB_USER_ARG=--user=root
fi

if [ ! -z $DB_PASSWD ]; then
    DB_PASSWD_ARG=--password=$DB_PASSWD
fi

if [ ! -z $DB_HOST ]; then
    DB_HOST_ARG=--host=$DB_HOST
fi

if [ ! -z $DB_PORT ]; then
    DB_PORT_ARG=--port=$DB_PORT
fi

trap 'quit' SIGINT

cd $DIR

MYSQL_CMD="mysql $DB_USER_ARG $DB_PASSWD_ARG $DB_HOST_ARG $DB_PORT_ARG"
echo MYSQL_CMD: $MYSQL_CMD

#
# Drop databases
#
echo
echo "Dropping account master database..."
time $MYSQL_CMD --execute "drop database if exists $ACCOUNT_MASTER_DATABASE"
echo $?

echo
echo "Creating account master database..."
time $MYSQL_CMD --execute "create database $ACCOUNT_MASTER_DATABASE"
if [ $? != 0 ]; then
    exit -1
fi


#
# Create schemas
#
echo
echo "Creating AccountMaster schema..."
time $MYSQL_CMD --database=$ACCOUNT_MASTER_DATABASE < ./ddl/CreateAccountMaster.sql
if [ $? != 0 ]; then
    exit -1
fi

echo
echo "Importing AccountMaster basic data..."
time $MYSQL_CMD --database $ACCOUNT_MASTER_DATABASE < ./data/AccountMasterBasic.sql
if [ $? != 0 ]; then
    exit -1
fi
