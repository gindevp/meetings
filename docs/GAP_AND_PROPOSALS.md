# Đối chiếu nghiệp vụ & Đề xuất bổ sung

Đối chiếu với tài liệu **Quy trình quản lý cuộc họp** và **MEETING_PROCESS_AND_PERMISSIONS.md**.

---

## 1. Đã có (khớp tài liệu)

| Nghiệp vụ / Màn hình                                                        | Ghi chú                                                |
| --------------------------------------------------------------------------- | ------------------------------------------------------ |
| Tạo/sửa lịch họp (thông tin chung, phòng, host, thư ký, level, mode)        | CreateMeetingPage, API meetings                        |
| Thành phần tham dự (cá nhân / phòng ban), agenda, task chuẩn bị             | Form 3 bước, with-details                              |
| Phê duyệt phòng / từ chối (kèm lý do)                                       | Dialog chi tiết, ROOM_MANAGER                          |
| **Lời mời chỉ khi đã phê duyệt**                                            | Filter APPROVED trên Lời mời + block xác nhận          |
| Xác nhận tham gia / Từ chối (có lý do)                                      | Màn Lời mời + block trong dialog chi tiết              |
| Quản lý kế hoạch (tab trạng thái, tìm kiếm)                                 | MeetingPlanPage                                        |
| Màn Lời mời của tôi                                                         | InvitationsPage, menu "Lời mời"                        |
| Phân quyền: requester/host/secretary (submit, cancel, complete, upload doc) | Backend canManageMeeting, document check               |
| Upload tài liệu (khi tạo/sửa meeting)                                       | Gửi kèm meeting, docType ATTACHMENT (hiện là text ref) |

---

## 2. Còn thiếu so với tài liệu

### 2.1. Trong cuộc họp (Execution)

| Nghiệp vụ        | Tài liệu                                                                       | Hiện trạng                                                                                                                        | Đề xuất                                                                                                                                                                                                                                                              |
| ---------------- | ------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Điểm danh**    | Khi cuộc họp bắt đầu, thành viên điểm danh; trạng thái Đã điểm danh / Vắng mặt | Backend có `attendance` (PRESENT, ABSENT, NOT_MARKED, EXCUSED). **Không có màn** cho host điểm danh hoặc participant tự điểm danh | **Màn/block "Điểm danh"**: với cuộc họp đã APPROVED (trong khung giờ hoặc sau khi bắt đầu), host/chủ trì xem danh sách participant và đánh dấu PRESENT/ABSENT; hoặc participant tự bấm "Tôi đã có mặt" (chỉ cập nhật bản thân). API: PATCH participant (attendance). |
| **Điểm danh bù** | Người tham dự gửi yêu cầu → Chủ trì phê duyệt/từ chối                          | Chưa có                                                                                                                           | **Giai đoạn 2**: Thêm trạng thái "Yêu cầu điểm danh bù" (backend có thể dùng `lateCheckInRequestedAt` hoặc trạng thái tương đương), API request + API host approve/reject, UI nút "Yêu cầu điểm danh bù" và block cho host "Phê duyệt điểm danh bù".                 |

### 2.2. Báo cáo sự cố trong cuộc họp

| Nghiệp vụ                                | Tài liệu                                                            | Hiện trạng                                                                                       | Đề xuất                                                                                                                                                                                                                                                                                                               |
| ---------------------------------------- | ------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Báo cáo sự cố** (thiết bị, kết nối, …) | Người tham dự báo cáo trên hệ thống; chuyển đến hỗ trợ / tạo ticket | Backend có **Incident** (POST/PUT/GET /api/incidents). **Frontend chưa có màn** tạo/xem incident | **Màn/block "Báo cáo sự cố"**: Trong dialog chi tiết cuộc họp (hoặc màn chi tiết cuộc họp đang diễn ra), khi meeting approved/đang trong khung giờ: nút "Báo cáo sự cố" → form (tiêu đề, mô tả, mức độ) → gọi POST /api/incidents (meetingId, reportedBy). Có thể thêm tab/danh sách "Sự cố" trong chi tiết cuộc họp. |

### 2.3. Sau cuộc họp (Post-meeting)

| Nghiệp vụ                      | Tài liệu                                             | Hiện trạng                                                                                                                              | Đề xuất                                                                                                                                                                                                                                                                                                       |
| ------------------------------ | ---------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Upload biên bản**            | Chủ trì hoặc Thư ký upload Biên bản sau khi kết thúc | Tài liệu được gửi kèm khi tạo/sửa meeting (docType ATTACHMENT). **Chưa có luồng rõ** "Upload biên bản" **sau khi** bấm Hoàn thành       | **Block "Upload biên bản"**: Khi meeting status = completed, trong dialog chi tiết (hoặc màn chi tiết) hiển thị block "Biên bản cuộc họp" cho host/secretary: upload file (docType = MINUTES), gọi POST meeting-documents. Cần hỗ trợ upload file thật (multipart hoặc base64) nếu hiện tại chỉ gửi tên file. |
| **Giao nhiệm vụ sau cuộc họp** | Chủ trì giao task cho cá nhân/phòng ban sau họp      | Task POST_MEETING có thể thêm khi tạo/sửa meeting. **Chưa có** thao tác "Thêm task sau khi hoàn thành" (chỉnh sửa meeting sau complete) | **Tùy chọn**: Cho phép host/secretary "Thêm công việc sau họp" khi meeting đã completed: mở form thêm task (POST_MEETING) gắn meetingId, assignee/department. Hoặc giữ như hiện tại (chỉ giao task khi tạo/sửa meeting).                                                                                      |

