#! /bin/bash

TO_DELETE=(FEE SOURCE_TRANSACTION ACCOUNT_VALUATION_HIST ACCOUNT_VALUATION_INIT \
    CASH_TRANSACTION SEC_TRANSACTION TRANSACTION CARRYING_VALUE_HIST POSITION_INIT \
    POSITION_VALUATION_HIST POSITION_HIST POSITION ACCOUNT MARKETDATA_SNAPSHOT)

usage() {
    echo "Usage: $0 [-u USER] [-p PASSWORD] [-h HOST] [-P PORT] [ACCOUNT_MASTER_DATABASE]"
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
    ACCOUNT_MASTER_DATABASE=accountmaster
fi

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

MYSQL_CMD="mysql $DB_USER_ARG $DB_PASSWD_ARG $DB_HOST_ARG $DB_PORT_ARG $ACCOUNT_MASTER_DATABASE"

echo "MYSQL_CMD=$MYSQL_CMD"

for t in ${TO_DELETE[@]}; do
    echo "Deleting from $t"
    $MYSQL_CMD --execute "delete from $t"
    if [ $? != 0 ]; then
        exit -1
    fi
done
