#!/bin/bash

curl -X PUT "http://localhost:9200/tasks" \
  -H "Content-Type: application/json" \
  -d "{
    \"settings\": $(cat tasks-settings.json),
    \"mappings\": $(cat tasks-mapping.json)
  }"