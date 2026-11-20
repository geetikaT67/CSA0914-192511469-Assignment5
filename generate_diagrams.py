import os
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as patches

os.makedirs("Report/diagrams", exist_ok=True)

# 1. Text and Binary File Record Layouts
def draw_record_layouts():
    fig, ax = plt.subplots(figsize=(10, 4.5), dpi=200)
    ax.axis('off')
    
    # Title
    ax.text(0.5, 0.95, "Bus-Pass Record Layout Comparison", ha='center', va='top', fontsize=12, fontweight='bold')
    
    # Text Layout
    ax.text(0.05, 0.82, "1. Delimited Text Record Layout (Variable Length, Pipe Delimited)", fontsize=10, fontweight='bold')
    fields_text = [
        ("passId\n(e.g. P1003)", 0.12),
        ("regNo\n(192511469)", 0.13),
        ("name\n(Geetika T)", 0.14),
        ("routeNo\n(Route-104)", 0.13),
        ("boardingStop\n(Poonamallee)", 0.16),
        ("validityDate\n(2026-12-31)", 0.13),
        ("amountPaid\n(2500.00)", 0.12)
    ]
    x_cur = 0.05
    for name, w in fields_text:
        rect = patches.Rectangle((x_cur, 0.58), w, 0.18, linewidth=1.2, edgecolor='#333333', facecolor='#e8f4f8')
        ax.add_patch(rect)
        ax.text(x_cur + w/2, 0.67, name, ha='center', va='center', fontsize=7.5)
        x_cur += w
        if x_cur < 0.9:
            ax.text(x_cur, 0.67, "|", ha='center', va='center', fontsize=12, fontweight='bold', color='#c0392b')
    ax.text(0.95, 0.67, "\\n", ha='center', va='center', fontsize=10, fontweight='bold', color='#27ae60')
    
    # Binary Layout
    ax.text(0.05, 0.44, "2. Binary Record Layout using DataOutputStream (Fixed Types & UTF-8 Prefixes)", fontsize=10, fontweight='bold')
    fields_bin = [
        ("passId\nwriteUTF\n[2B len + str]", 0.12),
        ("regNo\nwriteUTF\n[2B len + str]", 0.13),
        ("name\nwriteUTF\n[2B len + str]", 0.14),
        ("routeNo\nwriteUTF\n[2B len + str]", 0.13),
        ("boardingStop\nwriteUTF\n[2B len + str]", 0.16),
        ("validityDate\nwriteLong\n[8 Bytes]", 0.13),
        ("amountPaid\nwriteDouble\n[8 Bytes]", 0.12)
    ]
    x_cur = 0.05
    for name, w in fields_bin:
        rect = patches.Rectangle((x_cur, 0.18), w, 0.20, linewidth=1.2, edgecolor='#333333', facecolor='#fef9e7')
        ax.add_patch(rect)
        ax.text(x_cur + w/2, 0.28, name, ha='center', va='center', fontsize=7)
        x_cur += w

    ax.text(0.5, 0.06, "Total Delimited Size: ~70 Bytes (Variable)   |   Total Binary Size: ~73 Bytes (Compact, Typed)", 
            ha='center', va='center', fontsize=9, fontstyle='italic', color='#555555')

    plt.tight_layout()
    plt.savefig("Report/diagrams/record_layouts.png", bbox_inches='tight')
    plt.close()

