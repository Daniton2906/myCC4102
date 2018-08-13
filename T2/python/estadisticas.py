# -*- coding: utf-8 -*-
"""
Created on Wed Jul  4 10:56:36 2018

@author: Daniel
"""

import pandas
import os
import numpy as np

sort_path = r'../results/sort/'
sort_list = os.listdir(sort_path)
ins_meld_path = r'../results/ins_meld/'
ins_meld_list = os.listdir(ins_meld_path)

'''
sort_fn = {'heap-clasico': [],
           'cola-binomial': [],
           'cola-fibonacci': [],
           'leaftist-heap': [],
           'skew-heap': []}
'''

def codify(sort_path, sort_list):
    sort_dict = {}
    for filename in sort_list:
        aux = filename.split('-')
        key = "{}-{}".format(aux[0],aux[1])
        # print(sort_path + filename)
        heap_dataframe = pandas.read_csv(sort_path + filename, sep='\t', header=0)    
        if key not in sort_dict:
            sort_dict[key] = {}
        #print(heap_dataframe) # (7. 4)
        i = heap_dataframe.columns[0] # i
        for row in range(heap_dataframe.shape[0]):        
            for head in heap_dataframe.columns[1:]:
                head_key = "{}#{}".format(head, heap_dataframe.loc[row, i])
                if head_key not in sort_dict[key]:
                    sort_dict[key][head_key] = []
                sort_dict[key][head_key].append(heap_dataframe.loc[row, head])
    return sort_dict, heap_dataframe.columns
            
sort_dict, sort_columns = codify(sort_path, sort_list)
ins_meld_dict, ins_meld_columns = codify(ins_meld_path, ins_meld_list)

# print(ins_meld_dict)
# print(ins_meld_columns)

# print(sort_dict)
def to_csv(sort_dict, name, columns, i_list):
    mean_sort = {}
    std_sort = {}
    for key, value in sort_dict.items():
        if key not in mean_sort:
            mean_sort[key] = {}
            std_sort[key] = {}
        for key2, value2 in value.items():
            print(len(value2))
            mean_sort[key][key2] = np.mean(value2)
            std_sort[key][key2] = np.std(value2)
            
    #print(mean_sort)
    #print("")
    #print(std_sort)
    header = 'i'
    for column in columns[1:]:
        header += '\t' + column
    for key in mean_sort.keys():
        fn_mean = '{}-{}-{}.tsv'.format(key, name, 'mean')
        fn_std = '{}-{}-{}.tsv'.format(key, name, 'std')
        fd_mean = open(fn_mean, 'w')
        fd_std = open(fn_std, 'w')
        fd_mean.write(header + '\n')
        fd_std.write(header + '\n')
        for i in i_list:
            s_sort = str(i)
            s_std = str(i)
            for column in columns[1:]:                
                s_sort += '\t' + str(mean_sort[key][column + "#" + str(i)]).replace('.', ',')
                s_std += '\t' + str(std_sort[key][column + "#" + str(i)]).replace('.', ',')
            fd_mean.write(s_sort + '\n')
            fd_std.write(s_std + '\n')
        fd_mean.close()
        fd_std.close()
        
to_csv(sort_dict, 'sort', sort_columns, (15,16,17,18,19,20,21))
to_csv(ins_meld_dict, 'ins_meld', ins_meld_columns, (15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0))
        
            

'''
for key, lista in sort_dict.items():
    print(key)
    for e in lista:
        print(e.columns[2])
        print(e.loc[0, e.columns[2]])
    print("-----------------------------------")
'''

# data = pandas.read_csv(sort_path + 'cola-binomial-sort-1528228733146.tsv', sep='\t', header=0)

#print(data)

#for c in data.columns:
#    print(data[c])