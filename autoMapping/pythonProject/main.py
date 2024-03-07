import openpyxl

def uzupełnij_numery_kont():
    workbook = openpyxl.load_workbook('/Users/kamilgolawski/CGM/CGM-priv/autoMaping/test.xlsx')
    sheet = workbook.active


    for cell in sheet['A']:
        if cell.data_type == 'n':
            pattern = cell
        elif cell.data_type == 's':
                



uzupełnij_numery_kont()
