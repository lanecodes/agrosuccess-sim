#! /usr/bin/env bash

if [[ $# -eq 0 ]] ; then
    echo 'No model name specified'
    exit 1
fi

model_name=$1

as_home=/home/andrew/AgroSuccess/agrosuccess-sim
remote_model_dir=/users/k1455023/agrosuccess/$model_name

# Combine outputs from multiple instances on the server
ssh rosalind "cd $remote_model_dir; bash agrosuccess-outputcombiner.sh;"

remote_output_dir=rosalind:$remote_model_dir/combined_data/output
scp -r $remote_output_dir $as_home/output/output/$model_name
