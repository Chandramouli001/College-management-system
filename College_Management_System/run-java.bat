@echo off

REM Set paths
set SRC_DIR=src
set BIN_DIR=bin
set MYSQL_JAR=mysql-connector-j-8.0.32.jar

REM Compile the Java files and output to the bin directory
javac -cp ".;%MYSQL_JAR%" -d %BIN_DIR% %SRC_DIR%\*.java

REM Run the LoginPage class
java -cp ".;%MYSQL_JAR%;%BIN_DIR%" LoginPage

REM Close the terminal window after execution
exit
