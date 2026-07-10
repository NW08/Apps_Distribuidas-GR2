import os
import sys
from typing import Optional, Tuple

START_TIME = "08:00:00"
END_TIME = "18:00:00"


def parse_log_line(line: str) -> Optional[Tuple[str, bool]]:
    line = line.strip()
    if not line:
        return None

    parts = line.split()
    if len(parts) < 3:
        return None

    time_str, user_field = parts[1], parts[2]
    if ":" not in user_field:
        return None

    user = user_field.split(":", 1)[1]
    is_off_hours = time_str < START_TIME or time_str > END_TIME
    return user, is_off_hours


def process_log(filename: str):
    if not os.path.exists(filename):
        return

    out_file = f"{filename}.out"
    with open(filename, "r") as f, open(out_file, "w") as out:
        for line in f:
            parsed = parse_log_line(line)
            if parsed is None:
                continue
            user, is_off_hours = parsed
            if is_off_hours:
                out.write(f"{user}\t1\n")


if __name__ == "__main__":
    if len(sys.argv) > 1:
        process_log(sys.argv[1])
