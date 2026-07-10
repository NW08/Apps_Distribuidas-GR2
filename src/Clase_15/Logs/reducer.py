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
    with open(output_path, "w") as out:
        out.write("| User | Off-hours accesses |\n")
        out.write("|---|---|\n")
        for user, count in sorted(final_counts.items()):
            out.write(f"| {user} | {count} |\n")


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
