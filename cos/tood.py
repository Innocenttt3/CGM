konta_do_mapowania = dane_do_mapowania['test'].tolist()
nowe_konta = dane_do_ppk['dane'].tolist()


def znajdz_najblizniejsze_konto(stare_konto):
    najlepsze_dopasowanie = rapidfuzz.process.extractOne(stare_konto, nowe_konta,
                                                         scorer=rapidfuzz.fuzz.WRatio)
    return najlepsze_dopasowanie[0], najlepsze_dopasowanie[1]


dane_do_mapowania['nowa'] = dane_do_mapowania['test'].apply(znajdz_najblizniejsze_konto).apply(lambda x: x[0])

dane_do_mapowania.to_excel('/Users/kamilgolawski/CGM/CGM-priv/testRapida.xlsx', index=False)
