#! /bin/bash


echo "dropping accountmaster"

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

MYSQL_CMD="mysql $DB_USER_ARG $DB_PASSWD_ARG $DB_HOST_ARG $DB_PORT_ARG"

echo "MYSQL_CMD = " $MYSQL_CMD

$MYSQL_CMD --execute "drop database if exists accountmaster"
if [ $? != 0 ]; then
    exit -1
fi

echo "creating accountmaster"
$MYSQL_CMD --execute "create database accountmaster"
if [ $? != 0 ]; then
    exit -1
fi

echo "creating schema"
$MYSQL_CMD --database=accountmaster --execute "source ../ddl/CreateAccountMaster.sql"
if [ $? != 0 ]; then
    exit -1
fi

echo "importing basic data"
$MYSQL_CMD --database=accountmaster --execute "source ../data/AccountMasterBasic.sql"
if [ $? != 0 ]; then
    exit -1
fi
