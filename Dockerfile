# TODO manually build shadow jar outside of docker container, then copy inside
# This saves me from having to provide a proper build script, etc... Sorry.
FROM openjdk:8
EXPOSE 3000
RUN update-java-alternatives --set java-1.8.0-openjdk-amd64

WORKDIR /usr/src/app

COPY build/libs/stanford-corenlp-1.0-all.jar .
COPY stanford-corenlp-3.7.0-models.jar .
CMD java -cp 'stanford-corenlp-1.0-all.jar:stanford-corenlp-3.7.0-models.jar' it.skim.Main