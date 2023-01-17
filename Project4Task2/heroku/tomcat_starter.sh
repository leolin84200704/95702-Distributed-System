#!/bin/bash
# The above line, #!/bin/bash, identifies this file
# as a bash shell script.

# It should be made executable using
#    CMD chmod +x /home/tomcat_starter.sh
# in the Dockerfile.

# sed is the Linux stream editor used to edit files in-place
#    automatically instead of manually
# The s/a/b/ option substitutes the first thing (8080) for
#    the second thing ($PORT) in the server.xml file

# Change the configuration of Tomcat so that it listens to
# the port assigned by Heroku
sed -i s/8080/$PORT/ /usr/local/tomcat/conf/server.xml

# delete the default ROOT directory so ROOT.war is used
rm -rf /usr/local/tomcat/webapps/ROOT

# start the server using the built-in catalina.sh bash script
catalina.sh run