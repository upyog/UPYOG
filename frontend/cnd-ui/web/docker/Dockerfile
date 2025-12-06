FROM upyogio/alpine-node-builder-14:yarn AS build
RUN apk update && apk upgrade
RUN apk add --no-cache git>2.30.0
RUN yarn add @nudmcdgnpm/digit-ui-libraries
RUN yarn add @upyog/digit-ui-module-core
RUN yarn add @upyog/digit-ui-module-common
RUN yarn add @nudmcdgnpm/upyog-ui-module-cnd

ARG WORK_DIR
WORKDIR /app
ENV NODE_OPTIONS "--max-old-space-size=1792"
ENV GENERATE_SOURCEMAP "true"

COPY ${WORK_DIR} .
RUN ls -lah

#RUN node web/envs.js
RUN cd web/ \
    && node envs.js \
    && node -e 'console.log(v8.getHeapStatistics().heap_size_limit/(1792*1792))' \
    &&  ./install-deps.sh \
    && yarn install \
    && yarn build 
    

FROM nginx:mainline-alpine
ENV WORK_DIR=/var/web/cnd-ui

RUN mkdir -p ${WORK_DIR}

COPY --from=build /app/web/build ${WORK_DIR}/
COPY --from=build /app/web/docker/nginx.conf /etc/nginx/conf.d/default.conf
