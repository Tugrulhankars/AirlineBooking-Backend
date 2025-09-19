FROM ubuntu:latest
LABEL authors="karsl"

ENTRYPOINT ["top", "-b"]