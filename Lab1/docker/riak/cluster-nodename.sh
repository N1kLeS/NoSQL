#!/usr/bin/env bash

set -e

sed -i \
  "s/^nodename = .*/nodename = ${RIAK_NODENAME:-riak}@${RIAK_NODE_HOST}/" \
  "$RIAK_CONF"