# 2. Generic DAO Class Diagram
def draw_dao_diagram():
    fig, ax = plt.subplots(figsize=(8, 5), dpi=200)
    ax.axis('off')
    
    ax.text(0.5, 0.96, "Generic DAO Design Pattern Architecture", ha='center', va='top', fontsize=12, fontweight='bold')

    # Interface box
    rect1 = patches.Rectangle((0.15, 0.58), 0.7, 0.32, linewidth=1.2, edgecolor='#1b4f72', facecolor='#ebf5fb')
    ax.add_patch(rect1)
    ax.text(0.5, 0.86, "<<interface>>\nDao<T, K>", ha='center', va='center', fontsize=10, fontweight='bold')
    ax.plot([0.15, 0.85], [0.81, 0.81], color='#1b4f72', lw=1)
    dao_methods = "+ insert(entity: T): void\n+ findById(id: K): T\n+ findAll(): List<T>\n+ update(entity: T): void\n+ delete(id: K): void"
    ax.text(0.18, 0.70, dao_methods, ha='left', va='center', fontsize=8.5, fontfamily='monospace')

    # Arrow down
    ax.annotate("", xy=(0.5, 0.44), xytext=(0.5, 0.58),
                arrowprops=dict(facecolor='black', edgecolor='black', width=1, headwidth=6, linestyle='dashed'))
    ax.text(0.52, 0.51, "implements", fontsize=8, fontstyle='italic')

    # Concrete DAO box
    rect2 = patches.Rectangle((0.15, 0.12), 0.7, 0.32, linewidth=1.2, edgecolor='#196f3d', facecolor='#eafaf1')
    ax.add_patch(rect2)
    ax.text(0.5, 0.40, "StudentDao implements Dao<Student, String>", ha='center', va='center', fontsize=10, fontweight='bold')
    ax.plot([0.15, 0.85], [0.36, 0.36], color='#196f3d', lw=1)
    sdao_content = "- connection: Connection\n+ insert(student: Student): void\n+ findById(regNo: String): Student\n+ findAll(): List<Student>\n- mapRowToStudent(rs: ResultSet): Student"
    ax.text(0.18, 0.24, sdao_content, ha='left', va='center', fontsize=8.5, fontfamily='monospace')

    plt.tight_layout()
    plt.savefig("Report/diagrams/dao_class_diagram.png", bbox_inches='tight')
    plt.close()

# 3. Serialization Object Graph
def draw_serialization_graph():
    fig, ax = plt.subplots(figsize=(8.5, 4.5), dpi=200)
    ax.axis('off')
    
    ax.text(0.5, 0.95, "Hostel Object Graph Serialization Hierarchy", ha='center', va='top', fontsize=12, fontweight='bold')

    # Hostel Box
    rect1 = patches.Rectangle((0.05, 0.45), 0.25, 0.35, linewidth=1.2, edgecolor='#2c3e50', facecolor='#eaecee')
    ax.add_patch(rect1)
    ax.text(0.175, 0.74, "Hostel\n(Serializable)", ha='center', va='center', fontsize=9.5, fontweight='bold')
    ax.plot([0.05, 0.30], [0.68, 0.68], color='#2c3e50', lw=1)
    ax.text(0.07, 0.56, "- hostelName: String\n- rooms: List<Room>\n- serialVersionUID: 1L", ha='left', va='center', fontsize=8)

    # Arrow 1
    ax.annotate("", xy=(0.38, 0.62), xytext=(0.30, 0.62),
                arrowprops=dict(facecolor='black', edgecolor='black', width=1.5, headwidth=6))
    ax.text(0.34, 0.65, "1..*", ha='center', fontsize=8)

    # Room Box
    rect2 = patches.Rectangle((0.38, 0.45), 0.25, 0.35, linewidth=1.2, edgecolor='#2c3e50', facecolor='#eaecee')
    ax.add_patch(rect2)
    ax.text(0.505, 0.74, "Room\n(Serializable)", ha='center', va='center', fontsize=9.5, fontweight='bold')
    ax.plot([0.38, 0.63], [0.68, 0.68], color='#2c3e50', lw=1)
    ax.text(0.40, 0.56, "- roomNumber: int\n- capacity: int\n- residents: List<Res>\n- serialVersionUID: 1L", ha='left', va='center', fontsize=8)

    # Arrow 2
    ax.annotate("", xy=(0.71, 0.62), xytext=(0.63, 0.62),
                arrowprops=dict(facecolor='black', edgecolor='black', width=1.5, headwidth=6))
    ax.text(0.67, 0.65, "1..*", ha='center', fontsize=8)

    # Resident Box
    rect3 = patches.Rectangle((0.71, 0.30), 0.27, 0.55, linewidth=1.2, edgecolor='#b03a2e', facecolor='#fdf2e9')
    ax.add_patch(rect3)
    ax.text(0.845, 0.79, "Resident\n(Serializable)", ha='center', va='center', fontsize=9.5, fontweight='bold', color='#922b21')
    ax.plot([0.71, 0.98], [0.73, 0.73], color='#b03a2e', lw=1)
    res_text = "- name: String\n- regNo: String\n- phone: String\n\n[TRANSIENT / SAFE]:\n- password: String\n  (PROTECTED)\n- dbConn: Connection\n  (NON-SERIALIZABLE)"
    ax.text(0.73, 0.51, res_text, ha='left', va='center', fontsize=7.5)

    ax.text(0.5, 0.12, "Depth-First Serialization: Traverses tree -> Encodes class metadata -> Excludes transient fields",
            ha='center', va='center', fontsize=8.5, fontstyle='italic', color='#333333')

    plt.tight_layout()
    plt.savefig("Report/diagrams/serialization_graph.png", bbox_inches='tight')
    plt.close()

