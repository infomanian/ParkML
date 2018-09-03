# -*- coding: utf-8 -*-
"""
Created on Mon May 14 07:47:33 2018

@author: Atefeh
"""
import numpy as np
import pandas as pd
from flask import Flask, abort, jsonify, request
import cPickle as pickle

dt = pickle.load(open("Parkml3.pkl", "rb"))
colname = ["Timestamp", "Latitude", "Longitude", "Speed"]
k = 0
x = 0
Data = pd.DataFrame()
app = Flask(__name__)


@app.route('/api', methods=['POST'])
# y_decision=pickle.load(open("C:\Users\Atefeh\Desktop\UK_dataset.pkl","rb"))
def makepredict():
    global Data
    global k
    global x
    global t
    # global dfC1
    col = ['Timestamp', 'Latitude', 'Longitude', 'Speed', 'Parked', 'Prediction']
    dfC1 = pd.DataFrame(columns=col)
    data = request.get_json(force=True)

    # print(data)
    # print(type(data))
    # print(type(Data))
    dfC1.loc[0, "Timestamp"] = data['Timestamp']
    dfC1.loc[0, "Latitude"] = data['Latitude']
    dfC1.loc[0, "Longitude"] = data['Longitude']
    dfC1.loc[0, "Speed"] = data['Speed']
    dfC1.loc[0, "Parked"] = data['Parked']

    #  dfC1.loc[0,"Prediction"]
    #   dfC1.loc[0,"EnteredGeofence"]=0
    dfC2 = dfC1.head(1)
    Data = Data.append(dfC2)
    #    Data.reset_index(inplace=True)
    # #dfC=dfC.append(Data)
    df = Data

    # df=df.apply(lambda x: x.str.replace(',','.'))
    x = ['Latitude', 'Longitude', 'Speed']
    df[x] = df[x].astype(float)
    df = df.reset_index(drop=True)
    #    df.to_csv("sample1.csv")
    #    df.reset_index()
    #    for i in range(0,len(df)):
    #        if i<15:
    #            df.loc[i,'EnteredGeofence']=0

    # print(output)
    # Data.append(data)

    for i in range(0, len(df)):
        if i < 15:
            df.loc[i, 'EnteredGeofence'] = 0
        else:
            for j in range(3, 15):
                R = ((df.loc[i - j, 'Latitude'] - df.loc[i, 'Latitude']) ** 2 + (
                df.loc[i - j, 'Longitude'] - df.loc[i, 'Longitude']) ** 2)
                R1 = np.sqrt(R)
                if R1 < 0.5:
                    # and df.at[i-j,'Speed']!=0:
                    df.loc[i, 'EnteredGeofence'] = 1
                    continue
                else:
                    df.loc[i, 'EnteredGeofence'] = 0

    #    if df.ix[-1,'Parked']=='YES':
    #        df.to_exel('fulltrip.xlxs')
    #    print df.iloc[-1,:]


    #        #data=json.dumps("{'RDR_CALL_VOLUME':100,'RDR_GENERATION_TIMESTAMP':23,'RDR_TOTAL_USED_FREE_BYTES':1024,'RDR_AMA_VALIDITY_PERIOD':526}")
    #        #data=json.dumps(data)
    #        #data=json.loads(data)
    if df.iloc[-1, df.columns.get_loc("Speed")] < 5 and df.iloc[-1, df.columns.get_loc("EnteredGeofence")] == 1:  # and i>15:
        output = 'YES'
    else:
        predict_request = [df.iloc[-1, df.columns.get_loc("Speed")], df.iloc[-1, df.columns.get_loc("EnteredGeofence")]]
        y = np.asarray(predict_request)
        predict1 = y.reshape(1, -1)
        y_hat = dt.predict(predict1)
        output = y_hat[0]
    x = len(df) - 1
    df.loc[x, 'Prediction'] = output
    df.iloc[-1, df.columns.get_loc('Prediction')] = output
    Data.iloc[-1, df.columns.get_loc('Prediction')] = output
    #    if Data['Parked']=='YES':
    #        Data.to_excel("Travel.xlxs")
    if df.iloc[-1, df.columns.get_loc("Prediction")] == 'YES':
        df.to_csv("test" + str(k) + ".csv")
        k += 1
    return jsonify(results=output)


if __name__ == "__main__":
    app.run(host='192.168.1.102', port=8080, debug=True)
    # host='172.27.1.120'
