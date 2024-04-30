import pandas as pd
import rapidfuzz


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


# def fetch_accounts(synthetic_accounts):
#     accounts_dict = {}
#     for i, account in enumerate(synthetic_accounts):
#         accounts_dict[account] = list(range(i))
#     return accounts_dict

def fetch_accounts():
    accounts_dict = {}
    new_acc = pd.read_excel('/Users/kamilgolawski/CGM/CGM-priv/testRapidaPPK.xlsx')
    accounts_dict['200'] = new_acc['dane1']
    accounts_dict['011'] = new_acc['dane2']
    accounts_dict['070'] = new_acc['dane3']
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
accounts_dict = fetch_accounts()
process_excel(path_to_mapping_file)

