# FROM upyogio/alpine-node-builder-14:yarn AS build
#FROM ghcr.io/egovernments/alpine-node-builder-14:yarn AS build
FROM node:22.11.0-alpine AS build
RUN apk add --no-cache git>2.30.0


ARG WORK_DIR
WORKDIR /app
ENV NODE_OPTIONS "--max-old-space-size=3584"
ENV GENERATE_SOURCEMAP "false"

COPY ${WORK_DIR} .
RUN ls -lah

#RUN node web/envs.js
RUN cd web/ \
    && node -e 'const v8=require("v8"); const s=v8.getHeapStatistics(); console.log("Total Heap Memory Allocated (MB):", (s.total_heap_size/1048576).toFixed(2)); console.log("Total Heap Memory In Use (MB):", (s.used_heap_size/1048576).toFixed(2)); console.log("Total Heap Memory Available Limit (MB):", (s.heap_size_limit/1048576).toFixed(2));' \
    && yarn install \
    && yarn build 
    

FROM nginx:mainline-alpine
#FROM ghcr.io/egovernments/nginx:mainline-alpine
ENV WORK_DIR=/var/web/upyog-ui

RUN mkdir -p ${WORK_DIR}

COPY --from=build /app/web/build ${WORK_DIR}/
COPY --from=build /app/web/docker/nginx.conf /etc/nginx/conf.d/default.conf