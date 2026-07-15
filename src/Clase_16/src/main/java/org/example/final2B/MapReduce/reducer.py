import os
from collections import defaultdict

videos_vistas = defaultdict(int)
videos_likes = defaultdict(int)
videos_comentarios = defaultdict(int)
videos_shares = defaultdict(int)

usuarios = defaultdict(int)
horas = defaultdict(int)

for file in os.listdir("splits"):

    if not file.endswith(".out"):
        continue

    with open(os.path.join("splits", file), encoding="utf8") as f:

        for line in f:

            tipo, dato = line.strip().split("\t")

            clave, valor = dato.split(":")

            if tipo == "VISTAS":
                videos_vistas[clave] += int(valor)


            elif tipo == "LIKES":
                videos_likes[clave] += int(valor)


            elif tipo == "COMENTARIOS":
                videos_comentarios[clave] += int(valor)


            elif tipo == "SHARES":
                videos_shares[clave] += int(valor)


            elif tipo == "USUARIO":
                usuarios[clave] += int(valor)


            elif tipo == "HORA":
                horas[clave] += int(valor)

# Calcular ratio de interacción

ratios = {}

for video in videos_vistas:
    vistas = videos_vistas[video]
    likes = videos_likes[video]
    comentarios = videos_comentarios[video]
    shares = videos_shares[video]

    if vistas > 0:
        ratios[video] = (likes + comentarios + shares) / vistas

with open("resultado.md", "w", encoding="utf8") as out:
    out.write("# Resultados\n\n")

    if videos_vistas:
        out.write(
            f"Video más visto: "
            f"{max(videos_vistas, key=videos_vistas.get)}\n\n"
        )

    if videos_likes:
        out.write(
            f"Video con más likes: "
            f"{max(videos_likes, key=videos_likes.get)}\n\n"
        )

    if videos_comentarios:
        out.write(
            f"Video más comentado: "
            f"{max(videos_comentarios, key=videos_comentarios.get)}\n\n"
        )

    if usuarios:
        out.write(
            f"Usuario más recurrente: "
            f"{max(usuarios, key=usuarios.get)}\n\n"
        )

    if horas:
        out.write(
            f"Hora con mayor interacción: "
            f"{max(horas, key=horas.get)}\n\n"
        )

    if ratios:
        video_ratio = max(ratios, key=ratios.get)

        out.write(
            f"Mayor ratio de interacción: "
            f"{video_ratio} ({ratios[video_ratio]:.2f})\n"
        )

print("Proceso terminado")
