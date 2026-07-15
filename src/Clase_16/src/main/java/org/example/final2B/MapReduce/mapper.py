import csv
import sys

filename = sys.argv[1]

with open(filename, "r") as f, open(filename + ".out", "w") as out:
    reader = csv.DictReader(f)

    for row in reader:

        video = row["shortVideo"]
        usuario = row["usuario"]
        accion = row["accion"]
        hora = row["hora"]

        out.write(f"USUARIO\t{usuario}:1\n")

        out.write(f"HORA\t{hora}:1\n")

        if accion == "like":
            out.write(f"LIKES\t{video}:1\n")


        elif accion == "comment":
            out.write(f"COMMENTS\t{video}:1\n")


        elif accion == "share":
            out.write(f"SHARES\t{video}:1\n")


        elif accion == "view":
            out.write(f"VIEWS\t{video}:1\n")
