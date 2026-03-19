# Agent Instructions

## Cho Coder Agent

### Bạn cần biết
- Đây là CLI app xử lý CSV 1GB → **bắt buộc dùng streaming**, không `pd.read_csv()` load cả file
- Output là 2 file CSV nhỏ (chỉ 10 dòng mỗi file + header)
- Aggregation chỉ cần 1 dict/map trong RAM (key=campaign_id, value=sums) → memory-friendly
- Float precision quan trọng cho `spend` → cân nhắc dùng Decimal hoặc round cuối

### Kiến trúc khuyến nghị
```
src/
├── main.py (hoặc tương đương)  # CLI entrypoint
├── reader.py                    # CSV streaming reader
├── aggregator.py                # Aggregation logic
├── calculator.py                # CTR/CPA computation
├── writer.py                    # CSV output writer
└── utils.py                     # Logging, validation
tests/
├── test_reader.py
├── test_aggregator.py
├── test_calculator.py
└── test_writer.py
```

### Patterns nên dùng
- **Python:** `csv.reader` + iterator, hoặc `pandas.read_csv(chunksize=...)`
- **Go:** `encoding/csv` + `bufio.Scanner`
- **Node:** `readline` hoặc stream-based CSV parser
- **Rust:** `csv` crate với `Reader::from_reader(BufReader::new(file))`

### Conventions
- Tên biến/hàm: snake_case (Python/Rust/Go) hoặc camelCase (JS/Java)
- Không để dead code, commented-out blocks
- Mỗi function có docstring/comment ngắn
- Error messages bằng tiếng Anh, rõ ràng

### Java-specific (dự án này dùng Java)
- Áp dụng rules: `.cursor/rules/java-clean-code.mdc`, `java-modular-structure.mdc`, `gradle-dependencies.mdc`
- Tạo packages/classes mới để chia nhỏ logic (reader, model, aggregator, calculator, writer, cli)
- Được phép thêm Gradle dependencies cần thiết (commons-csv, picocli, slf4j, assertj)

### Lỗi thường gặp cần tránh
1. **Load toàn bộ file vào RAM** → ĐÂY LÀ SAI. File 1GB sẽ tốn >2GB RAM
2. **Không handle malformed rows** → Sẽ crash khi gặp dòng lỗi trong file 1GB
3. **Float precision errors** → Dùng `round()` hoặc `Decimal` cho spend
4. **Quên loại conversions=0 khỏi top10 CPA** → Yêu cầu rõ ràng trong đề
5. **Sai format output** → CTR phải 4 decimals, CPA phải 2 decimals
6. **Không có header row trong output CSV** → Phải có
7. **Sorting sai chiều** → CTR: DESC (cao nhất), CPA: ASC (thấp nhất)

---

## Cho Reviewer Agent

### Checklist review
- [ ] **Performance:** Có dùng streaming/chunked reading không?
- [ ] **Memory:** Peak memory có hợp lý không? (<200MB cho file 1GB)
- [ ] **Correctness:** CTR = clicks/impressions, CPA = spend/conversions
- [ ] **Edge cases:** Division by zero được handle chưa?
- [ ] **CPA filter:** Campaigns với conversions=0 có bị loại khỏi top10_cpa chưa?
- [ ] **Output format:** Header đúng, số format đúng (CTR 4dp, CPA 2dp)
- [ ] **CLI:** Chạy được với `--input` và `--output` args
- [ ] **Error handling:** File not found, malformed rows
- [ ] **Code quality:** Clean code, no dead code, meaningful names
- [ ] **Tests:** Có unit tests cho logic chính
- [ ] **README:** Đủ thông tin: setup, run, libraries, time, memory

### Red flags
- `pd.read_csv("file.csv")` không có chunksize → load cả file
- Không có try/except hoặc error handling
- Magic numbers không giải thích
- Output file thiếu header
- Không có tests

---

## Cho Tester Agent

### Test Cases bắt buộc

#### Unit Tests
1. **Parse row hợp lệ** → trả về đúng types
2. **Parse row malformed** (thiếu cột) → skip, không crash
3. **Parse row giá trị sai type** (clicks = "abc") → skip, không crash
4. **Aggregate 2 rows cùng campaign** → sums đúng
5. **CTR calculation** → clicks/impressions đúng
6. **CPA calculation bình thường** → spend/conversions đúng
7. **CPA khi conversions = 0** → null/None
8. **CTR khi impressions = 0** → 0 hoặc null
9. **Top 10 CTR sorted DESC** → verify order
10. **Top 10 CPA sorted ASC, loại conversions=0** → verify filter + order

#### Integration Tests
11. **Chạy với file mẫu nhỏ** → output CSV đúng format và giá trị
12. **Chạy với file rỗng** → handle gracefully
13. **Chạy với file không tồn tại** → error message rõ ràng
14. **Chạy thiếu arguments** → hiện help/usage

#### Performance Tests (manual)
15. **Chạy với file 1GB** → ghi nhận time và memory
16. **Memory không vượt ngưỡng** → peak < 200MB

### Test Data mẫu
```csv
campaign_id,date,impressions,clicks,spend,conversions
CMP001,2025-01-01,12000,300,45.50,12
CMP002,2025-01-01,8000,120,28.00,4
CMP001,2025-01-02,14000,340,48.20,15
CMP003,2025-01-01,5000,60,15.00,0
CMP002,2025-01-02,8500,150,31.00,5
```

### Expected aggregation cho test data trên
| campaign_id | total_impressions | total_clicks | total_spend | total_conversions | CTR    | CPA   |
|-------------|-------------------|--------------|-------------|-------------------|--------|-------|
| CMP001      | 26000             | 640          | 93.70       | 27                | 0.0246 | 3.47  |
| CMP002      | 16500             | 270          | 59.00       | 9                 | 0.0164 | 6.56  |
| CMP003      | 5000              | 60           | 15.00       | 0                 | 0.0120 | null  |
