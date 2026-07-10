import sys

INICIO_LABORAL = "08:00:00"
FIN_LABORAL = "18:00:00"

filename = sys.argv[1]
with open(filename, "r") as f, open(f"{filename}.out", "w") as out:
    for line in f:
        line = line.strip()
        if not line:
            continue

        partes = line.split()
        if len(partes) < 3:
            continue

        hora = partes[1]
        campo_usuario = partes[2]

        if ":" not in campo_usuario:
            continue
        usuario = campo_usuario.split(":", 1)[1]

        fuera_de_horario = hora < INICIO_LABORAL or hora > FIN_LABORAL

        if fuera_de_horario:
            out.write(f"{usuario}\t1\n")
