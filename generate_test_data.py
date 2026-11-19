import os
import random
from datetime import datetime, timedelta

os.makedirs("TestData", exist_ok=True)
os.makedirs("TestData/adversarial", exist_ok=True)
os.makedirs("ExperimentalData", exist_ok=True)

routes = ["Route-101", "Route-102", "Route-104", "Route-108", "Route-201", "Route-205", "Route-301", "Route-405"]
stops = ["Poonamallee", "Tambaram", "Guindy", "Avadi", "Koyambedu", "Porur", "Central", "T Nagar", "Velachery", "Adyar"]
first_names = ["Geetika", "Rahul", "Priya", "Arun", "Sneha", "Karthik", "Ananya", "Vijay", "Divya", "Suresh", "Pooja", "Manoj"]
last_names = ["T", "R", "K", "S", "M", "V", "B", "N", "P"]

def generate_bus_pass_file(filename, count, include_corruptions=False):
    filepath = os.path.join("TestData", filename)
    print(f"Generating {filepath} with {count} records...")
    with open(filepath, "w", encoding="utf-8") as f:
        # Header or direct records
        for i in range(1, count + 1):
            pass_id = f"P{1000 + i}"
            reg_no = f"19251{1400 + (i % 900):03d}"
            name = f"{random.choice(first_names)} {random.choice(last_names)}"
            route = random.choice(routes)
            stop = random.choice(stops)
            validity = (datetime(2026, 12, 31) - timedelta(days=random.randint(0, 180))).strftime("%Y-%m-%d")
            amount = 1500.00 + (random.randint(0, 10) * 250.00)
            
            # If sample file, specifically insert P1002 damaged and P1003 valid as per PDF requirement!
            if include_corruptions:
                if i == 2:
                    # Damaged P1002 (missing fields / corrupt)
                    f.write("P1002|192511470|DamagedRecord_MissingFields|Route-101\n")
                    continue
                elif i == 3:
                    f.write("P1003|192511471|Geetika T|Route-104|Poonamallee|2026-12-31|2500.00\n")
                    continue
            
            f.write(f"{pass_id}|{reg_no}|{name}|{route}|{stop}|{validity}|{amount:.2f}\n")

# 1. Sample file for PassStore testing
generate_bus_pass_file("bus_passes_sample.txt", 10, include_corruptions=True)

# 2. Files for search scalability: 1k, 10k, 100k
generate_bus_pass_file("bus_passes_1k.txt", 1000)
generate_bus_pass_file("bus_passes_10k.txt", 10000)
generate_bus_pass_file("bus_passes_100k.txt", 100000)

# 3. Attendance log files for buffered vs unbuffered line counting: 1 MB, 10 MB, 50 MB
def generate_log_file(filename, target_size_bytes):
    filepath = os.path.join("TestData", filename)
    print(f"Generating {filepath} of size {target_size_bytes / (1024*1024):.1f} MB...")
    line_template = "2026-11-19 08:30:15 INFO AttendanceLogger Student ID 192511469 checked in at Gate 4\n"
    line_bytes = line_template.encode("utf-8")
    line_len = len(line_bytes)
    
    with open(filepath, "wb") as f:
        bytes_written = 0
        chunk_lines = 1000
        chunk = line_bytes * chunk_lines
        chunk_size = len(chunk)
        
        while bytes_written + chunk_size <= target_size_bytes:
            f.write(chunk)
            bytes_written += chunk_size
            
        remaining = target_size_bytes - bytes_written
        if remaining > 0:
            lines_needed = remaining // line_len
            f.write(line_bytes * lines_needed)
            rem_bytes = remaining % line_len
            if rem_bytes > 0:
                f.write(line_bytes[:rem_bytes])

generate_log_file("attendance_1mb.log", 1 * 1024 * 1024)
generate_log_file("attendance_10mb.log", 10 * 1024 * 1024)
generate_log_file("attendance_50mb.log", 50 * 1024 * 1024)

# 4. Adversarial datasets
with open("TestData/adversarial/damaged_field_count.txt", "w", encoding="utf-8") as f:
    f.write("P9001|192511469|Geetika T|Route-101\n") # 4 fields instead of 7
    f.write("P9002|192511470\n") # 2 fields
    f.write("P9003|192511471|Arun R|Route-102|Tambaram|2026-12-31|1500.00|EXTRA_FIELD|ANOTHER\n") # 9 fields

with open("TestData/adversarial/damaged_non_numeric.txt", "w", encoding="utf-8") as f:
    f.write("P9004|192511472|Priya K|Route-104|Guindy|2026-12-31|FREE_PASS\n")
    f.write("P9005|192511473|Karthik S|Route-108|Avadi|2026-12-31|NaN\n")
    f.write("P9006|192511474|Sneha M|Route-201|Central|2026-12-31|-1500.00\n")

with open("TestData/adversarial/damaged_empty_lines.txt", "w", encoding="utf-8") as f:
    f.write("\n")
    f.write("P9007|192511475|Divya V|Route-205|Koyambedu|2026-12-31|2000.00\n")
    f.write("   \n")
    f.write("\n")
    f.write("P9008|192511476|Vijay B|Route-301|Porur|2026-12-31|2500.00\n")
    f.write("\n")

print("All test data generated successfully.")
