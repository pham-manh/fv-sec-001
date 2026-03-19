# Business Logic

## Luồng xử lý chính
```
Input CSV → Read/Stream → Parse Rows → Aggregate by campaign_id → Calculate Metrics → Sort & Rank → Output 2 CSV files
```

## Bước 1: Aggregate theo campaign_id

Với mỗi `campaign_id`, cộng dồn tất cả các dòng cùng campaign:
- `total_impressions` = SUM(impressions)
- `total_clicks` = SUM(clicks)
- `total_spend` = SUM(spend)
- `total_conversions` = SUM(conversions)

## Bước 2: Tính Metrics

### CTR (Click-Through Rate)
```
CTR = total_clicks / total_impressions
```
- Nếu total_impressions = 0 → **Cần làm rõ** (README không nói rõ, nên xử lý an toàn: trả về 0 hoặc null)

### CPA (Cost Per Acquisition)
```
CPA = total_spend / total_conversions
```
- Nếu total_conversions = 0 → trả về `null` (hoặc ignore)

## Bước 3: Tạo 2 danh sách kết quả

### A. Top 10 CTR cao nhất (`top10_ctr.csv`)
- Sort descending theo CTR
- Lấy 10 campaign đầu tiên
- Columns: campaign_id, total_impressions, total_clicks, total_spend, total_conversions, CTR, CPA

### B. Top 10 CPA thấp nhất (`top10_cpa.csv`)
- **Loại bỏ** campaign có total_conversions = 0
- Sort ascending theo CPA
- Lấy 10 campaign đầu tiên
- Columns: campaign_id, total_impressions, total_clicks, total_spend, total_conversions, CTR, CPA

## Edge Cases cần xử lý
| Case | Xử lý |
|------|--------|
| File không tồn tại | Báo lỗi rõ ràng, exit gracefully |
| Dòng CSV bị malformed (thiếu cột, sai format) | Skip dòng đó, log warning |
| impressions = 0 | CTR = 0 hoặc null, tránh chia cho 0 |
| conversions = 0 | CPA = null, loại khỏi top10_cpa |
| Giá trị âm (spend < 0, clicks < 0) | Validate và skip hoặc log warning |
| File rỗng | Báo lỗi hoặc tạo file output rỗng |
| Campaign chỉ có 1 record | Vẫn tính bình thường |
| Duplicate header rows | Skip |
| Encoding issues | Handle UTF-8 |

## Data Entities

### CampaignAggregate
```
{
  campaign_id: string,
  total_impressions: integer,
  total_clicks: integer,
  total_spend: float,
  total_conversions: integer,
  ctr: float,       // computed
  cpa: float | null  // computed
}
```

## Format Output CSV
- CTR: 4 decimal places (ví dụ: 0.0500)
- CPA: 2 decimal places (ví dụ: 20.00)
- Có header row
- Separator: comma