### 2.4. Trước cuộc họp (bổ sung)

| Nghiệp vụ                                                           | Tài liệu                                                                                                  | Hiện trạng                                                                                                                                                                                                 | Đề xuất                                                                                                                                                                                                                                                                                                                                                                                           |
| ------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Thư ký phòng ban chọn cá nhân đại diện**                          | Cấp Tổng công ty: participant theo phòng ban → thư ký phòng nhận thông báo, chọn cá nhân đại diện tham dự | Participant có thể là department. **Chưa có màn** cho thư ký (ROLE_SECRETARY) "Lời mời phòng tôi" để với từng meeting (company-level) mà participant là department của mình, chọn user thay cho department | **Màn "Đại diện phòng tham dự"** (hoặc tab trong Lời mời): List meeting cấp company có participant là department của thư ký; với từng meeting, form "Chọn cá nhân đại diện" (multi-select user thuộc phòng) → tạo/cập nhật participant (gắn user, có thể giữ hoặc bỏ department). Backend cho phép secretary cập nhật participant thuộc meeting đó khi participant.department = phòng của thư ký. |
| **Validation**: Người trình bày agenda phải trong danh sách tham dự | Quy tắc nghiệp vụ                                                                                         | Chưa validate                                                                                                                                                                                              | Có thể thêm validate khi lưu (backend hoặc frontend): so khớp presenterName với tên user trong participants, hoặc thêm AgendaItem.presenterId → User.                                                                                                                                                                                                                                             |
| **Validation**: Task chuẩn bị tài liệu → assignee bắt buộc tham dự  | Quy tắc nghiệp vụ                                                                                         | Chưa validate                                                                                                                                                                                              | Cảnh báo hoặc ràng buộc: nếu có PRE_MEETING task có assignee thì assignee phải có trong participants.                                                                                                                                                                                                                                                                                             |

---

## 3. Đề xuất ưu tiên triển khai

### Ưu tiên cao (đúng luồng tài liệu)

1. **Điểm danh (roll call)**

   - Màn/block: với cuộc họp đã APPROVED, host xem danh sách participant và đánh dấu Đã điểm danh / Vắng mặt; hoặc participant tự bấm "Tôi đã có mặt".
   - API: PATCH `/api/meeting-participants/{id}` (attendance). Ràng buộc: chỉ host/secretary được sửa attendance của người khác; participant chỉ được sửa attendance của chính mình (tự điểm danh).

2. **Báo cáo sự cố**

   - Trong dialog chi tiết cuộc họp (khi meeting approved hoặc trong khung giờ): nút "Báo cáo sự cố" → form (title, description, severity) → POST `/api/incidents`.
   - (Tùy chọn) Hiển thị danh sách sự cố của cuộc họp (GET incidents by meetingId).

3. **Upload biên bản sau khi hoàn thành**
   - Khi meeting status = completed: block "Upload biên bản" cho host/secretary, upload file (docType MINUTES), POST meeting-documents.
   - Cần kiểm tra backend có nhận file (binary/base64) và lưu đúng; nếu hiện chỉ nhận tên file thì bổ sung upload file thật.

### Ưu tiên trung bình

4. **Điểm danh bù**: Luồng yêu cầu điểm danh bù → chủ trì phê duyệt/từ chối (backend + UI).

5. **Thư ký phòng chọn cá nhân đại diện**: Màn/tab cho secretary chọn user đại diện cho các meeting cấp company mà participant là department của mình.

### Ưu tiên thấp / tùy chọn

6. Validation: presenter trong participants; assignee task chuẩn bị phải trong participants.
7. Thêm task sau khi hoàn thành (giao việc sau họp) mà không cần mở lại form sửa meeting.
8. Agenda: thêm startTime/endTime từng mục; validate tổng thời gian agenda ≤ thời gian cuộc họp.

---

## 4. Tóm tắt màn hình còn thiếu

| Màn hình / Block                      | Mô tả ngắn                                                                                                                                                                    |
| ------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Điểm danh**                         | Host/chủ trì đánh dấu có mặt/vắng cho từng participant; hoặc participant tự "Tôi đã có mặt". Có thể là block trong dialog chi tiết cuộc họp (khi approved) thay vì màn riêng. |
| **Báo cáo sự cố**                     | Form tạo incident gắn meeting (trong dialog chi tiết hoặc màn chi tiết); có thể kèm danh sách sự cố của cuộc họp.                                                             |
| **Upload biên bản**                   | Block "Upload biên bản" khi meeting đã hoàn thành, cho host/secretary, upload file MINUTES.                                                                                   |
| **Đại diện phòng tham dự** (tùy chọn) | Màn/tab cho thư ký: danh sách meeting cấp company có participant là phòng mình → chọn cá nhân đại diện.                                                                       |

Nếu cần, có thể triển khai lần lượt theo thứ tự: **Điểm danh** → **Báo cáo sự cố** → **Upload biên bản** → (sau đó) Điểm danh bù và Thư ký chọn đại diện.
