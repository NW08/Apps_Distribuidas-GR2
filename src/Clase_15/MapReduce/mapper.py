import sys

filename = sys.argv[1]

with open(filename, "r") as f, open(f"{filename}.out", "w") as out:
    for line in f:
        for word in line.lower().split():
            out.write(f"{word}\t1\n")
