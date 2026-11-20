FROM ruby:4.0.10-alpine3.24

WORKDIR /workdir

COPY Gemfile* ./

# Lets Brakeman 4.3.1 run on Ruby 3+/4.
COPY compat.rb /opt/brakeman-compat.rb
ENV RUBYOPT="-r/opt/brakeman-compat.rb"

RUN apk add --no-cache bash ca-certificates openjdk17-jre build-base && \
    bundle install && \
    adduser -u 2004 -D docker

COPY docs /docs
COPY /target/universal/stage/ /workdir/
RUN chmod +x /workdir/bin/codacy-brakeman
USER docker
ENTRYPOINT [ "bin/codacy-brakeman" ]