from customtkinter import *
import os

from logic import AccountProcessor


class FileChooserApp(CTk):
    ppk_path_file = None
    map_path_file = None
    column_in_ppk = ''
    column_old_acc = ''

    def __init__(self, master=None, **kwargs):
        super().__init__(master, **kwargs)
        self.title('Auto Mapowanie Kont')
        self.ppk_path_file = None
        self.map_path_file = None
        self.column_in_ppk = ''
        self.column_old_acc = ''
        self.master = master
        self.geometry('450x200')
        set_appearance_mode('dark')

        # Mapping file button
        self.mapping_file_button = CTkButton(master=self, text='Wybierz plik do mapowania',
                                             text_color='white', command=self.choose_map_file, width=200)
        self.mapping_file_button.grid(row=0, column=0, padx=10, pady=10)

        # Entry field for old account name
        self.entry_field_old_acc_name = CTkEntry(master=self, placeholder_text='Nazwa kolumny starych kont',
                                                 width=200)
        self.entry_field_old_acc_name.grid(row=0, column=1, padx=10, pady=10)

        # PPK file button
        self.full_chart_button = CTkButton(master=self, text='Wybierz plik z PPK', text_color='white',
                                           command=self.choose_ppk_file, width=200)
        self.full_chart_button.grid(row=1, column=0, padx=10, pady=10)

        # Entry field for PPK name
        self.entry_field_ppk_name = CTkEntry(master=self, placeholder_text='Nazwa kolumny PPK',
                                             width=200)
        self.entry_field_ppk_name.grid(row=1, column=1, padx=10, pady=10)

        # Execute button
        self.execute_button = CTkButton(master=self, text='Wykonaj', text_color='white',
                                        command=self.execute_processing, width=200)
        self.execute_button.grid(row=2, columnspan=2, padx=10, pady=10)
        self.execute_button.configure(fg_color="#00008B")

    def choose_ppk_file(self):
        file_path = filedialog.askopenfilename()
        if file_path:
            normalized_path = os.path.normpath(file_path)
            self.ppk_path_file = normalized_path

    def choose_map_file(self):
        file_path = filedialog.askopenfilename()
        if file_path:
            normalized_path = os.path.normpath(file_path)
            self.ppk_path_file = normalized_path

    def get_entry_values(self):
        self.column_old_acc = self.entry_field_old_acc_name.get()
        self.column_in_ppk = self.entry_field_ppk_name.get()

    def execute_processing(self):
        if self.ppk_path_file and self.map_path_file and self.column_in_ppk and self.column_old_acc:
            processor = AccountProcessor()
            processor.fetch_accounts_from_excel(self.ppk_path_file, self.column_in_ppk)
            processor.process_excel(self.map_path_file, self.column_old_acc)
