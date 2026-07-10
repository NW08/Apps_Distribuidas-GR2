import os
from collections import Counter


def parse_split_file(filepath: str) -> Counter:
    """Parsea un archivo .out y devuelve sus conteos por usuario."""
    counts = Counter()
    with open(filepath, "r") as f:
        for line in f:
            user, count = _parse_line(line)
            if user:
                counts[user] += count
    return counts


def _parse_line(line: str):
    line = line.strip()
    if not line:
        return None, 0
    parts = line.split("\t")
    if len(parts) != 2:
        return None, 0
    user, count = parts
    return user, int(count)


def write_report(final_counts: Counter, output_path: str = "resultado.md"):
    total_accesses = sum(final_counts.values())

    with open(output_path, "w") as out:
        out.write("# Reporte de Accesos Fuera de Horario\n\n")
        out.write("## Estadísticas Generales\n")
        out.write(f"- **Total de accesos fuera de horario:** {total_accesses}\n")
        if final_counts:
            most_frequent_user, max_count = final_counts.most_common(1)[0]
            out.write(
                f"- **Usuario con más incidentes:** {most_frequent_user} ({max_count})\n"
            )
        out.write("\n## Desglose por Usuario\n")
        out.write("| Usuario | Accesos fuera de horario | % del Total |\n")
        out.write("|---|---|---|\n")
        for user, count in sorted(
                final_counts.items(), key=lambda x: x[1], reverse=True
        ):
            percentage = (count / total_accesses * 100) if total_accesses > 0 else 0
            out.write(f"| {user} | {count} | {percentage:.2f}% |\n")


def reduce_results(splits_dir: str = "splits"):
    if not os.path.exists(splits_dir):
        return

    final_counts = Counter()
    for file in os.listdir(splits_dir):
        if file.endswith(".out"):
            final_counts += parse_split_file(os.path.join(splits_dir, file))

    write_report(final_counts)
    print("Result saved to resultado.md")


if __name__ == "__main__":
    reduce_results()
