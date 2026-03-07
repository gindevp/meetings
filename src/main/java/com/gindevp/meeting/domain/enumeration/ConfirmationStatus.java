package com.gindevp.meeting.domain.enumeration;

/**
 * Trạng thái xác nhận tham dự trước cuộc họp (invitee response).
 */
public enum ConfirmationStatus {
    /** Chưa xác nhận */
    PENDING,
    /** Đã xác nhận tham gia */
    CONFIRMED,
    /** Không tham gia (lý do ghi ở absentReason) */
    DECLINED,
}
