# Technical Requirements

## Tech Stack
- **Ngôn ngữ:** Tùy chọn — Python, NodeJS, Go, Java, Rust, etc.
- **Loại ứng dụng:** Console Application (CLI)
- **Input format:** CSV file (~1GB)
- **Output format:** 2 file CSV

## CLI Interface
```bash
# Ví dụ chạy chương trình
python aggregator.py --input ad_data.csv --output results/
```
- Tham số `--input`: đường dẫn tới file CSV đầu vào
- Tham số `--output`: đường dẫn folder chứa kết quả

## CSV Schema — Input
| Column       | Type    | Description                    |
|--------------|---------|--------------------------------|
| campaign_id  | string  | Campaign ID (format: CMPxxx)   |
| date         | string  | Ngày, format `YYYY-MM-DD`     |
| impressions  | integer | Số lần hiển thị               |
| clicks       | integer | Số lần click                  |
| spend        | float   | Chi phí quảng cáo (USD)       |
| conversions  | integer | Số lần chuyển đổi             |

## Yêu cầu Performance & Memory
- File ~1GB → **KHÔNG được load toàn bộ vào RAM**
- Phải dùng streaming/chunked processing
- Đo và báo cáo: processing time, peak memory usage
- Tối ưu: dùng buffer, batch processing, hoặc generator/iterator pattern

## Yêu cầu Output
- 2 file CSV: `top10_ctr.csv`, `top10_cpa.csv`
- Format số: CTR 4 chữ số thập phân (0.0500), CPA 2 chữ số thập phân (20.00)

## Deliverables
1. Source code trên GitHub repository
2. Output files: `top10_ctr.csv`, `top10_cpa.csv`
3. README.md: setup, cách chạy, libraries, processing time, peak memory
4. (Optional) Dockerfile, benchmark logs
5. (Nếu dùng AI) `PROMPTS.md` — paste nguyên prompt đã dùng, không chỉnh sửa

## Submission
- Gửi GitHub repo link qua email: backoffice@flinters.vn
