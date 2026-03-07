# Quy trình quản lý cuộc họp & Phân quyền

Tài liệu này đối chiếu **yêu cầu nghiệp vụ** với **hiện trạng hệ thống**, liệt kê phần còn thiếu/cần bổ sung, và mô tả **phân quyền** theo vai trò.

---

## 1. Tổng quan quy trình (3 giai đoạn)

| Giai đoạn                        | Mục đích                                                           |
| -------------------------------- | ------------------------------------------------------------------ |
| **Trước cuộc họp (Preparation)** | Lập kế hoạch, phê duyệt phòng, xác nhận tham dự, chuẩn bị tài liệu |
| **Trong cuộc họp (Execution)**   | Điểm danh, báo cáo sự cố                                           |
| **Sau cuộc họp (Post-meeting)**  | Upload biên bản, giao nhiệm vụ                                     |

---

## 2. Trước cuộc họp (Preparation)

### 2.1. Tạo lịch cuộc họp

**Actor:** Người tổ chức (requester).

| Yêu cầu                                | Hiện trạng                                    | Ghi chú                                                   |
| -------------------------------------- | --------------------------------------------- | --------------------------------------------------------- |
| Tên cuộc họp                           | ✅ `Meeting.title`                            |                                                           |
| Loại cuộc họp                          | ✅ `Meeting.type` (MeetingType)               |                                                           |
| Cấp: Tổng công ty / Phòng ban          | ✅ `Meeting.level` (CORPORATE / DEPARTMENT)   |                                                           |
| Thời gian bắt đầu / kết thúc           | ✅ `Meeting.startTime`, `endTime`             |                                                           |
| Phòng họp                              | ✅ `Meeting.room`                             |                                                           |
| Hình thức: trực tiếp / online / hybrid | ✅ `Meeting.mode` (IN_PERSON, ONLINE, HYBRID) |                                                           |
| Chủ trì cuộc họp                       | ✅ `Meeting.host`                             |                                                           |
| **Thư ký cuộc họp**                    | ✅ **Đã bổ sung** `Meeting.secretary`         | Dùng cho upload biên bản, xác nhận tham dự theo phòng ban |
| Mô tả nội dung                         | ✅ `Meeting.objectives`, `note`               |                                                           |
| Lý do nghiệp vụ                        | ⚠️ Dùng `note` hoặc `objectives`              | Có thể thêm trường `businessReason` riêng nếu cần         |

### 2.2. Thành phần tham dự

| Yêu cầu                                                         | Hiện trạng                                                                                                                        | Ghi chú                                                                                                    |
| --------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| Cấp Tổng công ty: chọn theo **phòng ban** hoặc **cá nhân**      | ✅ `MeetingParticipant` có `user` và `department`                                                                                 | Tham dự theo phòng = participant.department set, thư ký phòng chọn cá nhân sau                             |
| Cấp Phòng ban: chọn **cá nhân**                                 | ✅ Participant gắn `user`                                                                                                         |                                                                                                            |
| Giao task chuẩn bị tài liệu cho cá nhân/phòng ban               | ✅ `MeetingTask` type `PRE_MEETING`, `assignee` hoặc `department`                                                                 |                                                                                                            |
| Quy tắc: người được giao chuẩn bị tài liệu **bắt buộc tham dự** | ⚠️ Logic nghiệp vụ                                                                                                                | Có thể validate: nếu user có PRE_MEETING task thì phải có trong participants                               |
| Người trình bày agenda **bắt buộc** trong danh sách tham dự     | ⚠️ `AgendaItem.presenterName` (text)                                                                                              | Có thể ràng buộc: presenterName trùng tên/email trong participants hoặc thêm AgendaItem.presenterId → User |
| **Xác nhận tham gia / không tham gia (có lý do)**               | ✅ **Đã bổ sung** `MeetingParticipant.confirmationStatus` (PENDING / CONFIRMED / DECLINED), `absentReason` dùng cho lý do từ chối | Trước họp: invitee confirm/decline; trong họp: điểm danh (attendance)                                      |

