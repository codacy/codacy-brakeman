# Codacy Brakeman

This is the docker engine we use at Codacy to have [Brakeman](http://brakemanscanner.org/) support.
You can also create a docker to integrate the tool and language of your choice!
Check the **Docs** section for more information.

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/25f3667db77c4c638be01d9f7453dec8)](https://www.codacy.com/gh/codacy/codacy-brakeman?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=codacy/codacy-brakeman&amp;utm_campaign=Badge_Grade)
[![Build Status](https://circleci.com/gh/codacy/codacy-brakeman.svg?style=shield&circle-token=:circle-token)](https://circleci.com/gh/codacy/codacy-brakeman)

## Usage

You can create the docker by doing:

```bash
docker build -t codacy-brakeman-base .
sbt docker:publishLocal
```

The docker is ran with the following command:

```bash
docker run -it -v $srcDir:/src  <DOCKER_NAME>:<DOCKER_VERSION>
```

## Docs

[Tool Developer Guide](https://support.codacy.com/hc/en-us/articles/207994725-Tool-Developer-Guide)

[Tool Developer Guide - Using Scala](https://support.codacy.com/hc/en-us/articles/207280379-Tool-Developer-Guide-Using-Scala)

## Test

We use the [codacy-plugins-test](https://github.com/codacy/codacy-plugins-test) to test our external tools integration.
You can follow the instructions there to make sure your tool is working as expected.

## Update Version with Docs

```sh
bundle update
bundle install
bundle exec ruby src/main/ruby/codacy/brakeman/DocumentationGenerator.rb
```

Both `patterns.json` and `description.json` will be updated.
Copy the output and update the Brakeman.scala `warningToPatternId` method.

## What is Codacy

[Codacy](https://www.codacy.com/) is an Automated Code Review Tool that monitors your technical debt, helps you improve your code quality, teaches best practices to your developers, and helps you save time in Code Reviews.

### Among Codacy’s features

-   Identify new Static Analysis issues
-   Commit and Pull Request Analysis with GitHub, BitBucket/Stash, GitLab (and also direct git repositories)
-   Auto-comments on Commits and Pull Requests
-   Integrations with Slack, HipChat, Jira, YouTrack
-   Track issues in Code Style, Security, Error Proneness, Performance, Unused Code and other categories

Codacy also helps keep track of Code Coverage, Code Duplication, and Code Complexity.

Codacy supports PHP, Python, Ruby, Java, JavaScript, and Scala, among others.

### Free for Open Source

Codacy is free for Open Source projects.
