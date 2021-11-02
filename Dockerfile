FROM ruby:2.7.4-alpine3.14

WORKDIR /workdir

COPY Gemfile* ./

RUN apk add --no-cache bash ca-certificates openjdk11 ruby-dev build-base && \
    bundle install && \
    adduser --uid 2004 --disabled-password --gecos "" docker

COPY docs /docs

COPY /target/universal/stage/ /workdir/
RUN chmod +x /workdir/bin/codacy-brakeman 
USER docker
ENTRYPOINT [ "bin/codacy-brakeman" ]