# 4. JDBC Transaction Flow
def draw_transaction_flow():
    fig, ax = plt.subplots(figsize=(8.5, 4.5), dpi=200)
    ax.axis('off')
    
    ax.text(0.5, 0.95, "Atomic JDBC Wallet Transfer Transaction Architecture", ha='center', va='top', fontsize=12, fontweight='bold')

    steps = [
        ("1. Disable Auto-Commit\nconn.setAutoCommit(false)", 0.05, 0.60, 0.22, 0.22, '#d4efdf'),
        ("2. Debit Hostel Wallet\nUPDATE hostel_wallet\nSET balance = balance - 500", 0.32, 0.60, 0.24, 0.22, '#d4efdf'),
        ("3. Credit Canteen Wallet\nUPDATE canteen_wallet\nSET balance = balance + 500", 0.61, 0.60, 0.24, 0.22, '#d4efdf')
    ]
    for text, x, y, w, h, col in steps:
        rect = patches.Rectangle((x, y), w, h, linewidth=1.2, edgecolor='#196f3d', facecolor=col)
        ax.add_patch(rect)
        ax.text(x + w/2, y + h/2, text, ha='center', va='center', fontsize=8, fontweight='bold')

    # Arrows
    ax.annotate("", xy=(0.32, 0.71), xytext=(0.27, 0.71), arrowprops=dict(facecolor='black', width=1.2, headwidth=5))
    ax.annotate("", xy=(0.61, 0.71), xytext=(0.56, 0.71), arrowprops=dict(facecolor='black', width=1.2, headwidth=5))

    # Decision Split
    rect_commit = patches.Rectangle((0.68, 0.20), 0.25, 0.22, linewidth=1.2, edgecolor='#1e8449', facecolor='#a9dfbf')
    ax.add_patch(rect_commit)
    ax.text(0.805, 0.31, "SUCCESS:\nconn.commit()\nChanges Permanent", ha='center', va='center', fontsize=8.5, fontweight='bold', color='#145a32')

    rect_rollback = patches.Rectangle((0.32, 0.20), 0.25, 0.22, linewidth=1.2, edgecolor='#c0392b', facecolor='#f9d5d5')
    ax.add_patch(rect_rollback)
    ax.text(0.445, 0.31, "FAILURE DETECTED:\nconn.rollback()\nZero Money Lost/Created", ha='center', va='center', fontsize=8.5, fontweight='bold', color='#922b21')

    # Connecting branching arrows
    ax.annotate("", xy=(0.805, 0.42), xytext=(0.73, 0.60), arrowprops=dict(facecolor='#1e8449', edgecolor='#1e8449', width=1.2, headwidth=5))
    ax.text(0.79, 0.52, "Both OK", fontsize=8, fontweight='bold', color='#1e8449')

    ax.annotate("", xy=(0.445, 0.42), xytext=(0.445, 0.60), arrowprops=dict(facecolor='#c0392b', edgecolor='#c0392b', width=1.2, headwidth=5))
    ax.text(0.46, 0.52, "Exception", fontsize=8, fontweight='bold', color='#c0392b')

    plt.tight_layout()
    plt.savefig("Report/diagrams/transaction_flow.png", bbox_inches='tight')
    plt.close()

