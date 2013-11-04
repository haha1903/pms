Refresh Database Scripts
========================

### Purpose of the scripts

Allow the developers to setup the database and import sample data
quickly. Also useful for setting up testing environment.

### MySQL Configuration

Change the following setting in /etc/mysql/my.cnf to speed up data
import.

   [mysqld]
   innodb_flush_log_at_trx_commit=0

### How to Use

refresh_db.sh is used to execute the sql files. Run the script with no
arguments to see the help message.

   ./refresh_db.sh

### files List

1. README.md: this file
2. ddl/CreateSecurityMaster.sql: DDL for creating SecurityMaster
schema
3. ddl/CreateAccountMaster.sql: DDL for creating AccountMaster schema
4. data/SecurityMaster.sql: SecurityMaster sample data for import
5. data/AccountMaster.sql: AccountMaster sample data for import
