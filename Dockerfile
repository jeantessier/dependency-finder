#
# To build:
#     $ ./gradlew webapp:build
#     $ docker build . --build-arg version=1.3.2 --tag jeantessier/dependency-finder-tomcat:1.3.2 --tag jeantessier/dependency-finder-tomcat:latest
#
# To upload to hub.docker.com:
#     $ docker push jeantessier/dependency-finder-tomcat:1.3.2
#     $ docker push jeantessier/dependency-finder-tomcat:latest
#
# To run:
#     $ docker run --detach --rm --publish 8080:8080 --volume `pwd`/lib/build/libs/lib.jar:/code/DependencyFinder.jar:ro jeantessier/dependency-finder-tomcat
# or
#     $ docker run --detach --rm --publish 8080:8080 --volume `pwd`/lib:/code:ro jeantessier/dependency-finder-tomcat
#
# Mount code files in /code.
# Mount existing graph as /code/df.xml.
#

FROM tomcat

ARG version

# Sets "compilerSourceVM" and "compilerTargetVM" initialization parameters of
# the "jsp" servlet so JSPs can use Java features beyond the default Java 11.
RUN sed -i -e '/org.apache.jasper.servlet.JspServlet/a <init-param><param-name>compilerSourceVM</param-name><param-value>21</param-value></init-param><init-param><param-name>compilerTargetVM</param-name><param-value>21</param-value></init-param>' conf/web.xml

COPY webapp/build/libs/webapp.war /usr/local/tomcat/webapps/ROOT.war
