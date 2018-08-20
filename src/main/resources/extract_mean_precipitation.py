# coding utf-8
"""Extract mean annual precipitation values for study sites.

Using the BC paleo-GCM data available from the below link, extract the 
mean annual precipitation values for each study site.

http://www.worldclim.org/paleo-climate1
"""
from subprocess import check_output
from zipfile import ZipFile
from bs4 import BeautifulSoup
import pickle
import os

import pandas as pd

ssite_dict = {'albufera_alcudia': {'lat': 39.792778, 'lon': 3.119167},
              'laguna_guallar': {'lat': 41.4, 'lon': -0.216667},
              'monte_areo_mire': {'lat': 43.528889, 'lon': -5.768889},
              'navarres': {'lat': 39.1, 'lon': -0.683333},
              'sanabria_marsh': {'lat': 42.1, 'lon': -6.733333},
              'san_rafael': {'lat': 36.773611, 'lon': -2.601389}
              }

zip_fname = '/media/storage/dbase/climate/worldclim/midHolocene/BC/'\
            '30seconds/bcmidpr_30s.zip'

try:
    zip=ZipFile(zip_fname)
except IOError:
    print 'Could not find precipitation zip file, downloading from web...'
    import requests

    def download_file(url):
        local_filename = url.split('/')[-1]
        # NOTE the stream=True parameter
        r = requests.get(url, stream=True)
        with open(local_filename, 'wb') as f:
            for chunk in r.iter_content(chunk_size=1024): 
                if chunk: # filter out keep-alive new chunks
                    f.write(chunk)
        return local_filename

    data_url = 'http://biogeo.ucdavis.edu/data/climate/cmip5/mid/bcmidpr_30s.zip'
    download_file(data_url)
    print 'finished downloading precipitation data'

    zip_fname = 'bcmidpr_30s.zip'
    zip=zipfile.ZipFile(data_zip)

def get_monthly_precip_from_file(fname, lat, lon):
    cmd = 'gdallocationinfo -xml -wgs84 {0} {1} {2}'.format(fname, lon, lat)
    res = check_output(cmd, shell=True)
    soup = BeautifulSoup(res, "xml")
    return float(soup.Value.string)

def get_month_number(fname):
    """Extract month number from precipitation file name"""
    return  str(fname[7:].split('.tif')[0])
    
def add_precipitation_data_to_dict(ssite_dict, zipfile):
    for tif_file in zipfile.namelist():
        print 'processing: ' + tif_file
        for ssite in ssite_dict.keys():
            zip.extract(tif_file)
            month_num = get_month_number(tif_file)
            precip = get_monthly_precip_from_file(tif_file, ssite_dict[ssite]['lat'],
                                                  ssite_dict[ssite]['lon'])
            try:
                ssite_dict[ssite]['precip_months'][month_num] = precip
            except KeyError:
                ssite_dict[ssite]['precip_months'] = {}
                ssite_dict[ssite]['precip_months'][month_num] = precip

            os.remove(tif_file)

    with open('ssite_precip_data.pkl', 'wb') as f:
        pickle.dump(ssite_dict, f)

    return ssite_dict

try:
    with open('ssite_precip_data.pkl', 'rb') as f:
        ssite_dict = pickle.load(f)
except (IOError, EOFError) as e:
    print 'extracting precipitation data from zipfile'
    ssite_dict = add_precipitation_data_to_dict(ssite_dict, zip)

# convert dict data to a dataframe indexed by study site name
list_of_dicts = []
for ssite_name in ssite_dict.keys():
    d = {'ssite_name': ssite_name}
    month_dicts = ssite_dict[ssite_name]['precip_months']
    for month in month_dicts.keys():
        d[month] = month_dicts[month]
    list_of_dicts.append(d)

df = pd.DataFrame(list_of_dicts).set_index('ssite_name')
df.columns = [int(col) for col in df.columns]
df = df.sort_index(axis=1)
df['mean'] = df.mean(axis=1)
df['std'] = df.std(axis=1)
df = df.round()
for c in df.columns:
    df[c] = df[c].astype('int32')
print 'writing precipitation data to csv...'
df.to_csv('ssite_precipitation.tsv', sep='\t')
print 'done'

    


    
    