# 5. SQL Injection vs PreparedStatement Flow
def draw_sqli_flow():
    fig, ax = plt.subplots(figsize=(9, 4.8), dpi=200)
    ax.axis('off')
    
    ax.text(0.5, 0.95, "SQL Injection Vulnerability vs PreparedStatement Parameterization", ha='center', va='top', fontsize=12, fontweight='bold')

    # Unsafe Path
    ax.text(0.05, 0.82, "A. Unsafe Dynamic SQL (String Concatenation)", fontsize=9.5, fontweight='bold', color='#922b21')
    rect_un1 = patches.Rectangle((0.05, 0.55), 0.25, 0.22, linewidth=1, edgecolor='#c0392b', facecolor='#fadbd8')
    ax.add_patch(rect_un1)
    ax.text(0.175, 0.66, "User Input:\nadmin' --\n(Untrusted String)", ha='center', va='center', fontsize=8)

    ax.annotate("", xy=(0.35, 0.66), xytext=(0.30, 0.66), arrowprops=dict(facecolor='#c0392b', width=1.2, headwidth=5))

    rect_un2 = patches.Rectangle((0.35, 0.55), 0.32, 0.22, linewidth=1, edgecolor='#c0392b', facecolor='#fadbd8')
    ax.add_patch(rect_un2)
    ax.text(0.51, 0.66, "SQL Parser treats '--' as comment:\nSELECT * FROM users\nWHERE username='admin' -- ...", ha='center', va='center', fontsize=7.5)

    ax.annotate("", xy=(0.72, 0.66), xytext=(0.67, 0.66), arrowprops=dict(facecolor='#c0392b', width=1.2, headwidth=5))

    rect_un3 = patches.Rectangle((0.72, 0.55), 0.23, 0.22, linewidth=1.5, edgecolor='#78281f', facecolor='#f5b7b1')
    ax.add_patch(rect_un3)
    ax.text(0.835, 0.66, "CRITICAL DEFECT:\nPassword ignored!\nLogged in as admin!", ha='center', va='center', fontsize=8, fontweight='bold', color='#78281f')

    # Secure Path
    ax.text(0.05, 0.40, "B. Secure Parameterized SQL (PreparedStatement)", fontsize=9.5, fontweight='bold', color='#145a32')
    rect_sec1 = patches.Rectangle((0.05, 0.12), 0.25, 0.22, linewidth=1, edgecolor='#27ae60', facecolor='#d5f5e3')
    ax.add_patch(rect_sec1)
    ax.text(0.175, 0.23, "Pre-compiled Template:\nSELECT * FROM users\nWHERE user=? AND pass=?", ha='center', va='center', fontsize=7.5)

    ax.annotate("", xy=(0.35, 0.23), xytext=(0.30, 0.23), arrowprops=dict(facecolor='#27ae60', width=1.2, headwidth=5))

    rect_sec2 = patches.Rectangle((0.35, 0.12), 0.32, 0.22, linewidth=1, edgecolor='#27ae60', facecolor='#d5f5e3')
    ax.add_patch(rect_sec2)
    ax.text(0.51, 0.23, "pstmt.setString(1, \"admin' --\");\nInput bound as LITERAL DATA token.\nNo syntax modification possible.", ha='center', va='center', fontsize=7.5)

    ax.annotate("", xy=(0.72, 0.23), xytext=(0.67, 0.23), arrowprops=dict(facecolor='#27ae60', width=1.2, headwidth=5))

    rect_sec3 = patches.Rectangle((0.72, 0.12), 0.23, 0.22, linewidth=1.5, edgecolor='#145a32', facecolor='#abebc6')
    ax.add_patch(rect_sec3)
    ax.text(0.835, 0.23, "100% IMMUNE:\nLooks for literal user.\n0 rows matched -> Denied.", ha='center', va='center', fontsize=8, fontweight='bold', color='#145a32')

    plt.tight_layout()
    plt.savefig("Report/diagrams/sqli_vs_prepared.png", bbox_inches='tight')
    plt.close()

# 6. Batch Insertion Flow
def draw_batch_flow():
    fig, ax = plt.subplots(figsize=(8.5, 4.2), dpi=200)
    ax.axis('off')
    
    ax.text(0.5, 0.95, "PreparedStatement Batch Processing Flow (1,000 Rows/Batch)", ha='center', va='top', fontsize=12, fontweight='bold')

    boxes = [
        ("1. Application Loop\n(Iterate 100k records)", 0.05, 0.40, 0.22, 0.35, '#ebf5fb'),
        ("2. Client Buffer\npstmt.addBatch()\n(Accumulate in JVM RAM)", 0.34, 0.40, 0.26, 0.35, '#ebf5fb'),
        ("3. Flush at 1,000 rows\npstmt.executeBatch()\n(Single network TCP packet)", 0.68, 0.40, 0.27, 0.35, '#d4efdf')
    ]
    for text, x, y, w, h, col in boxes:
        rect = patches.Rectangle((x, y), w, h, linewidth=1.2, edgecolor='#2c3e50', facecolor=col)
        ax.add_patch(rect)
        ax.text(x + w/2, y + h/2, text, ha='center', va='center', fontsize=8.5, fontweight='bold')

    ax.annotate("", xy=(0.34, 0.57), xytext=(0.27, 0.57), arrowprops=dict(facecolor='black', width=1.2, headwidth=5))
    ax.annotate("", xy=(0.68, 0.57), xytext=(0.60, 0.57), arrowprops=dict(facecolor='black', width=1.2, headwidth=5))
    ax.text(0.64, 0.62, "if count % 1000 == 0", fontsize=7.5, fontstyle='italic')

    ax.text(0.5, 0.18, "Result: Network round-trips reduced by 1,000x | Measured speedup: 604x faster than Statement!",
            ha='center', va='center', fontsize=9, fontweight='bold', color='#196f3d')

    plt.tight_layout()
    plt.savefig("Report/diagrams/batch_insertion_flow.png", bbox_inches='tight')
    plt.close()

