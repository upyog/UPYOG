FROM upyogio/alpine-node-builder-14:yarn AS build
#FROM ghcr.io/egovernments/alpine-node-builder-14:yarn AS build
RUN apk update && apk upgrade
RUN apk add --no-cache git>2.30.0
RUN yarn add @upyog/digit-ui-libraries
RUN yarn add @upyog/digit-ui-module-pt@1.8.0
RUN yarn add @upyog/digit-ui-module-fsm@1.8.0
RUN yarn add @upyog/digit-ui-module-tl@1.8.0
RUN yarn add @upyog/digit-ui-module-dss@1.8.0
RUN yarn add @upyog/digit-ui-module-mcollect@1.8.0
RUN yarn add @upyog/digit-ui-module-pgr@1.8.0
RUN yarn add @upyog/digit-ui-module-noc@1.8.0
RUN yarn add @upyog/digit-ui-module-engagement@1.8.0
RUN yarn add @upyog/digit-ui-module-hrms@1.8.0
RUN yarn add @upyog/digit-ui-module-core@1.8.0
RUN yarn add @upyog/digit-ui-module-obps@1.8.0
RUN yarn add @upyog/digit-ui-module-common@1.8.0
RUN yarn add @upyog/digit-ui-module-ws@1.8.0
RUN yarn add @upyog/digit-ui-module-commonpt@1.8.0
RUN yarn add @upyog/digit-ui-module-bills@1.8.0
RUN yarn add @upyog-niua/upyog-ui-module-ptr@1.0.9
RUN yarn add @upyog/digit-ui-react-components@1.8.0

ARG WORK_DIR
WORKDIR /app
ENV NODE_OPTIONS "--max-old-space-size=4096"
ENV GENERATE_SOURCEMAP "false"

COPY ${WORK_DIR} .
RUN ls -lah

#RUN node web/envs.js
RUN cd web/ \
    && node envs.js \
    && node -e 'console.log(v8.getHeapStatistics().heap_size_limit/(4096*4096))' \
    &&  ./install-deps.sh \
    && yarn install \
    && yarn build 
    

FROM nginx:mainline-alpine
#FROM ghcr.io/egovernments/nginx:mainline-alpine
ENV WORK_DIR=/var/web/digit-ui

RUN mkdir -p ${WORK_DIR}

COPY --from=build /app/web/build ${WORK_DIR}/
COPY --from=build /app/web/docker/nginx.conf /etc/nginx/conf.d/default.conf
