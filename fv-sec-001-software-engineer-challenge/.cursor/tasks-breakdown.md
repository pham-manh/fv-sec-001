# Tasks Breakdown

## Task 1: Project Setup
- **Mô tả:** Khởi tạo project, cấu trúc folder, dependency management
- **Output:** Project skeleton với folder structure, config files
- **Acceptance Criteria:**
  - Có README.md template
  - Có .gitignore phù hợp
  - Dependency file (requirements.txt / package.json / go.mod / Cargo.toml)
- **Độ phức tạp:** Low
- **Dependency:** Không

## Task 2: CLI Argument Parsing
- **Mô tả:** Parse command-line arguments `--input` và `--output`
- **Input:** CLI args
- **Output:** Validated input file path và output directory path
- **Acceptance Criteria:**
  - `--input` nhận đường dẫn file CSV
  - `--output` nhận đường dẫn output folder (tạo nếu chưa có)
  - Báo lỗi rõ ràng nếu thiếu argument hoặc file không tồn tại
  - Có help message (`--help`)
- **Độ phức tạp:** Low
- **Dependency:** Task 1

## Task 3: CSV Streaming Reader
- **Mô tả:** Đọc file CSV theo stream/chunk, không load toàn bộ vào RAM
- **Input:** File path tới CSV ~1GB
- **Output:** Iterator/stream của parsed rows
- **Acceptance Criteria:**
  - Memory usage không tăng tuyến tính theo file size
  - Handle malformed rows (skip + log warning)
  - Handle header row đúng
  - Parse đúng type: string, integer, float
  - Peak memory < 100MB cho file 1GB (mục tiêu)
- **Độ phức tạp:** Medium
- **Dependency:** Task 2

## Task 4: Data Aggregation Engine
- **Mô tả:** Aggregate dữ liệu theo campaign_id
- **Input:** Stream of parsed rows
- **Output:** Dictionary/Map: campaign_id → {total_impressions, total_clicks, total_spend, total_conversions}
- **Acceptance Criteria:**
  - Cộng dồn chính xác các giá trị số
  - Handle float precision cho spend (dùng Decimal nếu cần)
  - Xử lý đúng khi campaign xuất hiện nhiều lần ở nhiều ngày khác nhau
- **Độ phức tạp:** Medium
- **Dependency:** Task 3

## Task 5: Metrics Calculator
- **Mô tả:** Tính CTR và CPA cho mỗi campaign
- **Input:** Aggregated data per campaign
- **Output:** Aggregated data + CTR + CPA
- **Acceptance Criteria:**
  - CTR = total_clicks / total_impressions
  - CPA = total_spend / total_conversions
  - Handle division by zero: impressions=0 → CTR=0, conversions=0 → CPA=null
  - CTR format: 4 decimal places
  - CPA format: 2 decimal places
- **Độ phức tạp:** Low
- **Dependency:** Task 4

## Task 6: Ranking & CSV Output
- **Mô tả:** Sort, rank, và xuất 2 file CSV kết quả
- **Input:** List of campaigns với computed metrics
- **Output:** `top10_ctr.csv` và `top10_cpa.csv`
- **Acceptance Criteria:**
  - top10_ctr.csv: Top 10 CTR cao nhất, sort DESC by CTR
  - top10_cpa.csv: Top 10 CPA thấp nhất (loại conversions=0), sort ASC by CPA
  - Header row đúng format
  - Columns: campaign_id, total_impressions, total_clicks, total_spend, total_conversions, CTR, CPA
  - Số format đúng (CTR 4 decimals, CPA 2 decimals)
- **Độ phức tạp:** Medium
- **Dependency:** Task 5

## Task 7: Error Handling & Logging
- **Mô tả:** Xử lý lỗi toàn diện và logging
- **Acceptance Criteria:**
  - File not found → clear error message
  - Malformed CSV rows → skip + log count
  - Invalid data types → skip + log
  - Summary log cuối: total rows processed, rows skipped, campaigns found, processing time
- **Độ phức tạp:** Medium
- **Dependency:** Task 3, 4, 5, 6

## Task 8: Unit Tests
- **Mô tả:** Viết tests cho các module chính
- **Acceptance Criteria:**
  - Test CSV parsing với dữ liệu mẫu
  - Test aggregation logic
  - Test CTR/CPA calculation (bao gồm edge cases: division by zero)
  - Test ranking logic
  - Test output CSV format
  - Test error handling (file not found, malformed data)
- **Độ phức tạp:** Medium
- **Dependency:** Task 3, 4, 5, 6

## Task 9: Performance Optimization & Benchmarking
- **Mô tả:** Đo và tối ưu performance
- **Acceptance Criteria:**
  - Đo processing time cho file 1GB
  - Đo peak memory usage
  - Ghi kết quả vào README
  - (Optional) So sánh các approach khác nhau
- **Độ phức tạp:** Medium
- **Dependency:** Task 6

## Task 10: Documentation & Packaging
- **Mô tả:** Viết README, tạo Dockerfile (optional)
- **Acceptance Criteria:**
  - README: setup instructions, cách chạy, libraries used, processing time, peak memory
  - (Optional) Dockerfile chạy được
  - (Optional) Benchmark logs
  - PROMPTS.md nếu dùng AI
- **Độ phức tạp:** Low
- **Dependency:** Task 9

## Thứ tự thực hiện
```
Task 1 → Task 2 → Task 3 → Task 4 → Task 5 → Task 6 → Task 7 → Task 8 → Task 9 → Task 10
         (setup)   (CLI)    (read)   (aggregate) (calc)  (output) (error)  (test)   (perf)   (docs)
```
