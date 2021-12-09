FROM java:8
EXPOSE 8080     # 这只是暴露容器的端口,所有微服务都可以用这个

VOLUME /tmp
ADD target/*.jar  /app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-jar","-Xms256m","-Xmx512m","/app.jar"]
