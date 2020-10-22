#! /usr/bin/env bash

if [[ $# -eq 0 ]] ; then
    echo 'No model name specified'
    exit 1
fi

model_name=$1

as_home=/home/andrew/AgroSuccess/agrosuccess-sim
as_model_archive=$as_home/output/complete_model.jar
as_run_script=$as_home/scripts/rosalind/runs/$model_name.slurm
as_wrapper=$as_home/scripts/rosalind/repastwrapper_slurm.sh
as_combine_script=$as_home/scripts/rosalind/agrosuccess-outputcombiner.sh

tmp_dir=/tmp/agrosuccess_$(date +%s)/$model_name
mkdir -p $tmp_dir

cp $as_model_archive $as_run_script $as_wrapper $as_combine_script $tmp_dir
scp -r $tmp_dir rosalind:/users/k1455023/agrosuccess
