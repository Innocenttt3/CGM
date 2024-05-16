from logic import AccountProcessor

if __name__ == "__main__":
    processor = AccountProcessor()
    processor.fetch_accounts_from_excel(self.ppk_path_file, self.column_in_ppk)
    processor.process_excel(self.map_path_file, self.column_old_acc)