### 2.3. Agenda cuộc họp

| Yêu cầu                                   | Hiện trạng                                                                    | Ghi chú                                                                              |
| ----------------------------------------- | ----------------------------------------------------------------------------- | ------------------------------------------------------------------------------------ |
| Nội dung (topic)                          | ✅ `AgendaItem.topic`                                                         |                                                                                      |
| Thời gian bắt đầu / kết thúc từng mục     | ⚠️ Chỉ có `durationMinutes`                                                   | Có thể thêm `startTime`/`endTime` cho AgendaItem để kiểm tra nằm trong khung meeting |
| Người trình bày                           | ✅ `AgendaItem.presenterName`                                                 |                                                                                      |
| Tài liệu liên quan                        | ✅ `MeetingDocument` gắn `meeting` (có thể gắn thêm `task` cho task chuẩn bị) |                                                                                      |
| Agenda nằm trong khung thời gian cuộc họp | ⚠️ Validate khi lưu                                                           | Có thể validate: sum(durationMinutes) ≤ (endTime - startTime) của meeting            |

### 2.4. Phê duyệt phòng họp

| Yêu cầu                                                 | Hiện trạng                                                 | Ghi chú      |
| ------------------------------------------------------- | ---------------------------------------------------------- | ------------ |
| Gửi yêu cầu đến Quản lý phòng họp                       | ✅ Submit meeting → status PENDING_APPROVAL (khi cần room) |              |
| Phê duyệt / Từ chối (kèm lý do)                         | ✅ `MeetingApproval`, `approveRoom` / `reject`             | ROOM_MANAGER |
| Từ chối → người tổ chức chỉnh sửa / đổi phòng / đổi giờ | ✅ Status REJECTED, chỉnh sửa rồi submit lại               |              |

**Quy tắc hiện tại:**

- Họp **cấp Tổng công ty**: có thể auto-approve (theo cấu hình).
- Họp **cấp Phòng ban** và có phòng (IN_PERSON/HYBRID): cần ROOM_MANAGER phê duyệt.

### 2.5. Xác nhận tham dự (sau khi phê duyệt phòng)

| Yêu cầu                                                                   | Hiện trạng                                                                              | Ghi chú                                                              |
| ------------------------------------------------------------------------- | --------------------------------------------------------------------------------------- | -------------------------------------------------------------------- |
| Cấp Tổng công ty: thư ký phòng ban nhận thông báo → chọn cá nhân đại diện | ✅ Participant theo `department`; thư ký (ROLE_SECRETARY) có quyền cập nhật participant | Cần UI + API cho thư ký chọn user thay cho department                |
| Cấp Phòng ban: cá nhân xác nhận Tham gia / Không tham gia (có lý do)      | ✅ `confirmationStatus` + `absentReason`                                                |                                                                      |
| Task chuẩn bị tài liệu: bắt buộc hoàn thành trước giờ họp                 | ⚠️ Logic / báo cáo                                                                      | Có thể cảnh báo nếu PRE_MEETING task chưa DONE khi meeting.startTime |

### 2.6. Chuẩn bị tài liệu

| Yêu cầu                                    | Hiện trạng                                                                  | Ghi chú |
| ------------------------------------------ | --------------------------------------------------------------------------- | ------- |
| Upload tài liệu / đánh dấu hoàn thành task | ✅ `MeetingDocument` (docType có thể là PREP/MINUTES), `MeetingTask.status` |         |

---

## 3. Trong cuộc họp (Execution)

### 3.1. Điểm danh

