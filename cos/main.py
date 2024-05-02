import pandas as pd
import getpass
import rapidfuzz
import oracledb


def get_single_synthetic_account(account_number):
    if '-' in account_number:
        index = account_number.index('-')
        synthetic_account = account_number[:index]
    else:
        synthetic_account = account_number

    return synthetic_account


def get_synthetic_accounts(accounts_to_map):
    synthetic_accounts = []
    seen_accounts = set()

    for account in accounts_to_map:
        if '-' in account:
            index = account.index('-')
            synthetic_account = account[:index]
        else:
            synthetic_account = account

        if synthetic_account not in seen_accounts:
            synthetic_accounts.append(synthetic_account)
            seen_accounts.add(synthetic_account)

    return synthetic_accounts


def fetch_accounts(synthetic_accounts):
    accounts_dict = {}
    connection = oracledb.connect(
        user=lg,
        password=pw,
        dsn="localhost:8007/clinprd1")

    query = f"SELECT * FROM fk.pelny_plan_kont WHERE account_number like :account_id%"

    for account in synthetic_accounts:
        params = {'account_id': account}
        cursor = connection.cursor()
        cursor.execute(query, params)
        result = cursor.fetchall()
        accounts_dict[account] = result
        cursor.close()

    connection.close()
    return accounts_dict


def process_excel(path_to_mapping_file):
    main_data = pd.read_excel(path_to_mapping_file)
    main_data['match_info'] = main_data['test'].apply(find_similar_account)
    main_data['nowe'] = main_data['match_info'].apply(lambda x: x[0])
    main_data['procent szans'] = main_data['match_info'].apply(lambda x: x[1])
    main_data.to_excel(path_to_mapping_file, index=False)


def find_similar_account(old_account_number):
    synthetic_account = get_single_synthetic_account(old_account_number)
    group_to_search = accounts_dict[synthetic_account]
    best_matches = rapidfuzz.process.extractOne(old_account_number, group_to_search, scorer=rapidfuzz.fuzz.WRatio)
    return best_matches[0], best_matches[1]


path_to_mapping_file = '/Users/kamilgolawski/CGM/CGM-priv/testRapida.xlsx'
data_to_map = pd.read_excel(path_to_mapping_file, sheet_name=0)
acc_column = data_to_map['test']
acc_to_map = acc_column.tolist()

synthetic_accounts = get_synthetic_accounts(acc_to_map)
accounts_dict = fetch_accounts(synthetic_accounts)
process_excel(path_to_mapping_file)
