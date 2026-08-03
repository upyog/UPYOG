FROM node:22.0.0-alpine AS build
RUN apk update && apk upgrade
RUN apk add --no-cache git>2.30.0
ARG WORK_DIR
WORKDIR /app
ENV NODE_OPTIONS "--max-old-space-size=8168"
ENV GENERATE_SOURCEMAP "false"


COPY ${WORK_DIR} .
RUN ls -lah

RUN cd web/ \
    && node -e 'console.log(v8.getHeapStatistics().heap_size_limit/(1024*1024))' \
    && yarn install \
    && yarn build

FROM nginx:mainline-alpine
ENV WORK_DIR=/var/web/workbench-ui
RUN mkdir -p ${WORK_DIR}

COPY --from=build /app/web/build ${WORK_DIR}/
COPY --from=build /app/web/docker/nginx.conf /etc/nginx/conf.d/default.conf