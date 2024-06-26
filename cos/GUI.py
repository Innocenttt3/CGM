from customtkinter import *
import os
from tkinter import filedialog, messagebox
import pandas as pd
from logic import AccountProcessor
from threading import Thread


class FileChooserApp(CTk):
    def __init__(self, master=None, **kwargs):
        super().__init__(master, **kwargs)
        self.title('Auto Mapowanie Kont')
        self.ppk_path_file = None
        self.map_path_file = None
        self.column_in_ppk = ''
        self.column_old_acc = ''
        self.ppk_all_columns_names = []
        self.map_all_columns_names = []
        self.master = master
        self.geometry('450x300')
        set_appearance_mode('dark')

        # PPK file button
        self.full_chart_button = CTkButton(master=self, text='Wybierz plik z PPK', text_color='white',
                                           command=self.choose_ppk_file, width=200)
        self.full_chart_button.grid(row=0, column=0, padx=10, pady=10)

        # Mapping file button
        self.mapping_file_button = CTkButton(master=self, text='Wybierz plik do mapowania',
                                             text_color='white', command=self.choose_map_file, width=200)
        self.mapping_file_button.grid(row=0, column=1, padx=10, pady=10)

        # Dropdown menu for available columns in PPK
        self.ppk_columns_dropdown = CTkComboBox(master=self, width=200, state='disabled')
        self.ppk_columns_dropdown.grid(row=1, column=0, padx=10, pady=10)

        # Dropdown menu for available columns in mapping file
        self.mapping_columns_dropdown = CTkComboBox(master=self, width=200, state='disabled')
        self.mapping_columns_dropdown.grid(row=1, column=1, padx=10, pady=10)

        # Execute button
        self.execute_button = CTkButton(master=self, text='Wykonaj', text_color='white',
                                        command=self.start_processing_thread, width=200)
        self.execute_button.grid(row=2, columnspan=2, padx=10, pady=10)
        self.execute_button.configure(fg_color="#00008B")

        # Progress bar
        self.progress_bar = CTkProgressBar(master=self, width=400)
        self.progress_bar.grid(row=3, columnspan=2, padx=10, pady=10)
        self.progress_bar.set(0)

    def choose_ppk_file(self):
        file_path = filedialog.askopenfilename()
        if file_path and (file_path.endswith('.xlsx') or file_path.endswith('.xls')):
            normalized_path = os.path.normpath(file_path)
            self.ppk_path_file = normalized_path
            self._fetch_columns_names(self.ppk_path_file, 'ppk')
            self._populate_combobox(self.ppk_columns_dropdown, self.ppk_all_columns_names)
        else:
            messagebox.showerror("Błąd", "Proszę wybrać plik Excel (.xlsx lub .xls)")

    def choose_map_file(self):
        file_path = filedialog.askopenfilename()
        if file_path and (file_path.endswith('.xlsx') or file_path.endswith('.xls')):
            normalized_path = os.path.normpath(file_path)
            self.map_path_file = normalized_path
            self._fetch_columns_names(self.map_path_file, 'map')
            self._populate_combobox(self.mapping_columns_dropdown, self.map_all_columns_names)
        else:
            messagebox.showerror("Błąd", "Proszę wybrać plik Excel (.xlsx lub .xls)")

    def get_combo_box_values(self):
        self.column_in_ppk = self.ppk_columns_dropdown.get()
        self.column_old_acc = self.mapping_columns_dropdown.get()

        if not self.column_in_ppk:
            messagebox.showerror("Błąd", "Nie wybrano kolumny PPK")
            return False

        if not self.column_old_acc:
            messagebox.showerror("Błąd", "Nie wybrano kolumny do mapowania")
            return False

        return True

    def _fetch_columns_names(self, path_to_file, which_columns):
        try:
            df = pd.read_excel(path_to_file, header=0)
            if which_columns == 'ppk':
                self.ppk_all_columns_names = df.columns.tolist()
                print("ppk:", self.ppk_all_columns_names)
            elif which_columns == 'map':
                self.map_all_columns_names = df.columns.tolist()
                print("map:", self.map_all_columns_names)
        except Exception as e:
            messagebox.showerror("Błąd", f"Nie można odczytać pliku: {str(e)}")

    def start_processing_thread(self):
        if self.get_combo_box_values():
            processing_thread = Thread(target=self.execute_processing)
            processing_thread.start()

    def execute_processing(self):
        if self.ppk_path_file and self.map_path_file and self.column_in_ppk and self.column_old_acc:
            processor = AccountProcessor()
            processor.fetch_accounts_from_excel(self.ppk_path_file, self.column_in_ppk)
            total_rows = processor.get_total_rows(self.map_path_file)
            self.progress_bar.set(0)

            for progress in processor.process_excel(self.map_path_file, self.column_old_acc):
                self.progress_bar.set(progress)
                self.update_idletasks()

            messagebox.showinfo("Informacja", "Gotowe")

    @staticmethod
    def _populate_combobox(combobox, column_names):
        combobox.configure(state='normal', values=column_names)


if __name__ == "__main__":
    app = FileChooserApp()
    app.mainloop()
