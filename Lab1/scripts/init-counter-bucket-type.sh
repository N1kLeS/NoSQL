#!/bin/sh

set -eu

container_name="${RIAK_CONTAINER_NAME:-lab1-riak1}"
bucket_type="page-visit-counters"

status_output=$(docker exec "$container_name" riak-admin bucket-type status "$bucket_type" 2>&1 || true)

case "$status_output" in
  *"not an existing bucket type"*)
    docker exec "$container_name" riak-admin bucket-type create \
      "$bucket_type" '{"props":{"datatype":"counter"}}'
    ;;
esac

status_output=$(docker exec "$container_name" riak-admin bucket-type status "$bucket_type" 2>&1)

case "$status_output" in
  *"active: true"*) ;;
  *) docker exec "$container_name" riak-admin bucket-type activate "$bucket_type" ;;
esac

docker exec "$container_name" riak-admin bucket-type status "$bucket_type"