# 7. Data Migration Architecture
def draw_migration_architecture():
    fig, ax = plt.subplots(figsize=(9, 4.5), dpi=200)
    ax.axis('off')
    
    ax.text(0.5, 0.95, "Multi-Campus Data Migration Engine Architecture", ha='center', va='top', fontsize=12, fontweight='bold')

    # Source File
    rect1 = patches.Rectangle((0.05, 0.35), 0.20, 0.40, linewidth=1.2, edgecolor='#333333', facecolor='#fcf3cf')
    ax.add_patch(rect1)
    ax.text(0.15, 0.55, "Legacy Bus Pass File\n(12 Campuses,\n800,000 Passes)", ha='center', va='center', fontsize=8.5, fontweight='bold')

    # Arrow to Validation
    ax.annotate("", xy=(0.32, 0.55), xytext=(0.25, 0.55), arrowprops=dict(facecolor='black', width=1.2, headwidth=5))

    # Validation Engine
    rect2 = patches.Rectangle((0.32, 0.25), 0.30, 0.60, linewidth=1.2, edgecolor='#2980b9', facecolor='#ebf5fb')
    ax.add_patch(rect2)
    ax.text(0.47, 0.77, "DataMigrationEngine", ha='center', va='center', fontsize=9.5, fontweight='bold', color='#1b4f72')
    ax.plot([0.32, 0.62], [0.70, 0.70], color='#2980b9', lw=1)
    rules_text = "Rule 1: Token Count == 7\nRule 2: Pass ID Regex ^P\\d+\nRule 3: Numeric RegNo\nRule 4: ISO-8601 Date\nRule 5: Amount >= 0.00"
    ax.text(0.34, 0.48, rules_text, ha='left', va='center', fontsize=7.5)

    # Branch to Rejection Audit
    ax.annotate("", xy=(0.70, 0.72), xytext=(0.62, 0.65), arrowprops=dict(facecolor='#c0392b', edgecolor='#c0392b', width=1.2, headwidth=5))
    rect_err = patches.Rectangle((0.70, 0.60), 0.25, 0.25, linewidth=1.2, edgecolor='#c0392b', facecolor='#fadbd8')
    ax.add_patch(rect_err)
    ax.text(0.825, 0.72, "Quarantined Audit Log:\nvalidation_failure_report.txt\n(Damaged lines isolated)", ha='center', va='center', fontsize=7.5, fontweight='bold', color='#78281f')

    # Branch to Database Batch
    ax.annotate("", xy=(0.70, 0.35), xytext=(0.62, 0.45), arrowprops=dict(facecolor='#27ae60', edgecolor='#27ae60', width=1.2, headwidth=5))
    rect_db = patches.Rectangle((0.70, 0.20), 0.25, 0.28, linewidth=1.2, edgecolor='#27ae60', facecolor='#d4efdf')
    ax.add_patch(rect_db)
    ax.text(0.825, 0.34, "MySQL InnoDB Database:\nbus_pass table\n- Batches of 1,000 rows\n- Atomic Transaction\n- 3.88M records/sec", ha='center', va='center', fontsize=7.5, fontweight='bold', color='#145a32')

    plt.tight_layout()
    plt.savefig("Report/diagrams/migration_architecture.png", bbox_inches='tight')
    plt.close()

draw_record_layouts()
draw_dao_diagram()
draw_serialization_graph()
draw_transaction_flow()
draw_sqli_flow()
draw_batch_flow()
draw_migration_architecture()

print("All 7 architectural diagrams generated successfully.")
