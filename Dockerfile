FROM amazoncorretto:8-alpine3.14-jre

WORKDIR /workdir

COPY Gemfile* ./

RUN apk add --no-cache bash ca-certificates build-base ruby ruby-bundler ruby-dev \
    && echo 'gem: --no-document' > /etc/gemrc \
    && gem update --system --no-user-install \
    && bundle install \
    && adduser --uid 2004 --disabled-password --gecos "" docker

COPY docs /docs

COPY /target/universal/stage/ /workdir/
RUN chmod +x /workdir/bin/codacy-brakeman 
USER docker
ENTRYPOINT [ "bin/codacy-brakeman" ]
