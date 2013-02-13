#Instructions to install the hcplab games#

##Install the linux environment##

###Install the mysql###

  Install MYSQL: 
  ```bash     
  sudo apt-get install mysql-server
  #You will need to enter a new password for the root user.
  ```
  When the installation is finished. Verify the the mysql server:
    
  ```bash     
  mysql -u root -proot
  ```
  
  In the mysql client shell, enter the command:
    
    use mysql (to select the mysql databae)
    
    show tables (You should see a list of table of the mysql database).
  
  To manually start/stop the mysql server:
  
  ```bash     
      sudo /etc/init.d/mysql start (to start the server)
    
      sudo /etc/init.d/mysql stop (to stop the server)
    
      sudo /etc/init.d/mysql restart (to stop and then start the server)
  ```

###Install the appache server###

  Install the apache server:
  
  ```bash     
       apt-get install apache2
  ```
   
  To verify the apache installation enter the hostname of ip of the server into the browser address bar

    http://ip  or http://hostname
    #You should see the message "It works!" 
  
  Maybe by default, the linux or window operating system will block the port 80. Check and make sure your OS security setting is properly configured.

###Install JAVA and JDK###
  
  You can either download the jdk from http://www.oracle.com/technetwork/java/javase/downloads/index.html or use the apt-get command to install
  
    apt-get install openjdk-7-jdk

###Install the tomcat server###
  
  Download the latest tomcat server version from:
    
    http://tomcat.apache.org
  
  Upload the tomcat package to the server:
  
    scp apache-tomcat-version.tar.gz username@hostname:~/
  
  Install the tomcat server:
    
    #Usually the optional package are installed in the /opt directory.
    cp apache-tomcat-version.tar.gz /opt
    
    #Unzip and extract
    tar -zxvf apache-tomcat-version.tar.gz
    
    #Rename the tomcat directory name, to make the name shorter
    mv apache-tomcat-version tomcat-version
  
  Verify the tomcat server:
    #Change to tomcat bin directory
    cd /opt/tomcat-version/bin
    
    #Run tomcat in the console mode
    ./catalina.sh run
    
  You should see tomcat start and the log, there should be no error or exception. Open the browser, enter the url
    
    http://hostname:8080
  
  You should see this message "If you're seeing this, you've successfully installed Tomcat. Congratulations!"
  
  Start the tomcat server automatically when the os start or restart
    #Create the the tomcat.sh file in the /etc/init.d directory.
    vi /etc/init.d/tomcat.sh

    #You can find this script githup repository in game-name/src/main/tomcat/tomcat.sh
    #The script should have:
    
    #!/bin/sh
    #
    # /etc/init.d/tomcat
    #
    # This is the init script for starting up the
    #  Jakarta Tomcat server
    #
    # description: Starts and stops the Tomcat daemon.
    #
    tomcat=/opt/tomcat-7.0.35
    startup=$tomcat/bin/startup.sh
    shutdown=$tomcat/bin/shutdown.sh

    start() {
      echo -n $"Starting Tomcat service: "
      sh $startup
      echo $?
    }

    stop() {
      echo -n $"Stopping Tomcat service: "
      sh $shutdown
      echo $?
    }

    restart() {
      stop
      start
    }

    status() {
      ps -aef | grep tomcat
    }

    # Handle the different input options
    case "$1" in
    start)
      start
      ;;
    stop)
      stop
      ;;
    status)
      status
      ;;
    restart)
      restart
      ;;
    *)
      echo $"Usage: $0 {start|stop|restart|status}"
      exit 1
    esac

    exit 0

  If you do not change to super user, you will need to add su to run the command in the super user mode. For exampple:
  
    su cp apache-tomcat-7.0.35.tar.gz /opt
    su tar -zxvf apache-tomcat-version.tar.gz

###Build and Deploy Wishes-Game###

  Checkout the Wishes-Game from githup repogitory
  
    git clone git@github.com:liscovich/Wishes-game.git
  
  Build the Whishes-Game:
  
    #Go to the Whishes-Game directory
    cd /path/to/Whishes-Game
  
    #Build the Whishes-Game with maven
    mvn clean install 
    #The build process should produce a hks.war file in the target directory
  
  Copy the game to the deployed server and run

    #Copy the hks.war to the deployed server
    scp target/hks.war username@hostname:~/
  
    #Copy the ddl.sql to the deployed server. This ddl.sql is used to create the hks user and hks tables
    scp src/main/resources/ddl.sql username@hostname:~/
  
    #Login to the deployed server
    ssh username@hostname
  
    #Deploy the hks.war to tomcat
    cp hks.war /opt/tomcat-version/webapps
  
    #For the first time and every time you update the hks database structure, you need to run the ddl.sql script again
    #Open the ddl.sql to see what the script does and some time you need to comment out the create user , 
    #create database instructions 
    mysql -u root -proot < ddl.sql
  
    #Go to the tomcat directory
    cd /opt/tomcat-version/bin
  
    #Launch tomcat in console mode
    ./catalina.sh run
  
    #To check if the game in launched properly, open the browser, enter http://hostname:8080/hks/admin_0982347509238477.html

###Configure tomcat and apache server###

  Make sure that mod_jk is installed. 
    #Check to see if the mod_jk is installed
    ls /etc/apache2/mods-available/jk.*
    #You should see the file jk.load and jk.conf
    
    #If the mod_jk is not installed yet. Install it with the apt-get command
    apt-get install libapache2-mod-jk
  
  Change the apache configuration
  
    #Open the file /etc/apache2/mods-available/jk.conf
    #Change:
      JkWorkersFile /etc/libapache2-mod-jk/workers.properties
      To
      JkWorkersFile /etc/apache2/jk_workers.properties
    
    #Create the file /etc/apache2/jk_workers.properties
    vi /etc/apache2/jk_workers.properties
    #You should have the following properties:
      workers.tomcat_home=/opt/tomcat-7.0.35
      workers.java_home=/usr/lib/jvm/java-7-openjdk-amd64
      ps=/
      worker.list=ajp13
      worker.ajp13_worker.port=8009
      worker.ajp13_worker.host=localhost
      worker.ajp13_worker.type=ajp13
      worker.ajp13_worker.lbfactor=1
      worker.loadbalancer.type=lb
      worker.loadbalancer.balance_workers=ajp13_worker
  
  Enable mod_jk configurations: 
    #Create a link in /etc/apache2/mods-enabled/jk.conf which points to /etc/apache2/mods-available/jk.conf. 
    #This will enable mod_jk configuration in apache when apache is restarted.
    ln -s /etc/apache2/mods-available/jk.conf /etc/apache2/mods-enabled/jk.conf
    ln -s /etc/apache2/mods-available/jk.load /etc/apache2/mods-enabled/jk.load
  
  Configure url forwarding in apache to tomcat: Put the following lines in you apache virtualhost to forward requests to tomcat.
  
    <VirtualHost *:80>
      ...
      #Send everything for wishes game "/hks" to worker ajp13
      JkMount /hks/ ajp13
      JkMount /hks/* ajp13

      #Send everything for coloring game "/hcp" to worker ajp13
      JkMount /hcp/ ajp13
      JkMount /hcp/* ajp13
      ...
    </VirtualHost>
    
  Configure AJP in tomcat server. Make sure the following line in $TOMCAT_HOME/conf/server.xml file under the Servies tag.
  
    <Service name="Catalina">
      ...
      <!-- Define an AJP 1.3 Connector on port 8009 -->
      <Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />
      ...
    </Service>
  
  Restart the apache server for the new configuration
  
    #Restart the apache server
    /etc/init.d/apache2 restart
