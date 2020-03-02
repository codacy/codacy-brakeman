FROM openjdk:8-jre-alpine

WORKDIR /workdir

RUN apk add --no-cache bash ca-certificates build-base ruby ruby-bundler ruby-dev \
    && echo 'gem: --no-document' > /etc/gemrc \
    && cd /opt/docker/setup \
    && bundle install \
    && gem cleanup \
    && apk del build-base ruby-bundler ruby-dev \
    && rm -rf /opt/docker/setup /tmp/* /var/cache/apk/*
