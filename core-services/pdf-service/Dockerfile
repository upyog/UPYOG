FROM egovio/alpine-node-builder-10:yarn

ARG WORK_DIR
ENV npm_config_cache=/tmp
WORKDIR /app

RUN apk update && \
    apk add --no-cache ca-certificates && \
    update-ca-certificates

COPY ${WORK_DIR} .

RUN npm install

# set your port
ENV PORT 8080
# expose the port to outside world
#EXPOSE  8080

# start command as per package.json
CMD ["npm", "start"]
