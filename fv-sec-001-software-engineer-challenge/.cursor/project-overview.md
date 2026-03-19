# Project Overview

## Tóm tắt
Đây là bài challenge tuyển dụng Software Engineer của công ty Flinters Vietnam (FV-SEC001). Ứng viên cần xây dựng một **CLI application** xử lý file CSV quảng cáo lớn (~1GB), tính toán các chỉ số hiệu suất (CTR, CPA), và xuất ra 2 file CSV kết quả.

## Mục tiêu chính
- Xử lý file CSV ~1GB chứa dữ liệu quảng cáo (ad performance records)
- Aggregate dữ liệu theo `campaign_id`
- Tính các metrics: total_impressions, total_clicks, total_spend, total_conversions, CTR, CPA
- Xuất 2 file kết quả: `top10_ctr.csv` và `top10_cpa.csv`

## Phạm vi (Scope)
- **Trong scope:**
  - Đọc và parse file CSV lớn
  - Aggregate dữ liệu theo campaign_id
  - Tính CTR = total_clicks / total_impressions
  - Tính CPA = total_spend / total_conversions (null nếu conversions = 0)
  - Xuất Top 10 CTR cao nhất → `top10_ctr.csv`
  - Xuất Top 10 CPA thấp nhất (loại campaign có 0 conversions) → `top10_cpa.csv`
  - CLI interface với tham số `--input` và `--output`
  - Error handling, tests, documentation

- **Ngoài scope:**
  - Không cần UI/web interface
  - Không cần database
  - Không cần real-time processing
  - Không cần API endpoint

## Đối tượng đánh giá
- Nhà tuyển dụng tại Flinters Vietnam (backoffice@flinters.vn)
- Đánh giá: code quality, performance, memory efficiency, clean code, error handling, testing
