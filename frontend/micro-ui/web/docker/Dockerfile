FROM upyogio/alpine-node-builder-14:yarn AS build

RUN apk add --no-cache curl && \
    curl -fsSL https://nodejs.org/dist/v14.20.0/node-v14.20.0-linux-x64.tar.xz | tar -xJ -C /usr/local --strip-components=1 --no-same-owner && \
    node -v && npm -v
#FROM ghcr.io/egovernments/alpine-node-builder-14:yarn AS build
RUN apk update && apk upgrade
RUN apk add --no-cache git>2.30.0

RUN yarn add @upyog/digit-ui-libraries@3.0.1
RUN yarn add @upyog/digit-ui-module-pt@1.7.0-beta.4
RUN yarn add @upyog/digit-ui-module-dss@1.7.0-beta.4
RUN yarn add @upyog/digit-ui-module-mcollect@1.7.0-beta.5
RUN yarn add @upyog/digit-ui-module-pgr@3.0.4
RUN yarn add @upyog/digit-ui-module-engagement@3.0.3
RUN yarn add @upyog/digit-ui-module-hrms@1.7.0-beta.3
RUN yarn add @upyog/digit-ui-module-core@1.7.0-beta.3
RUN yarn add @upyog/digit-ui-module-common@1.7.0-beta.5
RUN yarn add @upyog/digit-ui-module-ws@3.0.6
RUN yarn add @upyog/digit-ui-module-commonpt@3.0.3
RUN yarn add @upyog/upyog-ui-module-asset@3.0.1


ARG WORK_DIR
WORKDIR /app
ENV NODE_OPTIONS "--max-old-space-size=12288"
ENV GENERATE_SOURCEMAP "false"

COPY ${WORK_DIR} .
RUN ls -lah

#RUN node web/envs.js
RUN cd web/ \
    && node envs.js \
    && node -e 'console.log(v8.getHeapStatistics().heap_size_limit/(12288*12288))' \
    &&  ./install-deps.sh \
    && yarn install \
    && yarn build 
    

FROM nginx:mainline-alpine
#FROM ghcr.io/egovernments/nginx:mainline-alpine
ENV WORK_DIR=/var/web/digit-ui

RUN mkdir -p ${WORK_DIR}

COPY --from=build /app/web/build ${WORK_DIR}/
COPY --from=build /app/web/docker/nginx.conf /etc/nginx/conf.d/default.conf
