#
# To build:
#     $ docker build . --tag jeantessier/dependency-finder-tomcat:1.2.1-beta6 --tag jeantessier/dependency-finder-tomcat:latest
#
# To upload to hub.docker.com:
#     $ docker push jeantessier/dependency-finder-tomcat:1.2.1-beta6
#     $ docker push jeantessier/dependency-finder-tomcat:latest
#
# To run:
#     $ docker run -it --rm -p 8080:8080 -v `pwd`/lib/DependencyFinder.jar:/code/DependencyFinder.jar:ro depfind
#     $ docker run -it --rm -p 8080:8080 -v `pwd`/lib:/code:ro depfind
#
# Mount code files in /code.
# Mount existing graph as /code/df.xml.
#

FROM tomcat:10.0

COPY dist/DependencyFinder-1.2.1-beta6.war /usr/local/tomcat/webapps/ROOT.war
