#!/bin/bash
export PGPASSWORD='G12@kL4$$Px8!Rt3#Qw9Z'
psql -h 190.146.2.119 -p 2345 -U grupo12_user -d grupo12 -c "SELECT id FROM empresa LIMIT 1;"
