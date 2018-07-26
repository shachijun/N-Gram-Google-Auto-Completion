# N-Gram-Google-Auto-Completion
using docker (container) Hadoop and mapreduce as the method.

Please check final step file to find the result, I made some Screenshots  under final_step/running_step




* Construct N-Gram Library from the dimensional data set, build a language model based on statistical probability, generate data, and put it into the database.
* Use JQuery, PHP, Ajax to call the database, realize real-time automatic completion, and display the auto-complete function of the search engine on the webpage.



Require database, hadoop and docker 

1, find the ip address



SECOND Create Database

2, in terminal find database file and ./mysql -uroot -p (then input the database password)
Type following in terminal:
3, create database test(Name);
4, use test;
5, create table output(starting_phrase VARCHAR(250), following_word VARCHAR(250), count INT); 
6, GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'root(my local database password)' WITH GRANT OPTION; 
7, FLUSH PRIVILEGES;


Then turn on the docker and hadoop in terminal:

8, ./start-container.sh //(find where it locate)
9, ./start-hadoop.sh 
10, cd src
11, download the mysql-connector 
12, hdfs dfs -mkdir /mysql
13, hdfs dfs -put mysql-connector-java-*.jar /mysql/
14, download the file throught github which you will get a N-Gram-Google-Auto-Completion file
15, cd N-Gram-Google-Auto-Completion
set input file
16, hdfs dfs -mkdir -p input
17, hdfs dfs -put bookList/*  input/  //There are another booklist1 have about 4500 files, if you have time, feel free to use booklist1 instead of bookList, but they are doing the same job basically
18, cd src/main/java


Before Run and compile:

19, vi Driver.java
20, Chaging following:
     DBConfiguration.configureDB(conf2,
       "com.mysql.jdbc.Driver", 
       "jdbc:mysql://192.168.1.2:8889/test", // change this line with yourip:port/DatabaseName
       "root", // user name
       “root”); //password
     job2.addArchiveToClassPath(new Path(“The connetcor”));//change this as the connector you use for connecting the databse in step 11 namely /mysql/ConnectorFileName

compile and run the code:
21, hadoop com.sun.tools.javac.Main *.java
22, jar cf ngram.jar *.class
23, hadoop jar ngram.jar Driver input /output 2 3 4

check the database and finish:
24, select * from output limit 10 ;
25, then put the web page under localhost server file, then open any browser typein localhost you may access to the page and type whatever you want.
