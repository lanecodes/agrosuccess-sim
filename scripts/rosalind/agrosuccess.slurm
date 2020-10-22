#!/bin/bash -l

#SBATCH --job-name=AgroSuccess-0
#SBATCH --partition=shared
#SBATCH --nodes=1
#SBATCH --ntasks-per-node=2
#SBATCH --mem-per-cpu=1500
#SBATCH --time=0-0:20
#SBATCH --output=output.txt
#SBATCH --mail-user=andrew.lane@kcl.ac.uk
#SBATCH --mail-type=all

module load apps/openjdk/12.0.1

cd $SLURM_SUBMIT_DIR

ROOT=$( /bin/pwd )
PARAMETER_FILE=$ROOT/unrolledParamFile.txt
TOTAL_LINES=`wc -l $PARAMETER_FILE | cut -f1 -d' '`
echo "Total lines: $TOTAL_LINES"

PROCS=$(($SLURM_NTASKS_PER_NODE * $SLURM_JOB_NUM_NODES))
if [ "$TOTAL_LINES" -lt "$PROCS" ]
then
    echo "Number of allocated processes ($PROCS) is greater than the number of parameter combinations to run ($TOTAL_LINES). Nu\
mber of processes is equal to --nodes ($SLURM_JOB_NUM_NODES) * --ntasks-per_node ($SLURM_NTASKS_PER_NODE). Aborting job."
exit
fi

echo "Total Processes: $PROCS"

srun --ntasks $PROCS --ntasks-per-node=$SLURM_NTASKS_PER_NODE $SLURM_SUBMIT_DIR/repastwrapper_slurm.sh $TOTAL_LINES $PARAMETER_FILE
