# Đối chiếu nghiệp vụ: Cuộc họp cấp Tổng công ty

Tài liệu đối chiếu **quy trình nghiệp vụ** với **hiện trạng dự án** cho trường hợp cuộc họp cấp Tổng công ty.

---

## 1. Quy trình nghiệp vụ (yêu cầu)

### 1.1. Luồng chính

| Bước | Mô tả                                                      |
| ---- | ---------------------------------------------------------- |
| 1    | Thư ký phòng ban **nhận thông báo** khi phòng ban được mời |
| 2    | Thư ký phòng ban: **xác nhận phòng ban tham dự** (auto)    |
| 3    | Thư ký phòng ban: **chọn các cá nhân đại diện** tham dự    |
| 4    | Các cá nhân được chọn **nhận thông báo** tham dự           |

### 1.2. Quy tắc

- Các cá nhân được chỉ định: **bắt buộc tham dự** cuộc họp
- **Trường hợp đặc biệt** (trùng lịch hoặc bất khả kháng):
  - Cá nhân phải báo với **trưởng phòng** để đổi người
  - Phải xử lý **trước khi** thư ký xác nhận trên hệ thống

### 1.3. Lý do nghiệp vụ

- Đảm bảo phòng ban có đại diện phù hợp
- Tránh thay đổi sát giờ họp

---

## 2. Đối chiếu với hiện trạng

### 2.1. Đã có

| Yêu cầu                                  | Hiện trạng                                                  | Ghi chú |
| ---------------------------------------- | ----------------------------------------------------------- | ------- |
| Cấp Tổng công ty                         | ✅ `Meeting.level` = CORPORATE                              |         |
| Chọn phòng ban tham dự                   | ✅ CreateMeetingPage: chọn departments khi level = company  |         |
| Participant theo phòng ban               | ✅ `MeetingParticipant.department` set, `user` = null       |         |
| Cấp Phòng ban: chọn cá nhân              | ✅ Chọn users trực tiếp                                     |         |
| Xác nhận tham gia (cá nhân)              | ✅ InvitationsPage, confirmationStatus, respondToInvitation |         |
| Backend hỗ trợ participant có department | ✅ MeetingParticipant có cả user và department              |         |

### 2.2. Còn thiếu

| Yêu cầu                               | Hiện trạng                                                                                                                                                                  | Đề xuất bổ sung                                                                                                                           |
| ------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| **Thư ký phòng ban nhận thông báo**   | ❌ `MeetingNotificationService.notifyMeetingCreatedOrUpdated` chỉ gửi cho participant có `user != null`. Participant theo department (user = null) **không nhận thông báo** | Gửi thông báo cho **thư ký phòng ban** (user có ROLE_SECRETARY và user.department = participant.department) khi participant là department |
| **Màn "Lời mời phòng tôi"**           | ❌ InvitationsPage chỉ lọc `userId === user.id`. Thư ký không thấy lời mời theo phòng                                                                                       | Thêm tab/màn **"Lời mời phòng tôi"** cho ROLE_SECRETARY: danh sách meeting cấp company có participant.department = phòng của thư ký       |
| **Xác nhận phòng ban tham dự (auto)** | ❌ Chưa có logic auto-confirm department                                                                                                                                    | Có thể: khi thư ký mở "Lời mời phòng tôi" → coi như department đã xác nhận; hoặc thêm `departmentConfirmationStatus` trên participant     |
| **Thư ký chọn cá nhân đại diện**      | ❌ Chưa có UI                                                                                                                                                               | Form "Chọn cá nhân đại diện": multi-select user thuộc phòng → PATCH participant (gắn user, giữ department)                                |
| **Cá nhân được chọn nhận thông báo**  | ❌ Chỉ gửi khi meeting created/updated; khi thư ký chọn user mới không có trigger                                                                                           | Sau khi thư ký chọn user → gọi API cập nhật participant → trigger `notifyMeetingCreatedOrUpdated` hoặc service riêng                      |
| **Quy tắc: cá nhân bắt buộc tham dự** | ⚠️ Có `isRequired` nhưng chưa enforce                                                                                                                                       | Đảm bảo participant do thư ký chọn có `isRequired = true`                                                                                 |
| **Trùng lịch / bất khả kháng**        | ❌ Chưa có                                                                                                                                                                  | Có thể: kiểm tra trùng lịch khi thư ký chọn user; cảnh báo nếu conflict. Phần "báo trưởng phòng" là quy trình ngoài hệ thống              |

---

## 3. Đề xuất triển khai

### Ưu tiên 1: Thông báo cho thư ký phòng ban

**Backend** – `MeetingNotificationService`:

- Khi meeting được tạo/cập nhật/submit với participant theo department:
  - Tìm user có ROLE_SECRETARY và `user.department = participant.department`
  - Gửi notification + email cho thư ký đó (nếu có)

### Ưu tiên 2: Màn "Lời mời phòng tôi"

**Frontend** – InvitationsPage (hoặc tab mới):

- Điều kiện: user có ROLE_SECRETARY
- Hiển thị: meeting cấp company, status APPROVED, có participant.department = phòng của user
- Mỗi meeting: nút "Chọn đại diện" → mở form chọn user thuộc phòng

### Ưu tiên 3: API thư ký chọn đại diện

**Backend**:

- PATCH `/api/meeting-participants/{id}`: cho phép secretary cập nhật participant khi:
  - participant.department = department của secretary
  - meeting.level = CORPORATE
  - user được chọn thuộc department đó
- Sau khi cập nhật: gửi thông báo cho user được chọn

### Ưu tiên 4: Kiểm tra trùng lịch (tùy chọn)

- Khi thư ký chọn user: query meetings trong khoảng thời gian → cảnh báo nếu user đã có cuộc họp trùng

---

## 4. Tóm tắt (đã cập nhật sau triển khai)

| Hạng mục                            | Trạng thái                                   |
| ----------------------------------- | -------------------------------------------- |
| Thông báo thư ký khi phòng được mời | ✅ Đã triển khai                             |
| Màn "Lời mời phòng tôi"             | ✅ Đã triển khai                             |
| Form thư ký chọn cá nhân đại diện   | ✅ Đã triển khai                             |
| Thông báo cá nhân sau khi được chọn | ✅ Đã triển khai                             |
| Auto xác nhận phòng ban             | ⚠️ Coi như xác nhận khi thư ký chọn đại diện |
| Kiểm tra trùng lịch khi chọn        | ⚠️ Tùy chọn (chưa triển khai)                |

**Kết luận:** Nghiệp vụ cuộc họp cấp Tổng công ty đã được triển khai cơ bản.
