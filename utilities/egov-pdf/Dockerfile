#FROM  upyogio/alpine-node-builder-14:yarn
FROM node:18

ARG WORK_DIR
ENV npm_config_cache=/tmp
WORKDIR /app

COPY ${WORK_DIR}/package.json ${WORK_DIR}/yarn.lock .

RUN yarn

COPY ${WORK_DIR}/src .

CMD ["yarn", "start"]
