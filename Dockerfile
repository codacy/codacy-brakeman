FROM amazoncorretto:8-alpine3.14-jre

COPY Gemfile .
COPY Gemfile.lock .

RUN apk add --no-cache bash ca-certificates build-base ruby ruby-bundler ruby-dev \
    && echo 'gem: --no-document' > /etc/gemrc \
    && gem install bundler -v 2.1.4 \
    && bundle install \
    && gem cleanup \
    && apk del build-base ruby-bundler ruby-dev \
    && rm -rf /tmp/* /var/cache/apk/*
