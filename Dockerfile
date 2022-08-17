FROM amazoncorretto:17-alpine-jdk

WORKDIR /development

## glibc needed to run embedded mongodb in alpine images
RUN apk --no-cache add ca-certificates
RUN wget -q -O /etc/apk/keys/sgerrand.rsa.pub https://alpine-pkgs.sgerrand.com/sgerrand.rsa.pub

RUN wget https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.29-r0/glibc-2.29-r0.apk
RUN wget https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.29-r0/glibc-bin-2.29-r0.apk

RUN apk add glibc-2.29-r0.apk glibc-bin-2.29-r0.apk
## glib setup ends here

COPY . .
RUN ./gradlew clean build

CMD ["java", "-jar", "build/libs/article-service.jar"]
