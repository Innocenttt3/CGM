import pandas as pd
import rapidfuzz


class AccountProcessor:
    synthetic_accounts = []

    def __init__(self):
        self.accounts_dict = {}

    @staticmethod
    def get_single_synthetic_account(account_number):
        if '-' in account_number:
            index = account_number.index('-')
            synthetic_account = account_number[:index]
        else:
            synthetic_account = account_number
        return synthetic_account

    def fetch_accounts_from_excel(self, path_to_excel_file, column_name):
        main_data = pd.read_excel(path_to_excel_file)

        for index, account_number in main_data.loc[:, column_name].items():
            synthetic_account = self.get_single_synthetic_account(account_number)
            print(synthetic_account)

            if synthetic_account not in self.accounts_dict:
                self.accounts_dict[synthetic_account] = []

            self.accounts_dict[synthetic_account].append(account_number)

    def process_excel(self, path_to_mapping_file, column_name):
        main_data = pd.read_excel(path_to_mapping_file)
        main_data['match_info'] = main_data[column_name].apply(lambda x: self.find_similar_account(x, self.accounts_dict))
        main_data['nowe'] = main_data['match_info'].apply(lambda x: x[0])
        main_data['% poprawnosci'] = main_data['match_info'].apply(lambda x: x[1])
        main_data.to_excel(path_to_mapping_file, index=False)

    def find_similar_account(self, old_account_number, accounts_dict):
        synthetic_account = self.get_single_synthetic_account(old_account_number)
        group_to_search = accounts_dict[synthetic_account]
        best_matches = rapidfuzz.process.extractOne(old_account_number, group_to_search, scorer=rapidfuzz.fuzz.WRatio)
        return best_matches[0], best_matches[1]
