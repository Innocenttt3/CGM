import pandas as pd


def odczytaj_mapowanie(plik):
    df = pd.read_excel(plik)
    mapping_dict = dict(zip(df['stare'], df['nowe']))
    return mapping_dict


def odczytaj_i_mapuj_konto_wn(plik, mapping_dict):
    df = pd.read_excel(plik)
    df['nowe_konto_WN'] = df['KONTO_WN'].map(mapping_dict)

    return df


plik_mapowanie = "/ścieżka/do/pliku/mapowanie.xlsx"
plik_dane = "/ścieżka/do/pliku/dane.xlsx"

mapping_dict = odczytaj_mapowanie(plik_mapowanie)

df_z_nowymi_kontami = odczytaj_i_mapuj_konto_wn(plik_dane, mapping_dict)

print(df_z_nowymi_kontami)

plik_wyjsciowy = "/ścieżka/do/pliku/wynik.xlsx"
df_z_nowymi_kontami.to_excel(plik_wyjsciowy, index=False)
print("Zmodyfikowany DataFrame został zapisany do pliku:", plik_wyjsciowy)
