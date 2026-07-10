import os
from collections import Counter

final_counts = Counter()

for file in os.listdir("splits"):
    if file.endswith(".out"):
        with open(os.path.join("splits", file), "r") as f:
            for line in f:
                line = line.strip()
                if not line:
                    continue
                usuario, count = line.split("\t")
                final_counts[usuario] += int(count)

with open("resultado.md", "w") as out:
    out.write("| Usuario | Accesos fuera de horario |\n")
    out.write("|---|---|\n")
    for usuario, count in sorted(final_counts.items()):
        out.write(f"| {usuario} | {count} |\n")

print("Resultado guardado en resultado.md")
