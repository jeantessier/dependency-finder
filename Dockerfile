#
# To build:
#     $ ant war
#     $ docker build . --build-arg version=1.3.1 --tag jeantessier/dependency-finder-tomcat:1.3.1 --tag jeantessier/dependency-finder-tomcat:latest
#
# To upload to hub.docker.com:
#     $ docker push jeantessier/dependency-finder-tomcat:1.3.1
#     $ docker push jeantessier/dependency-finder-tomcat:latest
#
# To run:
#     $ docker run --detach --rm --publish 8080:8080 --volume `pwd`/lib/DependencyFinder.jar:/code/DependencyFinder.jar:ro jeantessier/dependency-finder-tomcat
# or
#     $ docker run --detach --rm --publish 8080:8080 --volume `pwd`/lib:/code:ro jeantessier/dependency-finder-tomcat
#
# Mount code files in /code.
# Mount existing graph as /code/df.xml.
#

FROM tomcat

ARG version

COPY dist/DependencyFinder-${version}.war /usr/local/tomcat/webapps/ROOT.war
