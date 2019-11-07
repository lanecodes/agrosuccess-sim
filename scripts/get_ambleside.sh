#!/usr/bin/env bash

PLACENAME="ambleside"
THISDIR="${PWD}"
PYTHON="/home/andrew/anaconda2/envs/gsa2017sa/bin/python"
MODDIR="/home/andrew/Dropbox/codes/python/notebooks/download_site_elevation_data"

cd $MODDIR
$PYTHON -m getelev $THISDIR/$PLACENAME.tif 27700 54.4251 -2.9626 10000
cd $THISDIR