| Yêu cầu                                           | Hiện trạng                                                               | Ghi chú                                                                                                                 |
| ------------------------------------------------- | ------------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------- |
| Trạng thái: Đã điểm danh / Vắng mặt               | ✅ `MeetingParticipant.attendance`: PRESENT, ABSENT, NOT_MARKED, EXCUSED |                                                                                                                         |
| Điểm danh bù: yêu cầu → Chủ trì phê duyệt/từ chối | ⚠️ Chưa có luồng rõ ràng                                                 | Đề xuất: thêm `lateCheckInRequestedAt` (Instant) trên participant; API "request late check-in" và "host approve/reject" |

### 3.2. Báo cáo sự cố trong cuộc họp

| Yêu cầu                                  | Hiện trạng                                                               | Ghi chú                                                                      |
| ---------------------------------------- | ------------------------------------------------------------------------ | ---------------------------------------------------------------------------- |
| Báo cáo sự cố (thiết bị, kết nối, …)     | ✅ `Incident`: title, description, severity, status, meeting, reportedBy |                                                                              |
| Chuyển đến nhân viên hỗ trợ / tạo ticket | ⚠️ Phần tích hợp                                                         | Có thể dùng `Incident.status` (OPEN/IN_PROGRESS/RESOLVED) và thông báo/email |

---

## 4. Sau cuộc họp (Post-meeting)

### 4.1. Upload biên bản

| Yêu cầu                             | Hiện trạng                                                                                         | Ghi chú |
| ----------------------------------- | -------------------------------------------------------------------------------------------------- | ------- |
| Chủ trì hoặc Thư ký upload Biên bản | ✅ `MeetingDocument` với docType = "MINUTES" (hoặc tương đương); quyền upload cho host + secretary |         |

### 4.2. Giao nhiệm vụ sau cuộc họp

| Yêu cầu                           | Hiện trạng                                                     | Ghi chú                                                  |
| --------------------------------- | -------------------------------------------------------------- | -------------------------------------------------------- |
| Giao task cho cá nhân / phòng ban | ✅ `MeetingTask` type `POST_MEETING`, assignee hoặc department |                                                          |
| Hệ thống chỉ ghi nhận giao task   | ✅ Đúng với thiết kế hiện tại                                  | Không bắt buộc quản lý tiến độ chi tiết trong module họp |

---

## 5. Phân quyền theo vai trò (Role)

### 5.1. Danh sách role (Authorities)

| Role (Authority)      | Tên hiển thị                 | Mô tả                                                                                                                                                             |
| --------------------- | ---------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ROLE_ADMIN**        | Quản trị viên                | Toàn quyền: quản lý user, phòng ban, phòng họp, cấu hình, và mọi API admin.                                                                                       |
| **ROLE_USER**         | Nhân viên                    | Người dùng cơ bản: tạo/sửa/xóa cuộc họp (của mình), tạo participant/task/agenda, submit, cancel, complete; xác nhận tham dự (bản thân); điểm danh; báo cáo sự cố. |
| **ROLE_SECRETARY**    | Thư ký                       | Trên cơ sở USER: đại diện phòng ban chọn cá nhân tham dự (company-level); upload biên bản; có thể được chỉ định là Thư ký cuộc họp (`Meeting.secretary`).         |
| **ROLE_ROOM_MANAGER** | Quản lý phòng họp            | Phê duyệt/từ chối yêu cầu đặt phòng (approve-room, reject); xem danh sách meeting liên quan phòng.                                                                |
| **ROLE_UNIT_MANAGER** | Quản lý đơn vị (cấp công ty) | Phê duyệt cấp tổng công ty (approve-unit); reject khi có quyền.                                                                                                   |

Một user có thể có nhiều role (ví dụ: vừa USER vừa SECRETARY, vừa ROOM_MANAGER).

### 5.2. Ma trận quyền theo nghiệp vụ

