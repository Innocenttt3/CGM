from GUI import FileChooserApp
import customtkinter as ctk

if __name__ == "__main__":
    root = ctk.CTk()
    app = FileChooserApp(root)
    root.mainloop()
