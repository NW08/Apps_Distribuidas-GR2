import os
from collections import Counter

final_counts = Counter()

for file in os.listdir("splits"):
    if file.endswith(".out"):
        with open(os.path.join("splits", file), "r") as f:
            for line in f:
                word, count = line.strip().split("\t")
                final_counts[word] += int(count)

with open("resultado.md", "w") as out:
    out.write("| Palabra | Conteo |\n")
    out.write("|---|---|\n")
    for word, count in sorted(final_counts.items()):
        out.write(f"| {word} | {count} |\n")

print("Resultado guardado en resultado.md")
