#!/bin/sh

set -eu

coordinator_container="lab1-riak1"
coordinator_node="riak@riak1.local"

join_node() {
  container_name="$1"
  node_name="$2"

  members=$(docker exec "$coordinator_container" riak-admin member-status)
  case "$members" in
    *"$node_name"*)
      echo "$node_name уже входит в кластер"
      ;;
    *)
      docker exec "$container_name" riak-admin cluster join "$coordinator_node"
      ;;
  esac
}

join_node "lab1-riak2" "riak@riak2.local"
join_node "lab1-riak3" "riak@riak3.local"

plan=$(docker exec "$coordinator_container" riak-admin cluster plan)
printf '%s\n' "$plan"

case "$plan" in
  *"There are no staged changes"*) ;;
  *) docker exec "$coordinator_container" riak-admin cluster commit ;;
esac

docker exec "$coordinator_container" riak-admin member-status
docker exec "$coordinator_container" riak-admin ring-status
