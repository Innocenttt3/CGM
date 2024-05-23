import pandas as pd
import rapidfuzz

class AccountProcessor:
    synthetic_accounts = []

    def __init__(self):
        self.accounts_dict = {}

    @staticmethod
    def get_single_synthetic_account(account_number):
        if not isinstance(account_number, str):
            return None
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
            if synthetic_account:
                if synthetic_account not in self.accounts_dict:
                    self.accounts_dict[synthetic_account] = []

                self.accounts_dict[synthetic_account].append(account_number)

    def get_total_rows(self, path_to_mapping_file):
        main_data = pd.read_excel(path_to_mapping_file)
        return len(main_data)

    def process_excel(self, path_to_mapping_file, column_name):
        main_data = pd.read_excel(path_to_mapping_file)
        total_rows = len(main_data)
        for index, row in main_data.iterrows():
            account_number = row[column_name]
            if not isinstance(account_number, str) or not account_number.strip():
                main_data.at[index, 'nowe'] = ""
                main_data.at[index, '% poprawnosci'] = 0
                yield (index + 1) / total_rows
                continue

            match_info = self.find_similar_account(account_number, self.accounts_dict)
            main_data.at[index, 'nowe'] = match_info[0]
            main_data.at[index, '% poprawnosci'] = match_info[1]
            yield (index + 1) / total_rows

        main_data.to_excel(path_to_mapping_file, index=False)
        print("Gotowe")

    def find_similar_account(self, old_account_number, accounts_dict):
        synthetic_account = self.get_single_synthetic_account(old_account_number)
        if synthetic_account is None:
            return None, 0
        group_to_search = accounts_dict.get(synthetic_account, [])
        if group_to_search:
            best_matches = rapidfuzz.process.extractOne(old_account_number, group_to_search, scorer=rapidfuzz.fuzz.WRatio)
            return best_matches[0], best_matches[1]
        else:
            return None, 0