| Nghiệp vụ                                         | USER                                 | SECRETARY                     | ROOM_MANAGER | UNIT_MANAGER | ADMIN |
| ------------------------------------------------- | ------------------------------------ | ----------------------------- | ------------ | ------------ | ----- |
| Tạo / sửa / xóa cuộc họp (của mình)               | ✅                                   | ✅                            | ✅\*         | ✅\*         | ✅    |
| Submit cuộc họp (trình duyệt)                     | ✅ (requester/host)                  | ✅ (secretary của cuộc đó)    | -            | -            | ✅    |
| Phê duyệt phòng (approve-room / reject)           | -                                    | -                             | ✅           | -            | ✅    |
| Phê duyệt đơn vị (approve-unit)                   | -                                    | -                             | -            | ✅           | ✅    |
| Hủy cuộc họp (cancel)                             | ✅ (requester/host/secretary)        | ✅                            | -            | -            | ✅    |
| Kết thúc cuộc họp (complete)                      | ✅ (host/secretary)                  | ✅                            | -            | -            | ✅    |
| Thêm/sửa/xóa participant, agenda, task            | ✅ (owner meeting)                   | ✅ (nếu là secretary meeting) | -            | -            | ✅    |
| Thư ký phòng chọn cá nhân tham dự (company-level) | -                                    | ✅ (theo phòng của mình)      | -            | -            | ✅    |
| Xác nhận tham gia / từ chối (bản thân)            | ✅                                   | ✅                            | -            | -            | ✅    |
| Điểm danh (bản thân)                              | ✅                                   | ✅                            | -            | -            | ✅    |
| Phê duyệt điểm danh bù (host)                     | ✅ (nếu là host)                     | ✅ (nếu là secretary)         | -            | -            | ✅    |
| Upload tài liệu / biên bản                        | ✅ (nếu là host/requester/secretary) | ✅                            | -            | -            | ✅    |
| Báo cáo sự cố (incident)                          | ✅ (người tham dự)                   | ✅                            | -            | -            | ✅    |
| Quản lý user, phòng ban, phòng họp, authority     | -                                    | -                             | -            | -            | ✅    |

\* ROOM_MANAGER / UNIT_MANAGER thường vẫn có ROLE_USER nên vẫn tạo/sửa meeting như nhân viên.

### 5.3. Ràng buộc theo ngữ cảnh (sẽ áp dụng trong API/UI)

- **Chỉ requester, host hoặc secretary của cuộc họp** mới được: submit, cancel, complete, sửa participant/agenda/task, upload biên bản, phê duyệt điểm danh bù.
- **ROOM_MANAGER**: chỉ được approve-room / reject các meeting đang PENDING_APPROVAL (và có thể giới hạn theo phòng mình quản lý nếu có).
- **UNIT_MANAGER**: chỉ được approve-unit / reject với meeting cấp Tổng công ty.
- **SECRETARY**: “Thư ký phòng chọn cá nhân” thường áp dụng cho meeting mà participant theo **department** và department trùng phòng ban của thư ký.

---

## 6. Cấu hình dữ liệu khởi tạo (Authorities)

Đảm bảo trong DB có các bản ghi `authority`:

- `ROLE_USER`
- `ROLE_ADMIN`
- `ROLE_SECRETARY`
- `ROLE_ROOM_MANAGER`
- `ROLE_UNIT_MANAGER`

Gán role cho user qua bảng `user_authority` (hoặc màn hình quản lý nhân viên). Ví dụ: user thư ký phòng có `ROLE_USER` + `ROLE_SECRETARY`; quản lý phòng họp có `ROLE_USER` + `ROLE_ROOM_MANAGER`.

---

## 7. Tóm tắt đã bổ sung trong code

- **Meeting.secretary** (User, optional): Thư ký cuộc họp; dùng trong luồng upload biên bản và quyền thao tác cuộc họp.
- **MeetingParticipant.confirmationStatus** (PENDING | CONFIRMED | DECLINED): Xác nhận tham dự trước họp; khi DECLINED dùng `absentReason` để ghi lý do.

Các mục đánh dấu ⚠️ có thể triển khai ở bước sau (validation presenter trong participants, agenda start/end, điểm danh bù, lý do nghiệp vụ riêng, tích hợp ticket sự cố).
